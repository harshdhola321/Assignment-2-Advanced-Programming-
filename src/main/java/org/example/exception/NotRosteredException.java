package org.example.exception;

/**
 * Exception thrown when a staff member attempts to perform an action when they are not rostered
 */
public class NotRosteredException extends Exception {
    public NotRosteredException(String message) {
        super(message);
    }
}
