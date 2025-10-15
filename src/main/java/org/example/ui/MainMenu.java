package org.example.ui;

import org.example.service.AuthenticationService;
import org.example.service.CareHomeService;

/**
 * Main menu for the Care Home System
 */
public class MainMenu implements Menu {
    private final MenuManager menuManager;
    private final AuthenticationService authService;
    private final CareHomeService careHomeService;

    /**
     * Constructor
     */
    public MainMenu() {
        menuManager = MenuManager.getInstance();
        authService = AuthenticationService.getInstance();
        careHomeService = CareHomeService.getInstance();
    }

    @Override
    public void display() {
        System.out.println("\n===================================");
        System.out.println("       RMIT CARE HOME SYSTEM       ");
        System.out.println("===================================");

        if (authService.isLoggedIn()) {
            System.out.println("Logged in as: " + authService.getCurrentUser().getFullName());
        } else {
            System.out.println("Not logged in");
        }

        System.out.println("\nMain Menu:");
        System.out.println("1. " + (authService.isLoggedIn() ? "Logout" : "Login"));

        if (authService.isLoggedIn()) {
            System.out.println("2. Staff Management");
            System.out.println("3. Patient Management");
            System.out.println("4. Medication Management");
            System.out.println("5. Ward Management");
        }

        System.out.println("0. Exit");
        System.out.println("===================================");
    }

    @Override
    public void handleInput(String input) {
        switch (input) {
            case "1":
                if (authService.isLoggedIn()) {
                    authService.logout();
                    menuManager.displayMessage("Logged out successfully");
                } else {
                    menuManager.navigateTo(new LoginMenu());
                }
                break;
            case "2":
                if (authService.isLoggedIn()) {
                    menuManager.navigateTo(new StaffMenu());
                } else {
                    menuManager.displayMessage("Please login first");
                }
                break;
            case "3":
                if (authService.isLoggedIn()) {
                    menuManager.navigateTo(new PatientMenu());
                } else {
                    menuManager.displayMessage("Please login first");
                }
                break;
            case "4":
                if (authService.isLoggedIn()) {
                    menuManager.navigateTo(new MedicationMenu());
                } else {
                    menuManager.displayMessage("Please login first");
                }
                break;
            case "5":
                if (authService.isLoggedIn()) {
                    menuManager.navigateTo(new WardMenu());
                } else {
                    menuManager.displayMessage("Please login first");
                }
                break;
            case "0":
                if (authService.isLoggedIn()) {
                    careHomeService.saveData();
                }
                menuManager.exit();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                break;
        }
    }

    @Override
    public String getTitle() {
        return "Main Menu";
    }
}
