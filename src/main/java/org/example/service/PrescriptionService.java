package org.example.service;

import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.Doctor;
import org.example.model.Medication;
import org.example.model.Patient;
import org.example.model.Prescription;

import java.util.List;

/**
 * Service interface for prescription management
 */
public interface PrescriptionService {
    /**
     * Add a prescription for a patient
     * @param patientId The patient ID
     * @param doctorId The doctor ID
     * @param medications The list of medications
     * @param notes Prescription notes
     * @return The created prescription
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    Prescription addPrescription(String patientId, String doctorId, List<Medication> medications, String notes)
            throws UnauthorizedActionException, NotRosteredException;
    
    /**
     * Add a prescription for a patient (simplified version)
     * @param patientId The patient ID
     * @param notes Prescription notes
     * @return The created prescription
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    Prescription addPrescription(String patientId, String notes)
            throws UnauthorizedActionException, NotRosteredException;
    
    /**
     * Add a medication to a prescription
     * @param prescriptionId The prescription ID
     * @param name The medication name
     * @param dosage The medication dosage
     * @param instructions The medication instructions
     * @return The created medication
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    Medication addMedicationToPrescription(String prescriptionId, String name, String dosage, String instructions)
            throws UnauthorizedActionException, NotRosteredException;
    
    /**
     * Add an administration time to a medication
     * @param medicationId The medication ID
     * @param time The administration time
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    void addAdministrationTimeToMedication(String medicationId, java.time.LocalTime time)
            throws UnauthorizedActionException, NotRosteredException;
}
