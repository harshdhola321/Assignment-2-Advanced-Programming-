package org.example.service;

import org.example.exception.ComplianceException;
import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.*;
import org.example.repository.PatientRepository;
import org.example.repository.StaffRepository;
import org.example.repository.WardRepository;
import org.example.repository.impl.FilePatientRepository;
import org.example.repository.impl.FileStaffRepository;
import org.example.repository.impl.FileWardRepository;
import org.example.util.DefaultDataGenerator;
import org.example.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for the care home system
 */
public class CareHomeService {
    private static final Logger logger = LoggerFactory.getLogger(CareHomeService.class);
    private static CareHomeService instance;

    // Repositories
    private WardRepository wardRepository;
    private StaffRepository staffRepository;
    private PatientRepository patientRepository;

    private List<Ward> wards;
    private List<Staff> staff;
    private List<Patient> patients;
    private List<Patient> dischargedPatients;

    private AuthenticationService authService;
    private LoggingService logService;

    /**
     * Private constructor for singleton pattern
     */
    private CareHomeService() {
        // Initialize repositories
        wardRepository = new FileWardRepository();
        staffRepository = new FileStaffRepository();
        patientRepository = new FilePatientRepository();

        // Load data from repositories
        wards = wardRepository.findAllWards();
        staff = staffRepository.findAll();
        patients = patientRepository.findAll();
        dischargedPatients = patientRepository.findDischargedPatients();

        logger.info("Data loaded from repositories");

        authService = AuthenticationService.getInstance();
        logService = LoggingService.getInstance();
    }

    /**
     * Get the singleton instance
     * @return The singleton instance
     */
    public static CareHomeService getInstance() {
        if (instance == null) {
            instance = new CareHomeService();
        }
        return instance;
    }

    /**
     * Save all data to repositories
     */
    public void saveData() {
        wardRepository.saveAllWards(wards);
        staffRepository.saveAll(staff);
        patientRepository.saveAll(patients);
        patientRepository.saveAllDischarged(dischargedPatients);
        logger.info("Data saved to repositories");
    }

    /**
     * Get all wards
     * @return All wards
     */
    public List<Ward> getAllWards() {
        return wardRepository.findAllWards();
    }

    /**
     * Get all staff
     * @return All staff
     */
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    /**
     * Get all discharged patients
     * @return All discharged patients
     */
    public List<Patient> getDischargedPatients() {
        return patientRepository.findDischargedPatients();
    }

