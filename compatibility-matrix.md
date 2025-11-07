# Dependency Compatibility Matrix

## Overview
This document provides a comprehensive comparison of dependencies before and after the Java 8 to Java 21 migration, including rationale for each upgrade and Java 21 compatibility notes.

## Core Framework Dependencies

| Dependency | Before | After | Rationale | Java 21 Compatibility |
|------------|--------|-------|-----------|----------------------|
| **Java Version** | 1.8 | 21 | Target Java version for migration | ✅ Fully compatible |
| **Spring Boot** | 2.7.18 | 3.2.5 | Required for Java 21 support. Spring Boot 2.x supports up to Java 19. Spring Boot 3.x requires Java 17+ and provides full Java 21 support. | ✅ Fully compatible |
| **Maven Compiler Plugin** | 3.11.0 | 3.13.0 | Required for Java 21 compilation support | ✅ Fully compatible |

## Build Tools and Plugins

| Dependency | Before | After | Rationale | Java 21 Compatibility |
|------------|--------|-------|-----------|----------------------|
| **JaCoCo Maven Plugin** | 0.8.11 | 0.8.12 | Java 21 bytecode support and improved coverage reporting | ✅ Fully compatible |

## Code Generation and Mapping

| Dependency | Before | After | Rationale | Java 21 Compatibility |
|------------|--------|-------|-----------|----------------------|
| **MapStruct** | 1.5.5.Final | 1.6.2 | Java 21 annotation processing support. Version 1.6.x includes fixes for Java 17+ module system and Java 21 compatibility. | ✅ Fully compatible |

## Testing Dependencies

| Dependency | Before | After | Rationale | Java 21 Compatibility |
|------------|--------|-------|-----------|----------------------|
| **Testcontainers** | 1.19.3 | 1.19.8 | Java 21 runtime support and bug fixes. Version 1.19.8 includes Java 21 compatibility improvements. | ✅ Fully compatible |
| **Cucumber** | 7.14.0 | 7.18.0 | Java 21 support and JUnit 5 compatibility improvements | ✅ Fully compatible |
| **Rest Assured** | 5.3.2 | 5.4.0 | Java 21 compatibility and HTTP client updates | ✅ Fully compatible |
| **Citrus Framework** | 4.0.0 | 4.0.0 | Verified compatible with Java 21. No upgrade required. | ✅ Compatible (verified) |

## Security and Authentication

| Dependency | Before | After | Rationale | Java 21 Compatibility |
|------------|--------|-------|-----------|----------------------|
| **JJWT (Java JWT)** | 0.11.5 | 0.12.6 | Java 21 support and API modernization. Version 0.12.x introduces new builder pattern and improved security. **Note:** API changes required (see JwtUtil.java). | ✅ Fully compatible (API changes required) |

## API Documentation

| Dependency | Before | After | Rationale | Java 21 Compatibility |
|------------|--------|-------|-----------|----------------------|
| **Springdoc OpenAPI** | 1.7.0 (springdoc-openapi-ui) | 2.5.0 (springdoc-openapi-starter-webmvc-ui) | Spring Boot 3.x compatibility. Artifact name changed from `springdoc-openapi-ui` to `springdoc-openapi-starter-webmvc-ui` for Spring Boot 3.x. Version 2.5.0 provides full Spring Boot 3.2.x support. | ✅ Fully compatible (artifact name change) |

## Jakarta EE Migration

| Package | Before | After | Rationale | Java 21 Compatibility |
|---------|--------|-------|-----------|----------------------|
| **javax.persistence** | javax.persistence.* | jakarta.persistence.* | Java EE to Jakarta EE migration. Required for Spring Boot 3.x and Java 9+. | ✅ Required for Spring Boot 3.x |
| **javax.validation** | javax.validation.* | jakarta.validation.* | Java EE to Jakarta EE migration. Required for Spring Boot 3.x and Java 9+. | ✅ Required for Spring Boot 3.x |
| **javax.servlet** | javax.servlet.* | jakarta.servlet.* | Java EE to Jakarta EE migration. Required for Spring Boot 3.x and Java 9+. | ✅ Required for Spring Boot 3.x |
| **javax.annotation** | javax.annotation.* | jakarta.annotation.* | Java EE to Jakarta EE migration. Required for Spring Boot 3.x and Java 9+. | ✅ Required for Spring Boot 3.x |

## Breaking Changes and Migration Notes

### Spring Boot 2.7.x → 3.2.x

1. **Security Configuration**
   - `WebSecurityConfigurerAdapter` deprecated → Use `SecurityFilterChain` bean
   - `authorizeRequests()` → `authorizeHttpRequests()`
   - `antMatchers()` → `requestMatchers()`
   - Lambda-based DSL for security configuration

2. **Springdoc OpenAPI**
   - Artifact name changed: `springdoc-openapi-ui` → `springdoc-openapi-starter-webmvc-ui`
   - Swagger UI path changed: `/swagger-ui.html` → `/swagger-ui/index.html`

3. **Jakarta EE**
   - All `javax.*` packages migrated to `jakarta.*`
   - Affects: JPA, Validation, Servlet API, Annotations

### JJWT 0.11.5 → 0.12.6

1. **Builder Pattern Changes**
   - `Jwts.builder().setSubject()` → `Jwts.builder().subject()`
   - `Jwts.builder().setIssuedAt()` → `Jwts.builder().issuedAt()`
   - `Jwts.builder().setExpiration()` → `Jwts.builder().expiration()`
   - `Jwts.builder().signWith(key, algorithm)` → `Jwts.builder().signWith(key)` (algorithm inferred)

2. **Parser Changes**
   - `Jwts.parserBuilder()` → `Jwts.parser()`
   - `.setSigningKey()` → `.verifyWith()`
   - `.parseClaimsJws()` → `.parseSignedClaims()`
   - `.getBody()` → `.getPayload()`

## Dependency Resolution Notes

- All dependencies resolved successfully from Maven Central
- No conflicts detected during dependency resolution
- All transitive dependencies are Java 21 compatible
- Spring Boot 3.2.5 BOM manages compatible versions of all Spring dependencies

## Verification Status

- ✅ All modules compile successfully on Java 21
- ✅ All test classes compile successfully on Java 21
- ✅ No deprecated APIs remain in active code paths
- ✅ All dependencies verified compatible with Java 21

## Future Considerations

The following dependencies were **not** upgraded as they are compatible with Java 21 and upgrades are deferred to a separate modernization phase:

- PostgreSQL Driver: Current version compatible
- Jackson: Managed by Spring Boot BOM
- Micrometer: Managed by Spring Boot BOM
- AssertJ: Current version compatible
- Awaitility: Current version compatible
- Gson: Current version compatible

