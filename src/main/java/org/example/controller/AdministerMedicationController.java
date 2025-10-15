package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.exception.NotRosteredException;
import org.example.exception.UnauthorizedActionException;
import org.example.model.Medication;
import org.example.model.MedicationAdministration;
import org.example.model.Patient;
import org.example.model.Prescription;
import org.example.service.AuthenticationService;
import org.example.service.CareHomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the administer medication view
 */
public class AdministerMedicationController {
    private static final Logger logger = LoggerFactory.getLogger(AdministerMedicationController.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private ComboBox<Patient> patientComboBox;

    @FXML
    private TableView<Prescription> prescriptionsTable;

    @FXML
    private TableColumn<Prescription, String> prescriptionDateColumn;

    @FXML
    private TableColumn<Prescription, String> prescriptionDoctorColumn;

    @FXML
    private TableColumn<Prescription, String> prescriptionNotesColumn;

    @FXML
    private TableView<Medication> medicationsTable;

    @FXML
    private TableColumn<Medication, String> medicationNameColumn;

    @FXML
    private TableColumn<Medication, String> medicationDosageColumn;

    @FXML
    private TableColumn<Medication, String> medicationInstructionsColumn;

    @FXML
    private TableColumn<Medication, String> medicationTimesColumn;

    @FXML
    private TextArea notesArea;

    @FXML
    private Label errorLabel;

    @FXML
    private Button administerButton;

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
        
        // Add listener to patient combo box to update prescriptions
        patientComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updatePrescriptions(newVal);
            } else {
                prescriptionsTable.getItems().clear();
                medicationsTable.getItems().clear();
            }
        });
        
        // Initialize prescriptions table
        prescriptionDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPrescriptionDateTime().toLocalDate().toString()));
        
        prescriptionDoctorColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDoctor().getFullName()));
        
        prescriptionNotesColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNotes()));
        
        // Add listener to prescriptions table to update medications
        prescriptionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateMedications(newVal);
            } else {
                medicationsTable.getItems().clear();
            }
        });
        
        // Initialize medications table
        medicationNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getName()));
        
        medicationDosageColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDosage()));
        
        medicationInstructionsColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getInstructions()));
        
        medicationTimesColumn.setCellValueFactory(cellData -> {
            List<LocalTime> times = cellData.getValue().getAdministrationTimes();
            String timesStr = times.stream()
                .map(time -> time.format(TIME_FORMATTER))
                .collect(Collectors.joining(", "));
            return new SimpleStringProperty(timesStr);
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
     * Update prescriptions for a patient
     * @param patient The patient
     */
    private void updatePrescriptions(Patient patient) {
        List<Prescription> prescriptions = patient.getPrescriptions();
        prescriptionsTable.setItems(FXCollections.observableArrayList(prescriptions));
        
        if (!prescriptions.isEmpty()) {
            prescriptionsTable.getSelectionModel().select(0);
        }
    }

    /**
     * Update medications for a prescription
     * @param prescription The prescription
     */
    private void updateMedications(Prescription prescription) {
        List<Medication> medications = prescription.getMedications();
        medicationsTable.setItems(FXCollections.observableArrayList(medications));
    }

    /**
     * Handle administer button click
     * @param event The action event
     */
    @FXML
    public void handleAdminister(ActionEvent event) {
        // Validate input
        if (!validateInput()) {
            return;
        }
        
        Patient patient = patientComboBox.getValue();
        Medication medication = medicationsTable.getSelectionModel().getSelectedItem();
        String notes = notesArea.getText().trim();
        
        try {
            // Administer the medication
            MedicationAdministration administration = careHomeService.administerMedication(
                medication,
                patient,
                notes
            );
            
            logger.info("Medication administered: {} to {}", medication.getName(), patient.getFullName());
            showAlert(Alert.AlertType.INFORMATION, "Success", "Medication Administered",
                    "Medication " + medication.getName() + " has been administered to " + patient.getFullName());
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
     * Validate input
     * @return true if input is valid, false otherwise
     */
    private boolean validateInput() {
        // Check patient
        if (patientComboBox.getValue() == null) {
            errorLabel.setText("Patient is required");
            return false;
        }
        
        // Check prescription
        if (prescriptionsTable.getSelectionModel().getSelectedItem() == null) {
            errorLabel.setText("Prescription is required");
            return false;
        }
        
        // Check medication
        if (medicationsTable.getSelectionModel().getSelectedItem() == null) {
            errorLabel.setText("Medication is required");
            return false;
        }
        
        // Check notes
        if (notesArea.getText().trim().isEmpty()) {
            errorLabel.setText("Administration notes are required");
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
}
