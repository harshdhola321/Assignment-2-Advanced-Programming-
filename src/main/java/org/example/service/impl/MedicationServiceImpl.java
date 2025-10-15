package org.example.service.impl;

import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.*;
import org.example.service.AuthenticationService;
import org.example.service.LoggingService;
import org.example.service.MedicationService;
import org.example.service.PatientService;
import org.example.service.StaffService;
import org.example.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of MedicationService
 */
public class MedicationServiceImpl implements MedicationService {
    private static final Logger logger = LoggerFactory.getLogger(MedicationServiceImpl.class);
    
    private final PatientService patientService;
    private final StaffService staffService;
    private final AuthenticationService authService;
    private final LoggingService logService;
    
    public MedicationServiceImpl(PatientService patientService,
                                StaffService staffService,
                                AuthenticationService authService,
                                LoggingService logService) {
        this.patientService = patientService;
        this.staffService = staffService;
        this.authService = authService;
        this.logService = logService;
    }
    
    @Override
    public MedicationAdministration administerMedication(String medicationId, String patientId, String notes)
            throws UnauthorizedActionException, NotRosteredException {
        
        // Check authorization
        authService.checkAuthorizedAndRostered("ADMINISTER_MEDICATION");
        
        if (!(authService.getCurrentUser() instanceof Nurse)) {
            throw new UnauthorizedActionException("Only nurses can administer medications");
        }
        
        Nurse nurse = (Nurse) authService.getCurrentUser();
        
        // Get patient and medication
        Patient patient = patientService.getPatientById(patientId)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));
        
        Medication medication = findMedicationById(medicationId)
            .orElseThrow(() -> new IllegalArgumentException("Medication not found: " + medicationId));
        
        // Create medication administration
        MedicationAdministration administration = new MedicationAdministration(
            IdGenerator.generateId("ADM"),
            medication,
            patient,
            nurse,
            LocalDateTime.now(),
            notes
        );
        
        // Add administration to patient
        patient.addMedicationAdministration(administration);
        
        // Log action
        logService.logAction(
            "ADMINISTER_MEDICATION",
            authService.getCurrentUser(),
            "Administered medication " + medication.getName() + " to patient " + patient.getFullName()
        );
        
        logger.info("Medication " + medication.getName() + " administered to patient " + patient.getFullName());
        return administration;
    }
    
    /**
     * Find a medication by ID
     * @param medicationId The medication ID
     * @return Optional containing the medication if found, empty otherwise
     */
    private Optional<Medication> findMedicationById(String medicationId) {
        // Search through all patients' prescriptions and medications
        for (Patient patient : patientService.getAllPatients()) {
            for (Prescription prescription : patient.getPrescriptions()) {
                for (Medication medication : prescription.getMedications()) {
                    if (medication.getId().equals(medicationId)) {
                        return Optional.of(medication);
                    }
                }
            }
        }
        return Optional.empty();
    }
}
