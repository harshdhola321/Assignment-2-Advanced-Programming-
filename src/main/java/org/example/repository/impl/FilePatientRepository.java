package org.example.repository.impl;

import org.example.model.Patient;
import org.example.repository.PatientRepository;
import org.example.util.DefaultDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * File-based implementation of PatientRepository
 */
public class FilePatientRepository implements PatientRepository {
    private static final Logger logger = LoggerFactory.getLogger(FilePatientRepository.class);
    private static final String PATIENTS_FILE = "patients_data.ser";
    private static final String DISCHARGED_PATIENTS_FILE = "discharged_patients.ser";

    private List<Patient> patients;
    private List<Patient> dischargedPatients;

    public FilePatientRepository() {
        loadData();
    }

    @Override
    public List<Patient> findAll() {
        return new ArrayList<>(patients);
    }

    @Override
    public Optional<Patient> findById(String id) {
        return patients.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    @Override
    public void save(Patient patient) {
        // If patient already exists, update it
        Optional<Patient> existingPatient = findById(patient.getId());
        if (existingPatient.isPresent()) {
            int index = patients.indexOf(existingPatient.get());
            patients.set(index, patient);
        } else {
            // Otherwise, add it
            patients.add(patient);
        }
        saveData();
    }

    @Override
    public void delete(Patient patient) {
        patients.remove(patient);
        saveData();
    }

    @Override
    public List<Patient> findDischargedPatients() {
        return new ArrayList<>(dischargedPatients);
    }

    @Override
    public void addDischargedPatient(Patient patient) {
        dischargedPatients.add(patient);
        saveData();
    }

    @Override
    public void saveAll(List<Patient> patients) {
        this.patients = patients;
        saveData();
    }

    @Override
    public void saveAllDischarged(List<Patient> patients) {
        this.dischargedPatients = patients;
        saveData();
    }

    private void loadData() {
        try {
            patients = loadPatientsFromFile();
            if (patients == null) {
                // If file doesn't exist or can't be read, initialize with empty list
                patients = new ArrayList<>();
                logger.info("Initialized with empty patient list");
            }

            dischargedPatients = loadDischargedPatientsFromFile();
            if (dischargedPatients == null) {
                // If file doesn't exist or can't be read, initialize with empty list
                dischargedPatients = new ArrayList<>();
                logger.info("Initialized with empty discharged patient list");
            }
        } catch (Exception e) {
            logger.error("Error loading patient data", e);
            // Fallback to empty lists
            patients = new ArrayList<>();
            dischargedPatients = new ArrayList<>();
            logger.info("Initialized with empty patient lists after error");
        }
    }

    private void saveData() {
        savePatientsToFile();
        saveDischargedPatientsToFile();
    }

    @SuppressWarnings("unchecked")
    private List<Patient> loadPatientsFromFile() {
        File file = new File(PATIENTS_FILE);
        if (!file.exists()) {
            logger.info("Patient data file not found: {}", PATIENTS_FILE);
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();

            // Check if the object is a CareHomeData instance (from old format)
            if (obj instanceof CareHomeData) {
                CareHomeData data = (CareHomeData) obj;
                return data.getPatients();
            } else if (obj instanceof List<?>) {
                // Direct list of patients (new format)
                return (List<Patient>) obj;
            } else {
                logger.warn("Unknown data format in file: {}", PATIENTS_FILE);
                return null;
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.warn("Could not load patient data from file: " + PATIENTS_FILE, e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List<Patient> loadDischargedPatientsFromFile() {
        File file = new File(DISCHARGED_PATIENTS_FILE);
        if (!file.exists()) {
            logger.info("Discharged patient data file not found: {}", DISCHARGED_PATIENTS_FILE);
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Patient>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.warn("Could not load discharged patient data from file: " + DISCHARGED_PATIENTS_FILE, e);
            return null;
        }
    }

    private void savePatientsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PATIENTS_FILE))) {
            oos.writeObject(patients);
            logger.info("Patient data saved to file");
        } catch (IOException e) {
            logger.error("Could not save patient data to file: " + PATIENTS_FILE, e);
        }
    }

    private void saveDischargedPatientsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DISCHARGED_PATIENTS_FILE))) {
            oos.writeObject(dischargedPatients);
            logger.info("Discharged patient data saved to file");
        } catch (IOException e) {
            logger.error("Could not save discharged patient data to file: " + DISCHARGED_PATIENTS_FILE, e);
        }
    }

    /**
     * Private class for serializing care home data (for backward compatibility)
     */
    private static class CareHomeData implements Serializable {
        private List<Object> wards;
        private List<Object> staff;
        private List<Patient> patients;
        private List<Patient> dischargedPatients;

        public List<Patient> getPatients() {
            return patients != null ? patients : new ArrayList<>();
        }

        public List<Patient> getDischargedPatients() {
            return dischargedPatients != null ? dischargedPatients : new ArrayList<>();
        }
    }
}
