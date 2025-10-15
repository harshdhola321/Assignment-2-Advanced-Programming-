package org.example.service;

import org.example.exception.UnauthorizedActionException;
import org.example.exception.NotRosteredException;
import org.example.model.Manager;
import org.example.model.Staff;
import org.example.util.DefaultDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for handling authentication and authorization
 * Implemented as a Singleton
 */
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private static AuthenticationService instance;
    private Staff currentUser;
    private Map<String, Staff> users;

    private AuthenticationService() {
        users = new HashMap<>();
    }

    /**
     * Get the singleton instance of the authentication service
     * @return The authentication service instance
     */
    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
            // Ensure admin user is registered
            instance.ensureAdminUserRegistered();
        }
        return instance;
    }
    
    /**
     * Ensure that the admin user is registered in the authentication service
     * This is called during initialization to guarantee admin access
     */
    private void ensureAdminUserRegistered() {
        // Check if admin user exists in the service
        if (!users.containsKey("admin")) {
            try {
                // Find admin user from CareHomeService
                CareHomeService careHomeService = CareHomeService.getInstance();
                List<Staff> allStaff = careHomeService.getAllStaff();
                
                if (allStaff != null && !allStaff.isEmpty()) {
                    for (Staff staff : allStaff) {
                        if (staff != null && "admin".equals(staff.getUsername())) {
                            // Register the admin user
                            users.put("admin", staff);
                            logger.info("Admin user registered in authentication service");
                            break;
                        }
                    }
                }
                
                // If admin user was not found, create a default one
                if (!users.containsKey("admin")) {
                    createDefaultAdminUser();
                }
            } catch (Exception e) {
                logger.error("Error registering admin user", e);
                createDefaultAdminUser();
            }
        }
    }
    
    /**
     * Create a default admin user
     */
    private void createDefaultAdminUser() {
        // Create a default admin user
        List<Staff> defaultStaff = DefaultDataGenerator.generateDefaultStaff();
        for (Staff staff : defaultStaff) {
            if ("admin".equals(staff.getUsername())) {
                users.put("admin", staff);
                logger.info("Default admin user created");
                break;
            }
        }
    }

    /**
     * Register a new user
     * @param staff The staff member to register
     * @return true if registration was successful, false if the username already exists
     */
    public boolean registerUser(Staff staff) {
        if (users.containsKey(staff.getUsername())) {
            return false;
        }
        users.put(staff.getUsername(), staff);
        return true;
    }

    /**
     * Login a user
     * @param username The username
     * @param password The password
     * @return The staff member if login was successful, null otherwise
     */
    public Staff login(String username, String password) {
        Staff staff = users.get(username);
        if (staff != null && staff.getPassword().equals(password)) {
            currentUser = staff;
            return staff;
        }
        return null;
    }

    /**
     * Logout the current user
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Get the current user
     * @return The current user
     */
    public Staff getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if a user is logged in
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Check if the current user is authorized to perform an action
     * @param action The action to check
     * @return true if authorized, false otherwise
     */
    public boolean isAuthorized(String action) {
        return currentUser != null && currentUser.isAuthorizedFor(action);
    }

    /**
     * Check if the current user is rostered for the current time
     * @return true if rostered, false otherwise
     */
    public boolean isRostered() {
        if (currentUser != null && "admin".equals(currentUser.getUsername())) {
            // Admin user is always considered rostered
            return true;
        }
        return currentUser != null && currentUser.isRosteredFor(LocalDateTime.now());
    }

    /**
     * Check if the current user is authorized and rostered for an action
     * @param action The action to check
     * @throws UnauthorizedActionException If the user is not authorized
     * @throws NotRosteredException If the user is not rostered
     */
    public void checkAuthorizedAndRostered(String action) throws UnauthorizedActionException, NotRosteredException {
        if (!isLoggedIn()) {
            throw new UnauthorizedActionException("No user is logged in");
        }
        
        if (!isAuthorized(action)) {
            throw new UnauthorizedActionException(currentUser.getFullName() + " is not authorized to " + action);
        }
        
        if (!isRostered()) {
            throw new NotRosteredException(currentUser.getFullName() + " is not rostered for the current time");
        }
    }
}
