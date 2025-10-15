package org.example.controller;

import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.Gender;
import org.example.model.Patient;
import org.example.service.PatientService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller for patient-related operations
 */
public class PatientController {
    private final PatientService patientService;
    
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }
    
    /**
     * Add a patient to the system
     * @param firstName The patient's first name
     * @param lastName The patient's last name
     * @param dateOfBirth The patient's date of birth
     * @param gender The patient's gender
     * @param medicalCondition The patient's medical condition
     * @param needsIsolation Whether the patient needs isolation
     * @param bedId The ID of the bed to assign the patient to
     * @return The created patient
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     * @throws IllegalArgumentException If any input is invalid
     */
    public Patient addPatient(String firstName, String lastName, LocalDate dateOfBirth, 
                             Gender gender, String medicalCondition, boolean needsIsolation, 
                             String bedId) throws UnauthorizedActionException, NotRosteredException {
        // Input validation
        if (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        
        if (lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }
        
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }
        
        if (gender == null) {
            throw new IllegalArgumentException("Gender cannot be null");
        }
        
        if (medicalCondition == null || medicalCondition.isEmpty()) {
            throw new IllegalArgumentException("Medical condition cannot be empty");
        }
        
        if (bedId == null || bedId.isEmpty()) {
            throw new IllegalArgumentException("Bed ID cannot be empty");
        }
        
        // Delegate to service
        return patientService.addPatient(firstName, lastName, dateOfBirth, gender, 
                                        medicalCondition, needsIsolation, bedId);
    }
    
    /**
     * Move a patient to a different bed
     * @param patientId The ID of the patient to move
     * @param newBedId The ID of the new bed to assign the patient to
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     * @throws IllegalArgumentException If any input is invalid
     */
    public void movePatient(String patientId, String newBedId) throws UnauthorizedActionException, NotRosteredException {
        // Input validation
        if (patientId == null || patientId.isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be empty");
        }
        
        if (newBedId == null || newBedId.isEmpty()) {
            throw new IllegalArgumentException("Bed ID cannot be empty");
        }
        
        // Delegate to service
        patientService.movePatient(patientId, newBedId);
    }
    
    /**
     * Discharge a patient
     * @param patientId The ID of the patient to discharge
     * @return true if the patient was discharged, false otherwise
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     * @throws IllegalArgumentException If any input is invalid
     */
    public boolean dischargePatient(String patientId) throws UnauthorizedActionException, NotRosteredException {
        // Input validation
        if (patientId == null || patientId.isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be empty");
        }
        
        // Delegate to service
        return patientService.dischargePatient(patientId);
    }
    
    /**
     * Get all patients in the system
     * @return All patients
     */
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }
    
    /**
     * Get all discharged patients
     * @return All discharged patients
     */
    public List<Patient> getDischargedPatients() {
        return patientService.getDischargedPatients();
    }
    
    /**
     * Get a patient by ID
     * @param id The patient ID
     * @return Optional containing the patient if found, empty otherwise
     * @throws IllegalArgumentException If the ID is invalid
     */
    public Optional<Patient> getPatientById(String id) {
        // Input validation
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be empty");
        }
        
        // Delegate to service
        return patientService.getPatientById(id);
    }
}
