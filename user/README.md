# User Module

## Description

The User module manages user accounts, authentication, and authorization for the application. It provides:

- **User Management**: CRUD operations for user accounts
- **Authentication**: JWT-based authentication with login endpoints
- **Authorization**: Role-based access control (USER, ADMIN roles)
- **Password Security**: BCrypt password hashing
- **User Profiles**: User information retrieval and updates

This module provides RESTful endpoints for user registration, login, and user management operations.

## Running Locally

This module is a library module and cannot be run standalone. It is included as part of the main application.

To run the application with this module included, see the [admin module README](../admin/README.md) for complete setup and running instructions.

**Quick start** (from project root):
```bash
# Build the module
mvn clean install -pl user

# Run the full application (admin module)
mvn spring-boot:run -pl admin -Dspring-boot.run.profiles=local
```

## Running Tests

```bash
# Run all tests (unit + integration)
mvn test -pl user

# Run only unit tests
mvn test -pl user -Dtest=*Test

# Run specific test class
mvn test -pl user -Dtest=UserServiceTest
```

## Running Integration Tests

Integration tests use Testcontainers and require Docker:

```bash
# Run integration tests
mvn test -pl user -Dtest=*IT

# Run specific integration test
mvn test -pl user -Dtest=UserControllerIT
```

**Note**: Integration tests automatically start a PostgreSQL container using Testcontainers. Make sure Docker is running.

## Example API Calls

### Create User
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "password123",
    "roles": ["USER"]
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/v1/users/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123"
  }'
```

### Get User by ID
```bash
curl -X GET http://localhost:8080/api/v1/users/{id} \
  -H "Authorization: Bearer <token>"
```

### Update User
```bash
curl -X PUT http://localhost:8080/api/v1/users/{id} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "username": "updateduser",
    "email": "updated@example.com"
  }'
```
