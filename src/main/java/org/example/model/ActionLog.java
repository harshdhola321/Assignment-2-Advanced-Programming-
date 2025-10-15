package org.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Class representing a log of an action performed in the system
 */
public class ActionLog implements Serializable {
    private String id;
    private String action;
    private Staff staff;
    private LocalDateTime timestamp;
    private String details;

    public ActionLog(String id, String action, Staff staff, LocalDateTime timestamp, String details) {
        this.id = id;
        this.action = action;
        this.staff = staff;
        this.timestamp = timestamp;
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return timestamp + " - " + action + " by " + staff.getFullName() + ": " + details;
    }
}
