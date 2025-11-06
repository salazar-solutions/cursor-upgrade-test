# Inventory Module

## Description

The Inventory module manages product inventory with atomic reservation and release operations. It provides:

- **Inventory Management**: Track available and reserved quantities for products
- **Atomic Operations**: Pessimistic locking for thread-safe inventory operations
- **Reservation System**: Reserve inventory for orders with atomic updates
- **Release System**: Release reserved inventory back to available stock
- **Stock Validation**: Check available quantities before reservation
- **Metrics**: Track failed inventory reservations via Micrometer

This module ensures data consistency and prevents race conditions when multiple orders attempt to reserve the same inventory simultaneously.

## Running Locally

This module is a library module and cannot be run standalone. It is included as part of the main application.

To run the application with this module included, see the [admin module README](../admin/README.md) for complete setup and running instructions.

**Quick start** (from project root):
```bash
# Build the module
mvn clean install -pl inventory

# Run the full application (admin module)
mvn spring-boot:run -pl admin -Dspring-boot.run.profiles=local
```

## Running Tests

```bash
# Run all tests
mvn test -pl inventory

# Run only unit tests
mvn test -pl inventory -Dtest=*Test

# Run specific test class
mvn test -pl inventory -Dtest=InventoryServiceTest
```

## Running Integration Tests

This module does not have standalone integration tests. Inventory operations are tested as part of the order flow integration tests in the `order` module.

To run integration tests that include inventory operations:

```bash
# From project root - run order integration tests
mvn test -pl order -Dtest=OrderFlowIT
```

## Example API Calls

### Get Inventory for Product
```bash
curl -X GET http://localhost:8080/api/v1/inventory/{productId} \
  -H "Authorization: Bearer <token>"
```

### Reserve Inventory
```bash
curl -X POST http://localhost:8080/api/v1/inventory/{productId}/reserve \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "quantity": 10
  }'
```

### Release Inventory
```bash
curl -X POST http://localhost:8080/api/v1/inventory/{productId}/release \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "quantity": 5
  }'
```
