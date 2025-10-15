package org.example.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.Bed;
import org.example.model.Gender;
import org.example.model.Patient;
import org.example.service.AuthenticationService;
import org.example.service.CareHomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the add patient view
 */
public class  AddPatientController {
    private static final Logger logger = LoggerFactory.getLogger(AddPatientController.class);

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private DatePicker dateOfBirthPicker;

    @FXML
    private ComboBox<Gender> genderComboBox;

    @FXML
    private CheckBox needsIsolationCheckBox;

    @FXML
    private ComboBox<Bed> bedComboBox;

    @FXML
    private TextArea medicalConditionArea;

    @FXML
    private Label errorLabel;

    @FXML
    private Button saveButton;

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
        
        // Initialize gender combo box
        genderComboBox.setItems(FXCollections.observableArrayList(Gender.values()));
        
        // Initialize bed combo box
        List<Bed> vacantBeds = careHomeService.getVacantBeds();
        bedComboBox.setItems(FXCollections.observableArrayList(vacantBeds));
        
        // Set cell factory for bed combo box to display bed information
        bedComboBox.setCellFactory(param -> new ListCell<Bed>() {
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
        bedComboBox.setConverter(new javafx.util.StringConverter<Bed>() {
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
     * Handle save button click
     * @param event The action event
     */
    @FXML
    public void handleSave(ActionEvent event) {
        // Validate input
        if (!validateInput()) {
            return;
        }
        
        // Get input values
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        LocalDate dateOfBirth = dateOfBirthPicker.getValue();
        Gender gender = genderComboBox.getValue();
        boolean needsIsolation = needsIsolationCheckBox.isSelected();
        Bed bed = bedComboBox.getValue();
        String medicalCondition = medicalConditionArea.getText();
        
        try {
            // Add the patient
            Patient patient = careHomeService.addPatient(
                firstName,
                lastName,
                dateOfBirth,
                gender,
                medicalCondition,
                needsIsolation,
                bed
            );
            
            if (patient != null) {
                logger.info("Patient added: {}", patient.getFullName());
                closeDialog();
            } else {
                errorLabel.setText("Failed to add patient. The bed may already be occupied.");
                logger.warn("Failed to add patient. Bed may be occupied: {}", bed);
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
        // Check first name
        if (firstNameField.getText().isEmpty()) {
            errorLabel.setText("First name is required");
            return false;
        }
        
        // Check last name
        if (lastNameField.getText().isEmpty()) {
            errorLabel.setText("Last name is required");
            return false;
        }
        
        // Check date of birth
        if (dateOfBirthPicker.getValue() == null) {
            errorLabel.setText("Date of birth is required");
            return false;
        }
        
        // Check if date of birth is in the future
        if (dateOfBirthPicker.getValue().isAfter(LocalDate.now())) {
            errorLabel.setText("Date of birth cannot be in the future");
            return false;
        }
        
        // Check gender
        if (genderComboBox.getValue() == null) {
            errorLabel.setText("Gender is required");
            return false;
        }
        
        // Check bed
        if (bedComboBox.getValue() == null) {
            errorLabel.setText("Bed assignment is required");
            return false;
        }
        
        // Check medical condition
        if (medicalConditionArea.getText().isEmpty()) {
            errorLabel.setText("Medical condition is required");
            return false;
        }
        
        // Check if the bed is suitable for the patient
        Bed selectedBed = bedComboBox.getValue();
        Gender selectedGender = genderComboBox.getValue();
        boolean needsIsolation = needsIsolationCheckBox.isSelected();
        
        List<Bed> suitableBeds = careHomeService.getSuitableVacantBeds(selectedGender, needsIsolation);
        if (!suitableBeds.contains(selectedBed)) {
            errorLabel.setText("Selected bed is not suitable for this patient");
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

    /**
     * Update the bed combo box based on gender and isolation needs
     */
    @FXML
    public void updateBedOptions() {
        if (genderComboBox.getValue() != null) {
            Gender gender = genderComboBox.getValue();
            boolean needsIsolation = needsIsolationCheckBox.isSelected();
            
            List<Bed> suitableBeds = careHomeService.getSuitableVacantBeds(gender, needsIsolation);
            bedComboBox.setItems(FXCollections.observableArrayList(suitableBeds));
            
            if (!suitableBeds.isEmpty()) {
                bedComboBox.setValue(suitableBeds.get(0));
            } else {
                bedComboBox.setValue(null);
            }
        }
    }
}
