package org.example.repository.impl;

import org.example.model.Staff;
import org.example.repository.StaffRepository;
import org.example.util.DefaultDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * File-based implementation of StaffRepository
 */
public class FileStaffRepository implements StaffRepository {
    private static final Logger logger = LoggerFactory.getLogger(FileStaffRepository.class);
    private static final String STAFF_FILE = "staff_data.ser";
    
    private List<Staff> staff;
    
    public FileStaffRepository() {
        loadData();
    }
    
    @Override
    public List<Staff> findAll() {
        return new ArrayList<>(staff);
    }
    
    @Override
    public Optional<Staff> findById(String id) {
        return staff.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }
    
    @Override
    public Optional<Staff> findByUsername(String username) {
        return staff.stream()
                .filter(s -> s.getUsername().equals(username))
                .findFirst();
    }
    
    @Override
    public boolean save(Staff staffMember) {
        // Check if username already exists
        boolean usernameExists = staff.stream()
                .anyMatch(s -> s.getUsername().equals(staffMember.getUsername()));
        
        if (usernameExists) {
            logger.warn("Username already exists: {}", staffMember.getUsername());
            return false;
        }
        
        // Add the staff member
        staff.add(staffMember);
        saveData();
        
        logger.info("Staff member added: {} ({})", staffMember.getFullName(), staffMember.getClass().getSimpleName());
        return true;
    }
    
    @Override
    public boolean update(Staff staffMember) {
        // Find the staff member in the list
        Optional<Staff> existingStaffOpt = findById(staffMember.getId());
        
        if (!existingStaffOpt.isPresent()) {
            logger.warn("Staff member not found: {}", staffMember.getId());
            return false;
        }
        
        // Replace the staff member
        int index = staff.indexOf(existingStaffOpt.get());
        staff.set(index, staffMember);
        saveData();
        
        logger.info("Staff member updated: {} ({})", staffMember.getFullName(), staffMember.getClass().getSimpleName());
        return true;
    }
    
    @Override
    public void delete(Staff staffMember) {
        staff.remove(staffMember);
        saveData();
    }

    @Override
    public void saveAll(List<Staff> staff) {
        this.staff = staff;
        saveData();
    }

    private void loadData() {
        try {
            staff = loadFromFile();
            if (staff == null) {
                // If file doesn't exist or can't be read, initialize with default data
                staff = DefaultDataGenerator.generateDefaultStaff();
                logger.info("Initialized with default staff data");
                saveData(); // Save the default data
            }
        } catch (Exception e) {
            logger.error("Error loading staff data", e);
            // Fallback to default data
            staff = DefaultDataGenerator.generateDefaultStaff();
            logger.info("Initialized with default staff data after error");
        }
    }
    
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STAFF_FILE))) {
            oos.writeObject(staff);
            logger.info("Staff data saved to file");
        } catch (IOException e) {
            logger.error("Could not save staff data to file: " + STAFF_FILE, e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<Staff> loadFromFile() {
        File file = new File(STAFF_FILE);
        if (!file.exists()) {
            logger.info("Staff data file not found: {}", STAFF_FILE);
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            
            // Check if the object is a CareHomeData instance (from old format)
            if (obj instanceof CareHomeData) {
                CareHomeData data = (CareHomeData) obj;
                return data.getStaff();
            } else if (obj instanceof List<?>) {
                // Direct list of staff (new format)
                return (List<Staff>) obj;
            } else {
                logger.warn("Unknown data format in file: {}", STAFF_FILE);
                return null;
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.warn("Could not load staff data from file: " + STAFF_FILE, e);
            return null;
        }
    }
    
    /**
     * Private class for serializing care home data (for backward compatibility)
     */
    private static class CareHomeData implements Serializable {
        private List<Staff> staff;
        private List<Object> wards;
        private List<Object> patients;
        private List<Object> dischargedPatients;
        
        public List<Staff> getStaff() {
            return staff;
        }
    }
}
