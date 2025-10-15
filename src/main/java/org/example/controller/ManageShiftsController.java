package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.example.model.Shift;
import org.example.model.Staff;
import org.example.service.AuthenticationService;
import org.example.service.CareHomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the manage shifts view
 */
public class ManageShiftsController {
    private static final Logger logger = LoggerFactory.getLogger(ManageShiftsController.class);
    
    @FXML
    private ComboBox<Staff> staffComboBox;
    
    @FXML
    private ListView<String> shiftsListView;
    
    @FXML
    private ComboBox<DayOfWeek> dayComboBox;
    
    @FXML
    private ComboBox<LocalTime> startTimeComboBox;
    
    @FXML
    private ComboBox<LocalTime> endTimeComboBox;
    
    @FXML
    private Button addShiftButton;
    
    @FXML
    private Button removeShiftButton;
    
    @FXML
    private Button closeButton;
    
    private CareHomeService careHomeService;
    private AuthenticationService authService;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    
    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        careHomeService = CareHomeService.getInstance();
        authService = AuthenticationService.getInstance();
        
        // Initialize day combo box
        dayComboBox.setItems(FXCollections.observableArrayList(DayOfWeek.values()));
        
        // Initialize time combo boxes
        List<LocalTime> times = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            times.add(LocalTime.of(hour, 0));
        }
        startTimeComboBox.setItems(FXCollections.observableArrayList(times));
        endTimeComboBox.setItems(FXCollections.observableArrayList(times));
        
        // Initialize staff combo box
        ObservableList<Staff> staffList = FXCollections.observableArrayList(careHomeService.getAllStaff());
        staffComboBox.setItems(staffList);
        staffComboBox.setConverter(new StaffStringConverter());
        
        // Add listener to staff combo box
        staffComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateShiftsList(newValue);
            }
        });
    }
    
    /**
     * Update the shifts list for the selected staff member
     * @param staff The selected staff member
     */
    private void updateShiftsList(Staff staff) {
        ObservableList<String> shifts = FXCollections.observableArrayList();
        for (Shift shift : staff.getShifts()) {
            shifts.add(formatShift(shift));
        }
        shiftsListView.setItems(shifts);
    }
    
    /**
     * Format a shift for display
     * @param shift The shift to format
     * @return The formatted shift string
     */
    private String formatShift(Shift shift) {
        return shift.getDayOfWeek() + ": " + 
               shift.getStartTime().format(timeFormatter) + " - " + 
               shift.getEndTime().format(timeFormatter);
    }
    
    /**
     * Handle add shift button click
     * @param event The action event
     */
    @FXML
    public void handleAddShift(ActionEvent event) {
        Staff selectedStaff = staffComboBox.getValue();
        DayOfWeek selectedDay = dayComboBox.getValue();
        LocalTime selectedStartTime = startTimeComboBox.getValue();
        LocalTime selectedEndTime = endTimeComboBox.getValue();
        
        if (selectedStaff == null || selectedDay == null || selectedStartTime == null || selectedEndTime == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Missing Information", "Please select all fields");
            return;
        }
        
        try {
            Shift newShift = new Shift(selectedDay, selectedStartTime, selectedEndTime);
            selectedStaff.addShift(newShift);
            updateShiftsList(selectedStaff);
            
            // Save changes
            careHomeService.saveData();
            
            logger.info("Shift added for " + selectedStaff.getFullName());
        } catch (Exception e) {
            logger.error("Error adding shift", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error Adding Shift", e.getMessage());
        }
    }
    
    /**
     * Handle remove shift button click
     * @param event The action event
     */
    @FXML
    public void handleRemoveShift(ActionEvent event) {
        Staff selectedStaff = staffComboBox.getValue();
        String selectedShiftStr = shiftsListView.getSelectionModel().getSelectedItem();
        
        if (selectedStaff == null || selectedShiftStr == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No Selection", "Please select a staff member and a shift");
            return;
        }
        
        try {
            // Parse the selected shift string
            String[] parts = selectedShiftStr.split(": ");
            DayOfWeek day = DayOfWeek.valueOf(parts[0]);
            String[] times = parts[1].split(" - ");
            LocalTime startTime = LocalTime.parse(times[0], timeFormatter);
            LocalTime endTime = LocalTime.parse(times[1], timeFormatter);
            
            // Find and remove the shift
            Shift shiftToRemove = null;
            for (Shift shift : selectedStaff.getShifts()) {
                if (shift.getDayOfWeek() == day && 
                    shift.getStartTime().equals(startTime) && 
                    shift.getEndTime().equals(endTime)) {
                    shiftToRemove = shift;
                    break;
                }
            }
            
            if (shiftToRemove != null) {
                selectedStaff.removeShift(shiftToRemove);
                updateShiftsList(selectedStaff);
                
                // Save changes
                careHomeService.saveData();
                
                logger.info("Shift removed for " + selectedStaff.getFullName());
            }
        } catch (Exception e) {
            logger.error("Error removing shift", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error Removing Shift", e.getMessage());
        }
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
