package org.example.repository;

import org.example.model.Patient;
import org.example.model.Ward;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Patient data access
 */
public interface PatientRepository {
    /**
     * Find all patients
     * @return List of all patients
     */
    List<Patient> findAll();
    
    /**
     * Find a patient by ID
     * @param id The patient ID
     * @return Optional containing the patient if found, empty otherwise
     */
    Optional<Patient> findById(String id);
    
    /**
     * Save a patient
     * @param patient The patient to save
     */
    void save(Patient patient);
    
    /**
     * Delete a patient
     * @param patient The patient to delete
     */
    void delete(Patient patient);
    
    /**
     * Find all discharged patients
     * @return List of discharged patients
     */
    List<Patient> findDischargedPatients();
    
    /**
     * Add a patient to discharged patients
     * @param patient The patient to discharge
     */
    void addDischargedPatient(Patient patient);

    void saveAll(List<Patient> patients);

    void saveAllDischarged(List<Patient> patients);
}
