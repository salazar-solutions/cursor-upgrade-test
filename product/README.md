# Product Module

## Description

The Product module manages the product catalog for the e-commerce application. It provides:

- **Product Management**: CRUD operations for products
- **Product Search**: Full-text search with pagination
- **Inventory Tracking**: Available quantity tracking
- **SKU Management**: Unique SKU validation
- **Product Information**: Product details including name, description, price, and availability

This module provides RESTful endpoints for creating, retrieving, searching, and managing products in the catalog.

## Running Locally

This module is a library module and cannot be run standalone. It is included as part of the main application.

To run the application with this module included, see the [admin module README](../admin/README.md) for complete setup and running instructions.

**Quick start** (from project root):
```bash
# Build the module
mvn clean install -pl product

# Run the full application (admin module)
mvn spring-boot:run -pl admin -Dspring-boot.run.profiles=local
```

## Running Tests

```bash
# Run all tests (unit + integration)
mvn test -pl product

# Run only unit tests
mvn test -pl product -Dtest=*Test

# Run specific test class
mvn test -pl product -Dtest=ProductServiceTest
```

## Running Integration Tests

Integration tests use Testcontainers and require Docker:

```bash
# Run integration tests
mvn test -pl product -Dtest=*IT

# Run specific integration test
mvn test -pl product -Dtest=ProductControllerIT
```

**Note**: Integration tests automatically start a PostgreSQL container using Testcontainers. Make sure Docker is running.

## Example API Calls

### Create Product
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "sku": "SKU-001",
    "name": "Test Product",
    "description": "Test Description",
    "price": 99.99,
    "availableQty": 100
  }'
```

### Get Product by ID
```bash
curl -X GET http://localhost:8080/api/v1/products/{id} \
  -H "Authorization: Bearer <token>"
```

### Search Products
```bash
curl -X GET "http://localhost:8080/api/v1/products?search=test&page=0&size=20" \
  -H "Authorization: Bearer <token>"
```

### Update Product
```bash
curl -X PUT http://localhost:8080/api/v1/products/{id} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "name": "Updated Product Name",
    "price": 129.99,
    "availableQty": 150
  }'
```
