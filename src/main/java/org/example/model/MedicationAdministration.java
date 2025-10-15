package org.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Class representing the administration of a medication to a patient
 */
public class MedicationAdministration implements Serializable {
    private String id;
    private Medication medication;
    private Patient patient;
    private Nurse nurse;
    private LocalDateTime administrationDateTime;
    private String notes;

    public MedicationAdministration(String id, Medication medication, Patient patient, Nurse nurse,
                                   LocalDateTime administrationDateTime, String notes) {
        this.id = id;
        this.medication = medication;
        this.patient = patient;
        this.nurse = nurse;
        this.administrationDateTime = administrationDateTime;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Medication getMedication() {
        return medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Nurse getNurse() {
        return nurse;
    }

    public void setNurse(Nurse nurse) {
        this.nurse = nurse;
    }

    public LocalDateTime getAdministrationDateTime() {
        return administrationDateTime;
    }

    public void setAdministrationDateTime(LocalDateTime administrationDateTime) {
        this.administrationDateTime = administrationDateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return medication.getName() + " administered by " + nurse.getFullName() + " on " + administrationDateTime;
    }
}