    /**
     * Add a new staff member to the system
     * @param staff The staff member to add
     * @return true if the staff was added successfully, false otherwise
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    public boolean addStaff(Staff staff) throws UnauthorizedActionException, NotRosteredException {
        authService.checkAuthorizedAndRostered("ADD_STAFF");

        // Check if username already exists
        boolean usernameExists = this.staff.stream()
                .anyMatch(s -> s.getUsername().equals(staff.getUsername()));

        if (usernameExists) {
            logger.warn("Username already exists: {}", staff.getUsername());
            return false;
        }

        // Add the staff member
        this.staff.add(staff);
        staffRepository.save(staff);

        logService.logAction(
                "ADD_STAFF",
                authService.getCurrentUser(),
                "Added staff member " + staff.getFullName() + " (" + staff.getClass().getSimpleName() + ")"
        );

        logger.info("Staff member added: {} ({})", staff.getFullName(), staff.getClass().getSimpleName());
        return true;
    }

    /**
     * Update a staff member in the system
     * @param staff The staff member to update
     * @return true if the staff was updated successfully, false otherwise
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    public boolean updateStaff(Staff staff) throws UnauthorizedActionException, NotRosteredException {
        authService.checkAuthorizedAndRostered("EDIT_STAFF");

        // Find the staff member in the list
        Optional<Staff> existingStaffOpt = this.staff.stream()
                .filter(s -> s.getId().equals(staff.getId()))
                .findFirst();

        if (!existingStaffOpt.isPresent()) {
            logger.warn("Staff member not found: {}", staff.getId());
            return false;
        }

        // Replace the staff member
        int index = this.staff.indexOf(existingStaffOpt.get());
        this.staff.set(index, staff);
        staffRepository.save(staff);

        logService.logAction(
                "UPDATE_STAFF",
                authService.getCurrentUser(),
                "Updated staff member " + staff.getFullName() + " (" + staff.getClass().getSimpleName() + ")"
        );

        logger.info("Staff member updated: {} ({})", staff.getFullName(), staff.getClass().getSimpleName());
        return true;
    }

    /**
     * Add a patient to the system
     * @param firstName The patient's first name
     * @param lastName The patient's last name
     * @param dateOfBirth The patient's date of birth
     * @param gender The patient's gender
     * @param medicalCondition The patient's medical condition
     * @param needsIsolation Whether the patient needs isolation
     * @param bed The bed to assign the patient to
     * @return The created patient
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    public Patient addPatient(String firstName, String lastName, LocalDate dateOfBirth, Gender gender,
                              String medicalCondition, boolean needsIsolation, Bed bed)
            throws UnauthorizedActionException, NotRosteredException {

        Patient patient = new Patient(
                IdGenerator.generateId("PAT"),
                firstName,
                lastName,
                dateOfBirth,
                gender,
                medicalCondition,
                needsIsolation
        );

        addPatient(patient, bed);

        return patient;
    }

    /**
     * Add a patient to the system
     * @param patient The patient to add
     * @param bed The bed to assign the patient to
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    public void addPatient(Patient patient, Bed bed) throws UnauthorizedActionException, NotRosteredException {
        authService.checkAuthorizedAndRostered("ADD_PATIENT");

        if (bed.isOccupied()) {
            throw new IllegalArgumentException("Bed is already occupied");
        }

        // Check if the room is suitable for the patient's gender and isolation needs
        Room room = bed.getRoom();
        boolean hasOtherGender = false;

        for (Bed otherBed : room.getBeds()) {
            if (otherBed != bed && otherBed.isOccupied() && otherBed.getPatient().getGender() != patient.getGender()) {
                hasOtherGender = true;
                break;
            }
        }

        if (hasOtherGender) {
            throw new IllegalArgumentException("Cannot assign patient to a room with patients of different gender");
        }

        if (patient.isNeedsIsolation() && room.getNumberOfBeds() > 1) {
            boolean hasOtherPatients = false;
            for (Bed otherBed : room.getBeds()) {
                if (otherBed != bed && otherBed.isOccupied()) {
                    hasOtherPatients = true;
                    break;
                }
            }

            if (hasOtherPatients) {
                throw new IllegalArgumentException("Cannot assign patient requiring isolation to a shared room");
            }
        }

        bed.assignPatient(patient);
        patients.add(patient);
        patientRepository.save(patient);
        wardRepository.saveAllWards(wards);

        logService.logAction(
                "ADD_PATIENT",
                authService.getCurrentUser(),
                "Added patient " + patient.getFullName() + " to " + bed.toString()
        );

        logger.info("Patient " + patient.getFullName() + " added to " + bed.toString());
    }

    /**
     * Move a patient to a different bed
     * @param patient The patient to move
     * @param newBed The new bed to assign the patient to
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    public void movePatient(Patient patient, Bed newBed) throws UnauthorizedActionException, NotRosteredException {
        authService.checkAuthorizedAndRostered("MOVE_PATIENT");

        if (newBed.isOccupied()) {
            throw new IllegalArgumentException("New bed is already occupied");
        }

        // Find the current bed of the patient
        Optional<Bed> currentBedOpt = findBedForPatient(patient);
        if (!currentBedOpt.isPresent()) {
            throw new IllegalArgumentException("Patient is not assigned to any bed");
        }

        Bed currentBed = currentBedOpt.get();

        // Check if the new room is suitable for the patient's gender and isolation needs
        Room newRoom = newBed.getRoom();
        boolean hasOtherGender = false;

        for (Bed otherBed : newRoom.getBeds()) {
            if (otherBed != newBed && otherBed.isOccupied() && otherBed.getPatient().getGender() != patient.getGender()) {
                hasOtherGender = true;
                break;
            }
        }

        if (hasOtherGender) {
            throw new IllegalArgumentException("Cannot move patient to a room with patients of different gender");
        }

        if (patient.isNeedsIsolation() && newRoom.getNumberOfBeds() > 1) {
            boolean hasOtherPatients = false;
            for (Bed otherBed : newRoom.getBeds()) {
                if (otherBed != newBed && otherBed.isOccupied()) {
                    hasOtherPatients = true;
                    break;
                }
            }

            if (hasOtherPatients) {
                throw new IllegalArgumentException("Cannot move patient requiring isolation to a shared room");
            }
        }

        currentBed.removePatient();
        newBed.assignPatient(patient);
        wardRepository.saveAllWards(wards);

        logService.logAction(
                "MOVE_PATIENT",
                authService.getCurrentUser(),
                "Moved patient " + patient.getFullName() + " from " + currentBed.toString() + " to " + newBed.toString()
        );

        logger.info("Patient " + patient.getFullName() + " moved from " + currentBed.toString() + " to " + newBed.toString());
    }

    /**
     * Discharge a patient
     * @param patient The patient to discharge
     * @return true if the patient was discharged, false otherwise
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    public boolean dischargePatient(Patient patient) throws UnauthorizedActionException, NotRosteredException {
        authService.checkAuthorizedAndRostered("DISCHARGE_PATIENT");

        // Find the current bed of the patient
        Optional<Bed> currentBedOpt = findBedForPatient(patient);
        if (!currentBedOpt.isPresent()) {
            throw new IllegalArgumentException("Patient is not assigned to any bed");
        }

        Bed currentBed = currentBedOpt.get();
        currentBed.removePatient();

        // Add the patient to the discharged patients list
        dischargedPatients.add(patient);
        patients.remove(patient);
        patientRepository.save(patient);
        wardRepository.saveAllWards(wards);

        logService.logAction(
                "DISCHARGE_PATIENT",
                authService.getCurrentUser(),
                "Discharged patient " + patient.getFullName() + " from " + currentBed.toString()
        );

        logger.info("Patient " + patient.getFullName() + " discharged from " + currentBed.toString());
        return true;
    }

    /**
     * Find the bed for a patient
     * @param patient The patient
     * @return The bed, or empty if not found
     */
    public Optional<Bed> findBedForPatient(Patient patient) {
        return wardRepository.findBedForPatient(patient);
    }

