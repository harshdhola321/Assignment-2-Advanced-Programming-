package org.example.service;

import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.Medication;
import org.example.model.MedicationAdministration;

/**
 * Service interface for medication administration
 */
public interface MedicationService {
    /**
     * Administer a medication to a patient
     * @param medicationId The medication ID
     * @param patientId The patient ID
     * @param notes Administration notes
     * @return The created medication administration
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    MedicationAdministration administerMedication(String medicationId, String patientId, String notes)
            throws UnauthorizedActionException, NotRosteredException;
}
