package org.example.util;

import org.example.model.Gender;
import org.example.model.Manager;
import org.example.model.Shift;
import org.example.model.Staff;
import org.example.service.AuthenticationService;
import org.example.service.CareHomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Utility class to ensure an admin user exists in the system
 */
public class AdminUserInitializer {
    private static final Logger logger = LoggerFactory.getLogger(AdminUserInitializer.class);
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    /**
     * Check if an admin user exists, and create one if it doesn't
     * @return true if an admin user exists or was created, false if there was an error
     */
    public static boolean ensureAdminUserExists() {
        try {
            AuthenticationService authService = AuthenticationService.getInstance();
            CareHomeService careHomeService = CareHomeService.getInstance();
            
            // Check if admin user exists
            List<Staff> allStaff = careHomeService.getAllStaff();
            boolean adminExists = allStaff.stream()
                    .anyMatch(staff -> staff instanceof Manager && DEFAULT_ADMIN_USERNAME.equals(staff.getUsername()));
            
            if (adminExists) {
                logger.info("Admin user already exists");
                return true;
            }
            
            // Create admin user if it doesn't exist
            logger.info("Admin user does not exist, creating default admin user");
            Manager adminUser = new Manager(
                IdGenerator.generateId("STAFF"),
                "Admin",
                "User",
                LocalDate.of(1980, 1, 1),
                Gender.MALE,
                DEFAULT_ADMIN_USERNAME,
                DEFAULT_ADMIN_PASSWORD,
                "Administration"
            );
            
            // Add shifts for all days of the week to ensure the admin is always rostered
            addAllShifts(adminUser);
            
            // Register the admin user
            boolean registered = authService.registerUser(adminUser);
            if (registered) {
                careHomeService.getAllStaff().add(adminUser);
                careHomeService.saveData();
                logger.info("Default admin user created successfully");
                return true;
            } else {
                logger.error("Failed to register admin user");
                return false;
            }
        } catch (Exception e) {
            logger.error("Error ensuring admin user exists", e);
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
