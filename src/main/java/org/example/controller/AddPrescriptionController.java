package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.Medication;
import org.example.model.Patient;
import org.example.model.Prescription;
import org.example.service.AuthenticationService;
import org.example.service.CareHomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the add prescription view
 */
public class AddPrescriptionController {
    private static final Logger logger = LoggerFactory.getLogger(AddPrescriptionController.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private ComboBox<Patient> patientComboBox;

    @FXML
    private TextArea notesArea;

    @FXML
    private TextField medicationNameField;

    @FXML
    private TextField dosageField;

    @FXML
    private TextField instructionsField;

    @FXML
    private TextField timeField;

    @FXML
    private Button addTimeButton;

    @FXML
    private TableView<LocalTime> timesTable;

    @FXML
    private TableColumn<LocalTime, String> timeColumn;

    @FXML
    private TableColumn<LocalTime, Button> removeColumn;

    @FXML
    private Button addMedicationButton;

    @FXML
    private TableView<MedicationEntry> medicationsTable;

    @FXML
    private TableColumn<MedicationEntry, String> nameColumn;

    @FXML
    private TableColumn<MedicationEntry, String> dosageColumn;

    @FXML
    private TableColumn<MedicationEntry, String> instructionsColumn;

    @FXML
    private TableColumn<MedicationEntry, String> timesCountColumn;

    @FXML
    private TableColumn<MedicationEntry, Button> removeMedColumn;

    @FXML
    private Label errorLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private CareHomeService careHomeService;
    private AuthenticationService authService;
    private ObservableList<LocalTime> administrationTimes;
    private ObservableList<MedicationEntry> medications;

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
        
        // Initialize administration times table
        administrationTimes = FXCollections.observableArrayList();
        timesTable.setItems(administrationTimes);
        
        timeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().format(TIME_FORMATTER)));
        
        removeColumn.setCellFactory(param -> new TableCell<LocalTime, Button>() {
            private final Button removeButton = new Button("X");
            
            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                    removeButton.setOnAction(event -> {
                        LocalTime time = getTableView().getItems().get(getIndex());
                        administrationTimes.remove(time);
                    });
                }
            }
        });
        
        // Initialize medications table
        medications = FXCollections.observableArrayList();
        medicationsTable.setItems(medications);
        
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dosageColumn.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        instructionsColumn.setCellValueFactory(new PropertyValueFactory<>("instructions"));
        timesCountColumn.setCellValueFactory(new PropertyValueFactory<>("timesCount"));
        
        removeMedColumn.setCellFactory(param -> new TableCell<MedicationEntry, Button>() {
            private final Button removeButton = new Button("X");
            
            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                    removeButton.setOnAction(event -> {
                        MedicationEntry entry = getTableView().getItems().get(getIndex());
                        medications.remove(entry);
                    });
                }
            }
        });
        
        // Clear error label
        errorLabel.setText("");
    }

    /**
     * Initialize the controller with a list of patients
     * @param patients The list of patients
     */
    public void initData(List<Patient> patients) {
        patientComboBox.setItems(FXCollections.observableArrayList(patients));
    }

    /**
     * Handle add time button click
     * @param event The action event
     */
    @FXML
    public void handleAddTime(ActionEvent event) {
        String timeStr = timeField.getText().trim();
        
        if (timeStr.isEmpty()) {
            errorLabel.setText("Please enter a time");
            return;
        }
        
        try {
            LocalTime time = LocalTime.parse(timeStr, TIME_FORMATTER);
            
            // Check if time already exists
            if (administrationTimes.contains(time)) {
                errorLabel.setText("This time is already added");
                return;
            }
            
            administrationTimes.add(time);
            timeField.clear();
            errorLabel.setText("");
        } catch (DateTimeParseException e) {
            errorLabel.setText("Invalid time format. Please use HH:MM format (e.g., 09:30)");
        }
    }

    /**
     * Handle add medication button click
     * @param event The action event
     */
    @FXML
    public void handleAddMedication(ActionEvent event) {
        // Validate medication input
        if (!validateMedicationInput()) {
            return;
        }
        
        String name = medicationNameField.getText().trim();
        String dosage = dosageField.getText().trim();
        String instructions = instructionsField.getText().trim();
        
        // Create a copy of the current administration times
        List<LocalTime> times = new ArrayList<>(administrationTimes);
        
        // Add medication to the table
        medications.add(new MedicationEntry(name, dosage, instructions, times));
        
        // Clear medication fields
        medicationNameField.clear();
        dosageField.clear();
        instructionsField.clear();
        administrationTimes.clear();
        errorLabel.setText("");
    }

    /**
     * Handle save button click
     * @param event The action event
     */
    @FXML
    public void handleSave(ActionEvent event) {
        // Validate prescription input
        if (!validatePrescriptionInput()) {
            return;
        }
        
        Patient patient = patientComboBox.getValue();
        String notes = notesArea.getText().trim();
        
        try {
            // Create prescription
            Prescription prescription = careHomeService.addPrescription(patient, notes);
            
            // Add medications to the prescription
            for (MedicationEntry entry : medications) {
                Medication medication = careHomeService.addMedicationToPrescription(
                    prescription,
                    entry.getName(),
                    entry.getDosage(),
                    entry.getInstructions()
                );
                
                // Add administration times to the medication
                for (LocalTime time : entry.getTimes()) {
                    careHomeService.addAdministrationTimeToMedication(medication, time);
                }
            }
            
            logger.info("Prescription added for patient: {}", patient.getFullName());
            closeDialog();
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
     * Validate medication input
     * @return true if input is valid, false otherwise
     */
    private boolean validateMedicationInput() {
        // Check medication name
        if (medicationNameField.getText().trim().isEmpty()) {
            errorLabel.setText("Medication name is required");
            return false;
        }
        
        // Check dosage
        if (dosageField.getText().trim().isEmpty()) {
            errorLabel.setText("Dosage is required");
            return false;
        }
        
        // Check instructions
        if (instructionsField.getText().trim().isEmpty()) {
            errorLabel.setText("Instructions are required");
            return false;
        }
        
        // Check if at least one administration time is added
        if (administrationTimes.isEmpty()) {
            errorLabel.setText("At least one administration time is required");
            return false;
        }
        
        return true;
    }

    /**
     * Validate prescription input
     * @return true if input is valid, false otherwise
     */
    private boolean validatePrescriptionInput() {
        // Check patient
        if (patientComboBox.getValue() == null) {
            errorLabel.setText("Patient is required");
            return false;
        }
        
        // Check notes
        if (notesArea.getText().trim().isEmpty()) {
            errorLabel.setText("Prescription notes are required");
            return false;
        }
        
        // Check if at least one medication is added
        if (medications.isEmpty()) {
            errorLabel.setText("At least one medication is required");
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
     * Class to represent a medication entry in the table
     */
    public static class MedicationEntry {
        private final String name;
        private final String dosage;
        private final String instructions;
        private final List<LocalTime> times;

        public MedicationEntry(String name, String dosage, String instructions, List<LocalTime> times) {
            this.name = name;
            this.dosage = dosage;
            this.instructions = instructions;
            this.times = new ArrayList<>(times);
        }

        public String getName() {
            return name;
        }

        public String getDosage() {
            return dosage;
        }

        public String getInstructions() {
            return instructions;
        }

        public List<LocalTime> getTimes() {
            return times;
        }

        public String getTimesCount() {
            return times.size() + " times";
        }
    }
}
