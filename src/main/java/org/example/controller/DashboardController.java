package org.example.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.Staff;
import org.example.model.Ward;
import org.example.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.service.CareHomeService;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the dashboard view
 */
public class DashboardController {
    
    private final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private CareHomeService careHomeService;
    private AuthenticationService authService;
    
    @FXML
    private Label currentUserLabel;
    
    @FXML
    private Label userRoleLabel;
    
    @FXML
    private BorderPane ward1Container;
    
    @FXML
    private BorderPane ward2Container;
    
    @FXML
    private TabPane mainTabPane;
    
    /**
     * Initialize the controller with required services
     * @param careHomeService The care home service
     */
    public void initialize(CareHomeService careHomeService) {
        this.careHomeService = careHomeService;
        this.authService = AuthenticationService.getInstance();
        updateUserInfo();
        
        // Initialize ward views
        initializeWardViews();
        
        // Add listener to tab selection to refresh ward views when ward tab is selected
        if (mainTabPane != null) {
            mainTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
                if (newTab != null && newTab.getText().equals("Ward View")) {
                    refreshWardViews();
                }
            });
        }
    }
    
    /**
     * Initialize the controller with default services
     */
    public void initData() {
        this.careHomeService = CareHomeService.getInstance();
        this.authService = AuthenticationService.getInstance();
        updateUserInfo();
        
        // Initialize ward views
        initializeWardViews();
        
        // Add listener to tab selection to refresh ward views when ward tab is selected
        if (mainTabPane != null) {
            mainTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
                if (newTab != null && newTab.getText().equals("Ward View")) {
                    refreshWardViews();
                }
            });
        }
    }
    
    /**
     * Refresh the ward views
     * This can be called after operations that might change ward data
     */
    public void refreshWardViews() {
        Platform.runLater(this::initializeWardViews);
    }
    
    /**
     * Initialize the ward views
     */
    private void initializeWardViews() {
        try {
            // Get all wards
            List<Ward> wards = careHomeService.getAllWards();
            
            // Initialize Ward 1 view if available
            if (!wards.isEmpty() && ward1Container != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ward_view.fxml"));
                Parent wardView = loader.load();
                
                WardViewController controller = loader.getController();
                controller.initData(wards.get(0));
                
                ward1Container.setCenter(wardView);
            }
            
            // Initialize Ward 2 view if available
            if (wards.size() > 1 && ward2Container != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ward_view.fxml"));
                Parent wardView = loader.load();
                
                WardViewController controller = loader.getController();
                controller.initData(wards.get(1));
                
                ward2Container.setCenter(wardView);
            }
        } catch (IOException e) {
            logger.error("Error initializing ward views", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error initializing ward views", e.getMessage());
        }
    }
    
    /**
     * Update the user information displayed in the UI
     */
    private void updateUserInfo() {
        Staff currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            currentUserLabel.setText(currentUser.getFullName());
            userRoleLabel.setText(currentUser.getClass().getSimpleName());
        }
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
     * Handle logout menu item click
     * @param event The action event
     */
    @FXML
    public void handleLogout(ActionEvent event) {
        authService.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) currentUserLabel.getScene().getWindow();
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.setTitle("RMIT Care Home - Login");
            stage.setResizable(false);
            stage.centerOnScreen();
            
            logger.info("User logged out");
        } catch (IOException e) {
            logger.error("Error returning to login screen", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error returning to login screen", e.getMessage());
        }
    }
    
    /**
     * Handle exit menu item click
     * @param event The action event
     */
    @FXML
    public void handleExit(ActionEvent event) {
        careHomeService.saveData();
        Platform.exit();
    }
    
    /**
     * Handle add patient button click
     * @param event The action event
     */
    @FXML
    public void handleAddPatient(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_patient.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Patient");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh ward views after adding a patient
            refreshWardViews();
        } catch (IOException e) {
            logger.error("Error opening add patient dialog", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error opening add patient dialog", e.getMessage());
        }
    }
    
    /**
     * Handle add staff button click
     * @param event The action event
     */
    @FXML
    public void handleAddStaff(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_staff.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Staff");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            logger.error("Error opening add staff dialog", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error opening add staff dialog", e.getMessage());
        }
    }
    
    /**
     * Handle manage shifts button click
     * @param event The action event
     */
    @FXML
    public void handleManageShifts(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manage_shifts.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Manage Shifts");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            logger.error("Error opening manage shifts dialog", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error opening manage shifts dialog", e.getMessage());
        }
    }
    
    /**
     * Handle about menu item click
     * @param event The action event
     */
    @FXML
    public void handleAbout(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "About", "RMIT Care Home Management System", 
                "Version 1.0\nDeveloped for RMIT University\n© 2025 All rights reserved");
    }
    
    /**
     * Handle view patient button click
     * @param event The action event
     */
    @FXML
    public void handleViewPatient(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/view_patient.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("View Patient");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            logger.error("Error opening view patient dialog", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error opening view patient dialog", e.getMessage());
        }
    }
    
    /**
     * Handle move patient button click
     * @param event The action event
     */
    @FXML
    public void handleMovePatient(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/move_patient.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Move Patient");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh ward views after moving a patient
            refreshWardViews();
        } catch (IOException e) {
            logger.error("Error opening move patient dialog", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error opening move patient dialog", e.getMessage());
        }
    }
    
    /**
     * Handle discharge patient button click
     * @param event The action event
     */
    @FXML
    public void handleDischargePatient(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/discharge_patient.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Discharge Patient");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh ward views after discharging a patient
            refreshWardViews();
        } catch (IOException e) {
            logger.error("Error opening discharge patient dialog", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error opening discharge patient dialog", e.getMessage());
        }
    }
    
    /**
     * Handle edit staff button click
     * @param event The action event
     */
    @FXML
    public void handleEditStaff(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_staff.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Staff");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            logger.error("Error opening edit staff dialog", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error opening edit staff dialog", e.getMessage());
        }
    }

    /**
     * Handle add prescription button click
     * @param event The action event
     */
    @FXML
    public void handleAddPrescription(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_prescription.fxml"));
            Parent root = loader.load();
            
            AddPrescriptionController controller = loader.getController();
            controller.initData(careHomeService.getAllPatients());
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add Prescription");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            logger.error("Error opening add prescription dialog", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error opening add prescription dialog", e.getMessage());
        }
    }
    
    /**
     * Handle view prescriptions button click
     * @param event The action event
     */
    @FXML
    public void handleViewPrescriptions(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/view_prescriptions.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("View Prescriptions");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            logger.error("Error opening view prescriptions dialog", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error opening view prescriptions dialog", e.getMessage());
        }
    }

    /**
     * Handle administer medication button click
     * @param event The action event
     */
    @FXML
    public void handleAdministerMedication(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/administer_medication.fxml"));
            Parent root = loader.load();
            
            AdministerMedicationController controller = loader.getController();
            controller.initData(careHomeService.getAllPatients());
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Administer Medication");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            logger.error("Error opening administer medication dialog", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error opening administer medication dialog", e.getMessage());
        }
    }
    
    /**
     * Handle view administrations button click
     * @param event The action event
     */
    @FXML
    public void handleViewAdministrations(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/view_administrations.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("View Administrations");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            logger.error("Error opening view administrations dialog", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error opening view administrations dialog", e.getMessage());
        }
    }
    
    /**
     * Handle view logs button click
     * @param event The action event
     */
    @FXML
    public void handleViewLogs(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/view_logs.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("System Action Logs");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            logger.error("Error opening view logs dialog", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error opening view logs dialog", e.getMessage());
        }
    }
}
