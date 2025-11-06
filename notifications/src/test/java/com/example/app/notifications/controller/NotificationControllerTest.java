package com.example.app.notifications.controller;

import com.example.app.notifications.domain.NotificationRequest;
import com.example.app.notifications.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {NotificationControllerTest.TestConfig.class})
class NotificationControllerTest {

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private NotificationController notificationController;

    private NotificationRequest notificationRequest;

    @BeforeEach
    void setUp() {
        notificationRequest = new NotificationRequest();
        notificationRequest.setUserId("user-123");
        notificationRequest.setMessage("Test notification");
        notificationRequest.setType("TEST");
    }

    @Test
    void testSendNotification_Success_ReturnsOk() {
        // Arrange
        doNothing().when(notificationService).sendNotification(any(NotificationRequest.class));

        // Act
        ResponseEntity<Map<String, String>> response = 
            notificationController.sendNotification(notificationRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("sent", response.getBody().get("status"));

        verify(notificationService).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void testSendNotification_VerifyServiceCalled() {
        // Arrange
        doNothing().when(notificationService).sendNotification(any(NotificationRequest.class));

        // Act
        notificationController.sendNotification(notificationRequest);

        // Assert
        verify(notificationService).sendNotification(argThat(request -> 
            request.getUserId().equals("user-123") &&
            request.getMessage().equals("Test notification") &&
            request.getType().equals("TEST")
        ));
    }

    @Configuration
    @Import(NotificationController.class)
    static class TestConfig {
        // Minimal configuration - only imports the controller under test
        // All dependencies are mocked via @MockBean
    }
}

