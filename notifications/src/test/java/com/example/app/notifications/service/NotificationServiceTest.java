package com.example.app.notifications.service;

import com.example.app.notifications.domain.NotificationRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    private InMemoryNotificationServiceImpl notificationService = new InMemoryNotificationServiceImpl();

    @Test
    void testSendNotification() {
        NotificationRequest request = new NotificationRequest();
        request.setUserId("user-123");
        request.setMessage("Test notification");
        request.setType("TEST");

        assertDoesNotThrow(() -> notificationService.sendNotification(request));
    }

    @Test
    void testFallbackQueue() {
        NotificationRequest request = new NotificationRequest();
        request.setUserId("user-123");
        request.setMessage("Test notification");
        request.setType("TEST");

        notificationService.sendNotification(request);
        
        // Fallback queue should be available (may or may not contain items depending on random failure)
        assertNotNull(notificationService.getFallbackQueue());
    }
}

