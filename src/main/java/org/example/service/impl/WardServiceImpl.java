package org.example.service.impl;

import org.example.model.Bed;
import org.example.model.Gender;
import org.example.model.Patient;
import org.example.model.Ward;
import org.example.repository.WardRepository;
import org.example.service.AuthenticationService;
import org.example.service.LoggingService;
import org.example.service.WardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of WardService
 */
public class WardServiceImpl implements WardService {
    private static final Logger logger = LoggerFactory.getLogger(WardServiceImpl.class);
    
    private final WardRepository wardRepository;
    private final AuthenticationService authService;
    private final LoggingService logService;
    
    public WardServiceImpl(WardRepository wardRepository,
                          AuthenticationService authService,
                          LoggingService logService) {
        this.wardRepository = wardRepository;
        this.authService = authService;
        this.logService = logService;
    }
    
    @Override
    public List<Ward> getAllWards() {
        return wardRepository.findAllWards();
    }
    
    @Override
    public Bed getBedById(String id) throws IllegalArgumentException {
        return wardRepository.findBedById(id)
            .orElseThrow(() -> new IllegalArgumentException("Bed not found: " + id));
    }
    
    @Override
    public Optional<Bed> findBedForPatient(Patient patient) {
        return wardRepository.findBedForPatient(patient);
    }
    
    @Override
    public void assignPatientToBed(Patient patient, Bed bed) throws IllegalArgumentException {
        if (bed.isOccupied()) {
            throw new IllegalArgumentException("Bed is already occupied");
        }
        
        // Check if the room is suitable for the patient's gender and isolation needs
        checkRoomSuitability(patient, bed);
        
        // Assign patient to bed
        bed.assignPatient(patient);
        
        // Save changes
        wardRepository.saveAllWards(wardRepository.findAllWards());
        
        logger.info("Patient " + patient.getFullName() + " assigned to " + bed.toString());
    }
    
    @Override
    public void movePatientToBed(Patient patient, Bed currentBed, Bed newBed) throws IllegalArgumentException {
        if (newBed.isOccupied()) {
            throw new IllegalArgumentException("New bed is already occupied");
        }
        
        // Check if the new room is suitable for the patient's gender and isolation needs
        checkRoomSuitability(patient, newBed);
        
        // Move patient
        currentBed.removePatient();
        newBed.assignPatient(patient);
        
        // Save changes
        wardRepository.saveAllWards(wardRepository.findAllWards());
        
        logger.info("Patient " + patient.getFullName() + " moved from " + currentBed.toString() + " to " + newBed.toString());
    }
    
    @Override
    public void removePatientFromBed(Patient patient, Bed bed) {
        bed.removePatient();
        
        // Save changes
        wardRepository.saveAllWards(wardRepository.findAllWards());
        
        logger.info("Patient " + patient.getFullName() + " removed from " + bed.toString());
    }
    
    @Override
    public List<Bed> getVacantBeds() {
        return wardRepository.findVacantBeds();
    }
    
    @Override
    public List<Bed> getSuitableVacantBeds(Gender gender, boolean needsIsolation) {
        List<Bed> vacantBeds = wardRepository.findVacantBeds();
        
        return vacantBeds.stream()
                .filter(bed -> isBedSuitable(bed, gender, needsIsolation))
                .collect(Collectors.toList());
    }
    
    /**
     * Check if a room is suitable for a patient based on gender and isolation needs
     * @param patient The patient
     * @param bed The bed
     * @throws IllegalArgumentException If the room is not suitable
     */
    private void checkRoomSuitability(Patient patient, Bed bed) throws IllegalArgumentException {
        // Check if the room is suitable for the patient's gender and isolation needs
        if (!isBedSuitable(bed, patient.getGender(), patient.isNeedsIsolation())) {
            if (patient.isNeedsIsolation()) {
                throw new IllegalArgumentException("Cannot assign patient requiring isolation to a shared room");
            } else {
                throw new IllegalArgumentException("Cannot assign patient to a room with patients of different gender");
            }
        }
    }
    
    /**
     * Check if a bed is suitable for a patient based on gender and isolation needs
     * @param bed The bed
     * @param gender The patient's gender
     * @param needsIsolation Whether the patient needs isolation
     * @return true if the bed is suitable, false otherwise
     */
    private boolean isBedSuitable(Bed bed, Gender gender, boolean needsIsolation) {
        // Get the room
        var room = bed.getRoom();
        
        // Check for isolation requirements
        if (needsIsolation) {
            // For isolation, we need a room with only one bed or all beds vacant
            if (room.getNumberOfBeds() > 1) {
                // Check if all other beds are vacant
                for (Bed otherBed : room.getBeds()) {
                    if (otherBed != bed && otherBed.isOccupied()) {
                        return false;
                    }
                }
            }
        } else {
            // For non-isolation, check if there are other patients of the same gender
            for (Bed otherBed : room.getBeds()) {
                if (otherBed != bed && otherBed.isOccupied() && otherBed.getPatient().getGender() != gender) {
                    return false;
                }
            }
        }
        
        return true;
    }
}
