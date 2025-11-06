package com.example.app.common.util;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateMapperTest {

    @Test
    void testToInstant_ValidDate_ReturnsInstant() {
        // Arrange
        Date date = new Date();
        Instant expectedInstant = date.toInstant();

        // Act
        Instant result = DateMapper.toInstant(date);

        // Assert
        assertNotNull(result);
        assertEquals(expectedInstant, result);
    }

    @Test
    void testToInstant_NullDate_ReturnsNull() {
        // Act
        Instant result = DateMapper.toInstant(null);

        // Assert
        assertNull(result);
    }

    @Test
    void testToDate_ValidInstant_ReturnsDate() {
        // Arrange
        Instant instant = Instant.now();
        Date expectedDate = Date.from(instant);

        // Act
        Date result = DateMapper.toDate(instant);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDate, result);
    }

    @Test
    void testToDate_NullInstant_ReturnsNull() {
        // Act
        Date result = DateMapper.toDate((Instant) null);

        // Assert
        assertNull(result);
    }

    @Test
    void testToLocalDateTime_ValidDate_ReturnsLocalDateTime() {
        // Arrange
        Date date = new Date();
        LocalDateTime expected = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"));

        // Act
        LocalDateTime result = DateMapper.toLocalDateTime(date);

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void testToLocalDateTime_NullDate_ReturnsNull() {
        // Act
        LocalDateTime result = DateMapper.toLocalDateTime(null);

        // Assert
        assertNull(result);
    }

    @Test
    void testToDate_ValidLocalDateTime_ReturnsDate() {
        // Arrange
        LocalDateTime localDateTime = LocalDateTime.now();
        Date expectedDate = Date.from(localDateTime.atZone(ZoneId.of("UTC")).toInstant());

        // Act
        Date result = DateMapper.toDate(localDateTime);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDate, result);
    }

    @Test
    void testToDate_NullLocalDateTime_ReturnsNull() {
        // Act
        Date result = DateMapper.toDate((LocalDateTime) null);

        // Assert
        assertNull(result);
    }

    @Test
    void testRoundTrip_DateToInstantToDate_PreservesValue() {
        // Arrange
        Date originalDate = new Date();

        // Act
        Instant instant = DateMapper.toInstant(originalDate);
        Date convertedDate = DateMapper.toDate(instant);

        // Assert
        assertNotNull(convertedDate);
        assertEquals(originalDate.getTime(), convertedDate.getTime());
    }

    @Test
    void testRoundTrip_DateToLocalDateTimeToDate_PreservesValue() {
        // Arrange
        Date originalDate = new Date();

        // Act
        LocalDateTime localDateTime = DateMapper.toLocalDateTime(originalDate);
        Date convertedDate = DateMapper.toDate(localDateTime);

        // Assert
        assertNotNull(convertedDate);
        // Allow for minor precision differences in conversion
        long diff = Math.abs(originalDate.getTime() - convertedDate.getTime());
        assertTrue(diff < 1000, "Time difference should be less than 1 second");
    }
}

