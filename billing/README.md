# Billing Module

## Description

The Billing module handles payment processing with retry logic and resilience patterns. It provides:

- **Payment Processing**: Create and manage payment records
- **Retry Logic**: Automatic retry mechanism for failed payment attempts (configurable max attempts and delay)
- **Payment Provider Integration**: Interface for external payment providers (with mock implementation)
- **Payment Status Tracking**: Track payment status (PENDING, PROCESSING, SUCCESS, FAILED, REFUNDED)
- **Metrics**: Track payment attempts via Micrometer
- **Adapter Pattern**: BillingAdapter interface for integration with other modules (e.g., order module)

This module ensures reliable payment processing with built-in retry mechanisms and error handling.

## Running Locally

This module is a library module and cannot be run standalone. It is included as part of the main application.

To run the application with this module included, see the [admin module README](../admin/README.md) for complete setup and running instructions.

**Module-specific Configuration** (optional, in `billing/src/main/resources/application.properties`):
```properties
billing.payment.retry.max-attempts=3
billing.payment.retry.delay-ms=100
```

**Quick start** (from project root):
```bash
# Build the module
mvn clean install -pl billing

# Run the full application (admin module)
mvn spring-boot:run -pl admin -Dspring-boot.run.profiles=local
```

## Running Tests

```bash
# Run all tests
mvn test -pl billing

# Run only unit tests
mvn test -pl billing -Dtest=*Test

# Run specific test class
mvn test -pl billing -Dtest=BillingServiceTest
```

## Running Integration Tests

This module does not have standalone integration tests. Payment operations are tested as part of the order flow integration tests in the `order` module.

To run integration tests that include payment processing:

```bash
# From project root - run order integration tests
mvn test -pl order -Dtest=OrderFlowIT
```

## Example API Calls

### Create Payment
```bash
curl -X POST http://localhost:8080/api/v1/billing/payments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "orderId": "order-uuid",
    "amount": 199.98
  }'
```

### Get Payment by ID
```bash
curl -X GET http://localhost:8080/api/v1/billing/payments/{id} \
  -H "Authorization: Bearer <token>"
```
