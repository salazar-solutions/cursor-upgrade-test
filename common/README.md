# Common Module

## Description

The Common module provides shared utilities, configurations, and infrastructure components used across all other modules in the multi-module application. This module includes:

- **Exception Handling**: Global exception handler with standardized error responses
- **Security Configuration**: JWT authentication filter, security configuration, and password encoding
- **DTOs**: Common data transfer objects like `PagedResponse` and `ApiError`
- **Utilities**: Date/time mappers, UUID utilities
- **Filters**: Correlation ID filter for request tracing
- **Metrics**: Micrometer configuration for observability

This is a library module and cannot be run standalone. It must be used as a dependency by other modules.

## Running Locally

This module is a library and cannot be run independently. To use this module:

1. Build the module:
```bash
mvn clean install -pl common
```

2. The module will be available as a dependency for other modules in the project.

## Running Tests

```bash
# Run all unit tests
mvn test -pl common

# Run specific test class
mvn test -pl common -Dtest=GlobalExceptionHandlerTest
```

## Running Integration Tests

This module does not have integration tests as it's a library module. Integration tests are available in modules that use this common module (e.g., `user`, `order`).

## Dependencies

- Spring Boot Web Starter
- Spring Boot Security Starter
- JWT (JJWT library)
- Micrometer Core
- Spring Boot Actuator

