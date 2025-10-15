package org.example.service;

import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.Gender;
import org.example.model.Patient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for patient management
 */
public interface PatientService {
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
     */
    Patient addPatient(String firstName, String lastName, LocalDate dateOfBirth, 
                      Gender gender, String medicalCondition, boolean needsIsolation, 
                      String bedId) throws UnauthorizedActionException, NotRosteredException;
    
    /**
     * Move a patient to a different bed
     * @param patientId The ID of the patient to move
     * @param newBedId The ID of the new bed to assign the patient to
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    void movePatient(String patientId, String newBedId) throws UnauthorizedActionException, 
                                                               NotRosteredException;
    
    /**
     * Discharge a patient
     * @param patientId The ID of the patient to discharge
     * @return true if the patient was discharged, false otherwise
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    boolean dischargePatient(String patientId) throws UnauthorizedActionException, 
                                                     NotRosteredException;
    
    /**
     * Get all patients in the system
     * @return All patients
     */
    List<Patient> getAllPatients();
    
    /**
     * Get all discharged patients
     * @return All discharged patients
     */
    List<Patient> getDischargedPatients();
    
    /**
     * Get a patient by ID
     * @param id The patient ID
     * @return Optional containing the patient if found, empty otherwise
     */
    Optional<Patient> getPatientById(String id);
}
