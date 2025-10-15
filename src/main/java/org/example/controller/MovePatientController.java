package org.example.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.Bed;
import org.example.model.Patient;
import org.example.service.AuthenticationService;
import org.example.service.CareHomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Controller for the move patient view
 */
public class MovePatientController {
    private static final Logger logger = LoggerFactory.getLogger(MovePatientController.class);

    @FXML
    private ComboBox<Patient> patientComboBox;

    @FXML
    private Label currentBedLabel;

    @FXML
    private ComboBox<Bed> newBedComboBox;

    @FXML
    private Label genderLabel;

    @FXML
    private Label isolationLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Button moveButton;

    @FXML
    private Button cancelButton;

    private CareHomeService careHomeService;
    private AuthenticationService authService;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        careHomeService = CareHomeService.getInstance();
        authService = AuthenticationService.getInstance();
        
        // Initialize patient combo box
        List<Patient> patients = careHomeService.getAllPatients();
        patientComboBox.setItems(FXCollections.observableArrayList(patients));
        
        // Set cell factory for patient combo box to display patient information
        patientComboBox.setCellFactory(param -> new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (empty || patient == null) {
                    setText(null);
                } else {
                    setText(patient.getFullName());
                }
            }
        });
        
        // Set converter for patient combo box to display patient information
        patientComboBox.setConverter(new javafx.util.StringConverter<Patient>() {
            @Override
            public String toString(Patient patient) {
                if (patient == null) {
                    return null;
                }
                return patient.getFullName();
            }

            @Override
            public Patient fromString(String string) {
                return null;
            }
        });
        
        // Add listener to patient combo box to update bed information
        patientComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updatePatientInfo(newVal);
            } else {
                clearPatientInfo();
            }
        });
        
        // Set cell factory for bed combo box to display bed information
        newBedComboBox.setCellFactory(param -> new ListCell<Bed>() {
            @Override
            protected void updateItem(Bed bed, boolean empty) {
                super.updateItem(bed, empty);
                if (empty || bed == null) {
                    setText(null);
                } else {
                    setText(bed.toString());
                }
            }
        });
        
        // Set converter for bed combo box to display bed information
        newBedComboBox.setConverter(new javafx.util.StringConverter<Bed>() {
            @Override
            public String toString(Bed bed) {
                if (bed == null) {
                    return null;
                }
                return bed.toString();
            }

            @Override
            public Bed fromString(String string) {
                return null;
            }
        });
        
        // Clear error label
        errorLabel.setText("");
    }

    /**
     * Update patient information
     * @param patient The selected patient
     */
    private void updatePatientInfo(Patient patient) {
        Optional<Bed> currentBedOpt = careHomeService.findBedForPatient(patient);
        if (currentBedOpt.isPresent()) {
            Bed currentBed = currentBedOpt.get();
            currentBedLabel.setText(currentBed.toString());
            genderLabel.setText(patient.getGender().toString());
            isolationLabel.setText(patient.isNeedsIsolation() ? "Yes" : "No");
            
            // Update available beds
            List<Bed> suitableBeds = careHomeService.getSuitableVacantBeds(patient.getGender(), patient.isNeedsIsolation());
            newBedComboBox.setItems(FXCollections.observableArrayList(suitableBeds));
            
            if (!suitableBeds.isEmpty()) {
                newBedComboBox.setValue(suitableBeds.get(0));
            } else {
                newBedComboBox.setValue(null);
            }
        } else {
            clearPatientInfo();
            errorLabel.setText("Patient is not assigned to any bed");
        }
    }

    /**
     * Clear patient information
     */
    private void clearPatientInfo() {
        currentBedLabel.setText("Not selected");
        genderLabel.setText("Not selected");
        isolationLabel.setText("Not selected");
        newBedComboBox.getItems().clear();
    }

    /**
     * Handle move button click
     * @param event The action event
     */
    @FXML
    public void handleMove(ActionEvent event) {
        // Validate input
        if (!validateInput()) {
            return;
        }
        
        Patient patient = patientComboBox.getValue();
        Bed newBed = newBedComboBox.getValue();
        
        try {
            careHomeService.movePatient(patient, newBed);
            
            if (true) {
                logger.info("Patient moved: {} to {}", patient.getFullName(), newBed);
                closeDialog();
            } else {
                errorLabel.setText("Failed to move patient. The bed may already be occupied.");
                logger.warn("Failed to move patient. Bed may be occupied: {}", newBed);
            }
        } catch (UnauthorizedActionException e) {
            errorLabel.setText("Unauthorized: " + e.getMessage());
            logger.warn("Unauthorized action: {}", e.getMessage());
        } catch (NotRosteredException e) {
            errorLabel.setText("Not rostered: " + e.getMessage());
            logger.warn("Not rostered: {}", e.getMessage());
        }
    }

    /**
     * Handle cancel button click
     * @param event The action event
     */
    @FXML
    public void handleCancel(ActionEvent event) {
        closeDialog();
    }

    /**
     * Validate input fields
     * @return true if input is valid, false otherwise
     */
    private boolean validateInput() {
        // Check patient
        if (patientComboBox.getValue() == null) {
            errorLabel.setText("Patient is required");
            return false;
        }
        
        // Check new bed
        if (newBedComboBox.getValue() == null) {
            errorLabel.setText("New bed is required");
            return false;
        }
        
        // Check if current and new bed are the same
        Optional<Bed> currentBedOpt = careHomeService.findBedForPatient(patientComboBox.getValue());
        if (currentBedOpt.isPresent() && currentBedOpt.get().equals(newBedComboBox.getValue())) {
            errorLabel.setText("New bed cannot be the same as current bed");
            return false;
        }
        
        return true;
    }

    /**
     * Close the dialog
     */
    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
