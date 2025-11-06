# Regression Test Suite Guide

This guide provides comprehensive instructions for setting up, running, and maintaining the regression test suite for the Spring Boot multi-module application.

## Table of Contents

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Setup Instructions](#setup-instructions)
4. [Profile Activation](#profile-activation)
5. [Running Tests](#running-tests)
6. [Tag-Based Filtering](#tag-based-filtering)
7. [Test Structure](#test-structure)
8. [Troubleshooting](#troubleshooting)
9. [Best Practices](#best-practices)

## Overview

The regression test suite uses:
- **Cucumber** with **Gherkin** for behavior-driven development (BDD) tests
- **Rest Assured** for REST API testing
- **Citrus** for HTTP messaging and orchestration
- **JUnit 5** as the test framework

Tests are organized by functional area and can be executed selectively using tags.

## Prerequisites

### Required Software

1. **Java 8 (JDK 1.8)**
   ```bash
   java -version
   ```

2. **Maven 3.6+**
   ```bash
   mvn -version
   ```

3. **PostgreSQL 13+** (for local database)
   - Database: `devdb`
   - Schema: `cursordb`
   - User: `devuser`
   - Password: `devpass`

4. **Docker** (optional, for Testcontainers if needed)

### Database Setup

1. Create the database:
   ```sql
   CREATE DATABASE devdb;
   ```

2. Run migration scripts:
   ```bash
   psql -U postgres -d devdb -f db/patches/001_create_schema.sql
   psql -U postgres -d devdb -f db/patches/002_insert_catalog_data.sql
   psql -U postgres -d devdb -f db/patches/003_insert_dummy_data.sql
   ```

## Setup Instructions

### 1. Build the Application

From the project root:

```bash
mvn clean install -DskipTests
```

### 2. Verify Configuration

Ensure `regression-test/src/test/resources/application-regression.properties` is configured correctly:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/devdb
spring.datasource.username=devuser
spring.datasource.password=devpass
server.port=8080
api.base.url=http://localhost:8080
```

### 3. Start the Application (if running tests against a running instance)

```bash
# Option 1: Run from admin module
mvn spring-boot:run -pl admin -Dspring-boot.run.profiles=local

# Option 2: Use your IDE to run RegressionTestApplication
```

## Profile Activation

**IMPORTANT**: Regression tests **ONLY** run when the `regression` profile is active.

### Activation Methods

#### Method 1: System Property (Recommended)

```bash
mvn test -pl regression-test -Dspring.profiles.active=regression
```

#### Method 2: Environment Variable

```bash
export SPRING_PROFILES_ACTIVE=regression
mvn test -pl regression-test
```

#### Method 3: Maven Properties

```bash
mvn test -pl regression-test -Dspring.profiles.active=regression
```

### Profile Configuration

The regression profile uses:
- **Active Profiles**: `local`, `regression`
- **Database**: Local PostgreSQL instance
- **Server Port**: 8080
- **Base URL**: `http://localhost:8080`

## Running Tests

### Run All Regression Tests

```bash
# From project root
mvn test -pl regression-test -Dspring.profiles.active=regression

# Or from regression-test directory
cd regression-test
mvn test -Dspring.profiles.active=regression
```

### Run Specific Test Classes

```bash
# Run Cucumber tests only
mvn test -pl regression-test -Dspring.profiles.active=regression -Dtest=CucumberRegressionTest

# Run Citrus tests only
mvn test -pl regression-test -Dspring.profiles.active=regression -Dtest=CitrusMessagingTest
```

### Run Tests from IDE

1. Set system property: `spring.profiles.active=regression`
2. Run `CucumberRegressionTest` or individual feature files
3. Ensure active profiles: `local,regression`

## Tag-Based Filtering

Tests are organized with tags for selective execution:

### Available Tags

- `@regression` - All regression tests (default filter)
- `@user` - User management tests
- `@authentication` / `@login` - Authentication tests
- `@product` - Product management tests
- `@inventory` - Inventory management tests
- `@order` - Order management tests
- `@checkout` - Checkout flow tests
- `@billing` / `@payment` - Payment processing tests
- `@admin` - Admin operations tests
- `@notifications` - Notification tests
- `@e2e` - End-to-end flow tests

### Running Tests by Tag

#### Using Maven

```bash
# Run only user tests
mvn test -pl regression-test -Dspring.profiles.active=regression \
  -Dcucumber.filter.tags="@user"

# Run only order and checkout tests
mvn test -pl regression-test -Dspring.profiles.active=regression \
  -Dcucumber.filter.tags="@order or @checkout"

# Run end-to-end tests
mvn test -pl regression-test -Dspring.profiles.active=regression \
  -Dcucumber.filter.tags="@e2e"
```

#### Using Cucumber Options (in code)

Modify `CucumberRegressionTest.java`:

```java
@ConfigurationParameter(
    key = Constants.FILTER_TAGS_PROPERTY_NAME, 
    value = "@regression and @order"
)
```

### Excluding Tags

```bash
# Run all tests except admin tests
mvn test -pl regression-test -Dspring.profiles.active=regression \
  -Dcucumber.filter.tags="@regression and not @admin"
```

## Test Structure

### Directory Structure

```
regression-test/
├── src/
│   └── test/
│       ├── java/
│       │   └── com/example/app/regression/
│       │       ├── config/              # Configuration classes
│       │       ├── steps/               # Cucumber step definitions
│       │       ├── citrus/              # Citrus test examples
│       │       └── CucumberRegressionTest.java
│       └── resources/
│           ├── application-regression.properties
│           └── features/                # Gherkin feature files
│               ├── user/
│               ├── product/
│               ├── inventory/
│               ├── order/
│               ├── billing/
│               ├── admin/
│               └── notifications/
└── pom.xml
```

### Feature Files

Feature files are located in `src/test/resources/features/`:

- `user/user_management.feature` - User CRUD operations
- `user/user_authentication.feature` - Login and authentication
- `product/product_management.feature` - Product catalog management
- `inventory/inventory_management.feature` - Stock management
- `order/order_management.feature` - Order operations
- `order/order_complete_flow.feature` - End-to-end order flow
- `billing/payment_processing.feature` - Payment processing
- `admin/admin_operations.feature` - Admin tasks
- `notifications/notification_management.feature` - Notification sending

### Step Definitions

Step definitions are in `src/test/java/com/example/app/regression/steps/`:

- `BaseStepDefinitions.java` - Common utilities and API setup
- `UserStepDefinitions.java` - User management steps
- `ProductStepDefinitions.java` - Product management steps
- `InventoryStepDefinitions.java` - Inventory management steps
- `OrderStepDefinitions.java` - Order management steps
- `BillingStepDefinitions.java` - Payment processing steps
- `AdminStepDefinitions.java` - Admin operations steps
- `NotificationStepDefinitions.java` - Notification steps

## Troubleshooting

### Common Issues

#### 1. Tests Not Running

**Problem**: Tests are skipped or not executed.

**Solution**: 
- Verify `spring.profiles.active=regression` is set
- Check that `@EnabledIfSystemProperty` annotation is present
- Ensure Maven surefire plugin configuration is correct

#### 2. Database Connection Errors

**Problem**: `Connection refused` or `Authentication failed`.

**Solution**:
- Verify PostgreSQL is running: `pg_isready`
- Check database credentials in `application-regression.properties`
- Ensure database `devdb` exists
- Verify schema `cursordb` exists

#### 3. API Not Available

**Problem**: `API is not available. Health check failed.`

**Solution**:
- Start the application: `mvn spring-boot:run -pl admin -Dspring-boot.run.profiles=local`
- Verify server is running on port 8080: `curl http://localhost:8080/api/v1/admin/health`
- Check `api.base.url` in `application-regression.properties`

#### 4. Port Already in Use

**Problem**: `Port 8080 is already in use`.

**Solution**:
- Stop other instances: `lsof -ti:8080 | xargs kill -9` (Linux/Mac)
- Or change port in `application-regression.properties`: `server.port=8081`

#### 5. Cucumber Step Definitions Not Found

**Problem**: `Step definition not found` errors.

**Solution**:
- Verify `@Component` annotation on step definition classes
- Check package scanning: `@SpringBootTest(scanBasePackages = "com.example.app")`
- Ensure `CucumberSpringConfiguration` is in the glue path

#### 6. Test Failures Due to Data Dependencies

**Problem**: Tests fail because of existing or missing data.

**Solution**:
- Run database migration scripts before tests
- Use unique test data (timestamps, UUIDs)
- Clean up test data after scenarios if needed

### Debug Mode

Enable debug logging:

```properties
logging.level.com.example.app.regression=DEBUG
logging.level.io.cucumber=DEBUG
logging.level.io.restassured=DEBUG
```

### View Test Reports

After running tests, view reports:

- **HTML Report**: `regression-test/target/cucumber-reports/cucumber.html`
- **JSON Report**: `regression-test/target/cucumber-reports/cucumber.json`
- **JUnit XML**: `regression-test/target/cucumber-reports/cucumber.xml`

## Best Practices

### 1. Test Data Management

- Use unique identifiers (UUIDs, timestamps) for test data
- Clean up test data when necessary
- Use Background steps for common setup

### 2. Test Organization

- Group related scenarios in feature files
- Use meaningful scenario names
- Tag tests appropriately for selective execution

### 3. Step Definitions

- Keep step definitions focused and reusable
- Use context variables (`scenarioContext`) to share data between steps
- Validate responses thoroughly

### 4. Error Handling

- Check response status codes
- Validate response bodies
- Use meaningful assertion messages

### 5. Performance

- Run tests in parallel when possible
- Use tags to run only relevant tests
- Avoid unnecessary waits or sleeps

### 6. Maintenance

- Update feature files when APIs change
- Keep step definitions synchronized with feature files
- Document complex test scenarios

## Additional Resources

- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [Rest Assured Documentation](https://rest-assured.io/)
- [Citrus Framework Documentation](https://citrusframework.org/)
- [Gherkin Syntax Reference](https://cucumber.io/docs/gherkin/reference/)

## Support

For issues or questions:
1. Check this guide first
2. Review test logs in `target/surefire-reports/`
3. Check Cucumber reports in `target/cucumber-reports/`
4. Review application logs

---

**Last Updated**: 2025-01-XX
**Version**: 1.0.0

