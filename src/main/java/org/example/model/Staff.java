package org.example.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class representing a staff member in the care home
 */
public abstract class Staff extends Person {
    private String username;
    private String password;
    private Set<Shift> shifts;

    public Staff(String id, String firstName, String lastName, LocalDate dateOfBirth, Gender gender,
                String username, String password) {
        super(id, firstName, lastName, dateOfBirth, gender);
        this.username = username;
        this.password = password;
        this.shifts = new HashSet<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Shift> getShifts() {
        return shifts;
    }

    public void addShift(Shift shift) {
        this.shifts.add(shift);
    }

    public void removeShift(Shift shift) {
        this.shifts.remove(shift);
    }

    /**
     * Check if the staff member is rostered for the given date and time
     * @param dateTime The date and time to check
     * @return true if the staff member is rostered, false otherwise
     */
    public boolean isRosteredFor(java.time.LocalDateTime dateTime) {
        return shifts.stream().anyMatch(shift -> shift.covers(dateTime));
    }

    /**
     * Abstract method to check if the staff member is authorized to perform the given action
     * @param action The action to check
     * @return true if authorized, false otherwise
     */
    public abstract boolean isAuthorizedFor(String action);
}
