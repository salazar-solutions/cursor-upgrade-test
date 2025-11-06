package com.example.app.common.util;

import java.util.UUID;

/**
 * Utility class for UUID operations with null-safe parsing and validation.
 * 
 * <p>Provides convenience methods for working with UUIDs, including safe parsing
 * that returns null instead of throwing exceptions for invalid input.
 * 
 * <p><b>Example usage:</b>
 * <pre>{@code
 * // Generate a new UUID
 * UUID id = UUIDUtil.generate();
 * 
 * // Parse with null handling
 * UUID parsed = UUIDUtil.fromString("550e8400-e29b-41d4-a716-446655440000");
 * if (parsed != null) {
 *     // Use parsed UUID
 * }
 * 
 * // Validate before parsing
 * if (UUIDUtil.isValid(uuidString)) {
 *     UUID id = UUIDUtil.fromString(uuidString);
 * }
 * }</pre>
 * 
 * @author Generated
 * @since 1.0.0
 */
public class UUIDUtil {
    /**
     * Generates a new random UUID (version 4).
     * 
     * @return a randomly generated UUID
     */
    public static UUID generate() {
        return UUID.randomUUID();
    }

    /**
     * Parses a UUID from a string representation, returning null if the string
     * is null, empty, or invalid.
     * 
     * <p>This method is null-safe and exception-safe, making it suitable for
     * parsing user input or configuration values where invalid UUIDs should
     * be handled gracefully.
     * 
     * @param uuidString the string to parse (can be null or empty)
     * @return the parsed UUID, or null if the string is invalid or empty
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
     * Validates whether a string represents a valid UUID.
     * 
     * <p>This method attempts to parse the string and returns true only if
     * parsing succeeds. Null or empty strings return false.
     * 
     * @param uuidString the string to validate (can be null)
     * @return true if the string is a valid UUID, false otherwise
     */
    public static boolean isValid(String uuidString) {
        return fromString(uuidString) != null;
    }
}

