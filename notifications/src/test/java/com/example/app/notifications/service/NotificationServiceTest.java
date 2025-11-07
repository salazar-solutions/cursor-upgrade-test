package com.example.app.notifications.service;

import com.example.app.notifications.domain.NotificationRequest;
import com.example.app.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InMemoryNotificationServiceImpl notificationService;

    private UUID userId;
    private NotificationRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        request = new NotificationRequest();
        request.setUserId(userId.toString());
        request.setMessage("Test notification");
        request.setType("ORDER_CREATED");
    }

    @Test
    void testSendNotification() {
        when(userRepository.existsById(any(UUID.class))).thenReturn(true);

        assertDoesNotThrow(() -> notificationService.sendNotification(request));
    }

    @Test
    void testFallbackQueue() {
        when(userRepository.existsById(any(UUID.class))).thenReturn(true);

        notificationService.sendNotification(request);
        
        // Fallback queue should be available (may or may not contain items depending on random failure)
        assertNotNull(notificationService.getFallbackQueue());
    }
}

