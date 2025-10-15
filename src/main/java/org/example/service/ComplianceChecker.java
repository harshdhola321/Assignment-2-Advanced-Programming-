package org.example.service;

import org.example.exception.ComplianceException;
import org.example.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class to check compliance with staffing regulations
 */
public class ComplianceChecker {
    private static final Logger logger = LoggerFactory.getLogger(ComplianceChecker.class);

    /**
     * Check compliance with staffing regulations:
     * - Nurses must be assigned to two shifts (8am-4pm and 2pm-10pm) every day
     * - Doctors must be assigned for at least 1 hour every day
     * - No nurse can work more than 8 hours in a single day
     * 
     * @param staff List of all staff members
     * @throws ComplianceException if any compliance rule is violated
     */
    public static void checkCompliance(List<Staff> staff) throws ComplianceException {
        // Check if we have at least one nurse and one doctor
        List<Nurse> nurses = staff.stream()
                .filter(s -> s instanceof Nurse)
                .map(s -> (Nurse) s)
                .collect(Collectors.toList());
                
        List<Doctor> doctors = staff.stream()
                .filter(s -> s instanceof Doctor)
                .map(s -> (Doctor) s)
                .collect(Collectors.toList());
                
        if (nurses.isEmpty()) {
            throw new ComplianceException("No nurses are registered in the system");
        }
        
        if (doctors.isEmpty()) {
            throw new ComplianceException("No doctors are registered in the system");
        }
        
        // Define the required shifts for nurses
        LocalTime morningShiftStart = LocalTime.of(8, 0);
        LocalTime morningShiftEnd = LocalTime.of(16, 0);
        LocalTime afternoonShiftStart = LocalTime.of(14, 0);
        LocalTime afternoonShiftEnd = LocalTime.of(22, 0);
        
        // Check nurse shift coverage for each day of the week
        for (DayOfWeek day : DayOfWeek.values()) {
            // Check morning shift coverage (8am-4pm)
            boolean morningShiftCovered = false;
            for (Nurse nurse : nurses) {
                if (nurse.getShifts().stream().anyMatch(s -> 
                        s.getDayOfWeek() == day && 
                        s.getStartTime().equals(morningShiftStart) && 
                        s.getEndTime().equals(morningShiftEnd))) {
                    morningShiftCovered = true;
                    break;
                }
            }
            
            if (!morningShiftCovered) {
                throw new ComplianceException("Morning shift (8am-4pm) not covered for " + day);
            }
            
            // Check afternoon shift coverage (2pm-10pm)
            boolean afternoonShiftCovered = false;
            for (Nurse nurse : nurses) {
                if (nurse.getShifts().stream().anyMatch(s -> 
                        s.getDayOfWeek() == day && 
                        s.getStartTime().equals(afternoonShiftStart) && 
                        s.getEndTime().equals(afternoonShiftEnd))) {
                    afternoonShiftCovered = true;
                    break;
                }
            }
            
            if (!afternoonShiftCovered) {
                throw new ComplianceException("Afternoon shift (2pm-10pm) not covered for " + day);
            }
            
            // Check doctor coverage (at least 1 hour per day)
            boolean doctorCovered = false;
            for (Doctor doctor : doctors) {
                if (doctor.getShifts().stream().anyMatch(s -> s.getDayOfWeek() == day)) {
                    doctorCovered = true;
                    break;
                }
            }
            
            if (!doctorCovered) {
                throw new ComplianceException("No doctor assigned for " + day);
            }
        }
        
        // Check that no nurse works more than 8 hours in a single day
        for (Nurse nurse : nurses) {
            Map<DayOfWeek, Integer> hoursPerDay = new HashMap<>();
            
            for (Shift shift : nurse.getShifts()) {
                DayOfWeek day = shift.getDayOfWeek();
                
                // Calculate shift duration in hours
                int startHour = shift.getStartTime().getHour();
                int endHour = shift.getEndTime().getHour();
                int duration;
                
                if (endHour < startHour) {
                    // Overnight shift
                    duration = (24 - startHour) + endHour;
                } else {
                    duration = endHour - startHour;
                }
                
                // Add hours to the day's total
                hoursPerDay.put(day, hoursPerDay.getOrDefault(day, 0) + duration);
                
                // If this is an overnight shift, add hours to the next day as well
                if (endHour < startHour) {
                    DayOfWeek nextDay = day.plus(1);
                    hoursPerDay.put(nextDay, hoursPerDay.getOrDefault(nextDay, 0) + endHour);
                }
            }
            
            // Check if any day exceeds 8 hours
            for (Map.Entry<DayOfWeek, Integer> entry : hoursPerDay.entrySet()) {
                if (entry.getValue() > 8) {
                    throw new ComplianceException("Nurse " + nurse.getFullName() + 
                            " is scheduled for more than 8 hours on " + entry.getKey());
                }
            }
        }
        
        logger.info("Compliance check passed successfully");
    }
}
