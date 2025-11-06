package com.example.app.admin.controller;

import com.example.app.admin.config.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AdminController.
 * These tests use the local devdb database and should only run with the integration profile.
 * Ensure PostgreSQL is running and devdb database exists before running these tests.
 */
@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles({"integration", "local"})
@Transactional
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "integration")
class AdminControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHealthCheck_Success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.database").value("UP"));
    }

    @Test
    void testMetrics_Success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.actuatorEndpoint").value("/actuator/metrics"));
    }
}

