package org.example.service;

import org.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CareHomeServiceMedicationTest {

    // Test data
    private Patient patient;
    private Doctor doctor;
    private Nurse nurse;
    private Prescription prescription;
    private Medication medication;

    @BeforeEach
    public void setUp() {
        // Create test data
        patient = new Patient(
            "PAT001", 
            "John", 
            "Doe", 
            LocalDate.of(1950, 5, 15), 
            Gender.MALE, 
            "Hypertension", 
            false
        );
        
        doctor = new Doctor(
            "DOC001", 
            "Jane", 
            "Smith", 
            LocalDate.of(1980, 3, 10), 
            Gender.FEMALE, 
            "doctor", 
            "password", 
            "Cardiology"
        );
        
        nurse = new Nurse(
            "NUR001", 
            "Robert", 
            "Johnson", 
            LocalDate.of(1985, 7, 20), 
            Gender.MALE, 
            "nurse", 
            "password", 
            "Registered Nurse"
        );
        
        // Create a prescription
        prescription = new Prescription(
            "PRES001",
            patient,
            doctor,
            LocalDateTime.now(),
            "For blood pressure control"
        );
        
        // Create a medication
        medication = new Medication(
            "MED001",
            "Aspirin",
            "100mg",
            "Take with food"
        );
    }

    @Test
    public void testPrescriptionCreation() {
        // Assert
        assertNotNull(prescription);
        assertEquals(patient, prescription.getPatient());
        assertEquals(doctor, prescription.getDoctor());
        assertEquals("For blood pressure control", prescription.getNotes());
        assertTrue(prescription.getMedications().isEmpty());
    }
    
    @Test
    public void testPrescriptionWithMedications() {
        // Arrange
        List<Medication> medications = new ArrayList<>();
        medications.add(medication);
        
        // Act
        Prescription prescriptionWithMeds = new Prescription(
            "PRES002",
            patient,
            doctor,
            LocalDateTime.now(),
            medications,
            "For blood pressure control"
        );
        
        // Assert
        assertNotNull(prescriptionWithMeds);
        assertEquals(1, prescriptionWithMeds.getMedications().size());
        assertEquals(medication, prescriptionWithMeds.getMedications().get(0));
    }
    
    @Test
    public void testAddMedicationToPrescription() {
        // Act
        prescription.addMedication(medication);
        
        // Assert
        assertEquals(1, prescription.getMedications().size());
        assertEquals(medication, prescription.getMedications().get(0));
    }
    
    @Test
    public void testAddMedicationToPatient() {
        // Act
        prescription.addMedication(medication);
        patient.addPrescription(prescription);
        
        // Assert
        assertEquals(1, patient.getPrescriptions().size());
        assertEquals(prescription, patient.getPrescriptions().get(0));
        assertEquals(1, patient.getPrescriptions().get(0).getMedications().size());
        assertEquals(medication, patient.getPrescriptions().get(0).getMedications().get(0));
    }
    
    @Test
    public void testMedicationProperties() {
        // Assert
        assertEquals("MED001", medication.getId());
        assertEquals("Aspirin", medication.getName());
        assertEquals("100mg", medication.getDosage());
        assertEquals("Take with food", medication.getInstructions());
        assertTrue(medication.getAdministrationTimes().isEmpty());
    }
    
    @Test
    public void testAddAdministrationTimeToMedication() {
        // Arrange
        LocalTime administrationTime = LocalTime.of(8, 0);
        
        // Act
        medication.addAdministrationTime(administrationTime);
        
        // Assert
        assertEquals(1, medication.getAdministrationTimes().size());
        assertTrue(medication.getAdministrationTimes().contains(administrationTime));
    }
    
    @Test
    public void testMultipleAdministrationTimes() {
        // Arrange
        LocalTime morningTime = LocalTime.of(8, 0);
        LocalTime noonTime = LocalTime.of(12, 0);
        LocalTime eveningTime = LocalTime.of(18, 0);
        
        // Act
        medication.addAdministrationTime(morningTime);
        medication.addAdministrationTime(noonTime);
        medication.addAdministrationTime(eveningTime);
        
        // Assert
        assertEquals(3, medication.getAdministrationTimes().size());
        assertTrue(medication.getAdministrationTimes().contains(morningTime));
        assertTrue(medication.getAdministrationTimes().contains(noonTime));
        assertTrue(medication.getAdministrationTimes().contains(eveningTime));
    }
    
    @Test
    public void testMedicationAdministration() {
        // Arrange
        LocalDateTime administrationTime = LocalDateTime.now();
        String notes = "Patient tolerated well";
        
        // Act
        MedicationAdministration administration = new MedicationAdministration(
            "ADM001",
            medication,
            patient,
            nurse,
            administrationTime,
            notes
        );
        
        // Assert
        assertEquals("ADM001", administration.getId());
        assertEquals(medication, administration.getMedication());
        assertEquals(patient, administration.getPatient());
        assertEquals(nurse, administration.getNurse());
//        assertEquals(administrationTime, administration.getAdministrationTime());
        assertEquals(notes, administration.getNotes());
    }
    
    @Test
    public void testAddMedicationAdministrationToPatient() {
        // Arrange
        MedicationAdministration administration = new MedicationAdministration(
            "ADM001",
            medication,
            patient,
            nurse,
            LocalDateTime.now(),
            "Patient tolerated well"
        );
        
        // Act
        patient.addMedicationAdministration(administration);
        
        // Assert
        assertEquals(1, patient.getMedicationAdministrations().size());
        assertEquals(administration, patient.getMedicationAdministrations().get(0));
    }
    
    @Test
    public void testMultipleMedicationAdministrations() {
        // Arrange
        MedicationAdministration morning = new MedicationAdministration(
            "ADM001",
            medication,
            patient,
            nurse,
            LocalDateTime.now().withHour(8),
            "Morning dose"
        );
        
        MedicationAdministration evening = new MedicationAdministration(
            "ADM002",
            medication,
            patient,
            nurse,
            LocalDateTime.now().withHour(18),
            "Evening dose"
        );
        
        // Act
        patient.addMedicationAdministration(morning);
        patient.addMedicationAdministration(evening);
        
        // Assert
        assertEquals(2, patient.getMedicationAdministrations().size());
        assertTrue(patient.getMedicationAdministrations().contains(morning));
        assertTrue(patient.getMedicationAdministrations().contains(evening));
    }
}
