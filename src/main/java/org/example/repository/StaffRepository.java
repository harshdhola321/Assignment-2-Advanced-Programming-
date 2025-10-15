package org.example.repository;

import org.example.model.Staff;
import org.example.model.Ward;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Staff data access
 */
public interface StaffRepository {
    /**
     * Find all staff members
     * @return List of all staff members
     */
    List<Staff> findAll();
    
    /**
     * Find a staff member by ID
     * @param id The staff ID
     * @return Optional containing the staff member if found, empty otherwise
     */
    Optional<Staff> findById(String id);
    
    /**
     * Find a staff member by username
     * @param username The username
     * @return Optional containing the staff member if found, empty otherwise
     */
    Optional<Staff> findByUsername(String username);
    
    /**
     * Save a staff member
     * @param staff The staff member to save
     * @return true if saved successfully, false if username already exists
     */
    boolean save(Staff staff);
    
    /**
     * Update a staff member
     * @param staff The staff member to update
     * @return true if updated successfully, false if not found
     */
    boolean update(Staff staff);
    
    /**
     * Delete a staff member
     * @param staff The staff member to delete
     */
    void delete(Staff staff);

    void saveAll(List<Staff> staff);
}
