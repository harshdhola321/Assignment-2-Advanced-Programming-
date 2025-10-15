package org.example.model;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftTest {

    @Test
    public void testShiftConstructor() {
        // Arrange
        DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(16, 0);
        
        // Act
        Shift shift = new Shift(dayOfWeek, startTime, endTime);
        
        // Assert
        assertEquals(dayOfWeek, shift.getDayOfWeek());
        assertEquals(startTime, shift.getStartTime());
        assertEquals(endTime, shift.getEndTime());
    }
    
    @Test
    public void testShiftEquals() {
        // Arrange
        Shift shift1 = new Shift(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0));
        Shift shift2 = new Shift(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0));
        Shift shift3 = new Shift(DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(16, 0));
        
        // Assert
        assertEquals(shift1, shift2);
        assertNotEquals(shift1, shift3);
        assertNotEquals(shift1, null);
        assertNotEquals(shift1, "Not a shift");
    }
    
    @Test
    public void testShiftHashCode() {
        // Arrange
        Shift shift1 = new Shift(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0));
        Shift shift2 = new Shift(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0));
        
        // Assert
        assertEquals(shift1.hashCode(), shift2.hashCode());
    }
    
    @Test
    public void testShiftToString() {
        // Arrange
        Shift shift = new Shift(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0));
        
        // Act
        String shiftString = shift.toString();
        
        // Assert
        assertTrue(shiftString.contains("MONDAY"));
        assertTrue(shiftString.contains("08:00"));
        assertTrue(shiftString.contains("16:00"));
    }
    
    @Test
    public void testCovers_RegularShift_WithinShift_ReturnsTrue() {
        // Arrange
        Shift shift = new Shift(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0));
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 13, 12, 0); // Monday at noon
        
        // Act & Assert
        assertTrue(shift.covers(dateTime));
    }
    
    @Test
    public void testCovers_RegularShift_BeforeShift_ReturnsFalse() {
        // Arrange
        Shift shift = new Shift(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0));
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 13, 7, 0); // Monday at 7am
        
        // Act & Assert
        assertFalse(shift.covers(dateTime));
    }
    
    @Test
    public void testCovers_RegularShift_AfterShift_ReturnsFalse() {
        // Arrange
        Shift shift = new Shift(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0));
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 13, 17, 0); // Monday at 5pm
        
        // Act & Assert
        assertFalse(shift.covers(dateTime));
    }
    
    @Test
    public void testCovers_RegularShift_WrongDay_ReturnsFalse() {
        // Arrange
        Shift shift = new Shift(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0));
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 14, 12, 0); // Tuesday at noon
        
        // Act & Assert
        assertFalse(shift.covers(dateTime));
    }
    
    @Test
    public void testCovers_OvernightShift_FirstDay_ReturnsTrue() {
        // Arrange
        Shift shift = new Shift(DayOfWeek.MONDAY, LocalTime.of(22, 0), LocalTime.of(6, 0));
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 13, 23, 0); // Monday at 11pm
        
        // Act & Assert
        assertTrue(shift.covers(dateTime));
    }
    
    @Test
    public void testCovers_OvernightShift_SecondDay_ReturnsTrue() {
        // Arrange
        Shift shift = new Shift(DayOfWeek.MONDAY, LocalTime.of(22, 0), LocalTime.of(6, 0));
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 14, 5, 0); // Tuesday at 5am
        
        // Act & Assert
        assertTrue(shift.covers(dateTime));
    }
    
    @Test
    public void testCovers_OvernightShift_SecondDayAfterEnd_ReturnsFalse() {
        // Arrange
        Shift shift = new Shift(DayOfWeek.MONDAY, LocalTime.of(22, 0), LocalTime.of(6, 0));
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 14, 7, 0); // Tuesday at 7am
        
        // Act & Assert
        assertFalse(shift.covers(dateTime));
    }
    
    @Test
    public void testCovers_OvernightShift_FirstDayBeforeStart_ReturnsFalse() {
        // Arrange
        Shift shift = new Shift(DayOfWeek.MONDAY, LocalTime.of(22, 0), LocalTime.of(6, 0));
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 13, 21, 0); // Monday at 9pm
        
        // Act & Assert
        assertFalse(shift.covers(dateTime));
    }
    
    @Test
    public void testCovers_OvernightShift_WrongDays_ReturnsFalse() {
        // Arrange
        Shift shift = new Shift(DayOfWeek.MONDAY, LocalTime.of(22, 0), LocalTime.of(6, 0));
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 15, 23, 0); // Wednesday at 11pm
        
        // Act & Assert
        assertFalse(shift.covers(dateTime));
    }
    
    @Test
    public void testCovers_MidnightShift_ReturnsTrue() {
        // Arrange
        Shift shift = new Shift(DayOfWeek.MONDAY, LocalTime.of(20, 0), LocalTime.MIDNIGHT);
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 13, 23, 59); // Monday at 11:59pm
        
        // Act & Assert
        assertTrue(shift.covers(dateTime));
    }
    
    @Test
    public void testCovers_StartTimeEqual_ReturnsTrue() {
        // Arrange
        Shift shift = new Shift(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0));
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 13, 8, 0); // Monday at 8am (start time)
        
        // Act & Assert
        assertTrue(shift.covers(dateTime));
    }
    
    @Test
    public void testCovers_EndTimeEqual_ReturnsTrue() {
        // Arrange
        Shift shift = new Shift(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0));
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 13, 16, 0); // Monday at 4pm (end time)
        
        // Act & Assert
        assertTrue(shift.covers(dateTime));
    }
    
    @Test
    public void testCovers_OvernightShiftStartTime_ReturnsTrue() {
        // Arrange
        Shift shift = new Shift(DayOfWeek.MONDAY, LocalTime.of(22, 0), LocalTime.of(6, 0));
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 13, 22, 0); // Monday at 10pm (start time)
        
        // Act & Assert
        assertTrue(shift.covers(dateTime));
    }
    
    @Test
    public void testCovers_OvernightShiftEndTime_ReturnsTrue() {
        // Arrange
        Shift shift = new Shift(DayOfWeek.MONDAY, LocalTime.of(22, 0), LocalTime.of(6, 0));
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 14, 6, 0); // Tuesday at 6am (end time)
        
        // Act & Assert
        assertTrue(shift.covers(dateTime));
    }
}
