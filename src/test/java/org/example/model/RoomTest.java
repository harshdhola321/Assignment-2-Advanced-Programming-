package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Room class
 */
public class RoomTest {
    private Room room;
    private Ward ward;
    private Patient patient;

    @BeforeEach
    public void setUp() {
        ward = new Ward("W1", "Test Ward");
        room = new Room("R1", "101", ward, 3);
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
    public void testRoomInitialization() {
        assertEquals("R1", room.getId());
        assertEquals("101", room.getNumber());
        assertEquals(ward, room.getWard());
        assertEquals(3, room.getBeds().size());
        assertEquals(3, room.getNumberOfBeds());
        assertEquals(3, room.getVacantBeds());
        assertEquals(0, room.getOccupiedBeds());
    }

    @Test
    public void testBedCreation() {
        // Verify beds were created with correct IDs and names
        assertEquals("R1-B1", room.getBeds().get(0).getId());
        assertEquals("Bed 1", room.getBeds().get(0).getName());
        assertEquals("R1-B2", room.getBeds().get(1).getId());
        assertEquals("Bed 2", room.getBeds().get(1).getName());
        assertEquals("R1-B3", room.getBeds().get(2).getId());
        assertEquals("Bed 3", room.getBeds().get(2).getName());
    }

    @Test
    public void testOccupancyCounts() {
        // Initially all beds are vacant
        assertEquals(3, room.getVacantBeds());
        assertEquals(0, room.getOccupiedBeds());
        
        // Assign patient to first bed
        room.getBeds().get(0).assignPatient(patient);
        
        // Verify counts are updated
        assertEquals(2, room.getVacantBeds());
        assertEquals(1, room.getOccupiedBeds());
        
        // Assign patient to second bed
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
        room.getBeds().get(1).assignPatient(patient2);
        
        // Verify counts are updated
        assertEquals(1, room.getVacantBeds());
        assertEquals(2, room.getOccupiedBeds());
        
        // Remove patient from first bed
        room.getBeds().get(0).removePatient();
        
        // Verify counts are updated
        assertEquals(2, room.getVacantBeds());
        assertEquals(1, room.getOccupiedBeds());
    }

    @Test
    public void testToString() {
        String expected = ward.getName() + " - Room " + room.getNumber();
        assertEquals(expected, room.toString());
    }
}
