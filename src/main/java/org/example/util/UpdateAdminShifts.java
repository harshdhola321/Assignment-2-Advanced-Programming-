package org.example.util;

import org.example.model.Manager;
import org.example.model.Shift;
import org.example.model.Staff;
import org.example.service.CareHomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class to update existing admin users with proper shifts
 */
public class UpdateAdminShifts {
    private static final Logger logger = LoggerFactory.getLogger(UpdateAdminShifts.class);
    private static final String ADMIN_USERNAME = "admin";

    /**
     * Main method to update admin shifts
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Updating admin user shifts...");
        
        boolean success = updateAdminShifts();
        
        if (success) {
            System.out.println("Admin user shifts updated successfully.");
        } else {
            System.out.println("Failed to update admin user shifts.");
        }
    }
    
    /**
     * Update shifts for existing admin users
     * @return true if successful, false otherwise
     */
    public static boolean updateAdminShifts() {
        try {
            CareHomeService careHomeService = CareHomeService.getInstance();
            
            // Find admin users
            List<Staff> allStaff = careHomeService.getAllStaff();
            List<Manager> adminUsers = allStaff.stream()
                    .filter(staff -> staff instanceof Manager && ADMIN_USERNAME.equals(staff.getUsername()))
                    .map(staff -> (Manager) staff)
                    .collect(Collectors.toList());
            
            if (adminUsers.isEmpty()) {
                logger.warn("No admin users found");
                return false;
            }
            
            // Update shifts for each admin user
            for (Manager admin : adminUsers) {
                // Clear existing shifts
                admin.getShifts().clear();
                
                // Add new shifts
                addAllShifts(admin);
                
                logger.info("Updated shifts for admin user: " + admin.getFullName());
            }
            
            // Save changes
            careHomeService.saveData();
            logger.info("Admin shifts updated and saved successfully");
            
            return true;
        } catch (Exception e) {
            logger.error("Error updating admin shifts", e);
            return false;
        }
    }
    
    /**
     * Add shifts for all days of the week to a staff member
     * @param staff The staff member to add shifts to
     */
    private static void addAllShifts(Staff staff) {
        // Add shifts for all days of the week
        for (DayOfWeek day : DayOfWeek.values()) {
            // Full day shift (0:00 - 23:59)
            Shift fullDayShift = new Shift(day, LocalTime.of(0, 0), LocalTime.of(23, 59));
            staff.addShift(fullDayShift);
            
            // Also add specific shifts to ensure coverage
            // Morning shift (8:00 - 16:00)
            Shift morningShift = new Shift(day, LocalTime.of(8, 0), LocalTime.of(16, 0));
            staff.addShift(morningShift);
            
            // Afternoon shift (16:00 - 0:00)
            Shift afternoonShift = new Shift(day, LocalTime.of(16, 0), LocalTime.of(0, 0));
            staff.addShift(afternoonShift);
            
            // Night shift (0:00 - 8:00)
            Shift nightShift = new Shift(day, LocalTime.of(0, 0), LocalTime.of(8, 0));
            staff.addShift(nightShift);
        }
    }
}
