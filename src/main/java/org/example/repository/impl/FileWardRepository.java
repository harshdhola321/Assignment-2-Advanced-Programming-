package org.example.repository.impl;

import org.example.model.Bed;
import org.example.model.Patient;
import org.example.model.Room;
import org.example.model.Ward;
import org.example.repository.WardRepository;
import org.example.util.DefaultDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * File-based implementation of WardRepository
 */
public class FileWardRepository implements WardRepository {
    private static final Logger logger = LoggerFactory.getLogger(FileWardRepository.class);
    private static final String WARDS_FILE = "wards_data.ser";
    
    private List<Ward> wards;
    
    public FileWardRepository() {
        loadData();
    }
    
    @Override
    public List<Ward> findAllWards() {
        return new ArrayList<>(wards);
    }
    
    @Override
    public Optional<Ward> findWardById(String id) {
        return wards.stream()
                .filter(w -> w.getId().equals(id))
                .findFirst();
    }
    
    @Override
    public Optional<Room> findRoomById(String id) {
        for (Ward ward : wards) {
            Optional<Room> room = ward.getRooms().stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst();
            if (room.isPresent()) {
                return room;
            }
        }
        return Optional.empty();
    }
    
    @Override
    public Optional<Bed> findBedById(String id) {
        for (Ward ward : wards) {
            for (Room room : ward.getRooms()) {
                Optional<Bed> bed = room.getBeds().stream()
                        .filter(b -> b.getId().equals(id))
                        .findFirst();
                if (bed.isPresent()) {
                    return bed;
                }
            }
        }
        return Optional.empty();
    }
    
    @Override
    public List<Bed> findVacantBeds() {
        List<Bed> vacantBeds = new ArrayList<>();
        for (Ward ward : wards) {
            for (Room room : ward.getRooms()) {
                for (Bed bed : room.getBeds()) {
                    if (!bed.isOccupied()) {
                        vacantBeds.add(bed);
                    }
                }
            }
        }
        return vacantBeds;
    }
    
    @Override
    public Optional<Bed> findBedForPatient(Patient patient) {
        for (Ward ward : wards) {
            for (Room room : ward.getRooms()) {
                for (Bed bed : room.getBeds()) {
                    if (bed.isOccupied() && bed.getPatient().equals(patient)) {
                        return Optional.of(bed);
                    }
                }
            }
        }
        return Optional.empty();
    }
    
    @Override
    public void saveWard(Ward ward) {
        // If ward already exists, update it
        Optional<Ward> existingWard = findWardById(ward.getId());
        if (existingWard.isPresent()) {
            int index = wards.indexOf(existingWard.get());
            wards.set(index, ward);
        } else {
            // Otherwise, add it
            wards.add(ward);
        }
        saveData();
    }
    
    @Override
    public void saveAllWards(List<Ward> wards) {
        this.wards = new ArrayList<>(wards);
        saveData();
    }
    
    private void loadData() {
        try {
            wards = loadFromFile();
            if (wards == null) {
                // If file doesn't exist or can't be read, initialize with default data
                wards = DefaultDataGenerator.generateDefaultWards();
                logger.info("Initialized with default ward data");
                saveData(); // Save the default data
            }
        } catch (Exception e) {
            logger.error("Error loading ward data", e);
            // Fallback to default data
            wards = DefaultDataGenerator.generateDefaultWards();
            logger.info("Initialized with default ward data after error");
        }
    }
    
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(WARDS_FILE))) {
            oos.writeObject(wards);
            logger.info("Ward data saved to file");
        } catch (IOException e) {
            logger.error("Could not save ward data to file: " + WARDS_FILE, e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<Ward> loadFromFile() {
        File file = new File(WARDS_FILE);
        if (!file.exists()) {
            logger.info("Ward data file not found: {}", WARDS_FILE);
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            
            // Check if the object is a CareHomeData instance (from old format)
            if (obj instanceof CareHomeData) {
                CareHomeData data = (CareHomeData) obj;
                return data.getWards();
            } else if (obj instanceof List<?>) {
                // Direct list of wards (new format)
                return (List<Ward>) obj;
            } else {
                logger.warn("Unknown data format in file: {}", WARDS_FILE);
                return null;
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.warn("Could not load ward data from file: " + WARDS_FILE, e);
            return null;
        }
    }
    
    /**
     * Private class for serializing care home data (for backward compatibility)
     */
    private static class CareHomeData implements Serializable {
        private List<Ward> wards;
        private List<Object> staff;
        private List<Object> patients;
        private List<Object> dischargedPatients;
        
        public List<Ward> getWards() {
            return wards;
        }
    }
}
