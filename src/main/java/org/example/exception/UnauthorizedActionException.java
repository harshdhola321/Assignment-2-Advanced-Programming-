package org.example.exception;

/**
 * Exception thrown when a staff member attempts to perform an action they are not authorized for
 */
public class UnauthorizedActionException extends Exception {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
