package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.Staff;
import org.example.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller for the login view
 */
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private AuthenticationService authService;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        authService = AuthenticationService.getInstance();
        errorLabel.setText("");
    }

    /**
     * Handle login button click
     * @param event The action event
     */
    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password");
            return;
        }

        Staff staff = authService.login(username, password);
        if (staff != null) {
            logger.info("User logged in: {}", staff.getUsername());
            openDashboard();
        } else {
            errorLabel.setText("Invalid username or password");
            logger.warn("Failed login attempt for username: {}", username);
        }
    }

    /**
     * Open the dashboard view
     */
    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            
            DashboardController controller = loader.getController();
            controller.initData();
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.setTitle("RMIT Care Home - Dashboard");
            stage.setResizable(true);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            logger.error("Error opening dashboard", e);
            errorLabel.setText("Error opening dashboard");
        }
    }
}
