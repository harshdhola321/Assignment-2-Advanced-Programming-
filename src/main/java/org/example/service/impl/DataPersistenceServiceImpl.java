package org.example.service.impl;

import org.example.model.*;
import org.example.service.DataPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Implementation of DataPersistenceService
 */
public class DataPersistenceServiceImpl implements DataPersistenceService {
    private static final Logger logger = LoggerFactory.getLogger(DataPersistenceServiceImpl.class);
    private static final String DB_URL = "jdbc:h2:./care_home_archive";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    public DataPersistenceServiceImpl() {
        initializeDatabase();
    }
    
    /**
     * Initialize the database
     */
    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Create tables for archiving patient data
            String createPatientTable = "CREATE TABLE IF NOT EXISTS patient_archive (" +
                    "id VARCHAR(50) PRIMARY KEY, " +
                    "first_name VARCHAR(100), " +
                    "last_name VARCHAR(100), " +
                    "date_of_birth DATE, " +
                    "gender VARCHAR(10), " +
                    "medical_condition VARCHAR(255), " +
                    "needs_isolation BOOLEAN, " +
                    "admission_date DATE, " +
                    "discharge_date DATE" +
                    ")";
            
            String createPrescriptionTable = "CREATE TABLE IF NOT EXISTS prescription_archive (" +
                    "id VARCHAR(50) PRIMARY KEY, " +
                    "patient_id VARCHAR(50), " +
                    "doctor_id VARCHAR(50), " +
                    "prescription_date_time TIMESTAMP, " +
                    "notes VARCHAR(255), " +
                    "FOREIGN KEY (patient_id) REFERENCES patient_archive(id)" +
                    ")";
            
            String createMedicationTable = "CREATE TABLE IF NOT EXISTS medication_archive (" +
                    "id VARCHAR(50) PRIMARY KEY, " +
                    "prescription_id VARCHAR(50), " +
                    "name VARCHAR(100), " +
                    "dosage VARCHAR(50), " +
                    "instructions VARCHAR(255), " +
                    "FOREIGN KEY (prescription_id) REFERENCES prescription_archive(id)" +
                    ")";
            
            String createAdministrationTable = "CREATE TABLE IF NOT EXISTS administration_archive (" +
                    "id VARCHAR(50) PRIMARY KEY, " +
                    "medication_id VARCHAR(50), " +
                    "patient_id VARCHAR(50), " +
                    "nurse_id VARCHAR(50), " +
                    "administration_date_time TIMESTAMP, " +
                    "notes VARCHAR(255), " +
                    "FOREIGN KEY (medication_id) REFERENCES medication_archive(id), " +
                    "FOREIGN KEY (patient_id) REFERENCES patient_archive(id)" +
                    ")";
            
            try (PreparedStatement stmt = conn.prepareStatement(createPatientTable)) {
                stmt.execute();
            }
            
            try (PreparedStatement stmt = conn.prepareStatement(createPrescriptionTable)) {
                stmt.execute();
            }
            
            try (PreparedStatement stmt = conn.prepareStatement(createMedicationTable)) {
                stmt.execute();
            }
            
            try (PreparedStatement stmt = conn.prepareStatement(createAdministrationTable)) {
                stmt.execute();
            }
            
            logger.info("Database initialized successfully");
        } catch (SQLException e) {
            logger.error("Error initializing database", e);
        }
    }
    
    @Override
    public boolean archivePatient(Patient patient) {
        if (patient.getDischargeDate() == null) {
            logger.error("Cannot archive a patient who has not been discharged");
            return false;
        }
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false);
            
            // Archive patient
            String insertPatient = "INSERT INTO patient_archive VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertPatient)) {
                stmt.setString(1, patient.getId());
                stmt.setString(2, patient.getFirstName());
                stmt.setString(3, patient.getLastName());
                stmt.setObject(4, patient.getDateOfBirth());
                stmt.setString(5, patient.getGender().toString());
                stmt.setString(6, patient.getMedicalCondition());
                stmt.setBoolean(7, patient.isNeedsIsolation());
                stmt.setObject(8, patient.getAdmissionDate());
                stmt.setObject(9, patient.getDischargeDate());
                stmt.executeUpdate();
            }
            
            // Archive prescriptions and medications
            for (Prescription prescription : patient.getPrescriptions()) {
                String insertPrescription = "INSERT INTO prescription_archive VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertPrescription)) {
                    stmt.setString(1, prescription.getId());
                    stmt.setString(2, patient.getId());
                    stmt.setString(3, prescription.getDoctor().getId());
                    stmt.setObject(4, prescription.getPrescriptionDateTime());
                    stmt.setString(5, prescription.getNotes());
                    stmt.executeUpdate();
                }
                
                for (Medication medication : prescription.getMedications()) {
                    String insertMedication = "INSERT INTO medication_archive VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(insertMedication)) {
                        stmt.setString(1, medication.getId());
                        stmt.setString(2, prescription.getId());
                        stmt.setString(3, medication.getName());
                        stmt.setString(4, medication.getDosage());
                        stmt.setString(5, medication.getInstructions());
                        stmt.executeUpdate();
                    }
                }
            }
            
            // Archive medication administrations
            for (MedicationAdministration admin : patient.getMedicationAdministrations()) {
                String insertAdmin = "INSERT INTO administration_archive VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertAdmin)) {
                    stmt.setString(1, admin.getId());
                    stmt.setString(2, admin.getMedication().getId());
                    stmt.setString(3, patient.getId());
                    stmt.setString(4, admin.getNurse().getId());
                    stmt.setObject(5, admin.getAdministrationDateTime());
                    stmt.setString(6, admin.getNotes());
                    stmt.executeUpdate();
                }
            }
            
            conn.commit();
            logger.info("Patient " + patient.getFullName() + " archived successfully");
            return true;
        } catch (SQLException e) {
            logger.error("Error archiving patient", e);
            return false;
        }
    }
}
