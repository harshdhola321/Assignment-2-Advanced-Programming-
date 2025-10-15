package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.model.Medication;
import org.example.model.Patient;
import org.example.model.Prescription;
import org.example.service.CareHomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Controller for the view prescriptions view
 */
public class ViewPrescriptionsController {
    private static final Logger logger = LoggerFactory.getLogger(ViewPrescriptionsController.class);
    
    @FXML
    private ComboBox<Patient> patientComboBox;
    
    @FXML
    private ListView<Prescription> prescriptionsListView;
    
    @FXML
    private ListView<Medication> medicationsListView;
    
    @FXML
    private TextArea notesTextArea;
    
    @FXML
    private Button closeButton;
    
    private CareHomeService careHomeService;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
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
                updatePrescriptionsList(newValue);
            } else {
                prescriptionsListView.setItems(FXCollections.observableArrayList());
                medicationsListView.setItems(FXCollections.observableArrayList());
                notesTextArea.clear();
            }
        });
        
        // Add listener to prescriptions list view
        prescriptionsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateMedicationsList(newValue);
                notesTextArea.setText(newValue.getNotes());
            } else {
                medicationsListView.setItems(FXCollections.observableArrayList());
                notesTextArea.clear();
            }
        });
        
        // Set cell factories for custom display
        prescriptionsListView.setCellFactory(param -> new javafx.scene.control.ListCell<Prescription>() {
            @Override
            protected void updateItem(Prescription item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatPrescription(item));
                }
            }
        });
        
        medicationsListView.setCellFactory(param -> new javafx.scene.control.ListCell<Medication>() {
            @Override
            protected void updateItem(Medication item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatMedication(item));
                }
            }
        });
    }
    
    /**
     * Update the prescriptions list for the selected patient
     * @param patient The selected patient
     */
    private void updatePrescriptionsList(Patient patient) {
        ObservableList<Prescription> prescriptions = FXCollections.observableArrayList(patient.getPrescriptions());
        prescriptionsListView.setItems(prescriptions);
    }
    
    /**
     * Update the medications list for the selected prescription
     * @param prescription The selected prescription
     */
    private void updateMedicationsList(Prescription prescription) {
        ObservableList<Medication> medications = FXCollections.observableArrayList(prescription.getMedications());
        medicationsListView.setItems(medications);
    }
    
    /**
     * Format a prescription for display
     * @param prescription The prescription to format
     * @return The formatted prescription string
     */
    private String formatPrescription(Prescription prescription) {
        return prescription.getPrescriptionDateTime().format(dateTimeFormatter) + 
               " - Dr. " + prescription.getDoctor().getLastName() + 
               " - " + prescription.getMedications().size() + " medication(s)";
    }
    
    /**
     * Format a medication for display
     * @param medication The medication to format
     * @return The formatted medication string
     */
    private String formatMedication(Medication medication) {
        return medication.getName() + " - " + medication.getDosage() + 
               " - Times: " + medication.getAdministrationTimes().stream()
                              .map(time -> time.format(DateTimeFormatter.ofPattern("HH:mm")))
                              .collect(Collectors.joining(", "));
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
