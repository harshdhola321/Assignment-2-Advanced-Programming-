package org.example.controller;

import org.example.exception.ComplianceException;
import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.Staff;
import org.example.service.StaffService;

import java.util.List;
import java.util.Optional;

/**
 * Controller for staff-related operations
 */
public class StaffController {
    private final StaffService staffService;
    
    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }
    
    /**
     * Add a new staff member to the system
     * @param staff The staff member to add
     * @return true if the staff was added successfully, false otherwise
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     * @throws IllegalArgumentException If any input is invalid
     */
    public boolean addStaff(Staff staff) throws UnauthorizedActionException, NotRosteredException {
        // Input validation
        if (staff == null) {
            throw new IllegalArgumentException("Staff cannot be null");
        }
        
        if (staff.getFirstName() == null || staff.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        
        if (staff.getLastName() == null || staff.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        
        if (staff.getUsername() == null || staff.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        if (staff.getPassword() == null || staff.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        // Delegate to service
        return staffService.addStaff(staff);
    }
    
    /**
     * Update a staff member in the system
     * @param staff The staff member to update
     * @return true if the staff was updated successfully, false otherwise
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     * @throws IllegalArgumentException If any input is invalid
     */
    public boolean updateStaff(Staff staff) throws UnauthorizedActionException, NotRosteredException {
        // Input validation
        if (staff == null) {
            throw new IllegalArgumentException("Staff cannot be null");
        }
        
        if (staff.getId() == null || staff.getId().isEmpty()) {
            throw new IllegalArgumentException("Staff ID cannot be empty");
        }
        
        // Delegate to service
        return staffService.updateStaff(staff);
    }
    
    /**
     * Get all staff members
     * @return All staff members
     */
    public List<Staff> getAllStaff() {
        return staffService.getAllStaff();
    }
    
    /**
     * Get a staff member by ID
     * @param id The staff ID
     * @return Optional containing the staff member if found, empty otherwise
     * @throws IllegalArgumentException If the ID is invalid
     */
    public Optional<Staff> getStaffById(String id) {
        // Input validation
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Staff ID cannot be empty");
        }
        
        // Delegate to service
        return staffService.getStaffById(id);
    }
    
    /**
     * Get a staff member by username
     * @param username The username
     * @return Optional containing the staff member if found, empty otherwise
     * @throws IllegalArgumentException If the username is invalid
     */
    public Optional<Staff> getStaffByUsername(String username) {
        // Input validation
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        // Delegate to service
        return staffService.getStaffByUsername(username);
    }
    
    /**
     * Check compliance with staffing regulations
     * @throws ComplianceException if any compliance rule is violated
     */
    public void checkCompliance() throws ComplianceException {
        staffService.checkCompliance();
    }
}
