package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.Patient;
import org.example.service.CareHomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

/**
 * Controller for the discharge patient view
 */
public class DischargePatientController {
    private static final Logger logger = LoggerFactory.getLogger(DischargePatientController.class);
    
    @FXML
    private ComboBox<Patient> patientComboBox;
    
    @FXML
    private Label patientInfoLabel;
    
    @FXML
    private Button dischargeButton;
    
    @FXML
    private Button cancelButton;
    
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
                updatePatientInfo(newValue);
            } else {
                patientInfoLabel.setText("");
            }
        });
    }
    
    /**
     * Update patient info label
     * @param patient The patient
     */
    private void updatePatientInfo(Patient patient) {
        patientInfoLabel.setText(
            patient.getFullName() + " (" + patient.getGender() + "), " +
            "DOB: " + patient.getDateOfBirth().format(dateFormatter) + ", " +
            "Admitted: " + patient.getAdmissionDate().format(dateFormatter) + ", " +
            "Condition: " + patient.getMedicalCondition()
        );
    }
    
    /**
     * Handle discharge button click
     * @param event The action event
     */
    @FXML
    public void handleDischarge(ActionEvent event) {
        Patient selectedPatient = patientComboBox.getValue();
        
        if (selectedPatient == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No Selection", "Please select a patient to discharge");
            return;
        }
        
        try {
            boolean discharged = careHomeService.dischargePatient(selectedPatient);
            
            if (discharged) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Patient Discharged", 
                        "Patient " + selectedPatient.getFullName() + " has been discharged and their records archived");
                
                // Close the dialog
                Stage stage = (Stage) dischargeButton.getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Discharge Failed", 
                        "Failed to discharge patient " + selectedPatient.getFullName());
            }
        } catch (UnauthorizedActionException e) {
            logger.warn("Unauthorized: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Unauthorized", e.getMessage());
        } catch (NotRosteredException e) {
            logger.warn("Not rostered: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Not Rostered", e.getMessage());
        } catch (Exception e) {
            logger.error("Error discharging patient", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error Discharging Patient", e.getMessage());
        }
    }
    
    /**
     * Handle cancel button click
     * @param event The action event
     */
    @FXML
    public void handleCancel(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Show an alert dialog
     * @param type The alert type
     * @param title The alert title
     * @param header The alert header
     * @param content The alert content
     */
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
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
