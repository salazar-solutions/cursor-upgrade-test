# Payment Module

## Description

The Payment module provides core payment processing functionality with payment provider integration. It provides:

- **Payment Processing**: Process payments for orders with provider integration
- **Payment Provider Interface**: Extensible interface for payment providers (with mock implementation)
- **Payment Status Management**: Track payment status (PENDING, SUCCESS, FAILED, etc.)
- **Payment Persistence**: Store and retrieve payment records
- **Provider Reference Tracking**: Track external payment provider references

This module serves as the core payment processing layer that can be integrated with external payment providers (Stripe, PayPal, etc.). The `billing` module acts as an orchestration layer that coordinates payment operations using this module.

## Running Locally

This module is a library module and cannot be run standalone. It is included as part of the main application.

To run the application with this module included, see the [admin module README](../admin/README.md) for complete setup and running instructions.

**Quick start** (from project root):
```bash
# Build the module
mvn clean install -pl payment

# Run the full application (admin module)
mvn spring-boot:run -pl admin -Dspring-boot.run.profiles=local
```

## Running Tests

```bash
# Run all tests
mvn test -pl payment

# Run only unit tests
mvn test -pl payment -Dtest=*Test

# Run integration tests
mvn test -pl payment -Dtest=*IT
```

## Running Integration Tests

Integration tests use Testcontainers with PostgreSQL and require the integration profile:

```bash
# Run integration tests
mvn test -pl payment -Dtest=PaymentServiceIT -Dspring.profiles.active=integration

# Or run all tests including integration tests
mvn verify -pl payment -Dspring.profiles.active=integration
```

**Note**: Ensure PostgreSQL is accessible or Testcontainers will start a container automatically.

## Payment Provider Integration

The module uses a `PaymentProvider` interface for external payment provider integration:

- **Current Implementation**: `PaymentProviderImpl` provides a mock implementation that simulates payment processing
- **Production**: Replace with actual payment provider implementation (Stripe, PayPal, etc.)
- **Error Handling**: `PaymentProcessingException` is thrown when payment processing fails

## Module Structure

- **Service Layer**: `PaymentService` - Core payment processing operations
- **Provider Layer**: `PaymentProvider` - Interface for external payment providers
- **Entity Layer**: `Payment`, `PaymentStatus` - Payment domain entities
- **Repository Layer**: `PaymentRepository` - Data access layer

## Dependencies

- Spring Boot Data JPA
- PostgreSQL (runtime)
- Common module (foundation)
- Testcontainers (test scope)

## Integration with Other Modules

- **Used by**: `billing` module (orchestration layer)
- **Coordinates with**: Payment providers (external services)
- **Provides**: Payment processing services for order workflows

