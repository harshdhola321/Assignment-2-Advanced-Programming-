package org.example.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.*;
import org.example.service.AuthenticationService;
import org.example.service.CareHomeService;
import org.example.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

/**
 * Controller for the add staff view
 */
public class AddStaffController {
    private static final Logger logger = LoggerFactory.getLogger(AddStaffController.class);

    @FXML
    private ComboBox<String> staffTypeComboBox;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private DatePicker dateOfBirthPicker;

    @FXML
    private ComboBox<Gender> genderComboBox;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label specialtyLabel;

    @FXML
    private TextField specialtyField;

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
        
        // Initialize staff type combo box
        staffTypeComboBox.setItems(FXCollections.observableArrayList("Doctor", "Nurse", "Manager"));
        staffTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateSpecialtyLabel(newVal);
            }
        });
        staffTypeComboBox.setValue("Doctor");
        
        // Initialize gender combo box
        genderComboBox.setItems(FXCollections.observableArrayList(Gender.values()));
        
        // Clear error label
        errorLabel.setText("");
    }

    /**
     * Update the specialty label based on staff type
     * @param staffType The selected staff type
     */
    private void updateSpecialtyLabel(String staffType) {
        switch (staffType) {
            case "Doctor":
                specialtyLabel.setText("Specialization:");
                break;
            case "Nurse":
                specialtyLabel.setText("Qualification:");
                break;
            case "Manager":
                specialtyLabel.setText("Department:");
                break;
        }
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
        String staffType = staffTypeComboBox.getValue();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        LocalDate dateOfBirth = dateOfBirthPicker.getValue();
        Gender gender = genderComboBox.getValue();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String specialty = specialtyField.getText();
        
        try {
            // Create staff based on type
            Staff staff;
            switch (staffType) {
                case "Doctor":
                    staff = new Doctor(
                        IdGenerator.generateId("STAFF"),
                        firstName,
                        lastName,
                        dateOfBirth,
                        gender,
                        username,
                        password,
                        specialty
                    );
                    break;
                case "Nurse":
                    staff = new Nurse(
                        IdGenerator.generateId("STAFF"),
                        firstName,
                        lastName,
                        dateOfBirth,
                        gender,
                        username,
                        password,
                        specialty
                    );
                    break;
                case "Manager":
                    staff = new Manager(
                        IdGenerator.generateId("STAFF"),
                        firstName,
                        lastName,
                        dateOfBirth,
                        gender,
                        username,
                        password,
                        specialty
                    );
                    break;
                default:
                    errorLabel.setText("Invalid staff type");
                    return;
            }
            
            // Add the staff
            boolean added = careHomeService.addStaff(staff);
            
            if (added) {
                logger.info("Staff added: {} ({})", staff.getFullName(), staffType);
                closeDialog();
            } else {
                errorLabel.setText("Failed to add staff. Username may already exist.");
                logger.warn("Failed to add staff. Username may already exist: {}", username);
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
        // Check staff type
        if (staffTypeComboBox.getValue() == null) {
            errorLabel.setText("Staff type is required");
            return false;
        }
        
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
        
        // Check username
        if (usernameField.getText().isEmpty()) {
            errorLabel.setText("Username is required");
            return false;
        }
        
        // Check password
        if (passwordField.getText().isEmpty()) {
            errorLabel.setText("Password is required");
            return false;
        }
        
        // Check specialty
        if (specialtyField.getText().isEmpty()) {
            errorLabel.setText(specialtyLabel.getText().replace(":", "") + " is required");
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
