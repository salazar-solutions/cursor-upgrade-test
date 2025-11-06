package com.example.app.notifications.service;

import com.example.app.notifications.domain.NotificationRequest;

/**
 * Service interface for notification delivery operations.
 * 
 * <p>This service handles sending notifications to users for various events
 * such as order creation, status changes, and system alerts. Notifications
 * are sent asynchronously and failures are handled gracefully to avoid
 * impacting the main business flow.
 * 
 * @author Generated
 * @since 1.0.0
 */
public interface NotificationService {
    /**
     * Sends a notification to a user.
     * 
     * <p>The notification is processed asynchronously. Failures are logged
     * but do not throw exceptions, ensuring notification failures don't
     * impact the calling operation.
     * 
     * @param request the notification request containing user ID, message, and type
     * @throws Exception if notification delivery fails (typically caught and logged by callers)
     */
    void sendNotification(NotificationRequest request);
}

