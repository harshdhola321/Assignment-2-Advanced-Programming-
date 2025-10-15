package org.example.model;

import java.io.Serializable;

/**
 * Class representing a bed in a room
 */
public class Bed implements Serializable {
    private String id;
    private String name;
    private Room room;
    private Patient patient;

    public Bed(String id, String name, Room room) {
        this.id = id;
        this.name = name;
        this.room = room;
        this.patient = null;
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

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Patient getPatient() {
        return patient;
    }

    /**
     * Assign a patient to this bed
     * @param patient The patient to assign
     * @return true if the patient was assigned, false if the bed is already occupied
     */
    public boolean assignPatient(Patient patient) {
        if (this.patient != null) {
            return false;
        }
        this.patient = patient;
        return true;
    }

    /**
     * Remove the patient from this bed
     * @return The patient that was removed, or null if the bed was vacant
     */
    public Patient removePatient() {
        Patient removedPatient = this.patient;
        this.patient = null;
        return removedPatient;
    }

    /**
     * Check if the bed is occupied
     * @return true if occupied, false if vacant
     */
    public boolean isOccupied() {
        return patient != null;
    }

    @Override
    public String toString() {
        return room.toString() + " - " + name;
    }
}
