# Admin Module

## Description

The Admin module is the main application module that provides admin endpoints, monitoring, and serves as the entry point for the entire multi-module Spring Boot application. It provides:

- **Application Entry Point**: Main Spring Boot application class (`Application.java`)
- **Health Checks**: Health endpoint for monitoring
- **Metrics**: Metrics endpoint for observability
- **User Administration**: Admin endpoints for user management
- **API Documentation**: Swagger/OpenAPI documentation

This module depends on all other modules and serves as the deployable application artifact.

## Running Locally

1. **Prerequisites**:
   - Java 8+
   - Maven 3.6+
   - PostgreSQL 13+ (or use Docker)

2. **Database Setup**:
   ```bash
   # Create database
   createdb -U postgres devdb
   
   # Run migrations (from project root)
   psql -U postgres -d devdb -f db/patches/001_create_schema.sql
   psql -U postgres -d devdb -f db/patches/002_insert_catalog_data.sql
   psql -U postgres -d devdb -f db/patches/003_insert_dummy_data.sql
   ```

3. **Configure Database** (in `admin/src/main/resources/application-local.properties`):
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/devdb
   spring.datasource.username=devuser
   spring.datasource.password=devpass
   ```

4. **Set Active Profile** (in `admin/src/main/resources/application.properties`):
   ```properties
   spring.profiles.active=local
   ```

   Or pass as JVM argument:
   ```bash
   mvn spring-boot:run -pl admin -Dspring-boot.run.profiles=local
   ```

5. **Run the Application** (from project root):
   ```bash
   mvn spring-boot:run -pl admin
   ```

   Or run directly:
   ```bash
   cd admin
   mvn spring-boot:run
   ```

6. **Access the Application**:
   - Base URL: `http://localhost:8080`
   - API Documentation: `http://localhost:8080/swagger-ui.html`
   - Health Check: `http://localhost:8080/api/v1/admin/health`
   - Metrics: `http://localhost:8080/api/v1/admin/metrics`
   - Actuator: `http://localhost:8080/actuator/health`

## Running Tests

```bash
# Run all tests (from project root)
mvn test

# Run tests for admin module only
mvn test -pl admin

# Run tests for all modules
mvn test
```

## Running Integration Tests

Integration tests use Testcontainers and require Docker:

```bash
# Run all integration tests (from project root)
mvn test -Dtest=*IT

# Run integration tests for specific module
mvn test -pl user -Dtest=*IT
mvn test -pl product -Dtest=*IT
mvn test -pl order -Dtest=*IT
```

**Note**: Integration tests automatically start PostgreSQL containers using Testcontainers. Make sure Docker is running.

## Example API Calls

### Health Check
```bash
curl -X GET http://localhost:8080/api/v1/admin/health
```

### Metrics
```bash
curl -X GET http://localhost:8080/api/v1/admin/metrics \
  -H "Authorization: Bearer <token>"
```

### List All Users (Admin only)
```bash
curl -X GET http://localhost:8080/api/v1/admin/users \
  -H "Authorization: Bearer <admin-token>"
```

### Disable User (Admin only)
```bash
curl -X POST http://localhost:8080/api/v1/admin/users/{id}/disable \
  -H "Authorization: Bearer <admin-token>"
```

## Configuration Profiles

The admin module supports multiple profiles:

- **default**: Uses `application.properties` (production-like settings)
- **local**: Uses `application-local.properties` (development database, verbose logging)
- **test**: Uses `application-test.properties` (Testcontainers, permissive security)

Set the active profile in `application.properties` or via environment variable:
```bash
export SPRING_PROFILES_ACTIVE=local
```

## Building the Application

```bash
# Build all modules
mvn clean install

# Build only admin module (with dependencies)
mvn clean install -pl admin -am

# Build and package as JAR
mvn clean package -pl admin
```

The resulting JAR will be in `admin/target/admin-1.0.0-SNAPSHOT.jar` and can be run with:
```bash
java -jar admin/target/admin-1.0.0-SNAPSHOT.jar
```
