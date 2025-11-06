package com.example.app.common.util;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UUIDUtilTest {

    @Test
    void testGenerate_ReturnsValidUUID() {
        // Act
        UUID result = UUIDUtil.generate();

        // Assert
        assertNotNull(result);
        assertNotNull(result.toString());
        assertTrue(result.toString().matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }

    @Test
    void testGenerate_MultipleCalls_ReturnsDifferentUUIDs() {
        // Act
        UUID uuid1 = UUIDUtil.generate();
        UUID uuid2 = UUIDUtil.generate();

        // Assert
        assertNotNull(uuid1);
        assertNotNull(uuid2);
        assertNotEquals(uuid1, uuid2);
    }

    @Test
    void testFromString_ValidUUIDString_ReturnsUUID() {
        // Arrange
        String validUUID = "550e8400-e29b-41d4-a716-446655440000";

        // Act
        UUID result = UUIDUtil.fromString(validUUID);

        // Assert
        assertNotNull(result);
        assertEquals(validUUID, result.toString());
    }

    @Test
    void testFromString_NullString_ReturnsNull() {
        // Act
        UUID result = UUIDUtil.fromString(null);

        // Assert
        assertNull(result);
    }

    @Test
    void testFromString_EmptyString_ReturnsNull() {
        // Act
        UUID result = UUIDUtil.fromString("");

        // Assert
        assertNull(result);
    }

    @Test
    void testFromString_WhitespaceString_ReturnsNull() {
        // Act
        UUID result = UUIDUtil.fromString("   ");

        // Assert
        assertNull(result);
    }

    @Test
    void testFromString_InvalidUUIDString_ReturnsNull() {
        // Arrange
        String invalidUUID = "not-a-valid-uuid";

        // Act
        UUID result = UUIDUtil.fromString(invalidUUID);

        // Assert
        assertNull(result);
    }

    @Test
    void testFromString_MalformedUUIDString_ReturnsNull() {
        // Arrange
        String malformedUUID = "550e8400-e29b-41d4-a716";

        // Act
        UUID result = UUIDUtil.fromString(malformedUUID);

        // Assert
        assertNull(result);
    }

    @Test
    void testIsValid_ValidUUIDString_ReturnsTrue() {
        // Arrange
        String validUUID = "550e8400-e29b-41d4-a716-446655440000";

        // Act
        boolean isValid = UUIDUtil.isValid(validUUID);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testIsValid_NullString_ReturnsFalse() {
        // Act
        boolean isValid = UUIDUtil.isValid(null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testIsValid_EmptyString_ReturnsFalse() {
        // Act
        boolean isValid = UUIDUtil.isValid("");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testIsValid_InvalidUUIDString_ReturnsFalse() {
        // Arrange
        String invalidUUID = "not-a-valid-uuid";

        // Act
        boolean isValid = UUIDUtil.isValid(invalidUUID);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testRoundTrip_GenerateToString_ReturnsSameUUID() {
        // Arrange
        UUID generated = UUIDUtil.generate();

        // Act
        UUID parsed = UUIDUtil.fromString(generated.toString());

        // Assert
        assertNotNull(parsed);
        assertEquals(generated, parsed);
    }
}

