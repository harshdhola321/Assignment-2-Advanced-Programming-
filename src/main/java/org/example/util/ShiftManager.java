package org.example.util;

import org.example.model.Doctor;
import org.example.model.Nurse;
import org.example.model.Shift;
import org.example.model.Staff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

/**
 * Utility class for managing staff shifts
 */
public class ShiftManager {
    private static final Logger logger = LoggerFactory.getLogger(ShiftManager.class);
    
    // Standard shift times
    public static final LocalTime MORNING_SHIFT_START = LocalTime.of(8, 0);
    public static final LocalTime MORNING_SHIFT_END = LocalTime.of(16, 0);
    public static final LocalTime AFTERNOON_SHIFT_START = LocalTime.of(14, 0);
    public static final LocalTime AFTERNOON_SHIFT_END = LocalTime.of(22, 0);
    public static final LocalTime DOCTOR_SHIFT_START = LocalTime.of(10, 0);
    public static final LocalTime DOCTOR_SHIFT_END = LocalTime.of(11, 0);
    
    /**
     * Assign standard shifts to a nurse
     * @param nurse The nurse to assign shifts to
     * @param shiftType The type of shift to assign (1 for morning, 2 for afternoon)
     */
    public static void assignStandardShiftsToNurse(Nurse nurse, int shiftType) {
        // Clear existing shifts
        nurse.getShifts().clear();
        
        // Assign shifts for all days of the week
        for (DayOfWeek day : DayOfWeek.values()) {
            if (shiftType == 1) {
                // Morning shift (8am-4pm)
                nurse.addShift(new Shift(day, MORNING_SHIFT_START, MORNING_SHIFT_END));
            } else {
                // Afternoon shift (2pm-10pm)
                nurse.addShift(new Shift(day, AFTERNOON_SHIFT_START, AFTERNOON_SHIFT_END));
            }
        }
        
        logger.info("Standard shifts assigned to nurse: " + nurse.getFullName());
    }
    
    /**
     * Assign standard shifts to a doctor
     * @param doctor The doctor to assign shifts to
     */
    public static void assignStandardShiftsToDoctor(Doctor doctor) {
        // Clear existing shifts
        doctor.getShifts().clear();
        
        // Assign shifts for all days of the week
        for (DayOfWeek day : DayOfWeek.values()) {
            // Doctor shift (1 hour per day)
            doctor.addShift(new Shift(day, DOCTOR_SHIFT_START, DOCTOR_SHIFT_END));
        }
        
        logger.info("Standard shifts assigned to doctor: " + doctor.getFullName());
    }
    
    /**
     * Check if a staff member has any shifts assigned
     * @param staff The staff member to check
     * @return true if the staff member has shifts, false otherwise
     */
    public static boolean hasShifts(Staff staff) {
        return !staff.getShifts().isEmpty();
    }
    
    /**
     * Get the total hours assigned to a staff member for a specific day
     * @param staff The staff member
     * @param day The day of the week
     * @return The total hours assigned
     */
    public static int getTotalHoursForDay(Staff staff, DayOfWeek day) {
        int totalHours = 0;
        
        for (Shift shift : staff.getShifts()) {
            if (shift.getDayOfWeek() == day) {
                int startHour = shift.getStartTime().getHour();
                int endHour = shift.getEndTime().getHour();
                
                if (endHour < startHour) {
                    // Overnight shift
                    totalHours += (24 - startHour) + endHour;
                } else {
                    totalHours += endHour - startHour;
                }
            }
        }
        
        return totalHours;
    }
}
