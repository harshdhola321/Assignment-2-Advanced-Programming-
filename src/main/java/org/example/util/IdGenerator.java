package org.example.util;

import java.util.UUID;

/**
 * Utility class for generating unique IDs
 */
public class IdGenerator {
    
    /**
     * Generate a unique ID
     * @return A unique ID string
     */
    public static String generateId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Generate a unique ID with a prefix
     * @param prefix The prefix to use
     * @return A unique ID string with the prefix
     */
    public static String generateId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
