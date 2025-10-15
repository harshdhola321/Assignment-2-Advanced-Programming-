package org.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a ward in the care home
 */
public class Ward implements Serializable {
    private String id;
    private String name;
    private List<Room> rooms;

    public Ward(String id, String name) {
        this.id = id;
        this.name = name;
        this.rooms = new ArrayList<>();
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

    public List<Room> getRooms() {
        return rooms;
    }

    public void addRoom(Room room) {
        this.rooms.add(room);
    }

    /**
     * Get the total number of beds in the ward
     * @return The total number of beds
     */
    public int getTotalBeds() {
        return rooms.stream().mapToInt(Room::getNumberOfBeds).sum();
    }

    /**
     * Get the number of vacant beds in the ward
     * @return The number of vacant beds
     */
    public int getVacantBeds() {
        return rooms.stream().mapToInt(Room::getVacantBeds).sum();
    }

    /**
     * Get the number of occupied beds in the ward
     * @return The number of occupied beds
     */
    public int getOccupiedBeds() {
        return rooms.stream().mapToInt(Room::getOccupiedBeds).sum();
    }

    @Override
    public String toString() {
        return name;
    }
}
