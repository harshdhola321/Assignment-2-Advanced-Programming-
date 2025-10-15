package org.example.service;

import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationServiceTest {

    private AuthenticationService authService;
    private Doctor doctor;
    private Nurse nurse;
    private Manager manager;
    private Manager adminUser;
    
    @BeforeEach
    public void setUp() throws Exception {
        // Reset the singleton instance for testing
        resetAuthServiceSingleton();
        
        // Get a fresh instance for each test
        authService = AuthenticationService.getInstance();
        
        // Create test staff members
        doctor = new Doctor(
            "DOC001", 
            "John", 
            "Smith", 
            LocalDate.of(1980, 1, 1), 
            Gender.MALE, 
            "doctor", 
            "password", 
            "Cardiology"
        );
        
        nurse = new Nurse(
            "NUR001", 
            "Jane", 
            "Doe", 
            LocalDate.of(1985, 5, 15), 
            Gender.FEMALE, 
            "nurse", 
            "password", 
            "Registered Nurse"
        );
        
        manager = new Manager(
            "MNG001", 
            "Mike", 
            "Johnson", 
            LocalDate.of(1975, 10, 20), 
            Gender.MALE, 
            "manager", 
            "password", 
            "Administration"
        );
        
        adminUser = new Manager(
            "ADM001", 
            "Admin", 
            "User", 
            LocalDate.of(1990, 1, 1), 
            Gender.MALE, 
            "admin", 
            "admin", 
            "System Administration"
        );
        
        // Register users directly
        Map<String, Staff> users = getPrivateUsersMap();
        users.put(doctor.getUsername(), doctor);
        users.put(nurse.getUsername(), nurse);
        users.put(manager.getUsername(), manager);
        users.put(adminUser.getUsername(), adminUser);
    }
    
    private void resetAuthServiceSingleton() throws Exception {
        java.lang.reflect.Field instance = AuthenticationService.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Staff> getPrivateUsersMap() throws Exception {
        java.lang.reflect.Field usersField = AuthenticationService.class.getDeclaredField("users");
        usersField.setAccessible(true);
        return (Map<String, Staff>) usersField.get(authService);
    }
    
    private void setCurrentUser(Staff staff) throws Exception {
        java.lang.reflect.Field currentUserField = AuthenticationService.class.getDeclaredField("currentUser");
        currentUserField.setAccessible(true);
        currentUserField.set(authService, staff);
    }
    
    @Test
    public void testLogin_ValidCredentials_Success() {
        // Act
        Staff loggedInStaff = authService.login("doctor", "password");
        
        // Assert
        assertNotNull(loggedInStaff);
        assertEquals(doctor, loggedInStaff);
        assertEquals(doctor, authService.getCurrentUser());
        assertTrue(authService.isLoggedIn());
    }
    
    @Test
    public void testLogin_InvalidUsername_ReturnsNull() {
        // Act
        Staff loggedInStaff = authService.login("nonexistent", "password");
        
        // Assert
        assertNull(loggedInStaff);
        assertNull(authService.getCurrentUser());
        assertFalse(authService.isLoggedIn());
    }
    
    @Test
    public void testLogin_InvalidPassword_ReturnsNull() {
        // Act
        Staff loggedInStaff = authService.login("doctor", "wrongpassword");
        
        // Assert
        assertNull(loggedInStaff);
        assertNull(authService.getCurrentUser());
        assertFalse(authService.isLoggedIn());
    }
    
    @Test
    public void testLogout_Success() throws Exception {
        // Arrange
        setCurrentUser(doctor);
        assertTrue(authService.isLoggedIn());
        
        // Act
        authService.logout();
        
        // Assert
        assertNull(authService.getCurrentUser());
        assertFalse(authService.isLoggedIn());
    }
    
    @Test
    public void testRegisterUser_NewUser_Success() {
        // Arrange
        Staff newStaff = new Doctor(
            "DOC002", 
            "New", 
            "Doctor", 
            LocalDate.of(1982, 2, 2), 
            Gender.MALE, 
            "newdoctor", 
            "password", 
            "Neurology"
        );
        
        // Act
        boolean result = authService.registerUser(newStaff);
        
        // Assert
        assertTrue(result);
        
        // Verify the user can log in
        Staff loggedInStaff = authService.login("newdoctor", "password");
        assertNotNull(loggedInStaff);
        assertEquals(newStaff, loggedInStaff);
    }
    
    @Test
    public void testRegisterUser_ExistingUsername_Failure() {
        // Arrange
        Staff duplicateStaff = new Doctor(
            "DOC002", 
            "Duplicate", 
            "Doctor", 
            LocalDate.of(1982, 2, 2), 
            Gender.MALE, 
            "doctor", // Same username as existing doctor
            "password", 
            "Neurology"
        );
        
        // Act
        boolean result = authService.registerUser(duplicateStaff);
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    public void testIsAuthorized_Doctor_AuthorizedForPrescription() throws Exception {
        // Arrange
        setCurrentUser(doctor);
        
        // Act & Assert
        assertTrue(authService.isAuthorized("ADD_PRESCRIPTION"));
        assertFalse(authService.isAuthorized("ADMINISTER_MEDICATION"));
    }
    
    @Test
    public void testIsAuthorized_Nurse_AuthorizedForMedication() throws Exception {
        // Arrange
        setCurrentUser(nurse);
        
        // Act & Assert
        assertTrue(authService.isAuthorized("ADMINISTER_MEDICATION"));
        assertTrue(authService.isAuthorized("MOVE_PATIENT"));
        assertFalse(authService.isAuthorized("ADD_PRESCRIPTION"));
    }
    
    @Test
    public void testIsAuthorized_Manager_AuthorizedForStaffManagement() throws Exception {
        // Arrange
        setCurrentUser(manager);
        
        // Act & Assert
        assertTrue(authService.isAuthorized("ADD_PATIENT"));
        assertTrue(authService.isAuthorized("ADD_STAFF"));
        assertTrue(authService.isAuthorized("MODIFY_STAFF"));
        assertFalse(authService.isAuthorized("ADD_PRESCRIPTION"));
    }
    
    @Test
    public void testIsAuthorized_AdminUser_AuthorizedForAll() throws Exception {
        // Arrange
        setCurrentUser(adminUser);
        
        // Act & Assert
        assertTrue(authService.isAuthorized("ADD_PRESCRIPTION"));
        assertTrue(authService.isAuthorized("ADMINISTER_MEDICATION"));
        assertTrue(authService.isAuthorized("ADD_PATIENT"));
        assertTrue(authService.isAuthorized("ADD_STAFF"));
    }
    
    @Test
    public void testIsRostered_NoShifts_ReturnsFalse() throws Exception {
        // Arrange
        setCurrentUser(doctor);
        
        // Act & Assert
        assertFalse(authService.isRostered());
    }
    
    @Test
    public void testIsRostered_WithCurrentShift_ReturnsTrue() throws Exception {
        // Arrange
        setCurrentUser(doctor);
        
        // Add a shift for the current day and time
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek today = now.getDayOfWeek();
        LocalTime startTime = LocalTime.of(0, 0);
        LocalTime endTime = LocalTime.of(23, 59);
        
        Shift currentShift = new Shift(today, startTime, endTime);
        doctor.addShift(currentShift);
        
        // Act & Assert
        assertTrue(authService.isRostered());
    }
    
    @Test
    public void testIsRostered_WithPastShift_ReturnsFalse() throws Exception {
        // Arrange
        setCurrentUser(doctor);
        
        // Add a shift for yesterday
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek yesterday = now.getDayOfWeek().minus(1);
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(16, 0);
        
        Shift pastShift = new Shift(yesterday, startTime, endTime);
        doctor.addShift(pastShift);
        
        // Act & Assert
        assertFalse(authService.isRostered());
    }
    
    @Test
    public void testIsRostered_AdminUser_AlwaysReturnsTrue() throws Exception {
        // Arrange
        setCurrentUser(adminUser);
        
        // Act & Assert
        assertTrue(authService.isRostered());
    }
    
    @Test
    public void testCheckAuthorizedAndRostered_ValidUser_NoException() throws Exception {
        // Arrange
        setCurrentUser(adminUser);
        
        // Act & Assert
        assertDoesNotThrow(() -> authService.checkAuthorizedAndRostered("ADD_PRESCRIPTION"));
    }
    
    @Test
    public void testCheckAuthorizedAndRostered_NotLoggedIn_ThrowsException() {
        // Act & Assert
        UnauthorizedActionException exception = assertThrows(
            UnauthorizedActionException.class,
            () -> authService.checkAuthorizedAndRostered("ADD_PRESCRIPTION")
        );
        assertEquals("No user is logged in", exception.getMessage());
    }
    
    @Test
    public void testCheckAuthorizedAndRostered_NotAuthorized_ThrowsException() throws Exception {
        // Arrange
        setCurrentUser(nurse);
        
        // Act & Assert
        UnauthorizedActionException exception = assertThrows(
            UnauthorizedActionException.class,
            () -> authService.checkAuthorizedAndRostered("ADD_PRESCRIPTION")
        );
        assertTrue(exception.getMessage().contains("is not authorized to ADD_PRESCRIPTION"));
    }
    
    @Test
    public void testCheckAuthorizedAndRostered_NotRostered_ThrowsException() throws Exception {
        // Arrange - Set doctor as current user (not rostered)
        setCurrentUser(doctor);
        
        // Act & Assert
        NotRosteredException exception = assertThrows(
            NotRosteredException.class,
            () -> authService.checkAuthorizedAndRostered("ADD_PRESCRIPTION")
        );
        assertTrue(exception.getMessage().contains("is not rostered for the current time"));
    }
}
