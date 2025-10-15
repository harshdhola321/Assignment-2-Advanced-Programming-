package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.example.model.Patient;
import org.example.model.Prescription;
import org.example.service.CareHomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

/**
 * Controller for the view patient view
 */
public class ViewPatientController {
    private static final Logger logger = LoggerFactory.getLogger(ViewPatientController.class);
    
    @FXML
    private ComboBox<Patient> patientComboBox;
    
    @FXML
    private Label patientIdLabel;
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private Label dobLabel;
    
    @FXML
    private Label genderLabel;
    
    @FXML
    private Label medicalConditionLabel;
    
    @FXML
    private Label admissionDateLabel;
    
    @FXML
    private ListView<String> prescriptionsListView;
    
    @FXML
    private Button closeButton;
    
    private CareHomeService careHomeService;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        careHomeService = CareHomeService.getInstance();
        
        // Initialize patient combo box
        ObservableList<Patient> patientList = FXCollections.observableArrayList(careHomeService.getAllPatients());
        patientComboBox.setItems(patientList);
        patientComboBox.setConverter(new PatientStringConverter());
        
        // Add listener to patient combo box
        patientComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayPatientDetails(newValue);
            }
        });
        
        // Clear labels initially
        clearLabels();
    }
    
    /**
     * Display patient details
     * @param patient The patient to display
     */
    private void displayPatientDetails(Patient patient) {
        patientIdLabel.setText(patient.getId());
        nameLabel.setText(patient.getFullName());
        dobLabel.setText(patient.getDateOfBirth().format(dateFormatter));
        genderLabel.setText(patient.getGender().toString());
        medicalConditionLabel.setText(patient.getMedicalCondition());
        admissionDateLabel.setText(patient.getAdmissionDate().format(dateFormatter));
        
        // Display prescriptions
        ObservableList<String> prescriptions = FXCollections.observableArrayList();
        for (Prescription prescription : patient.getPrescriptions()) {
            prescriptions.add(formatPrescription(prescription));
        }
        prescriptionsListView.setItems(prescriptions);
    }
    
    /**
     * Format a prescription for display
     * @param prescription The prescription to format
     * @return The formatted prescription string
     */
    private String formatPrescription(Prescription prescription) {
        return prescription.getPrescriptionDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + 
               " - Dr. " + prescription.getDoctor().getLastName() + 
               " - " + prescription.getMedications().size() + " medication(s)";
    }
    
    /**
     * Clear all labels
     */
    private void clearLabels() {
        patientIdLabel.setText("");
        nameLabel.setText("");
        dobLabel.setText("");
        genderLabel.setText("");
        medicalConditionLabel.setText("");
        admissionDateLabel.setText("");
        prescriptionsListView.setItems(FXCollections.observableArrayList());
    }
    
    /**
     * Handle close button click
     * @param event The action event
     */
    @FXML
    public void handleClose(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Converter for displaying patients in combo box
     */
    private static class PatientStringConverter extends javafx.util.StringConverter<Patient> {
        @Override
        public String toString(Patient patient) {
            return patient == null ? "" : patient.getFullName();
        }
        
        @Override
        public Patient fromString(String string) {
            return null; // Not needed for this use case
        }
    }
}
