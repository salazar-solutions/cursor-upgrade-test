package com.example.app.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {AdminController.class})
class AdminControllerTest {

    @MockitoBean
    private DataSource dataSource;

    @Autowired
    private AdminController adminController;

    private Connection connection;

    @BeforeEach
    void setUp() throws Exception {
        connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
    }

    @Test
    void testHealth_DatabaseConnected_ReturnsUp() throws Exception {
        // Arrange
        when(connection.isValid(anyInt())).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.health();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("UP", response.getBody().get("database"));

        verify(dataSource).getConnection();
        verify(connection).isValid(anyInt());
        verify(connection).close();
    }

    @Test
    void testHealth_DatabaseDisconnected_ReturnsDown() throws Exception {
        // Arrange
        when(connection.isValid(anyInt())).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.health();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("DOWN", response.getBody().get("database"));

        verify(dataSource).getConnection();
        verify(connection).isValid(anyInt());
        verify(connection).close();
    }

    @Test
    void testHealth_DatabaseException_ReturnsDown() throws Exception {
        // Arrange
        when(dataSource.getConnection()).thenThrow(new java.sql.SQLException("Connection failed"));

        // Act
        ResponseEntity<Map<String, Object>> response = adminController.health();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("DOWN", response.getBody().get("database"));

        verify(dataSource).getConnection();
    }

    @Test
    void testMetrics_ReturnsOk() {
        // Act
        ResponseEntity<Map<String, String>> response = adminController.metrics();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
        assertTrue(response.getBody().containsKey("actuatorEndpoint"));
        assertEquals("/actuator/metrics", response.getBody().get("actuatorEndpoint"));
    }
}

