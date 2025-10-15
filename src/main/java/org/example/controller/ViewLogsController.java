package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.model.ActionLog;
import org.example.model.Staff;
import org.example.service.AuthenticationService;
import org.example.service.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller for the view logs screen
 */
public class ViewLogsController {
    private final Logger logger = LoggerFactory.getLogger(ViewLogsController.class);
    private LoggingService loggingService;
    private AuthenticationService authService;
    private ObservableList<ActionLog> allLogs;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    private TableView<ActionLog> logsTableView;

    @FXML
    private TableColumn<ActionLog, String> idColumn;

    @FXML
    private TableColumn<ActionLog, String> timestampColumn;

    @FXML
    private TableColumn<ActionLog, String> staffColumn;

    @FXML
    private TableColumn<ActionLog, String> actionColumn;

    @FXML
    private TableColumn<ActionLog, String> detailsColumn;

    @FXML
    private ComboBox<String> staffComboBox;

    @FXML
    private ComboBox<String> actionComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Label totalLogsLabel;

    @FXML
    private Button filterButton;

    @FXML
    private Button clearFilterButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Button closeButton;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        loggingService = LoggingService.getInstance();
        authService = AuthenticationService.getInstance();
        
        // Configure table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        timestampColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTimestamp().format(formatter)));
        staffColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStaff().getFullName()));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
        
        // Load logs
        loadLogs();
        
        // Populate filter comboboxes
        populateFilterComboBoxes();
    }

    /**
     * Load all logs into the table
     */
    private void loadLogs() {
        List<ActionLog> logs = loggingService.getAllLogs();
        allLogs = FXCollections.observableArrayList(logs);
        logsTableView.setItems(allLogs);
        updateTotalLogsLabel();
    }
    
    /**
     * Populate the filter comboboxes with available options
     */
    private void populateFilterComboBoxes() {
        // Populate staff filter
        Set<String> staffNames = new HashSet<>();
        staffNames.add("All Staff");
        
        // Populate action filter
        Set<String> actions = new HashSet<>();
        actions.add("All Actions");
        
        for (ActionLog log : allLogs) {
            staffNames.add(log.getStaff().getFullName());
            actions.add(log.getAction());
        }
        
        staffComboBox.setItems(FXCollections.observableArrayList(staffNames));
        staffComboBox.getSelectionModel().selectFirst();
        
        actionComboBox.setItems(FXCollections.observableArrayList(actions));
        actionComboBox.getSelectionModel().selectFirst();
    }
    
    /**
     * Update the total logs label
     */
    private void updateTotalLogsLabel() {
        totalLogsLabel.setText("Total Logs: " + logsTableView.getItems().size());
    }
    
    /**
     * Handle filter button click
     * @param event The action event
     */
    @FXML
    public void handleFilter(ActionEvent event) {
        String selectedStaff = staffComboBox.getValue();
        String selectedAction = actionComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();
        
        List<ActionLog> filteredLogs = allLogs.stream()
            .filter(log -> "All Staff".equals(selectedStaff) || log.getStaff().getFullName().equals(selectedStaff))
            .filter(log -> "All Actions".equals(selectedAction) || log.getAction().equals(selectedAction))
            .filter(log -> selectedDate == null || 
                   (log.getTimestamp().toLocalDate().equals(selectedDate)))
            .collect(Collectors.toList());
        
        logsTableView.setItems(FXCollections.observableArrayList(filteredLogs));
        updateTotalLogsLabel();
    }
    
    /**
     * Handle clear filter button click
     * @param event The action event
     */
    @FXML
    public void handleClearFilter(ActionEvent event) {
        staffComboBox.getSelectionModel().selectFirst();
        actionComboBox.getSelectionModel().selectFirst();
        datePicker.setValue(null);
        logsTableView.setItems(allLogs);
        updateTotalLogsLabel();
    }
    
    /**
     * Handle refresh button click
     * @param event The action event
     */
    @FXML
    public void handleRefresh(ActionEvent event) {
        loadLogs();
        populateFilterComboBoxes();
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
}
