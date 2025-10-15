package org.example.config;

import org.example.controller.*;
import org.example.repository.*;
import org.example.repository.impl.*;
import org.example.service.*;
import org.example.service.impl.*;

/**
 * Configuration class for dependency injection
 */
public class ApplicationConfig {
    // Repositories
    private PatientRepository patientRepository;
    private StaffRepository staffRepository;
    private WardRepository wardRepository;
    
    // Services
    private AuthenticationService authService;
    private LoggingService logService;
    private PatientService patientService;
    private StaffService staffService;
    private WardService wardService;
    private PrescriptionService prescriptionService;
    private MedicationService medicationService;
    private DataPersistenceService dataPersistenceService;
    
    // Controllers
    private PatientController patientController;
    private StaffController staffController;
    private WardController wardController;
    private PrescriptionController prescriptionController;
    private MedicationController medicationController;
    
    /**
     * Initialize the application configuration
     */
    public ApplicationConfig() {
        initializeRepositories();
        initializeServices();
        initializeControllers();
    }
    
    /**
     * Initialize repositories
     */
    private void initializeRepositories() {
        patientRepository = new FilePatientRepository();
        staffRepository = new FileStaffRepository();
        wardRepository = new FileWardRepository();
    }
    
    /**
     * Initialize services
     */
    private void initializeServices() {
        // Initialize singleton services
        authService = AuthenticationService.getInstance();
        logService = LoggingService.getInstance();
        
        // Initialize other services with dependencies
        dataPersistenceService = new DataPersistenceServiceImpl();
        wardService = new WardServiceImpl(wardRepository, authService, logService);
        patientService = new PatientServiceImpl(patientRepository, wardService, authService, logService);
        staffService = new StaffServiceImpl(staffRepository, authService, logService);
        prescriptionService = new PrescriptionServiceImpl(patientService, staffService, authService, logService);
        medicationService = new MedicationServiceImpl(patientService, staffService, authService, logService);
    }
    
    /**
     * Initialize controllers
     */
    private void initializeControllers() {
        patientController = new PatientController(patientService);
        staffController = new StaffController(staffService);
        wardController = new WardController(wardService);
        prescriptionController = new PrescriptionController(prescriptionService);
        medicationController = new MedicationController(medicationService);
    }
    
    // Getters for controllers
    
    /**
     * Get the patient controller
     * @return The patient controller
     */
    public PatientController getPatientController() {
        return patientController;
    }
    
    /**
     * Get the staff controller
     * @return The staff controller
     */
    public StaffController getStaffController() {
        return staffController;
    }
    
    /**
     * Get the ward controller
     * @return The ward controller
     */
    public WardController getWardController() {
        return wardController;
    }
    
    /**
     * Get the prescription controller
     * @return The prescription controller
     */
    public PrescriptionController getPrescriptionController() {
        return prescriptionController;
    }
    
    /**
     * Get the medication controller
     * @return The medication controller
     */
    public MedicationController getMedicationController() {
        return medicationController;
    }
    
    /**
     * Get the data persistence service
     * @return The data persistence service
     */
    public DataPersistenceService getDataPersistenceService() {
        return dataPersistenceService;
    }
}
