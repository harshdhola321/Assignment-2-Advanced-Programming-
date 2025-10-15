package org.example.service;

import org.example.exception.ComplianceException;
import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.Staff;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for staff management
 */
public interface StaffService {
    /**
     * Add a new staff member to the system
     * @param staff The staff member to add
     * @return true if the staff was added successfully, false otherwise
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    boolean addStaff(Staff staff) throws UnauthorizedActionException, NotRosteredException;
    
    /**
     * Update a staff member in the system
     * @param staff The staff member to update
     * @return true if the staff was updated successfully, false otherwise
     * @throws UnauthorizedActionException If the current user is not authorized
     * @throws NotRosteredException If the current user is not rostered
     */
    boolean updateStaff(Staff staff) throws UnauthorizedActionException, NotRosteredException;
    
    /**
     * Get all staff members
     * @return All staff members
     */
    List<Staff> getAllStaff();
    
    /**
     * Get a staff member by ID
     * @param id The staff ID
     * @return Optional containing the staff member if found, empty otherwise
     */
    Optional<Staff> getStaffById(String id);
    
    /**
     * Get a staff member by username
     * @param username The username
     * @return Optional containing the staff member if found, empty otherwise
     */
    Optional<Staff> getStaffByUsername(String username);
    
    /**
     * Check compliance with staffing regulations
     * @throws ComplianceException if any compliance rule is violated
     */
    void checkCompliance() throws ComplianceException;
}
