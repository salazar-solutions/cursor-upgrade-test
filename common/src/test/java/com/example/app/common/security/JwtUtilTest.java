package com.example.app.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {JwtUtil.class})
@TestPropertySource(properties = {
    "jwt.secret=test-secret-key-for-unit-testing-jwt-util-must-be-at-least-512-bits-long-for-hs512-algorithm",
    "jwt.expiration=86400000"
})
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    private UUID userId;
    private String username;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        username = "testuser";
    }

    @Test
    void testGenerateToken_Success_ReturnsValidToken() {
        // Act
        String token = jwtUtil.generateToken(userId, username);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts separated by dots
    }

    @Test
    void testGenerateToken_DifferentUsers_ReturnsDifferentTokens() {
        // Arrange
        UUID userId2 = UUID.randomUUID();
        String username2 = "testuser2";

        // Act
        String token1 = jwtUtil.generateToken(userId, username);
        String token2 = jwtUtil.generateToken(userId2, username2);

        // Assert
        assertNotEquals(token1, token2);
    }

    @Test
    void testGetUserIdFromToken_ValidToken_ReturnsCorrectUserId() {
        // Arrange
        String token = jwtUtil.generateToken(userId, username);

        // Act
        UUID result = jwtUtil.getUserIdFromToken(token);

        // Assert
        assertEquals(userId, result);
    }

    @Test
    void testGetUsernameFromToken_ValidToken_ReturnsCorrectUsername() {
        // Arrange
        String token = jwtUtil.generateToken(userId, username);

        // Act
        String result = jwtUtil.getUsernameFromToken(token);

        // Assert
        assertEquals(username, result);
    }

    @Test
    void testValidateToken_ValidToken_ReturnsTrue() {
        // Arrange
        String token = jwtUtil.generateToken(userId, username);

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken_ReturnsFalse() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_NullToken_ReturnsFalse() {
        // Act
        boolean isValid = jwtUtil.validateToken(null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_EmptyToken_ReturnsFalse() {
        // Act
        boolean isValid = jwtUtil.validateToken("");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_MalformedToken_ReturnsFalse() {
        // Arrange
        String malformedToken = "not.a.valid.jwt.token";

        // Act
        boolean isValid = jwtUtil.validateToken(malformedToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testTokenRoundTrip_GenerateAndExtract_ReturnsOriginalValues() {
        // Arrange
        String token = jwtUtil.generateToken(userId, username);

        // Act
        UUID extractedUserId = jwtUtil.getUserIdFromToken(token);
        String extractedUsername = jwtUtil.getUsernameFromToken(token);
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertEquals(userId, extractedUserId);
        assertEquals(username, extractedUsername);
        assertTrue(isValid);
    }
}

