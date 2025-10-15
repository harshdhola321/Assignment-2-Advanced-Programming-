package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Staff class and its subclasses
 */
public class StaffTest {
    private Doctor doctor;
    private Nurse nurse;
    private Manager manager;
    private Shift morningShift;
    private Shift afternoonShift;

    @BeforeEach
    public void setUp() {
        doctor = new Doctor(
            "D1",
            "John",
            "Smith",
            LocalDate.of(1975, 5, 15),
            Gender.MALE,
            "drsmith",
            "password123",
            "Cardiology"
        );
        
        nurse = new Nurse(
            "N1",
            "Jane",
            "Johnson",
            LocalDate.of(1980, 8, 20),
            Gender.FEMALE,
            "jjohnson",
            "password456",
            "Registered Nurse"
        );
        
        manager = new Manager(
            "M1",
            "Robert",
            "Brown",
            LocalDate.of(1970, 3, 10),
            Gender.MALE,
            "rbrown",
            "password789",
            "Administration"
        );
        
        morningShift = new Shift(
            DayOfWeek.MONDAY,
            LocalTime.of(8, 0),
            LocalTime.of(16, 0)
        );
        
        afternoonShift = new Shift(
            DayOfWeek.TUESDAY,
            LocalTime.of(16, 0),
            LocalTime.of(0, 0)
        );
    }

    @Test
    public void testDoctorInitialization() {
        assertEquals("D1", doctor.getId());
        assertEquals("John", doctor.getFirstName());
        assertEquals("Smith", doctor.getLastName());
        assertEquals(LocalDate.of(1975, 5, 15), doctor.getDateOfBirth());
        assertEquals(Gender.MALE, doctor.getGender());
        assertEquals("drsmith", doctor.getUsername());
        assertEquals("password123", doctor.getPassword());
        assertEquals("Cardiology", doctor.getSpecialization());
        assertEquals("John Smith", doctor.getFullName());
        assertTrue(doctor.getShifts().isEmpty());
    }

    @Test
    public void testNurseInitialization() {
        assertEquals("N1", nurse.getId());
        assertEquals("Jane", nurse.getFirstName());
        assertEquals("Johnson", nurse.getLastName());
        assertEquals(LocalDate.of(1980, 8, 20), nurse.getDateOfBirth());
        assertEquals(Gender.FEMALE, nurse.getGender());
        assertEquals("jjohnson", nurse.getUsername());
        assertEquals("password456", nurse.getPassword());
        assertEquals("Registered Nurse", nurse.getQualification());
        assertEquals("Jane Johnson", nurse.getFullName());
        assertTrue(nurse.getShifts().isEmpty());
    }

    @Test
    public void testManagerInitialization() {
        assertEquals("M1", manager.getId());
        assertEquals("Robert", manager.getFirstName());
        assertEquals("Brown", manager.getLastName());
        assertEquals(LocalDate.of(1970, 3, 10), manager.getDateOfBirth());
        assertEquals(Gender.MALE, manager.getGender());
        assertEquals("rbrown", manager.getUsername());
        assertEquals("password789", manager.getPassword());
        assertEquals("Administration", manager.getDepartment());
        assertEquals("Robert Brown", manager.getFullName());
        assertTrue(manager.getShifts().isEmpty());
    }

    @Test
    public void testShiftManagement() {
        // Add shifts
        doctor.addShift(morningShift);
        doctor.addShift(afternoonShift);
        
        // Verify shifts were added
        assertEquals(2, doctor.getShifts().size());
        assertTrue(doctor.getShifts().contains(morningShift));
        assertTrue(doctor.getShifts().contains(afternoonShift));
        
        // Remove a shift
        doctor.removeShift(morningShift);
        
        // Verify shift was removed
        assertEquals(1, doctor.getShifts().size());
        assertFalse(doctor.getShifts().contains(morningShift));
        assertTrue(doctor.getShifts().contains(afternoonShift));
    }

    @Test
    public void testIsRosteredFor() {
        // Add shifts
        doctor.addShift(morningShift);
        
        // Test times within shift
        assertTrue(doctor.isRosteredFor(
            LocalDateTime.of(LocalDate.now().with(DayOfWeek.MONDAY), LocalTime.of(8, 0))
        ));
        assertTrue(doctor.isRosteredFor(
            LocalDateTime.of(LocalDate.now().with(DayOfWeek.MONDAY), LocalTime.of(12, 0))
        ));
        assertTrue(doctor.isRosteredFor(
            LocalDateTime.of(LocalDate.now().with(DayOfWeek.MONDAY), LocalTime.of(16, 0))
        ));
        
        // Test times outside shift
        assertFalse(doctor.isRosteredFor(
            LocalDateTime.of(LocalDate.now().with(DayOfWeek.MONDAY), LocalTime.of(7, 59))
        ));
        assertFalse(doctor.isRosteredFor(
            LocalDateTime.of(LocalDate.now().with(DayOfWeek.MONDAY), LocalTime.of(16, 1))
        ));
        assertFalse(doctor.isRosteredFor(
            LocalDateTime.of(LocalDate.now().with(DayOfWeek.TUESDAY), LocalTime.of(12, 0))
        ));
    }

    @Test
    public void testDoctorAuthorization() {
        assertTrue(doctor.isAuthorizedFor("ADD_PRESCRIPTION"));
        assertFalse(doctor.isAuthorizedFor("ADMINISTER_MEDICATION"));
        assertFalse(doctor.isAuthorizedFor("MOVE_PATIENT"));
        assertFalse(doctor.isAuthorizedFor("ADD_PATIENT"));
        assertFalse(doctor.isAuthorizedFor("ADD_STAFF"));
        assertFalse(doctor.isAuthorizedFor("MODIFY_STAFF"));
    }

    @Test
    public void testNurseAuthorization() {
        assertFalse(nurse.isAuthorizedFor("ADD_PRESCRIPTION"));
        assertTrue(nurse.isAuthorizedFor("ADMINISTER_MEDICATION"));
        assertTrue(nurse.isAuthorizedFor("MOVE_PATIENT"));
        assertFalse(nurse.isAuthorizedFor("ADD_PATIENT"));
        assertFalse(nurse.isAuthorizedFor("ADD_STAFF"));
        assertFalse(nurse.isAuthorizedFor("MODIFY_STAFF"));
    }

    @Test
    public void testManagerAuthorization() {
        assertFalse(manager.isAuthorizedFor("ADD_PRESCRIPTION"));
        assertFalse(manager.isAuthorizedFor("ADMINISTER_MEDICATION"));
        assertFalse(manager.isAuthorizedFor("MOVE_PATIENT"));
        assertTrue(manager.isAuthorizedFor("ADD_PATIENT"));
        assertTrue(manager.isAuthorizedFor("ADD_STAFF"));
        assertTrue(manager.isAuthorizedFor("MODIFY_STAFF"));
    }
}
