package org.example.model;

import java.time.LocalDate;

/**
 * Class representing a doctor in the care home
 */
public class Doctor extends Staff {
    private String specialization;

    public Doctor(String id, String firstName, String lastName, LocalDate dateOfBirth, Gender gender,
                 String username, String password, String specialization) {
        super(id, firstName, lastName, dateOfBirth, gender, username, password);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public boolean isAuthorizedFor(String action) {
        if ("admin".equals(getUsername())) {
            return true;
        }
        // Doctors can add prescriptions but cannot administer medication
        return "ADD_PRESCRIPTION".equals(action);
    }
}
