package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.model.MedicationAdministration;
import org.example.model.Patient;
import org.example.service.CareHomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Controller for the view administrations view
 */
public class ViewAdministrationsController {
    private static final Logger logger = LoggerFactory.getLogger(ViewAdministrationsController.class);
    
    @FXML
    private ComboBox<Patient> patientComboBox;
    
    @FXML
    private TableView<MedicationAdministration> administrationsTableView;
    
    @FXML
    private TableColumn<MedicationAdministration, LocalDateTime> dateTimeColumn;
    
    @FXML
    private TableColumn<MedicationAdministration, String> medicationColumn;
    
    @FXML
    private TableColumn<MedicationAdministration, String> nurseColumn;
    
    @FXML
    private TableColumn<MedicationAdministration, String> notesColumn;
    
    @FXML
    private TextArea notesTextArea;
    
    @FXML
    private Button closeButton;
    
    private CareHomeService careHomeService;
    
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
        
        // Configure table columns
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("administrationDateTime"));
        
        // Custom cell factories for complex properties
        medicationColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getMedication().getName() + " " + 
                cellData.getValue().getMedication().getDosage()
            )
        );
        
        nurseColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getNurse().getFullName()
            )
        );
        
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        
        // Add listener to patient combo box
        patientComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateAdministrationsTable(newValue);
            } else {
                administrationsTableView.setItems(FXCollections.observableArrayList());
                notesTextArea.clear();
            }
        });
        
        // Add listener to administrations table
        administrationsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                notesTextArea.setText(newValue.getNotes());
            } else {
                notesTextArea.clear();
            }
        });
    }
    
    /**
     * Update the administrations table for the selected patient
     * @param patient The selected patient
     */
    private void updateAdministrationsTable(Patient patient) {
        ObservableList<MedicationAdministration> administrations = 
            FXCollections.observableArrayList(patient.getMedicationAdministrations());
        administrationsTableView.setItems(administrations);
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
