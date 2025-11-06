package com.example.app.notifications.service;

import com.example.app.notifications.domain.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * In-memory implementation of notification service with fallback queue for failed deliveries.
 * 
 * <p>This is a stub implementation that simulates notification delivery. In production,
 * this would integrate with actual notification services (email, SMS, push notifications, etc.).
 * 
 * <p><b>Current Behavior:</b>
 * <ul>
 *   <li>Logs notification requests (simulates sending)</li>
 *   <li>Simulates 10% failure rate for testing purposes</li>
 *   <li>Stores failed notifications in an in-memory fallback queue</li>
 * </ul>
 * 
 * <p><b>Fallback Queue:</b> Failed notifications are stored in a thread-safe queue
 * for potential retry processing. In production, this would be replaced with a
 * persistent message queue (RabbitMQ, Kafka, etc.).
 * 
 * <p><b>Production Note:</b> Replace this implementation with actual notification
 * service integration. Implement proper retry logic, dead-letter queues, and
 * notification delivery tracking.
 * 
 * @author Generated
 * @since 1.0.0
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
     * Retrieves the fallback queue containing failed notifications.
     * 
     * <p>This method is primarily intended for testing and administrative purposes.
     * In production, failed notifications would be handled by a message queue system.
     * 
     * @return the thread-safe queue containing failed notification requests
     */
    public ConcurrentLinkedQueue<NotificationRequest> getFallbackQueue() {
        return fallbackQueue;
    }
}

