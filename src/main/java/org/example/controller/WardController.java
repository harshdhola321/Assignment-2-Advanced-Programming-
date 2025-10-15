package org.example.controller;

import org.example.model.Bed;
import org.example.model.Gender;
import org.example.model.Patient;
import org.example.model.Ward;
import org.example.service.WardService;

import java.util.List;
import java.util.Optional;

/**
 * Controller for ward-related operations
 */
public class WardController {
    private final WardService wardService;
    
    public WardController(WardService wardService) {
        this.wardService = wardService;
    }
    
    /**
     * Get all wards
     * @return All wards
     */
    public List<Ward> getAllWards() {
        return wardService.getAllWards();
    }
    
    /**
     * Get a bed by ID
     * @param id The bed ID
     * @return The bed
     * @throws IllegalArgumentException If the bed is not found
     */
    public Bed getBedById(String id) throws IllegalArgumentException {
        // Input validation
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Bed ID cannot be empty");
        }
        
        // Delegate to service
        return wardService.getBedById(id);
    }
    
    /**
     * Find the bed for a patient
     * @param patient The patient
     * @return Optional containing the bed if found, empty otherwise
     * @throws IllegalArgumentException If the patient is invalid
     */
    public Optional<Bed> findBedForPatient(Patient patient) {
        // Input validation
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }
        
        // Delegate to service
        return wardService.findBedForPatient(patient);
    }
    
    /**
     * Get all vacant beds
     * @return All vacant beds
     */
    public List<Bed> getVacantBeds() {
        return wardService.getVacantBeds();
    }
    
    /**
     * Get all vacant beds suitable for a patient based on gender and isolation needs
     * @param gender The patient's gender
     * @param needsIsolation Whether the patient needs isolation
     * @return Suitable vacant beds
     * @throws IllegalArgumentException If the gender is invalid
     */
    public List<Bed> getSuitableVacantBeds(Gender gender, boolean needsIsolation) {
        // Input validation
        if (gender == null) {
            throw new IllegalArgumentException("Gender cannot be null");
        }
        
        // Delegate to service
        return wardService.getSuitableVacantBeds(gender, needsIsolation);
    }
}
