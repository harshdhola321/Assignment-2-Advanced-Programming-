package org.example.service.impl;

import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.*;
import org.example.service.AuthenticationService;
import org.example.service.LoggingService;
import org.example.service.PatientService;
import org.example.service.PrescriptionService;
import org.example.service.StaffService;
import org.example.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of PrescriptionService
 */
public class PrescriptionServiceImpl implements PrescriptionService {
    private static final Logger logger = LoggerFactory.getLogger(PrescriptionServiceImpl.class);
    
    private final PatientService patientService;
    private final StaffService staffService;
    private final AuthenticationService authService;
    private final LoggingService logService;
    
    public PrescriptionServiceImpl(PatientService patientService,
                                  StaffService staffService,
                                  AuthenticationService authService,
                                  LoggingService logService) {
        this.patientService = patientService;
        this.staffService = staffService;
        this.authService = authService;
        this.logService = logService;
    }
    
    @Override
    public Prescription addPrescription(String patientId, String doctorId, List<Medication> medications, String notes)
            throws UnauthorizedActionException, NotRosteredException {
        
        // Check authorization
        authService.checkAuthorizedAndRostered("ADD_PRESCRIPTION");
        
        if (!(authService.getCurrentUser() instanceof Doctor)) {
            throw new UnauthorizedActionException("Only doctors can add prescriptions");
        }
        
        // Get patient and doctor
        Patient patient = patientService.getPatientById(patientId)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));
        
        Doctor doctor = (Doctor) staffService.getStaffById(doctorId)
            .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + doctorId));
        
        // Create prescription
        Prescription prescription = new Prescription(
            IdGenerator.generateId("PRES"),
            patient,
            doctor,
            LocalDateTime.now(),
            medications != null ? new ArrayList<>(medications) : new ArrayList<>(),
            notes
        );
        
        // Add prescription to patient
        patient.addPrescription(prescription);
        
        // Log action
        logService.logAction(
            "ADD_PRESCRIPTION",
            authService.getCurrentUser(),
            "Added prescription for patient " + patient.getFullName()
        );
        
        logger.info("Prescription added for patient " + patient.getFullName());
        return prescription;
    }
    
    @Override
    public Prescription addPrescription(String patientId, String notes)
            throws UnauthorizedActionException, NotRosteredException {
        
        // Check authorization
        authService.checkAuthorizedAndRostered("ADD_PRESCRIPTION");
        
        if (!(authService.getCurrentUser() instanceof Doctor)) {
            throw new UnauthorizedActionException("Only doctors can add prescriptions");
        }
        
        Doctor doctor = (Doctor) authService.getCurrentUser();
        
        // Get patient
        Patient patient = patientService.getPatientById(patientId)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));
        
        // Create prescription
        Prescription prescription = new Prescription(
            IdGenerator.generateId("PRES"),
            patient,
            doctor,
            LocalDateTime.now(),
            notes
        );
        
        // Add prescription to patient
        patient.addPrescription(prescription);
        
        // Log action
        logService.logAction(
            "ADD_PRESCRIPTION",
            authService.getCurrentUser(),
            "Added prescription for patient " + patient.getFullName()
        );
        
        logger.info("Prescription added for patient " + patient.getFullName());
        return prescription;
    }
    
    @Override
    public Medication addMedicationToPrescription(String prescriptionId, String name, String dosage, String instructions)
            throws UnauthorizedActionException, NotRosteredException {
        
        // Check authorization
        authService.checkAuthorizedAndRostered("ADD_PRESCRIPTION");
        
        if (!(authService.getCurrentUser() instanceof Doctor)) {
            throw new UnauthorizedActionException("Only doctors can add medications to prescriptions");
        }
        
        // Find prescription
        Optional<Prescription> prescriptionOpt = findPrescriptionById(prescriptionId);
        if (!prescriptionOpt.isPresent()) {
            throw new IllegalArgumentException("Prescription not found: " + prescriptionId);
        }
        
        Prescription prescription = prescriptionOpt.get();
        
        // Create medication
        Medication medication = new Medication(
            IdGenerator.generateId("MED"),
            name,
            dosage,
            instructions
        );
        
        // Add medication to prescription
        prescription.addMedication(medication);
        
        // Log action
        logService.logAction(
            "ADD_MEDICATION",
            authService.getCurrentUser(),
            "Added medication " + name + " to prescription for patient " + prescription.getPatient().getFullName()
        );
        
        logger.info("Medication " + name + " added to prescription for patient " + prescription.getPatient().getFullName());
        return medication;
    }
    
    @Override
    public void addAdministrationTimeToMedication(String medicationId, LocalTime time)
            throws UnauthorizedActionException, NotRosteredException {
        
        // Check authorization
        authService.checkAuthorizedAndRostered("ADD_PRESCRIPTION");
        
        if (!(authService.getCurrentUser() instanceof Doctor)) {
            throw new UnauthorizedActionException("Only doctors can add administration times to medications");
        }
        
        // Find medication
        Optional<Medication> medicationOpt = findMedicationById(medicationId);
        if (!medicationOpt.isPresent()) {
            throw new IllegalArgumentException("Medication not found: " + medicationId);
        }
        
        Medication medication = medicationOpt.get();
        
        // Add administration time
        medication.addAdministrationTime(time);
        
        // Log action
        logService.logAction(
            "ADD_ADMINISTRATION_TIME",
            authService.getCurrentUser(),
            "Added administration time " + time + " to medication " + medication.getName()
        );
        
        logger.info("Administration time " + time + " added to medication " + medication.getName());
    }
    
    /**
     * Find a prescription by ID
     * @param prescriptionId The prescription ID
     * @return Optional containing the prescription if found, empty otherwise
     */
    private Optional<Prescription> findPrescriptionById(String prescriptionId) {
        // Search through all patients' prescriptions
        for (Patient patient : patientService.getAllPatients()) {
            for (Prescription prescription : patient.getPrescriptions()) {
                if (prescription.getId().equals(prescriptionId)) {
                    return Optional.of(prescription);
                }
            }
        }
        return Optional.empty();
    }
    
    /**
     * Find a medication by ID
     * @param medicationId The medication ID
     * @return Optional containing the medication if found, empty otherwise
     */
    private Optional<Medication> findMedicationById(String medicationId) {
        // Search through all patients' prescriptions and medications
        for (Patient patient : patientService.getAllPatients()) {
            for (Prescription prescription : patient.getPrescriptions()) {
                for (Medication medication : prescription.getMedications()) {
                    if (medication.getId().equals(medicationId)) {
                        return Optional.of(medication);
                    }
                }
            }
        }
        return Optional.empty();
    }
}
