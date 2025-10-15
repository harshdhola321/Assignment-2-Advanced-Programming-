package org.example.util;

import org.example.model.*;
import org.example.service.AuthenticationService;
import org.example.service.DataPersistenceService;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to generate default data for the application
 */
public class DefaultDataGenerator {

    /**
     * Generate default data and save it to file
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("Generating default data...");
            
            // Create wards
            List<Ward> wards = generateDefaultWards();
            List<Staff> staffList = generateDefaultStaff();
            
            // Create action logs list (empty for now)
            List<ActionLog> logs = new ArrayList<>();
            
            // Save data to file
            String dataFile = "care_home_data.ser";
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile))) {
                oos.writeObject(wards);
                oos.writeObject(staffList);
                oos.writeObject(logs);
                System.out.println("Data saved to file: " + dataFile);
                System.out.println("Created users:");
                System.out.println("- Manager: username=admin, password=admin123");
                System.out.println("- Doctor: username=doctor, password=doctor123");
                System.out.println("- Nurse: username=nurse, password=nurse123");
            }
            
        } catch (Exception e) {
            System.err.println("Error generating default data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Generate default wards, rooms, and beds
     * @return List of wards
     */
    public static List<Ward> generateDefaultWards() {
        // Create wards
        List<Ward> wards = new ArrayList<>();
        Ward ward1 = new Ward(IdGenerator.generateId("WARD"), "Ward 1");
        Ward ward2 = new Ward(IdGenerator.generateId("WARD"), "Ward 2");
        
        // Create 6 rooms in Ward 1 with specified bed configuration:
        // - 1 room with 1 bed
        // - 1 room with 2 beds
        // - 4 rooms with 4 beds each
        Room room1_1 = new Room(IdGenerator.generateId("ROOM"), "101", ward1, 1);  // 1 bed
        Room room1_2 = new Room(IdGenerator.generateId("ROOM"), "102", ward1, 2);  // 2 beds
        Room room1_3 = new Room(IdGenerator.generateId("ROOM"), "103", ward1, 4);  // 4 beds
        Room room1_4 = new Room(IdGenerator.generateId("ROOM"), "104", ward1, 4);  // 4 beds
        Room room1_5 = new Room(IdGenerator.generateId("ROOM"), "105", ward1, 4);  // 4 beds
        Room room1_6 = new Room(IdGenerator.generateId("ROOM"), "106", ward1, 4);  // 4 beds
        
        ward1.addRoom(room1_1);
        ward1.addRoom(room1_2);
        ward1.addRoom(room1_3);
        ward1.addRoom(room1_4);
        ward1.addRoom(room1_5);
        ward1.addRoom(room1_6);
        
        // Create 6 rooms in Ward 2 with the same bed configuration
        Room room2_1 = new Room(IdGenerator.generateId("ROOM"), "201", ward2, 1);  // 1 bed
        Room room2_2 = new Room(IdGenerator.generateId("ROOM"), "202", ward2, 2);  // 2 beds
        Room room2_3 = new Room(IdGenerator.generateId("ROOM"), "203", ward2, 4);  // 4 beds
        Room room2_4 = new Room(IdGenerator.generateId("ROOM"), "204", ward2, 4);  // 4 beds
        Room room2_5 = new Room(IdGenerator.generateId("ROOM"), "205", ward2, 4);  // 4 beds
        Room room2_6 = new Room(IdGenerator.generateId("ROOM"), "206", ward2, 4);  // 4 beds
        
        ward2.addRoom(room2_1);
        ward2.addRoom(room2_2);
        ward2.addRoom(room2_3);
        ward2.addRoom(room2_4);
        ward2.addRoom(room2_5);
        ward2.addRoom(room2_6);
        
        wards.add(ward1);
        wards.add(ward2);
        
        return wards;
    }
    
    /**
     * Generate default staff members
     * @return List of staff members
     */
    public static List<Staff> generateDefaultStaff() {
        List<Staff> staffList = new ArrayList<>();
        
        // Create shifts for all days of the week
        Shift morningShift = new Shift(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0));
        Shift afternoonShift = new Shift(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(0, 0));
        Shift nightShift = new Shift(DayOfWeek.MONDAY, LocalTime.of(0, 0), LocalTime.of(8, 0));
        
        // Create a default manager
        Manager manager = new Manager(
            IdGenerator.generateId("STAFF"),
            "Admin",
            "User",
            LocalDate.of(1980, 1, 1),
            Gender.MALE,
            "admin",
            "admin123",
            "Administration"
        );
        manager.addShift(morningShift);
        manager.addShift(afternoonShift);
        manager.addShift(nightShift);
        staffList.add(manager);
        
        // Create a doctor
        Doctor doctor = new Doctor(
            IdGenerator.generateId("STAFF"),
            "John",
            "Smith",
            LocalDate.of(1975, 5, 15),
            Gender.MALE,
            "doctor",
            "doctor123",
            "Cardiology"
        );
        doctor.addShift(morningShift);
        doctor.addShift(afternoonShift);
        staffList.add(doctor);
        
        // Create a nurse
        Nurse nurse = new Nurse(
            IdGenerator.generateId("STAFF"),
            "Jane",
            "Johnson",
            LocalDate.of(1985, 8, 20),
            Gender.FEMALE,
            "nurse",
            "nurse123",
            "Registered Nurse"
        );
        nurse.addShift(morningShift);
        nurse.addShift(afternoonShift);
        nurse.addShift(nightShift);
        staffList.add(nurse);
        
        return staffList;
    }
}
