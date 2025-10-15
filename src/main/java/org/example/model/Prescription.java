package org.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a prescription for a patient
 */
public class Prescription implements Serializable {
    private String id;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime prescriptionDateTime;
    private String notes;
    private List<Medication> medications;

    public Prescription(String id, Patient patient, Doctor doctor, LocalDateTime prescriptionDateTime, String notes) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.prescriptionDateTime = prescriptionDateTime;
        this.notes = notes;
        this.medications = new ArrayList<>();
    }
    
    /**
     * Constructor with medications list
     * @param id The prescription ID
     * @param patient The patient
     * @param doctor The prescribing doctor
     * @param prescriptionDateTime The date and time of the prescription
     * @param medications The list of medications
     * @param notes Additional notes
     */
    public Prescription(String id, Patient patient, Doctor doctor, LocalDateTime prescriptionDateTime, 
                        List<Medication> medications, String notes) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.prescriptionDateTime = prescriptionDateTime;
        this.notes = notes;
        this.medications = new ArrayList<>(medications);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getPrescriptionDateTime() {
        return prescriptionDateTime;
    }

    public void setPrescriptionDateTime(LocalDateTime prescriptionDateTime) {
        this.prescriptionDateTime = prescriptionDateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<Medication> getMedications() {
        return medications;
    }

    public void addMedication(Medication medication) {
        this.medications.add(medication);
    }

    @Override
    public String toString() {
        return "Prescription by Dr. " + doctor.getLastName() + " on " + prescriptionDateTime.toLocalDate();
    }
}
