package org.example.ui;

/**
 * Interface for all menus in the system
 */
public interface Menu {
    /**
     * Display the menu options
     */
    void display();

    /**
     * Handle user input
     * @param input The user input
     */
    void handleInput(String input);

    /**
     * Get the title of the menu
     * @return The menu title
     */
    String getTitle();
}
