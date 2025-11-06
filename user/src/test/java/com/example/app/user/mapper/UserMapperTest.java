package com.example.app.user.mapper;

import com.example.app.user.dto.UserResponse;
import com.example.app.user.entity.Role;
import com.example.app.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userMapper = UserMapper.INSTANCE;
        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("encodedPassword");

        Date createdAt = new Date();
        Date updatedAt = new Date();
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        roles.add(Role.ADMIN);
        user.setRoles(roles);
    }

    @Test
    void testToResponse_Success_MapsAllFields() {
        // Act
        UserResponse response = userMapper.toResponse(user);

        // Assert
        assertNotNull(response);
        assertEquals(userId.toString(), response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        assertNotNull(response.getRoles());
        assertEquals(2, response.getRoles().size());
        assertTrue(response.getRoles().contains("USER"));
        assertTrue(response.getRoles().contains("ADMIN"));
    }

    @Test
    void testToResponse_DateConversion_ConvertsToInstant() {
        // Arrange
        Date testDate = new Date();
        user.setCreatedAt(testDate);
        user.setUpdatedAt(testDate);

        // Act
        UserResponse response = userMapper.toResponse(user);

        // Assert
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        assertEquals(testDate.toInstant(), response.getCreatedAt());
        assertEquals(testDate.toInstant(), response.getUpdatedAt());
    }

    @Test
    void testToResponse_NullRoles_HandlesGracefully() {
        // Arrange
        user.setRoles(null);

        // Act
        UserResponse response = userMapper.toResponse(user);

        // Assert
        assertNotNull(response);
        assertNull(response.getRoles());
    }

    @Test
    void testToResponse_EmptyRoles_ReturnsEmptySet() {
        // Arrange
        user.setRoles(new HashSet<>());

        // Act
        UserResponse response = userMapper.toResponse(user);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getRoles());
        assertTrue(response.getRoles().isEmpty());
    }

    @Test
    void testToResponse_SingleRole_MapsCorrectly() {
        // Arrange
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        user.setRoles(roles);

        // Act
        UserResponse response = userMapper.toResponse(user);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getRoles().size());
        assertTrue(response.getRoles().contains("USER"));
    }

}

