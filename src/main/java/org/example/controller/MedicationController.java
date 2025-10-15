package org.example.controller;

import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.MedicationAdministration;
import org.example.service.MedicationService;

/**
 * Controller for medication administration operations
 */
public class MedicationController {
    private final MedicationService medicationService;
    
    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }
    
    /**
     * Administer a medication to a patient
     * @param medicationId The medication ID
     * @param patientId The patient ID
     * @param notes Administration notes
     * @return The created medication administration
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     * @throws IllegalArgumentException If any input is invalid
     */
    public MedicationAdministration administerMedication(String medicationId, String patientId, String notes)
            throws UnauthorizedActionException, NotRosteredException {
        
        // Input validation
        if (medicationId == null || medicationId.isEmpty()) {
            throw new IllegalArgumentException("Medication ID cannot be empty");
        }
        
        if (patientId == null || patientId.isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be empty");
        }
        
        // Delegate to service
        return medicationService.administerMedication(medicationId, patientId, notes);
    }
}
