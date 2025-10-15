package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Bed class
 */
public class BedTest {
    private Bed bed;
    private Room room;
    private Ward ward;
    private Patient patient;

    @BeforeEach
    public void setUp() {
        ward = new Ward("W1", "Test Ward");
        room = new Room("R1", "101", ward, 2);
        bed = new Bed("B1", "Bed 1", room);
        patient = new Patient(
            "P1",
            "John",
            "Doe",
            LocalDate.of(1980, 1, 1),
            Gender.MALE,
            "Test Condition",
            false,
            LocalDate.now()
        );
    }

    @Test
    public void testBedInitialization() {
        assertEquals("B1", bed.getId());
        assertEquals("Bed 1", bed.getName());
        assertEquals(room, bed.getRoom());
        assertNull(bed.getPatient());
        assertFalse(bed.isOccupied());
    }

    @Test
    public void testAssignPatient() {
        assertTrue(bed.assignPatient(patient));
        assertEquals(patient, bed.getPatient());
        assertTrue(bed.isOccupied());
    }

    @Test
    public void testAssignPatientToOccupiedBed() {
        // Assign first patient
        assertTrue(bed.assignPatient(patient));
        
        // Create a second patient
        Patient patient2 = new Patient(
            "P2",
            "Jane",
            "Doe",
            LocalDate.of(1985, 1, 1),
            Gender.FEMALE,
            "Test Condition 2",
            false,
            LocalDate.now()
        );
        
        // Try to assign second patient to the same bed
        assertFalse(bed.assignPatient(patient2));
        
        // Verify the first patient is still assigned
        assertEquals(patient, bed.getPatient());
    }

    @Test
    public void testRemovePatient() {
        // Assign patient
        bed.assignPatient(patient);
        
        // Remove patient
        Patient removedPatient = bed.removePatient();
        
        // Verify patient was removed
        assertEquals(patient, removedPatient);
        assertNull(bed.getPatient());
        assertFalse(bed.isOccupied());
    }

    @Test
    public void testRemovePatientFromEmptyBed() {
        // Try to remove patient from empty bed
        Patient removedPatient = bed.removePatient();
        
        // Verify no patient was removed
        assertNull(removedPatient);
        assertFalse(bed.isOccupied());
    }

    @Test
    public void testToString() {
        String expected = room.toString() + " - " + bed.getName();
        assertEquals(expected, bed.toString());
    }
}
