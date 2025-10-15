package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PatientTest {

    private Patient patient;
    private Doctor doctor;
    private Nurse nurse;
    private Prescription prescription;
    private Medication medication;

    @BeforeEach
    public void setUp() {
        // Create a patient
        patient = new Patient(
            "PAT001",
            "Robert",
            "Brown",
            LocalDate.of(1950, 3, 15),
            Gender.MALE,
            "Hypertension",
            false
        );
        
        // Create a doctor
        doctor = new Doctor(
            "DOC001",
            "John",
            "Smith",
            LocalDate.of(1980, 1, 1),
            Gender.MALE,
            "doctor",
            "password",
            "Cardiology"
        );
        
        // Create a nurse
        nurse = new Nurse(
            "NUR001",
            "Jane",
            "Doe",
            LocalDate.of(1985, 5, 15),
            Gender.FEMALE,
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
    public void testPatientConstructor() {
        // Assert
        assertEquals("PAT001", patient.getId());
        assertEquals("Robert", patient.getFirstName());
        assertEquals("Brown", patient.getLastName());
        assertEquals(LocalDate.of(1950, 3, 15), patient.getDateOfBirth());
        assertEquals(Gender.MALE, patient.getGender());
        assertEquals("Hypertension", patient.getMedicalCondition());
        assertFalse(patient.isNeedsIsolation());
        assertEquals(LocalDate.now(), patient.getAdmissionDate());
        assertNull(patient.getDischargeDate());
        assertTrue(patient.getPrescriptions().isEmpty());
        assertTrue(patient.getMedicationAdministrations().isEmpty());
    }
    
    @Test
    public void testPatientConstructorWithAdmissionDate() {
        // Arrange
        LocalDate admissionDate = LocalDate.of(2025, 10, 1);
        
        // Act
        Patient patientWithAdmissionDate = new Patient(
            "PAT002",
            "Mary",
            "Johnson",
            LocalDate.of(1955, 5, 20),
            Gender.FEMALE,
            "Diabetes",
            true,
            admissionDate
        );
        
        // Assert
        assertEquals("PAT002", patientWithAdmissionDate.getId());
        assertEquals("Mary", patientWithAdmissionDate.getFirstName());
        assertEquals("Johnson", patientWithAdmissionDate.getLastName());
        assertEquals(LocalDate.of(1955, 5, 20), patientWithAdmissionDate.getDateOfBirth());
        assertEquals(Gender.FEMALE, patientWithAdmissionDate.getGender());
        assertEquals("Diabetes", patientWithAdmissionDate.getMedicalCondition());
        assertTrue(patientWithAdmissionDate.isNeedsIsolation());
        assertEquals(admissionDate, patientWithAdmissionDate.getAdmissionDate());
        assertNull(patientWithAdmissionDate.getDischargeDate());
    }
    
    @Test
    public void testSetMedicalCondition() {
        // Act
        patient.setMedicalCondition("Diabetes");
        
        // Assert
        assertEquals("Diabetes", patient.getMedicalCondition());
    }
    
    @Test
    public void testSetNeedsIsolation() {
        // Act
        patient.setNeedsIsolation(true);
        
        // Assert
        assertTrue(patient.isNeedsIsolation());
    }
    
    @Test
    public void testSetAdmissionDate() {
        // Arrange
        LocalDate newAdmissionDate = LocalDate.of(2025, 9, 1);
        
        // Act
        patient.setAdmissionDate(newAdmissionDate);
        
        // Assert
        assertEquals(newAdmissionDate, patient.getAdmissionDate());
    }
    
    @Test
    public void testSetDischargeDate() {
        // Arrange
        LocalDate dischargeDate = LocalDate.of(2025, 10, 15);
        
        // Act
        patient.setDischargeDate(dischargeDate);
        
        // Assert
        assertEquals(dischargeDate, patient.getDischargeDate());
    }
    
    @Test
    public void testAddPrescription() {
        // Act
        patient.addPrescription(prescription);
        
        // Assert
        assertEquals(1, patient.getPrescriptions().size());
        assertTrue(patient.getPrescriptions().contains(prescription));
    }
    
    @Test
    public void testAddMedicationAdministration() {
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
        assertTrue(patient.getMedicationAdministrations().contains(administration));
    }
    
    @Test
    public void testGetFullName() {
        // Act
        String fullName = patient.getFullName();
        
        // Assert
        assertEquals("Robert Brown", fullName);
    }
    
    @Test
    public void testMultiplePrescriptions() {
        // Arrange
        Prescription prescription1 = new Prescription(
            "PRES001",
            patient,
            doctor,
            LocalDateTime.now(),
            "For blood pressure"
        );
        
        Prescription prescription2 = new Prescription(
            "PRES002",
            patient,
            doctor,
            LocalDateTime.now(),
            "For cholesterol"
        );
        
        // Act
        patient.addPrescription(prescription1);
        patient.addPrescription(prescription2);
        
        // Assert
        assertEquals(2, patient.getPrescriptions().size());
        assertTrue(patient.getPrescriptions().contains(prescription1));
        assertTrue(patient.getPrescriptions().contains(prescription2));
    }
    
    @Test
    public void testMultipleMedicationAdministrations() {
        // Arrange
        MedicationAdministration administration1 = new MedicationAdministration(
            "ADM001",
            medication,
            patient,
            nurse,
            LocalDateTime.now().minusHours(8),
            "Morning dose"
        );
        
        MedicationAdministration administration2 = new MedicationAdministration(
            "ADM002",
            medication,
            patient,
            nurse,
            LocalDateTime.now(),
            "Evening dose"
        );
        
        // Act
        patient.addMedicationAdministration(administration1);
        patient.addMedicationAdministration(administration2);
        
        // Assert
        assertEquals(2, patient.getMedicationAdministrations().size());
        assertTrue(patient.getMedicationAdministrations().contains(administration1));
        assertTrue(patient.getMedicationAdministrations().contains(administration2));
    }
}
