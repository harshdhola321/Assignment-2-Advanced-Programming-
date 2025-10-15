package org.example.util;

import org.example.model.Staff;
import org.example.service.AuthenticationService;
import org.example.service.CareHomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to register all staff members with the AuthenticationService
 */
public class RegisterAllStaff {
    private static final Logger logger = LoggerFactory.getLogger(RegisterAllStaff.class);

    /**
     * Register all staff members with the AuthenticationService
     * @return true if successful, false otherwise
     */
    public static boolean registerAllStaff() {
        try {
            CareHomeService careHomeService = CareHomeService.getInstance();
            AuthenticationService authService = AuthenticationService.getInstance();

            int count = 0;

            // Get all staff members
            for (Staff staff : careHomeService.getAllStaff()) {
                // Register with authentication service
                boolean registered = authService.registerUser(staff);

                if (registered) {
                    count++;
                    logger.info("Registered user: " + staff.getUsername() + " (" + staff.getClass().getSimpleName() + ")");
                } else {
                    logger.warn("Failed to register user: " + staff.getUsername() + " (already exists)");
                }
            }

            logger.info("Registered " + count + " staff members with authentication service");
            return true;
        } catch (Exception e) {
            logger.error("Error registering staff members", e);
            return false;
        }
    }

    /**
     * Main method to register all staff members
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Registering all staff members with authentication service...");

        boolean success = registerAllStaff();

        if (success) {
            System.out.println("Staff members registered successfully.");
        } else {
            System.out.println("Failed to register staff members.");
        }
    }
}
