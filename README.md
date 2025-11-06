# Multi-Module Spring Boot Application

A comprehensive Maven multi-module Spring Boot 2.7.x application running on Java 8, implementing a complete e-commerce order management system.

## Project Structure

The application consists of 8 modules:

- **common** - Shared utilities, exception handling, security configuration
- **user** - User management and authentication
- **product** - Product catalog management
- **inventory** - Inventory management with reservation/release
- **order** - Order processing with business logic
- **billing** - Payment processing with retry logic
- **notifications** - Notification service with fallback queue
- **admin** - Admin endpoints and monitoring

## Prerequisites

- Java 8 (JDK 1.8)
- Maven 3.6+
- PostgreSQL 13+ (for local development)
- Docker (optional, for Testcontainers in integration tests)

## Database Setup

### Local PostgreSQL

1. Create database:
```sql
CREATE DATABASE devdb;
```

2. Run migration scripts from `db/patches/`:
```bash
psql -U postgres -d devdb -f db/patches/001_create_schema.sql
psql -U postgres -d devdb -f db/patches/002_insert_catalog_data.sql
psql -U postgres -d devdb -f db/patches/003_insert_dummy_data.sql
```

### Database Connection

The application is configured to connect to:
- **Database**: `devdb`
- **Schema**: `cursordb`
- **Local**: `jdbc:postgresql://localhost:5432/devdb`
- **Test**: Uses Testcontainers (automatic PostgreSQL container)

## Building the Application

```bash
# Build all modules
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Build specific module
mvn clean install -pl user -am
```

## Running the Application

### Local Profile

```bash
mvn spring-boot:run -pl user -Dspring-boot.run.profiles=local
```

Or set the active profile in `admin/src/main/resources/application.properties`:
```properties
spring.profiles.active=local
```

### Using Spring Boot Application Class

Since this is a multi-module project, you'll need a main application class. Create one in a module that depends on all others (e.g., `admin` module):

```java
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.app")
@EntityScan(basePackages = "com.example.app")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## Running Tests

### Unit Tests

```bash
# Run all unit tests
mvn test

# Run tests for specific module
mvn test -pl user

# Run specific test class
mvn test -Dtest=UserServiceTest
```

### Integration Tests

Integration tests use Testcontainers and require Docker:

```bash
# Run all tests including integration tests
mvn test

# Run only integration tests
mvn test -Dtest=*IT
```

### Test Profile

Tests run with the `test` profile which:
- Uses Testcontainers for database
- Permits all requests (no authentication)
- Uses hardcoded JWT secret for testing

## API Documentation

Once the application is running, access:
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

## Example API Calls

### Create User

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "roles": ["USER"]
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/v1/users/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### Create Product

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "SKU-001",
    "name": "Test Product",
    "description": "Test Description",
    "price": 99.99,
    "availableQty": 100
  }'
```

### Create Order

```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "orderLines": [
      {
        "productId": "660e8400-e29b-41d4-a716-446655440000",
        "quantity": 2
      }
    ]
  }'
```

## Module Documentation

Each module has its own README with:
- Module purpose
- Test commands
- Example API calls

See individual module directories for details.

## Key Features

- **JWT Authentication** - Token-based authentication
- **Global Exception Handling** - Standardized error responses
- **Pagination** - Paged responses for list endpoints
- **Observability** - Micrometer metrics and correlation IDs
- **Validation** - Request validation with clear error messages
- **Database Migrations** - SQL scripts with rollback support
- **Integration Tests** - End-to-end tests with Testcontainers

## Technologies

- Spring Boot 2.7.18
- Spring Data JPA
- Spring Security
- MapStruct (for object mapping)
- Micrometer (metrics)
- Testcontainers (integration tests)
- PostgreSQL
- JWT (JJWT)

## Package Structure

- **entity** - JPA entities for persistence
- **domain** - Request models/DTOs coming into controllers
- **dto** - Response DTOs returned from controllers
- **repository** - Spring Data repositories
- **service** - Business logic
- **controller** - REST endpoints
- **mapper** - Object mapping (MapStruct or manual)
- **config** - Configuration classes
- **exception** - Custom exceptions

## Troubleshooting

### Database Connection Issues

- Ensure PostgreSQL is running
- Check connection string in `application.yml`
- Verify database exists: `CREATE DATABASE devdb;`

### Build Issues

- Ensure Java 8 is installed: `java -version`
- Clear Maven cache: `mvn clean`
- Check for dependency conflicts: `mvn dependency:tree`

### Test Failures

- Ensure Docker is running (for Testcontainers)
- Check test profile configuration
- Review test logs for specific errors

## License

This is a sample application for demonstration purposes.

