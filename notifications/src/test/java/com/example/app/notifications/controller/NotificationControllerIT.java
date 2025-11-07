package com.example.app.notifications.controller;

import com.example.app.notifications.domain.NotificationRequest;
import com.example.app.user.entity.User;
import com.example.app.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for NotificationController.
 * These tests use the local devdb database and should only run with the integration profile.
 * Ensure PostgreSQL is running and devdb database exists before running these tests.
 */
@SpringBootTest(classes = {com.example.app.notifications.config.TestApplication.class})
@AutoConfigureMockMvc
@ActiveProfiles({"integration", "local"})
@Transactional
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "integration")
class NotificationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UUID testUserId;
    private UUID testUserId2;

    @BeforeEach
    void setUp() {
        // Clean up
        userRepository.deleteAll();

        // Create test users with unique names to avoid conflicts
        long timestamp = System.currentTimeMillis();
        User user1 = new User();
        user1.setUsername("testuser1_" + timestamp);
        user1.setEmail("testuser1_" + timestamp + "@example.com");
        user1.setPasswordHash(passwordEncoder.encode("password"));
        User savedUser1 = userRepository.save(user1);
        testUserId = savedUser1.getId();

        User user2 = new User();
        user2.setUsername("testuser2_" + timestamp);
        user2.setEmail("testuser2_" + timestamp + "@example.com");
        user2.setPasswordHash(passwordEncoder.encode("password"));
        User savedUser2 = userRepository.save(user2);
        testUserId2 = savedUser2.getId();
    }

    @Test
    void testSendNotification_Success() throws Exception {
        NotificationRequest request = new NotificationRequest();
        request.setUserId(testUserId.toString());
        request.setMessage("Your order has been created successfully");
        request.setType("ORDER_CREATED");

        mockMvc.perform(post("/api/v1/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("sent"));
    }

    @Test
    void testSendNotification_ValidationError() throws Exception {
        NotificationRequest request = new NotificationRequest();
        // Missing required fields
        request.setUserId("");

        mockMvc.perform(post("/api/v1/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSendNotification_DifferentTypes() throws Exception {
        // Test ORDER_CREATED
        NotificationRequest request1 = new NotificationRequest();
        request1.setUserId(testUserId.toString());
        request1.setMessage("Order created");
        request1.setType("ORDER_CREATED");
        mockMvc.perform(post("/api/v1/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        // Test ORDER_SHIPPED
        NotificationRequest request2 = new NotificationRequest();
        request2.setUserId(testUserId2.toString());
        request2.setMessage("Order shipped");
        request2.setType("ORDER_SHIPPED");
        mockMvc.perform(post("/api/v1/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());
    }

    @Test
    void testSendNotification_EmptyMessage() throws Exception {
        NotificationRequest request = new NotificationRequest();
        request.setUserId(testUserId.toString());
        request.setMessage(""); // Empty message
        request.setType("ORDER_CREATED");

        mockMvc.perform(post("/api/v1/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSendNotification_EmptyType() throws Exception {
        NotificationRequest request = new NotificationRequest();
        request.setUserId(testUserId.toString());
        request.setMessage("Test message");
        request.setType(""); // Empty type

        mockMvc.perform(post("/api/v1/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSendNotification_NullFields() throws Exception {
        NotificationRequest request = new NotificationRequest();
        // All fields null

        mockMvc.perform(post("/api/v1/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

