package org.example.model;

import java.time.LocalDate;

/**
 * Class representing a nurse in the care home
 */
public class Nurse extends Staff {
    private String qualification;

    public Nurse(String id, String firstName, String lastName, LocalDate dateOfBirth, Gender gender,
                String username, String password, String qualification) {
        super(id, firstName, lastName, dateOfBirth, gender, username, password);
        this.qualification = qualification;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    @Override
    public boolean isAuthorizedFor(String action) {
        // Nurses can administer medication and move patients but cannot add prescriptions
        return "ADMINISTER_MEDICATION".equals(action) || "MOVE_PATIENT".equals(action);
    }
}
