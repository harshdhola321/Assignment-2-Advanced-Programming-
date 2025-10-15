package org.example.service;

import org.example.model.ActionLog;
import org.example.model.Staff;
import org.example.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for logging actions in the system
 * Implemented as a Singleton
 */
public class LoggingService {
    private static LoggingService instance;
    private List<ActionLog> logs;

    private LoggingService() {
        logs = new ArrayList<>();
    }

    /**
     * Get the singleton instance of the logging service
     * @return The logging service instance
     */
    public static LoggingService getInstance() {
        if (instance == null) {
            instance = new LoggingService();
        }
        return instance;
    }

    /**
     * Log an action
     * @param action The action performed
     * @param staff The staff member who performed the action
     * @param details Details of the action
     * @return The created action log
     */
    public ActionLog logAction(String action, Staff staff, String details) {
        ActionLog log = new ActionLog(
            IdGenerator.generateId("LOG"),
            action,
            staff,
            LocalDateTime.now(),
            details
        );
        logs.add(log);
        return log;
    }

    /**
     * Get all logs
     * @return All action logs
     */
    public List<ActionLog> getAllLogs() {
        return new ArrayList<>(logs);
    }

    /**
     * Get logs for a specific staff member
     * @param staff The staff member
     * @return Logs for the staff member
     */
    public List<ActionLog> getLogsForStaff(Staff staff) {
        List<ActionLog> staffLogs = new ArrayList<>();
        for (ActionLog log : logs) {
            if (log.getStaff().getId().equals(staff.getId())) {
                staffLogs.add(log);
            }
        }
        return staffLogs;
    }

    /**
     * Get logs for a specific action
     * @param action The action
     * @return Logs for the action
     */
    public List<ActionLog> getLogsForAction(String action) {
        List<ActionLog> actionLogs = new ArrayList<>();
        for (ActionLog log : logs) {
            if (log.getAction().equals(action)) {
                actionLogs.add(log);
            }
        }
        return actionLogs;
    }

    /**
     * Clear all logs
     */
    public void clearLogs() {
        logs.clear();
    }
}
