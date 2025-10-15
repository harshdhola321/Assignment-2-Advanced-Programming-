package org.example.model;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a medication in a prescription
 */
public class Medication implements Serializable {
    private String id;
    private String name;
    private String dosage;
    private String instructions;
    private List<LocalTime> administrationTimes;
    private Prescription prescription;

    public Medication(String id, String name, String dosage, String instructions, Prescription prescription) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.instructions = instructions;
        this.prescription = prescription;
        this.administrationTimes = new ArrayList<>();
    }
    
    /**
     * Constructor without prescription
     * @param id The medication ID
     * @param name The medication name
     * @param dosage The medication dosage
     * @param instructions The medication instructions
     */
    public Medication(String id, String name, String dosage, String instructions) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.instructions = instructions;
        this.administrationTimes = new ArrayList<>();
        this.prescription = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<LocalTime> getAdministrationTimes() {
        return administrationTimes;
    }

    public void addAdministrationTime(LocalTime time) {
        this.administrationTimes.add(time);
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    @Override
    public String toString() {
        return name + " " + dosage;
    }
}
