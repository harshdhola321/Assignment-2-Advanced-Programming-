package org.example.service.impl;

import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.Bed;
import org.example.model.Gender;
import org.example.model.Patient;
import org.example.repository.PatientRepository;
import org.example.service.AuthenticationService;
import org.example.service.LoggingService;
import org.example.service.PatientService;
import org.example.service.WardService;
import org.example.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of PatientService
 */
public class PatientServiceImpl implements PatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);
    
    private final PatientRepository patientRepository;
    private final WardService wardService;
    private final AuthenticationService authService;
    private final LoggingService logService;
    
    public PatientServiceImpl(PatientRepository patientRepository, 
                             WardService wardService,
                             AuthenticationService authService,
                             LoggingService logService) {
        this.patientRepository = patientRepository;
        this.wardService = wardService;
        this.authService = authService;
        this.logService = logService;
    }
    
    @Override
    public Patient addPatient(String firstName, String lastName, LocalDate dateOfBirth, 
                            Gender gender, String medicalCondition, boolean needsIsolation, 
                            String bedId) throws UnauthorizedActionException, NotRosteredException {
        // Check authorization
        authService.checkAuthorizedAndRostered("ADD_PATIENT");
        
        // Create patient
        Patient patient = new Patient(
            IdGenerator.generateId("PAT"),
            firstName,
            lastName,
            dateOfBirth,
            gender,
            medicalCondition,
            needsIsolation
        );
        
        // Get bed and assign patient
        Bed bed = wardService.getBedById(bedId);
        wardService.assignPatientToBed(patient, bed);
        
        // Save patient
        patientRepository.save(patient);
        
        // Log action
        logService.logAction(
            "ADD_PATIENT",
            authService.getCurrentUser(),
            "Added patient " + patient.getFullName() + " to " + bed.toString()
        );
        
        logger.info("Patient " + patient.getFullName() + " added to " + bed.toString());
        return patient;
    }
    
    @Override
    public void movePatient(String patientId, String newBedId) throws UnauthorizedActionException, NotRosteredException {
        // Check authorization
        authService.checkAuthorizedAndRostered("MOVE_PATIENT");
        
        // Get patient
        Patient patient = getPatientById(patientId)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));
        
        // Get current and new beds
        Optional<Bed> currentBedOpt = wardService.findBedForPatient(patient);
        if (!currentBedOpt.isPresent()) {
            throw new IllegalArgumentException("Patient is not assigned to any bed");
        }
        
        Bed currentBed = currentBedOpt.get();
        Bed newBed = wardService.getBedById(newBedId);
        
        // Move patient
        wardService.movePatientToBed(patient, currentBed, newBed);
        
        // Log action
        logService.logAction(
            "MOVE_PATIENT",
            authService.getCurrentUser(),
            "Moved patient " + patient.getFullName() + " from " + currentBed.toString() + " to " + newBed.toString()
        );
        
        logger.info("Patient " + patient.getFullName() + " moved from " + currentBed.toString() + " to " + newBed.toString());
    }
    
    @Override
    public boolean dischargePatient(String patientId) throws UnauthorizedActionException, NotRosteredException {
        // Check authorization
        authService.checkAuthorizedAndRostered("DISCHARGE_PATIENT");
        
        // Get patient
        Patient patient = getPatientById(patientId)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));
        
        // Get current bed
        Optional<Bed> currentBedOpt = wardService.findBedForPatient(patient);
        if (!currentBedOpt.isPresent()) {
            throw new IllegalArgumentException("Patient is not assigned to any bed");
        }
        
        Bed currentBed = currentBedOpt.get();
        
        // Remove patient from bed
        wardService.removePatientFromBed(patient, currentBed);
        
        // Set discharge date and move to discharged patients
        patient.setDischargeDate(LocalDate.now());
        patientRepository.delete(patient);
        patientRepository.addDischargedPatient(patient);
        
        // Log action
        logService.logAction(
            "DISCHARGE_PATIENT",
            authService.getCurrentUser(),
            "Discharged patient " + patient.getFullName() + " from " + currentBed.toString()
        );
        
        logger.info("Patient " + patient.getFullName() + " discharged from " + currentBed.toString());
        return true;
    }
    
    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }
    
    @Override
    public List<Patient> getDischargedPatients() {
        return patientRepository.findDischargedPatients();
    }
    
    @Override
    public Optional<Patient> getPatientById(String id) {
        return patientRepository.findById(id);
    }
}