    /**
     * Add a prescription for a patient
     * @param patient The patient
     * @param doctor The doctor
     * @param medications The list of medications
     * @param notes Prescription notes
     * @return The created prescription
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    public Prescription addPrescription(Patient patient, Doctor doctor, List<Medication> medications, String notes)
            throws UnauthorizedActionException, NotRosteredException {

        authService.checkAuthorizedAndRostered("ADD_PRESCRIPTION");

        if (!(authService.getCurrentUser() instanceof Doctor)) {
            throw new UnauthorizedActionException("Only doctors can add prescriptions");
        }

        Prescription prescription = new Prescription(
                IdGenerator.generateId("PRES"),
                patient,
                doctor,
                LocalDateTime.now(),
                medications,
                notes
        );

        patient.addPrescription(prescription);
        patientRepository.save(patient);

        logService.logAction(
                "ADD_PRESCRIPTION",
                authService.getCurrentUser(),
                "Added prescription for patient " + patient.getFullName()
        );

        logger.info("Prescription added for patient " + patient.getFullName());
        return prescription;
    }

    /**
     * Add a prescription for a patient (simplified version)
     * @param patient The patient
     * @param notes Prescription notes
     * @return The created prescription
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    public Prescription addPrescription(Patient patient, String notes)
            throws UnauthorizedActionException, NotRosteredException {

        authService.checkAuthorizedAndRostered("ADD_PRESCRIPTION");

        if (!(authService.getCurrentUser() instanceof Doctor)) {
            throw new UnauthorizedActionException("Only doctors can add prescriptions");
        }

        Doctor doctor = (Doctor) authService.getCurrentUser();

        Prescription prescription = new Prescription(
                IdGenerator.generateId("PRES"),
                patient,
                doctor,
                LocalDateTime.now(),
                notes
        );

        patient.addPrescription(prescription);
        patientRepository.save(patient);

        logService.logAction(
                "ADD_PRESCRIPTION",
                authService.getCurrentUser(),
                "Added prescription for patient " + patient.getFullName()
        );

        logger.info("Prescription added for patient " + patient.getFullName());
        return prescription;
    }

    /**
     * Add a medication to a prescription
     * @param prescription The prescription
     * @param name The medication name
     * @param dosage The medication dosage
     * @param instructions The medication instructions
     * @return The created medication
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    public Medication addMedicationToPrescription(Prescription prescription, String name, String dosage, String instructions)
            throws UnauthorizedActionException, NotRosteredException {

        authService.checkAuthorizedAndRostered("ADD_PRESCRIPTION");

        if (!(authService.getCurrentUser() instanceof Doctor)) {
            throw new UnauthorizedActionException("Only doctors can add medications to prescriptions");
        }

        Medication medication = new Medication(
                IdGenerator.generateId("MED"),
                name,
                dosage,
                instructions
        );

        prescription.addMedication(medication);
        patientRepository.save(prescription.getPatient());

        logService.logAction(
                "ADD_MEDICATION",
                authService.getCurrentUser(),
                "Added medication " + name + " to prescription for patient " + prescription.getPatient().getFullName()
        );

        logger.info("Medication " + name + " added to prescription for patient " + prescription.getPatient().getFullName());
        return medication;
    }

    /**
     * Add an administration time to a medication
     * @param medication The medication
     * @param time The administration time
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    public void addAdministrationTimeToMedication(Medication medication, java.time.LocalTime time)
            throws UnauthorizedActionException, NotRosteredException {

        authService.checkAuthorizedAndRostered("ADD_PRESCRIPTION");

        if (!(authService.getCurrentUser() instanceof Doctor)) {
            throw new UnauthorizedActionException("Only doctors can add administration times to medications");
        }

        medication.addAdministrationTime(time);

        logService.logAction(
                "ADD_ADMINISTRATION_TIME",
                authService.getCurrentUser(),
                "Added administration time " + time + " to medication " + medication.getName()
        );

        logger.info("Administration time " + time + " added to medication " + medication.getName());
    }

    /**
     * Administer a medication to a patient
     * @param medication The medication
     * @param patient The patient
     * @param notes Administration notes
     * @return The created medication administration
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    public MedicationAdministration administerMedication(Medication medication, Patient patient, String notes)
            throws UnauthorizedActionException, NotRosteredException {

        authService.checkAuthorizedAndRostered("ADMINISTER_MEDICATION");

        if (!(authService.getCurrentUser() instanceof Nurse)) {
            throw new UnauthorizedActionException("Only nurses can administer medications");
        }

        Nurse nurse = (Nurse) authService.getCurrentUser();

        MedicationAdministration administration = new MedicationAdministration(
                IdGenerator.generateId("ADM"),
                medication,
                patient,
                nurse,
                LocalDateTime.now(),
                notes
        );

        patient.addMedicationAdministration(administration);
        patientRepository.save(patient);

        logService.logAction(
                "ADMINISTER_MEDICATION",
                authService.getCurrentUser(),
                "Administered medication " + medication.getName() + " to patient " + patient.getFullName()
        );

        logger.info("Medication " + medication.getName() + " administered to patient " + patient.getFullName());
        return administration;
    }

    /**
     * Get all patients in the system
     * @return All patients
     */
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Get all vacant beds in the system
     * @return All vacant beds
     */
    public List<Bed> getVacantBeds() {
        return wardRepository.findVacantBeds();
    }

