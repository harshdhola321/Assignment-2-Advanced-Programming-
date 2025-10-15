package org.example.service;

import org.example.model.*;

/**
 * Service for handling data persistence
 */
public interface DataPersistenceService {
    /**
     * Archive a discharged patient's data to the database
     * @param patient The discharged patient
     * @return true if the archive was successful, false otherwise
     */
    boolean archivePatient(Patient patient);
}
