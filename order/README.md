# Order Module

## Description

The Order module manages order processing with comprehensive business logic. It provides:

- **Order Creation**: Create orders with multiple line items, validate products, reserve inventory, and process payments
- **Order Status Management**: Track order status transitions (PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED)
- **Order History**: Maintain history of status changes
- **Business Logic**: Orchestrates inventory reservation, payment processing, and notifications
- **Integration**: Coordinates with inventory, billing, product, and notification modules
- **Metrics**: Track order creation via Micrometer

This module is the core of the e-commerce system, orchestrating the complete order lifecycle from creation to delivery.

## Running Locally

This module is a library module and cannot be run standalone. It is included as part of the main application.

To run the application with this module included, see the [admin module README](../admin/README.md) for complete setup and running instructions.

**Quick start** (from project root):
```bash
# Build the module
mvn clean install -pl order

# Run the full application (admin module)
mvn spring-boot:run -pl admin -Dspring-boot.run.profiles=local
```

## Running Tests

```bash
# Run all tests (unit + integration)
mvn test -pl order

# Run only unit tests
mvn test -pl order -Dtest=*Test

# Run specific test class
mvn test -pl order -Dtest=OrderServiceTest
```

## Running Integration Tests

Integration tests use Testcontainers and require Docker:

```bash
# Run integration tests (end-to-end order flow)
mvn test -pl order -Dtest=*IT

# Run specific integration test
mvn test -pl order -Dtest=OrderFlowIT
```

**Note**: Integration tests automatically start a PostgreSQL container using Testcontainers. Make sure Docker is running.

The `OrderFlowIT` test performs a complete end-to-end flow:
1. Creates a user
2. Creates a product
3. Creates inventory
4. Creates an order
5. Verifies database state

## Example API Calls

### Create Order
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "userId": "user-uuid",
    "orderLines": [
      {
        "productId": "product-uuid",
        "quantity": 2
      }
    ]
  }'
```

### Get Order by ID
```bash
curl -X GET http://localhost:8080/api/v1/orders/{id} \
  -H "Authorization: Bearer <token>"
```

### Get Orders (with pagination and filters)
```bash
curl -X GET "http://localhost:8080/api/v1/orders?userId={userId}&status=PENDING&page=0&size=20" \
  -H "Authorization: Bearer <token>"
```

### Change Order Status
```bash
curl -X POST http://localhost:8080/api/v1/orders/{id}/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "newStatus": "CONFIRMED"
  }'
```