    /**
     * Get all vacant beds suitable for a patient based on gender and isolation needs
     * @param gender The patient's gender
     * @param needsIsolation Whether the patient needs isolation
     * @return Suitable vacant beds
     */
    public List<Bed> getSuitableVacantBeds(Gender gender, boolean needsIsolation) {
        List<Bed> suitableBeds = new ArrayList<>();

        for (Ward ward : wards) {
            for (Room room : ward.getRooms()) {
                // Check if the room is suitable for isolation if needed
                if (needsIsolation) {
                    // For isolation, we need a room with only one bed or all beds vacant
                    if (room.getNumberOfBeds() == 1 || room.getVacantBeds() == room.getNumberOfBeds()) {
                        suitableBeds.addAll(room.getBeds().stream()
                                .filter(bed -> !bed.isOccupied())
                                .collect(Collectors.toList()));
                    }
                } else {
                    // For non-isolation, check if there are other patients of the same gender
                    boolean hasOtherGender = false;
                    for (Bed bed : room.getBeds()) {
                        if (bed.isOccupied() && bed.getPatient().getGender() != gender) {
                            hasOtherGender = true;
                            break;
                        }
                    }

                    if (!hasOtherGender) {
                        suitableBeds.addAll(room.getBeds().stream()
                                .filter(bed -> !bed.isOccupied())
                                .collect(Collectors.toList()));
                    }
                }
            }
        }

        return suitableBeds;
    }

    /**
     * Check compliance with staffing regulations:
     * - Nurses must be assigned to two shifts (8am-4pm and 2pm-10pm) every day
     * - Doctors must be assigned for at least 1 hour every day
     * - No nurse can work more than 8 hours in a single day
     *
     * @throws ComplianceException if any compliance rule is violated
     */
    public void checkCompliance() throws ComplianceException {
        // Use the ComplianceChecker utility to check compliance
        ComplianceChecker.checkCompliance(staff);
    }
}
