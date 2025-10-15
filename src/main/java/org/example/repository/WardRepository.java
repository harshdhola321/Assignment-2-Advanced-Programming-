package org.example.repository;

import org.example.model.Bed;
import org.example.model.Patient;
import org.example.model.Room;
import org.example.model.Ward;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Ward, Room, and Bed data access
 */
public interface WardRepository {
    /**
     * Find all wards
     * @return List of all wards
     */
    List<Ward> findAllWards();
    
    /**
     * Find a ward by ID
     * @param id The ward ID
     * @return Optional containing the ward if found, empty otherwise
     */
    Optional<Ward> findWardById(String id);
    
    /**
     * Find a room by ID
     * @param id The room ID
     * @return Optional containing the room if found, empty otherwise
     */
    Optional<Room> findRoomById(String id);
    
    /**
     * Find a bed by ID
     * @param id The bed ID
     * @return Optional containing the bed if found, empty otherwise
     */
    Optional<Bed> findBedById(String id);
    
    /**
     * Find all vacant beds
     * @return List of vacant beds
     */
    List<Bed> findVacantBeds();
    
    /**
     * Find the bed for a patient
     * @param patient The patient
     * @return Optional containing the bed if found, empty otherwise
     */
    Optional<Bed> findBedForPatient(Patient patient);
    
    /**
     * Save a ward
     * @param ward The ward to save
     */
    void saveWard(Ward ward);
    
    /**
     * Save all wards
     * @param wards The wards to save
     */
    void saveAllWards(List<Ward> wards);
}
