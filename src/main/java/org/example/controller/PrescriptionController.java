package org.example.controller;

import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.Medication;
import org.example.model.Prescription;
import org.example.service.PrescriptionService;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Controller for prescription-related operations
 */
public class PrescriptionController {
    private final PrescriptionService prescriptionService;
    
    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }
    
    /**
     * Add a prescription for a patient
     * @param patientId The patient ID
     * @param doctorId The doctor ID
     * @param medications The list of medications
     * @param notes Prescription notes
     * @return The created prescription
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     * @throws IllegalArgumentException If any input is invalid
     */
    public Prescription addPrescription(String patientId, String doctorId, List<Medication> medications, String notes)
            throws UnauthorizedActionException, NotRosteredException {
        
        // Input validation
        if (patientId == null || patientId.isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be empty");
        }
        
        if (doctorId == null || doctorId.isEmpty()) {
            throw new IllegalArgumentException("Doctor ID cannot be empty");
        }
        
        // Delegate to service
        return prescriptionService.addPrescription(patientId, doctorId, medications, notes);
    }
    
    /**
     * Add a prescription for a patient (simplified version)
     * @param patientId The patient ID
     * @param notes Prescription notes
     * @return The created prescription
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     * @throws IllegalArgumentException If any input is invalid
     */
    public Prescription addPrescription(String patientId, String notes)
            throws UnauthorizedActionException, NotRosteredException {
        
        // Input validation
        if (patientId == null || patientId.isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be empty");
        }
        
        // Delegate to service
        return prescriptionService.addPrescription(patientId, notes);
    }
    
    /**
     * Add a medication to a prescription
     * @param prescriptionId The prescription ID
     * @param name The medication name
     * @param dosage The medication dosage
     * @param instructions The medication instructions
     * @return The created medication
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     * @throws IllegalArgumentException If any input is invalid
     */
    public Medication addMedicationToPrescription(String prescriptionId, String name, String dosage, String instructions)
            throws UnauthorizedActionException, NotRosteredException {
        
        // Input validation
        if (prescriptionId == null || prescriptionId.isEmpty()) {
            throw new IllegalArgumentException("Prescription ID cannot be empty");
        }
        
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Medication name cannot be empty");
        }
        
        if (dosage == null || dosage.isEmpty()) {
            throw new IllegalArgumentException("Medication dosage cannot be empty");
        }
        
        // Delegate to service
        return prescriptionService.addMedicationToPrescription(prescriptionId, name, dosage, instructions);
    }
    
    /**
     * Add an administration time to a medication
     * @param medicationId The medication ID
     * @param timeStr The administration time as a string (format: HH:mm)
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     * @throws IllegalArgumentException If any input is invalid
     */
    public void addAdministrationTimeToMedication(String medicationId, String timeStr)
            throws UnauthorizedActionException, NotRosteredException {
        
        // Input validation
        if (medicationId == null || medicationId.isEmpty()) {
            throw new IllegalArgumentException("Medication ID cannot be empty");
        }
        
        if (timeStr == null || timeStr.isEmpty()) {
            throw new IllegalArgumentException("Administration time cannot be empty");
        }
        
        // Parse time
        LocalTime time;
        try {
            time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid time format. Use HH:mm (e.g., 08:30)");
        }
        
        // Delegate to service
        prescriptionService.addAdministrationTimeToMedication(medicationId, time);
    }
}
