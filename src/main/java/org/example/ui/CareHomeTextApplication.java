package org.example.ui;

import org.example.service.CareHomeService;
import org.example.util.AdminUserInitializer;
import org.example.util.RegisterAllStaff;
import org.example.util.UpdateAdminShifts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for the text-based Care Home System
 */
public class CareHomeTextApplication {
    private static final Logger logger = LoggerFactory.getLogger(CareHomeTextApplication.class);

    /**
     * Main method to start the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            // Initialize services
            CareHomeService careHomeService = CareHomeService.getInstance();

            // Initialize admin users and staff
            AdminUserInitializer.ensureAdminUserExists();
            UpdateAdminShifts.updateAdminShifts();
            RegisterAllStaff.registerAllStaff();

            logger.info("Care Home Text Application starting...");

            // Start the menu system
            MenuManager menuManager = MenuManager.getInstance();
            menuManager.start(new MainMenu());

            // Save data before exiting
            careHomeService.saveData();

            logger.info("Care Home Text Application stopped");
        } catch (Exception e) {
            logger.error("Error in Care Home Text Application", e);
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
