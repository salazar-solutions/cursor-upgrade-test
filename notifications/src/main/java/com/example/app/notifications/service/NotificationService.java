package com.example.app.notifications.service;

import com.example.app.notifications.domain.NotificationRequest;

/**
 * Service interface for notification operations.
 */
public interface NotificationService {
    void sendNotification(NotificationRequest request);
}

