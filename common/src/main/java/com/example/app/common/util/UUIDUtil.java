package com.example.app.common.util;

import java.util.UUID;

/**
 * Utility class for UUID operations.
 */
public class UUIDUtil {
    /**
     * Generates a new UUID.
     */
    public static UUID generate() {
        return UUID.randomUUID();
    }

    /**
     * Parses a UUID from string, returns null if invalid.
     */
    public static UUID fromString(String uuidString) {
        if (uuidString == null || uuidString.trim().isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Validates if a string is a valid UUID.
     */
    public static boolean isValid(String uuidString) {
        return fromString(uuidString) != null;
    }
}

