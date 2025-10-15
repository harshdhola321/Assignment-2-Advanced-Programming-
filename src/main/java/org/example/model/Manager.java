package org.example.model;

import java.time.LocalDate;

/**
 * Class representing a manager in the care home
 */
public class Manager extends Staff {
    private String department;

    public Manager(String id, String firstName, String lastName, LocalDate dateOfBirth, Gender gender,
                  String username, String password, String department) {
        super(id, firstName, lastName, dateOfBirth, gender, username, password);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public boolean isAuthorizedFor(String action) {
        if ("admin".equals(getUsername())) {
            return true;
        }
        // Managers can add patients, add staff, and modify staff details
        return "ADD_PATIENT".equals(action) || "ADD_STAFF".equals(action) || "MODIFY_STAFF".equals(action);
    }
}
