package com.example.app.notifications.service;

import com.example.app.notifications.domain.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * In-memory implementation of notification service with fallback queue.
 */
@Service
public class InMemoryNotificationServiceImpl implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryNotificationServiceImpl.class);
    
    private final ConcurrentLinkedQueue<NotificationRequest> fallbackQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void sendNotification(NotificationRequest request) {
        try {
            // Simulate sending notification (in production, this would call external service)
            logger.info("Sending notification to user {}: {} [{}]", 
                request.getUserId(), request.getMessage(), request.getType());
            
            // Simulate potential failure
            if (Math.random() < 0.1) { // 10% failure rate for testing
                throw new RuntimeException("Notification service temporarily unavailable");
            }
        } catch (Exception e) {
            logger.warn("Failed to send notification, storing in fallback queue", e);
            fallbackQueue.offer(request);
        }
    }

    /**
     * Get the fallback queue (for testing/admin purposes).
     */
    public ConcurrentLinkedQueue<NotificationRequest> getFallbackQueue() {
        return fallbackQueue;
    }
}

