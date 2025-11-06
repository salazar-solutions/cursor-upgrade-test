package com.example.app.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for admin operations.
 */
@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Admin and monitoring API")
public class AdminController {
    
    @Autowired
    private DataSource dataSource;

    @GetMapping("/health")
    @Operation(summary = "Health check endpoint")
    @ApiResponse(responseCode = "200", description = "Application is healthy")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        
        // Check database connection
        boolean dbHealthy = false;
        try (Connection connection = dataSource.getConnection()) {
            dbHealthy = connection.isValid(1);
        } catch (Exception e) {
            // Database connection failed
        }
        
        health.put("database", dbHealthy ? "UP" : "DOWN");
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/metrics")
    @Operation(summary = "Get application metrics")
    @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully")
    public ResponseEntity<Map<String, String>> metrics() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Metrics endpoint - use /actuator/metrics for detailed metrics");
        response.put("actuatorEndpoint", "/actuator/metrics");
        return ResponseEntity.ok(response);
    }
}

