package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.Doctor;
import org.example.model.Gender;
import org.example.model.Manager;
import org.example.model.Nurse;
import org.example.model.Staff;
import org.example.service.AuthenticationService;
import org.example.service.CareHomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

/**
 * Controller for the edit staff view
 */
public class EditStaffController {
    private static final Logger logger = LoggerFactory.getLogger(EditStaffController.class);
    
    @FXML
    private ComboBox<Staff> staffComboBox;
    
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
    private TextField specializationField;
    
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
        
        // Initialize staff combo box
        ObservableList<Staff> staffList = FXCollections.observableArrayList(careHomeService.getAllStaff());
        staffComboBox.setItems(staffList);
        staffComboBox.setConverter(new StaffStringConverter());
        
        // Add listener to staff combo box
        staffComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateFields(newValue);
            }
        });
    }
    
    /**
     * Populate the fields with the selected staff member's data
     * @param staff The selected staff member
     */
    private void populateFields(Staff staff) {
        firstNameField.setText(staff.getFirstName());
        lastNameField.setText(staff.getLastName());
        dateOfBirthPicker.setValue(staff.getDateOfBirth());
        genderComboBox.setValue(staff.getGender());
        usernameField.setText(staff.getUsername());
        passwordField.setText(staff.getPassword());
        
        if (staff instanceof Doctor) {
            specializationField.setText(((Doctor) staff).getSpecialization());
        } else if (staff instanceof Nurse) {
            specializationField.setText(((Nurse) staff).getQualification());
        } else if (staff instanceof Manager) {
            specializationField.setText(((Manager) staff).getDepartment());
        }
    }
    
    /**
     * Handle save button click
     * @param event The action event
     */
    @FXML
    public void handleSave(ActionEvent event) {
        Staff selectedStaff = staffComboBox.getValue();
        
        if (selectedStaff == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No Selection", "Please select a staff member");
            return;
        }
        
        try {
            // Update staff details
            selectedStaff.setFirstName(firstNameField.getText());
            selectedStaff.setLastName(lastNameField.getText());
            selectedStaff.setDateOfBirth(dateOfBirthPicker.getValue());
            selectedStaff.setGender(genderComboBox.getValue());
            selectedStaff.setPassword(passwordField.getText());
            
            // Update specialization based on staff type
            if (selectedStaff instanceof Doctor) {
                ((Doctor) selectedStaff).setSpecialization(specializationField.getText());
            } else if (selectedStaff instanceof Nurse) {
                ((Nurse) selectedStaff).setQualification(specializationField.getText());
            } else if (selectedStaff instanceof Manager) {
                ((Manager) selectedStaff).setDepartment(specializationField.getText());
            }
            
            // Update staff in the system
            careHomeService.updateStaff(selectedStaff);
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Staff Updated", 
                    "Staff member " + selectedStaff.getFullName() + " has been updated");
            
            logger.info("Staff member " + selectedStaff.getFullName() + " updated");
            
            // Close the dialog
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        } catch (UnauthorizedActionException e) {
            logger.warn("Unauthorized: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Unauthorized", e.getMessage());
        } catch (NotRosteredException e) {
            logger.warn("Not rostered: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Not Rostered", e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating staff", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error Updating Staff", e.getMessage());
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
     * Converter for displaying staff in combo box
     */
    private static class StaffStringConverter extends javafx.util.StringConverter<Staff> {
        @Override
        public String toString(Staff staff) {
            return staff == null ? "" : staff.getFullName() + " (" + staff.getClass().getSimpleName() + ")";
        }
        
        @Override
        public Staff fromString(String string) {
            return null; // Not needed for this use case
        }
    }
}
