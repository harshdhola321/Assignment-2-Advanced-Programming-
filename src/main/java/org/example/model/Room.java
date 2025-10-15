package org.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a room in a ward
 */
public class Room implements Serializable {
    private String id;
    private String number;
    private Ward ward;
    private List<Bed> beds;

    public Room(String id, String number, Ward ward, int numberOfBeds) {
        this.id = id;
        this.number = number;
        this.ward = ward;
        this.beds = new ArrayList<>();
        
        // Create the specified number of beds
        for (int i = 1; i <= numberOfBeds; i++) {
            Bed bed = new Bed(id + "-B" + i, "Bed " + i, this);
            beds.add(bed);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Ward getWard() {
        return ward;
    }

    public void setWard(Ward ward) {
        this.ward = ward;
    }

    public List<Bed> getBeds() {
        return beds;
    }

    /**
     * Get the number of beds in the room
     * @return The number of beds
     */
    public int getNumberOfBeds() {
        return beds.size();
    }

    /**
     * Get the number of vacant beds in the room
     * @return The number of vacant beds
     */
    public int getVacantBeds() {
        return (int) beds.stream().filter(bed -> !bed.isOccupied()).count();
    }

    /**
     * Get the number of occupied beds in the room
     * @return The number of occupied beds
     */
    public int getOccupiedBeds() {
        return (int) beds.stream().filter(Bed::isOccupied).count();
    }

    @Override
    public String toString() {
        return ward.getName() + " - Room " + number;
    }
}
