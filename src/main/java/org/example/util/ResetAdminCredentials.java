package org.example.util;

import org.example.model.Manager;
import org.example.model.Staff;
import org.example.service.AuthenticationService;
import org.example.service.CareHomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Utility class to reset admin credentials
 * This can be run as a standalone application
 */
public class ResetAdminCredentials {
    private static final Logger logger = LoggerFactory.getLogger(ResetAdminCredentials.class);
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";
    private static final String DATA_FILE = "care_home_data.ser";

    /**
     * Main method to reset admin credentials
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Resetting admin credentials...");
        
        // Delete existing data file if it exists
        File dataFile = new File(DATA_FILE);
        if (dataFile.exists()) {
            boolean deleted = dataFile.delete();
            if (deleted) {
                System.out.println("Existing data file deleted.");
            } else {
                System.out.println("Failed to delete existing data file.");
                return;
            }
        }
        
        // Initialize the CareHomeService to create new data
        CareHomeService careHomeService = CareHomeService.getInstance();
        
        // Ensure admin user exists
        boolean result = AdminUserInitializer.ensureAdminUserExists();
        
        if (result) {
            System.out.println("Admin user reset successfully.");
            System.out.println("Username: " + DEFAULT_ADMIN_USERNAME);
            System.out.println("Password: " + DEFAULT_ADMIN_PASSWORD);
        } else {
            System.out.println("Failed to reset admin user.");
        }
    }
    
    /**
     * Reset admin credentials in existing data
     * @return true if successful, false otherwise
     */
    public static boolean resetAdminCredentials() {
        try {
            CareHomeService careHomeService = CareHomeService.getInstance();
            AuthenticationService authService = AuthenticationService.getInstance();
            
            // Find admin user
            List<Staff> allStaff = careHomeService.getAllStaff();
            for (Staff staff : allStaff) {
                if (staff instanceof Manager && DEFAULT_ADMIN_USERNAME.equals(staff.getUsername())) {
                    // Reset password
                    staff.setPassword(DEFAULT_ADMIN_PASSWORD);
                    
                    // Re-register with authentication service
                    authService.registerUser(staff);
                    
                    // Save data
                    careHomeService.saveData();
                    
                    logger.info("Admin credentials reset successfully");
                    return true;
                }
            }
            
            // If admin user not found, create one
            return AdminUserInitializer.ensureAdminUserExists();
            
        } catch (Exception e) {
            logger.error("Error resetting admin credentials", e);
            return false;
        }
    }
}
