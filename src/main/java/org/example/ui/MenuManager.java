package org.example.ui;

import java.util.Scanner;
import java.util.Stack;

/**
 * Manages the menu system for the text-based interface
 */
public class MenuManager {
    private static MenuManager instance;
    private final Scanner scanner;
    private final Stack<Menu> menuStack;
    private boolean running;

    /**
     * Private constructor for singleton pattern
     */
    private MenuManager() {
        scanner = new Scanner(System.in);
        menuStack = new Stack<>();
        running = false;
    }

    /**
     * Get the singleton instance
     * @return The singleton instance
     */
    public static MenuManager getInstance() {
        if (instance == null) {
            instance = new MenuManager();
        }
        return instance;
    }

    /**
     * Start the menu system with the given menu as the main menu
     * @param mainMenu The main menu
     */
    public void start(Menu mainMenu) {
        running = true;
        menuStack.push(mainMenu);

        while (running && !menuStack.isEmpty()) {
            Menu currentMenu = menuStack.peek();
            currentMenu.display();

            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim();

            try {
                currentMenu.handleInput(input);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }

        System.out.println("Thank you for using the Care Home System!");
    }

    /**
     * Navigate to a new menu
     * @param menu The menu to navigate to
     */
    public void navigateTo(Menu menu) {
        menuStack.push(menu);
    }

    /**
     * Go back to the previous menu
     */
    public void goBack() {
        if (menuStack.size() > 1) {
            menuStack.pop();
        }
    }

    /**
     * Go back to the main menu
     */
    public void goToMainMenu() {
        while (menuStack.size() > 1) {
            menuStack.pop();
        }
    }

    /**
     * Exit the menu system
     */
    public void exit() {
        running = false;
    }

    /**
     * Get user input as a string
     * @param prompt The prompt to display
     * @return The user input
     */
    public String getInput(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    /**
     * Get user input as an integer
     * @param prompt The prompt to display
     * @return The user input as an integer
     */
    public int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * Display a message and wait for the user to press Enter
     * @param message The message to display
     */
    public void displayMessage(String message) {
        System.out.println(message);
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
}
