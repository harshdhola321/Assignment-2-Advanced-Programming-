package org.example.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.example.model.Bed;
import org.example.model.Gender;
import org.example.model.Room;
import org.example.model.Ward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the ward view
 */
public class WardViewController {
    private static final Logger logger = LoggerFactory.getLogger(WardViewController.class);

    @FXML
    private Label wardNameLabel;

    @FXML
    private GridPane roomsGrid;

    private Ward ward;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        // This will be called when the FXML is loaded
    }

    /**
     * Initialize data after FXML is loaded
     * @param ward The ward to display
     */
    public void initData(Ward ward) {
        this.ward = ward;
        wardNameLabel.setText(ward.getName());
        
        // Display the rooms and beds
        displayRooms();
    }

    /**
     * Display the rooms and beds in the ward
     */
    private void displayRooms() {
        roomsGrid.getChildren().clear();
        
        int rowIndex = 0;
        int colIndex = 0;
        int maxCols = 3; // Maximum number of columns in the grid
        
        for (Room room : ward.getRooms()) {
            // Create a pane for the room
            BorderPane roomPane = createRoomPane(room);
            
            // Add the room pane to the grid
            roomsGrid.add(roomPane, colIndex, rowIndex);
            
            // Update the row and column indices
            colIndex++;
            if (colIndex >= maxCols) {
                colIndex = 0;
                rowIndex++;
            }
        }
    }

    /**
     * Create a pane for a room
     * @param room The room
     * @return The room pane
     */
    private BorderPane createRoomPane(Room room) {
        BorderPane roomPane = new BorderPane();
        roomPane.setPrefSize(250, 200);
        roomPane.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-background-color: #f8f8f8;");
        
        // Create a label for the room number
        Label roomLabel = new Label("Room " + room.getNumber());
        roomLabel.setFont(new Font("System Bold", 14));
        roomLabel.setPadding(new Insets(5));
        roomPane.setTop(roomLabel);
        
        // Create a grid for the beds
        GridPane bedsGrid = new GridPane();
        bedsGrid.setAlignment(Pos.CENTER);
        bedsGrid.setHgap(10);
        bedsGrid.setVgap(10);
        bedsGrid.setPadding(new Insets(10));
        
        // Add the beds to the grid
        int numBeds = room.getNumberOfBeds();
        int numRows = (int) Math.ceil(Math.sqrt(numBeds));
        int numCols = (int) Math.ceil((double) numBeds / numRows);
        
        int bedIndex = 0;
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (bedIndex < numBeds) {
                    Bed bed = room.getBeds().get(bedIndex);
                    StackPane bedPane = createBedPane(bed);
                    bedsGrid.add(bedPane, col, row);
                    bedIndex++;
                }
            }
        }
        
        roomPane.setCenter(bedsGrid);
        
        return roomPane;
    }

    /**
     * Create a pane for a bed
     * @param bed The bed
     * @return The bed pane
     */
    private StackPane createBedPane(Bed bed) {
        StackPane bedPane = new StackPane();
        bedPane.setPrefSize(100, 60);
        
        // Create a rectangle for the bed
        Rectangle bedRect = new Rectangle(100, 60);
        bedRect.setArcWidth(10);
        bedRect.setArcHeight(10);
        
        // Set the color based on occupancy and gender
        if (bed.isOccupied()) {
            if (bed.getPatient().getGender() == Gender.MALE) {
                bedRect.setFill(Color.DARKBLUE); // More vibrant blue
            } else {
                bedRect.setFill(Color.RED); // More vibrant pink
            }
        } else {
            bedRect.setFill(Color.WHITE);
        }
        
        bedRect.setStroke(Color.BLACK);
        
        // Create a label for the bed
        VBox labelBox = new VBox();
        labelBox.setAlignment(Pos.CENTER);
        
        Label bedLabel = new Label(bed.getName());
        bedLabel.setFont(new Font("System Bold", 12));
        
        Label patientLabel = new Label(bed.isOccupied() ? bed.getPatient().getFullName() : "Vacant");
        patientLabel.setFont(new Font("System", 10));
        
        // Set text color for better visibility
        if (bed.isOccupied() && bed.getPatient().getGender() == Gender.MALE) {
            bedLabel.setTextFill(Color.WHITE);
            patientLabel.setTextFill(Color.WHITE);
        }
        
        labelBox.getChildren().addAll(bedLabel, patientLabel);
        
        // Add the rectangle and label to the pane
        bedPane.getChildren().addAll(bedRect, labelBox);
        
        // Add a click handler to show patient details
        bedPane.setOnMouseClicked(event -> {
            if (bed.isOccupied()) {
                showPatientDetails(bed);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Vacant Bed", "Bed " + bed.getName(), "This bed is vacant.");
            }
        });
        
        return bedPane;
    }

    /**
     * Show patient details
     * @param bed The bed containing the patient
     */
    private void showPatientDetails(Bed bed) {
        if (!bed.isOccupied()) {
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("Name: ").append(bed.getPatient().getFullName()).append("\n");
        details.append("Gender: ").append(bed.getPatient().getGender()).append("\n");
        details.append("Date of Birth: ").append(bed.getPatient().getDateOfBirth()).append("\n");
        details.append("Medical Condition: ").append(bed.getPatient().getMedicalCondition()).append("\n");
        details.append("Needs Isolation: ").append(bed.getPatient().isNeedsIsolation() ? "Yes" : "No").append("\n");
        details.append("Admission Date: ").append(bed.getPatient().getAdmissionDate()).append("\n");
        
        details.append("\nPrescriptions: ").append(bed.getPatient().getPrescriptions().size()).append("\n");
        
        showAlert(Alert.AlertType.INFORMATION, "Patient Details", 
                "Patient in " + bed.toString(), details.toString());
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
