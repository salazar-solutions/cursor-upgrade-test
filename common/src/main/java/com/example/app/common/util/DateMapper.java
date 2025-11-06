package com.example.app.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Utility class for converting between java.util.Date and java.time types.
 */
public class DateMapper {
    private static final ZoneId UTC = ZoneId.of("UTC");

    /**
     * Converts java.util.Date to java.time.Instant.
     */
    public static Instant toInstant(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant();
    }

    /**
     * Converts java.time.Instant to java.util.Date.
     */
    public static Date toDate(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Date.from(instant);
    }

    /**
     * Converts java.util.Date to java.time.LocalDateTime (UTC).
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), UTC);
    }

    /**
     * Converts java.time.LocalDateTime to java.util.Date (UTC).
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(UTC).toInstant());
    }
}

