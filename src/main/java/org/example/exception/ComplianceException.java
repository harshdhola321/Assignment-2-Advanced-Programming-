package org.example.exception;

/**
 * Exception thrown when compliance rules are violated
 */
public class ComplianceException extends Exception {
    public ComplianceException(String message) {
        super(message);
    }
}
