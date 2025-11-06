# Notifications Module

## Description

The Notifications module provides a notification service with an in-memory fallback queue. It provides:

- **Notification Service**: Send notifications to users
- **In-Memory Queue**: Fallback queue for notifications when external services fail
- **Notification Types**: Support for different notification types (ORDER_CREATED, etc.)
- **User Notifications**: Send messages to specific users
- **Resilience**: Fallback mechanism ensures notifications are not lost

This module implements a simple in-memory notification service that can be extended to integrate with external notification providers (email, SMS, push notifications, etc.).

## Running Locally

This module is a library module and cannot be run standalone. It is included as part of the main application.

To run the application with this module included, see the [admin module README](../admin/README.md) for complete setup and running instructions.

**Quick start** (from project root):
```bash
# Build the module
mvn clean install -pl notifications

# Run the full application (admin module)
mvn spring-boot:run -pl admin -Dspring-boot.run.profiles=local
```

## Running Tests

```bash
# Run all tests
mvn test -pl notifications

# Run only unit tests
mvn test -pl notifications -Dtest=*Test

# Run specific test class
mvn test -pl notifications -Dtest=NotificationServiceTest
```

## Running Integration Tests

This module does not have standalone integration tests. Notification operations are tested as part of the order flow integration tests in the `order` module.

To run integration tests that include notifications:

```bash
# From project root - run order integration tests
mvn test -pl order -Dtest=OrderFlowIT
```

## Example API Calls

### Send Notification
```bash
curl -X POST http://localhost:8080/api/v1/notifications/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "userId": "user-123",
    "message": "Your order has been created successfully",
    "type": "ORDER_CREATED"
  }'
```

## Module Configuration

The notification service uses an in-memory implementation. In a production environment, you would:

1. Replace `InMemoryNotificationServiceImpl` with an implementation that integrates with external services
2. Configure external service credentials in `notifications/src/main/resources/application.properties`
3. Implement proper error handling and retry logic for external services
