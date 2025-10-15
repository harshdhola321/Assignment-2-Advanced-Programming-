package org.example.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a patient in the care home
 */
public class Patient extends Person {
    private String medicalCondition;
    private boolean needsIsolation;
    private LocalDate admissionDate;
    private LocalDate dischargeDate;
    private List<Prescription> prescriptions;
    private List<MedicationAdministration> medicationAdministrations;

    public Patient(String id, String firstName, String lastName, LocalDate dateOfBirth, Gender gender,
                  String medicalCondition, boolean needsIsolation, LocalDate admissionDate) {
        super(id, firstName, lastName, dateOfBirth, gender);
        this.medicalCondition = medicalCondition;
        this.needsIsolation = needsIsolation;
        this.admissionDate = admissionDate;
        this.prescriptions = new ArrayList<>();
        this.medicationAdministrations = new ArrayList<>();
    }
    
    /**
     * Constructor with current date as admission date
     * @param id The patient ID
     * @param firstName The patient's first name
     * @param lastName The patient's last name
     * @param dateOfBirth The patient's date of birth
     * @param gender The patient's gender
     * @param medicalCondition The patient's medical condition
     * @param needsIsolation Whether the patient needs isolation
     */
    public Patient(String id, String firstName, String lastName, LocalDate dateOfBirth, Gender gender,
                  String medicalCondition, boolean needsIsolation) {
        super(id, firstName, lastName, dateOfBirth, gender);
        this.medicalCondition = medicalCondition;
        this.needsIsolation = needsIsolation;
        this.admissionDate = LocalDate.now();
        this.prescriptions = new ArrayList<>();
        this.medicationAdministrations = new ArrayList<>();
    }

    public String getMedicalCondition() {
        return medicalCondition;
    }

    public void setMedicalCondition(String medicalCondition) {
        this.medicalCondition = medicalCondition;
    }

    public boolean isNeedsIsolation() {
        return needsIsolation;
    }

    public void setNeedsIsolation(boolean needsIsolation) {
        this.needsIsolation = needsIsolation;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }

    public LocalDate getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(LocalDate dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void addPrescription(Prescription prescription) {
        this.prescriptions.add(prescription);
    }

    public List<MedicationAdministration> getMedicationAdministrations() {
        return medicationAdministrations;
    }

    public void addMedicationAdministration(MedicationAdministration administration) {
        this.medicationAdministrations.add(administration);
    }
}
