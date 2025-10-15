package org.example.service.impl;

import org.example.exception.ComplianceException;
import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.Doctor;
import org.example.model.Nurse;
import org.example.model.Staff;
import org.example.repository.StaffRepository;
import org.example.service.AuthenticationService;
import org.example.service.LoggingService;
import org.example.service.StaffService;
import org.example.service.ComplianceChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of StaffService
 */
public class StaffServiceImpl implements StaffService {
    private static final Logger logger = LoggerFactory.getLogger(StaffServiceImpl.class);
    
    private final StaffRepository staffRepository;
    private final AuthenticationService authService;
    private final LoggingService logService;
    private List<Doctor> qualifiedDoctors;
    private List<Nurse> qualifiedNurses;
    
    public StaffServiceImpl(StaffRepository staffRepository,
                           AuthenticationService authService,
                           LoggingService logService) {
        this.staffRepository = staffRepository;
        this.authService = authService;
        this.logService = logService;
    }
    
    @Override
    public boolean addStaff(Staff staff) throws UnauthorizedActionException, NotRosteredException {
        authService.checkAuthorizedAndRostered("ADD_STAFF");
        
        // Add the staff member
        boolean success = staffRepository.save(staff);
        
        if (success) {
            logService.logAction(
                "ADD_STAFF",
                authService.getCurrentUser(),
                "Added staff member " + staff.getFullName() + " (" + staff.getClass().getSimpleName() + ")"
            );
            
            logger.info("Staff member added: {} ({})", staff.getFullName(), staff.getClass().getSimpleName());
        }
        
        return success;
    }
    
    @Override
    public boolean updateStaff(Staff staff) throws UnauthorizedActionException, NotRosteredException {
        authService.checkAuthorizedAndRostered("EDIT_STAFF");
        
        // Update the staff member
        boolean success = staffRepository.update(staff);
        
        if (success) {
            logService.logAction(
                "UPDATE_STAFF",
                authService.getCurrentUser(),
                "Updated staff member " + staff.getFullName() + " (" + staff.getClass().getSimpleName() + ")"
            );
            
            logger.info("Staff member updated: {} ({})", staff.getFullName(), staff.getClass().getSimpleName());
        }
        
        return success;
    }
    
    @Override
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }
    
    @Override
    public Optional<Staff> getStaffById(String id) {
        return staffRepository.findById(id);
    }
    
    @Override
    public Optional<Staff> getStaffByUsername(String username) {
        return staffRepository.findByUsername(username);
    }
    
    @Override
    public void checkCompliance() throws ComplianceException {
        // Use the ComplianceChecker utility to check compliance
        ComplianceChecker.checkCompliance(staffRepository.findAll());
    }

    private void initializeQualifiedStaffLists() {
        qualifiedDoctors = staffRepository.findAll().stream()
                .filter(s -> s instanceof Doctor)
                .map(s -> (Doctor) s)
                .collect(Collectors.toList());

        qualifiedNurses = staffRepository.findAll().stream()
                .filter(s -> s instanceof Nurse)
                .map(s -> (Nurse) s)
                .collect(Collectors.toList());
    }

    // Getters for qualified staff lists
    public List<Doctor> getQualifiedDoctors() {
        return qualifiedDoctors;
    }

    public List<Nurse> getQualifiedNurses() {
        return qualifiedNurses;
    }
}
