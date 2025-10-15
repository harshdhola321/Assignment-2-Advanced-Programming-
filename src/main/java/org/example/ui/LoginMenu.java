package org.example.ui;

import org.example.model.Staff;
import org.example.service.AuthenticationService;

/**
 * Login menu for the Care Home System
 */
public class LoginMenu implements Menu {
    private final MenuManager menuManager;
    private final AuthenticationService authService;

    /**
     * Constructor
     */
    public LoginMenu() {
        menuManager = MenuManager.getInstance();
        authService = AuthenticationService.getInstance();
    }

    @Override
    public void display() {
        System.out.println("\n===================================");
        System.out.println("              LOGIN               ");
        System.out.println("===================================");
        System.out.println("Enter your credentials or 'back' to return to the main menu");
        System.out.println("===================================");
    }

    @Override
    public void handleInput(String input) {
        if ("back".equalsIgnoreCase(input)) {
            menuManager.goBack();
            return;
        }

        String username = menuManager.getInput("Username");

        if ("back".equalsIgnoreCase(username)) {
            menuManager.goBack();
            return;
        }

        String password = menuManager.getInput("Password");

        if ("back".equalsIgnoreCase(password)) {
            menuManager.goBack();
            return;
        }

        Staff staff = authService.login(username, password);

        if (staff != null) {
            menuManager.displayMessage("Login successful! Welcome, " + staff.getFullName());
            menuManager.goBack(); // Return to main menu
        } else {
            menuManager.displayMessage("Invalid username or password. Please try again.");
        }
    }

    @Override
    public String getTitle() {
        return "Login";
    }
}
