package org.example.service;

import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.Bed;
import org.example.model.Gender;
import org.example.model.Patient;
import org.example.model.Ward;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for ward, room, and bed management
 */
public interface WardService {
    /**
     * Get all wards
     * @return All wards
     */
    List<Ward> getAllWards();
    
    /**
     * Get a bed by ID
     * @param id The bed ID
     * @return The bed
     * @throws IllegalArgumentException If the bed is not found
     */
    Bed getBedById(String id) throws IllegalArgumentException;
    
    /**
     * Find the bed for a patient
     * @param patient The patient
     * @return Optional containing the bed if found, empty otherwise
     */
    Optional<Bed> findBedForPatient(Patient patient);
    
    /**
     * Assign a patient to a bed
     * @param patient The patient
     * @param bed The bed
     * @throws IllegalArgumentException If the bed is already occupied or not suitable
     */
    void assignPatientToBed(Patient patient, Bed bed) throws IllegalArgumentException;
    
    /**
     * Move a patient from one bed to another
     * @param patient The patient
     * @param currentBed The current bed
     * @param newBed The new bed
     * @throws IllegalArgumentException If the new bed is already occupied or not suitable
     */
    void movePatientToBed(Patient patient, Bed currentBed, Bed newBed) throws IllegalArgumentException;
    
    /**
     * Remove a patient from a bed
     * @param patient The patient
     * @param bed The bed
     */
    void removePatientFromBed(Patient patient, Bed bed);
    
    /**
     * Get all vacant beds
     * @return All vacant beds
     */
    List<Bed> getVacantBeds();
    
    /**
     * Get all vacant beds suitable for a patient based on gender and isolation needs
     * @param gender The patient's gender
     * @param needsIsolation Whether the patient needs isolation
     * @return Suitable vacant beds
     */
    List<Bed> getSuitableVacantBeds(Gender gender, boolean needsIsolation);
}
