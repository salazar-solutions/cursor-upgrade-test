package com.example.app.common.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {SecurityConfig.class})
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testPasswordEncoder_BeanCreation_ReturnsBCryptEncoder() {
        // Act
        PasswordEncoder encoder = passwordEncoder;

        // Assert
        assertNotNull(encoder);
        String encoded = encoder.encode("password");
        assertTrue(encoder.matches("password", encoded));
        assertFalse(encoder.matches("wrongpassword", encoded));
    }

    @Test
    void testPasswordEncoder_DifferentPasswords_EncodesDifferently() {
        // Arrange
        PasswordEncoder encoder = passwordEncoder;

        // Act
        String encoded1 = encoder.encode("password1");
        String encoded2 = encoder.encode("password2");

        // Assert
        assertNotEquals(encoded1, encoded2);
        assertTrue(encoder.matches("password1", encoded1));
        assertTrue(encoder.matches("password2", encoded2));
        assertFalse(encoder.matches("password1", encoded2));
        assertFalse(encoder.matches("password2", encoded1));
    }

    @Test
    void testPasswordEncoder_SamePasswordMultipleTimes_ProducesDifferentHashes() {
        // Arrange
        PasswordEncoder encoder = passwordEncoder;
        String password = "testpassword";

        // Act
        String encoded1 = encoder.encode(password);
        String encoded2 = encoder.encode(password);

        // Assert - BCrypt should produce different hashes due to salt
        assertNotEquals(encoded1, encoded2);
        // But both should match the original password
        assertTrue(encoder.matches(password, encoded1));
        assertTrue(encoder.matches(password, encoded2));
    }
}

