package org.example.service;

import org.example.exception.ComplianceException;
import org.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ComplianceCheckerTest {

    private List<Staff> staff;
    private Nurse nurse1;
    private Nurse nurse2;
    private Doctor doctor;

    @BeforeEach
    public void setUp() {
        staff = new ArrayList<>();

        // Create test nurses and doctor
        nurse1 = new Nurse(
                "NUR001",
                "Jane",
                "Doe",
                LocalDate.of(1985, 5, 15),
                Gender.FEMALE,
                "nurse1",
                "password",
                "Registered Nurse"
        );

        nurse2 = new Nurse(
                "NUR002",
                "Sarah",
                "Johnson",
                LocalDate.of(1990, 8, 20),
                Gender.FEMALE,
                "nurse2",
                "password",
                "Registered Nurse"
        );

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

        staff.add(nurse1);
        staff.add(nurse2);
        staff.add(doctor);
    }

    @Test
    public void testCheckCompliance_ValidStaffing_NoException() {
        // Arrange
        // Add morning shifts (8am-4pm) for nurse1 for all days
        for (DayOfWeek day : DayOfWeek.values()) {
            nurse1.addShift(new Shift(day, LocalTime.of(8, 0), LocalTime.of(16, 0)));
        }

        // Add afternoon shifts (2pm-10pm) for nurse2 for all days
        for (DayOfWeek day : DayOfWeek.values()) {
            nurse2.addShift(new Shift(day, LocalTime.of(14, 0), LocalTime.of(22, 0)));
        }

        // Add doctor shifts for all days (1 hour each)
        for (DayOfWeek day : DayOfWeek.values()) {
            doctor.addShift(new Shift(day, LocalTime.of(9, 0), LocalTime.of(10, 0)));
        }

        // Act & Assert
        assertDoesNotThrow(() -> ComplianceChecker.checkCompliance(staff));
    }

    @Test
    public void testCheckCompliance_NoNurses_ThrowsException() {
        // Arrange
        List<Staff> staffWithoutNurses = new ArrayList<>();
        staffWithoutNurses.add(doctor);

        // Act & Assert
        ComplianceException exception = assertThrows(
                ComplianceException.class,
                () -> ComplianceChecker.checkCompliance(staffWithoutNurses)
        );
        assertEquals("No nurses are registered in the system", exception.getMessage());
    }

    @Test
    public void testCheckCompliance_NoDoctors_ThrowsException() {
        // Arrange
        List<Staff> staffWithoutDoctors = new ArrayList<>();
        staffWithoutDoctors.add(nurse1);
        staffWithoutDoctors.add(nurse2);

        // Act & Assert
        ComplianceException exception = assertThrows(
                ComplianceException.class,
                () -> ComplianceChecker.checkCompliance(staffWithoutDoctors)
        );
        assertEquals("No doctors are registered in the system", exception.getMessage());
    }

    @Test
    public void testCheckCompliance_MissingMorningShift_ThrowsException() {
        // Arrange
        // Add morning shifts for nurse1 for all days except MONDAY
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day != DayOfWeek.MONDAY) {
                nurse1.addShift(new Shift(day, LocalTime.of(8, 0), LocalTime.of(16, 0)));
            }
        }

        // Add afternoon shifts for nurse2 for all days
        for (DayOfWeek day : DayOfWeek.values()) {
            nurse2.addShift(new Shift(day, LocalTime.of(14, 0), LocalTime.of(22, 0)));
        }

        // Add doctor shifts for all days
        for (DayOfWeek day : DayOfWeek.values()) {
            doctor.addShift(new Shift(day, LocalTime.of(9, 0), LocalTime.of(10, 0)));
        }

        // Act & Assert
        ComplianceException exception = assertThrows(
                ComplianceException.class,
                () -> ComplianceChecker.checkCompliance(staff)
        );
        assertEquals("Morning shift (8am-4pm) not covered for MONDAY", exception.getMessage());
    }

    @Test
    public void testCheckCompliance_MissingAfternoonShift_ThrowsException() {
        // Arrange
        // Add morning shifts for nurse1 for all days
        for (DayOfWeek day : DayOfWeek.values()) {
            nurse1.addShift(new Shift(day, LocalTime.of(8, 0), LocalTime.of(16, 0)));
        }

        // Add afternoon shifts for nurse2 for all days except TUESDAY
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day != DayOfWeek.TUESDAY) {
                nurse2.addShift(new Shift(day, LocalTime.of(14, 0), LocalTime.of(22, 0)));
            }
        }

        // Add doctor shifts for all days
        for (DayOfWeek day : DayOfWeek.values()) {
            doctor.addShift(new Shift(day, LocalTime.of(9, 0), LocalTime.of(10, 0)));
        }

        // Act & Assert
        ComplianceException exception = assertThrows(
                ComplianceException.class,
                () -> ComplianceChecker.checkCompliance(staff)
        );
        assertEquals("Afternoon shift (2pm-10pm) not covered for TUESDAY", exception.getMessage());
    }

    @Test
    public void testCheckCompliance_MissingDoctorShift_ThrowsException() {
        // Arrange
        // Add morning shifts for nurse1 for all days
        for (DayOfWeek day : DayOfWeek.values()) {
            nurse1.addShift(new Shift(day, LocalTime.of(8, 0), LocalTime.of(16, 0)));
        }

        // Add afternoon shifts for nurse2 for all days
        for (DayOfWeek day : DayOfWeek.values()) {
            nurse2.addShift(new Shift(day, LocalTime.of(14, 0), LocalTime.of(22, 0)));
        }

        // Add doctor shifts for all days except WEDNESDAY
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day != DayOfWeek.WEDNESDAY) {
                doctor.addShift(new Shift(day, LocalTime.of(9, 0), LocalTime.of(10, 0)));
            }
        }

        // Act & Assert
        ComplianceException exception = assertThrows(
                ComplianceException.class,
                () -> ComplianceChecker.checkCompliance(staff)
        );
        assertEquals("No doctor assigned for WEDNESDAY", exception.getMessage());
    }

    @Test
    public void testCheckCompliance_NurseWorksMoreThan8Hours_ThrowsException() {
        // Arrange
        // Add morning shifts for nurse1 for all days
        for (DayOfWeek day : DayOfWeek.values()) {
            nurse1.addShift(new Shift(day, LocalTime.of(8, 0), LocalTime.of(16, 0)));
        }

        // Add afternoon shifts for nurse2 for all days
        for (DayOfWeek day : DayOfWeek.values()) {
            nurse2.addShift(new Shift(day, LocalTime.of(14, 0), LocalTime.of(22, 0)));
        }

        // Add an additional shift for nurse1 on MONDAY (exceeding 8 hours)
        nurse1.addShift(new Shift(DayOfWeek.MONDAY, LocalTime.of(17, 0), LocalTime.of(20, 0)));

        // Add doctor shifts for all days
        for (DayOfWeek day : DayOfWeek.values()) {
            doctor.addShift(new Shift(day, LocalTime.of(9, 0), LocalTime.of(10, 0)));
        }

        // Act & Assert
        ComplianceException exception = assertThrows(
                ComplianceException.class,
                () -> ComplianceChecker.checkCompliance(staff)
        );
        assertTrue(exception.getMessage().contains("is scheduled for more than 8 hours on MONDAY"));
    }

    @Test
    public void testCheckCompliance_OvernightShift_CorrectHoursCounting() {
        // Arrange
        // Add morning shifts for nurse1 for all days
        for (DayOfWeek day : DayOfWeek.values()) {
            nurse1.addShift(new Shift(day, LocalTime.of(8, 0), LocalTime.of(16, 0)));
        }

        // Add afternoon shifts for nurse2 for all days
        for (DayOfWeek day : DayOfWeek.values()) {
            nurse2.addShift(new Shift(day, LocalTime.of(14, 0), LocalTime.of(22, 0)));
        }

        // Add an overnight shift for nurse1 on SUNDAY (8pm to 2am)
        // This adds 4 hours to SUNDAY and 2 hours to MONDAY
        nurse1.addShift(new Shift(DayOfWeek.SUNDAY, LocalTime.of(20, 0), LocalTime.of(2, 0)));

        // Add doctor shifts for all days
        for (DayOfWeek day : DayOfWeek.values()) {
            doctor.addShift(new Shift(day, LocalTime.of(9, 0), LocalTime.of(10, 0)));
        }

        // Act & Assert
        // This should not throw an exception because:
        // - SUNDAY: 8 hours (day shift) + 4 hours (evening part of overnight) = 12 hours, which exceeds 8 hours
        ComplianceException exception = assertThrows(
                ComplianceException.class,
                () -> ComplianceChecker.checkCompliance(staff)
        );
        assertTrue(exception.getMessage().contains("is scheduled for more than 8 hours on SUNDAY"));
    }

    @Test
    public void testCheckCompliance_ShiftWithWrongTimes_ThrowsException() {
        // Arrange
        // Add morning shifts for nurse1 for all days, but with wrong time on MONDAY
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day == DayOfWeek.MONDAY) {
                // Wrong time (9am-5pm instead of 8am-4pm)
                nurse1.addShift(new Shift(day, LocalTime.of(9, 0), LocalTime.of(17, 0)));
            } else {
                nurse1.addShift(new Shift(day, LocalTime.of(8, 0), LocalTime.of(16, 0)));
            }
        }

        // Add afternoon shifts for nurse2 for all days
        for (DayOfWeek day : DayOfWeek.values()) {
            nurse2.addShift(new Shift(day, LocalTime.of(14, 0), LocalTime.of(22, 0)));
        }

        // Add doctor shifts for all days
        for (DayOfWeek day : DayOfWeek.values()) {
            doctor.addShift(new Shift(day, LocalTime.of(9, 0), LocalTime.of(10, 0)));
        }

        // Act & Assert
        ComplianceException exception = assertThrows(
                ComplianceException.class,
                () -> ComplianceChecker.checkCompliance(staff)
        );
        assertEquals("Morning shift (8am-4pm) not covered for MONDAY", exception.getMessage());
    }
}
