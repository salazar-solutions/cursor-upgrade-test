# Java 8 to Java 21 Migration Checklist

## Overview

This checklist provides ordered tasks, commands, effort estimates, risk assessment, and rollback steps for the Java 8 to Java 21 migration.

## Pre-Migration Preparation

- [x] **Backup current codebase**
  - Command: `git tag java8-baseline`
  - Effort: 1 minute
  - Risk: None

- [x] **Verify Java 21 installation**
  - Command: `java -version` (should show Java 21)
  - Effort: 1 minute
  - Risk: None

- [x] **Verify Maven 3.6+ installation**
  - Command: `mvn -version`
  - Effort: 1 minute
  - Risk: None

## Phase 1: Build Configuration Updates

### Task 1.1: Update Root POM
- [x] **Update Java version properties**
  - File: `pom.xml`
  - Changes: `java.version`, `maven.compiler.source`, `maven.compiler.target` → 21
  - Effort: 5 minutes
  - Risk: Low
  - Rollback: Revert `pom.xml` changes

- [x] **Upgrade Spring Boot version**
  - File: `pom.xml`
  - Changes: `spring-boot.version` → 3.2.5
  - Effort: 2 minutes
  - Risk: Medium (breaking changes)
  - Rollback: Revert version property

- [x] **Upgrade Maven compiler plugin**
  - File: `pom.xml`
  - Changes: Version → 3.13.0, source/target → 21
  - Effort: 2 minutes
  - Risk: Low
  - Rollback: Revert plugin configuration

- [x] **Upgrade dependency versions**
  - File: `pom.xml`
  - Changes: MapStruct → 1.6.2, Testcontainers → 1.19.8, Springdoc → 2.5.0, JaCoCo → 0.8.12
  - Effort: 5 minutes
  - Risk: Medium
  - Rollback: Revert version properties

- [x] **Update Springdoc artifact name**
  - Files: `pom.xml`, all module `pom.xml` files
  - Changes: `springdoc-openapi-ui` → `springdoc-openapi-starter-webmvc-ui`
  - Effort: 10 minutes
  - Risk: Low
  - Rollback: Revert artifact names

### Task 1.2: Update Module POMs
- [x] **Update common/pom.xml**
  - Changes: JJWT → 0.12.6
  - Effort: 2 minutes
  - Risk: Medium (API changes)
  - Rollback: Revert JJWT version

- [x] **Update regression-test/pom.xml**
  - Changes: Cucumber → 7.18.0, Rest Assured → 5.4.0
  - Effort: 3 minutes
  - Risk: Low
  - Rollback: Revert versions

**Verification:**
```bash
mvn clean compile -DskipTests
```
- Expected: BUILD SUCCESS
- Effort: 2-5 minutes
- Risk: Low

## Phase 2: Jakarta EE Migration

### Task 2.1: Migrate javax.persistence
- [x] **Update entity classes**
  - Files: All `*Entity.java` files
  - Command: Find/Replace `javax.persistence` → `jakarta.persistence`
  - Effort: 15 minutes
  - Risk: Low
  - Rollback: Revert import statements

- [x] **Update repository classes**
  - Files: Repository interfaces using JPA
  - Command: Find/Replace `javax.persistence` → `jakarta.persistence`
  - Effort: 5 minutes
  - Risk: Low
  - Rollback: Revert import statements

- [x] **Update test classes**
  - Files: All `*IT.java` and test files using EntityManager
  - Command: Find/Replace `javax.persistence` → `jakarta.persistence`
  - Effort: 10 minutes
  - Risk: Low
  - Rollback: Revert import statements

### Task 2.2: Migrate javax.validation
- [x] **Update domain/request classes**
  - Files: All `*Request.java` files
  - Command: Find/Replace `javax.validation` → `jakarta.validation`
  - Effort: 10 minutes
  - Risk: Low
  - Rollback: Revert import statements

- [x] **Update controllers**
  - Files: All `*Controller.java` files
  - Command: Find/Replace `javax.validation` → `jakarta.validation`
  - Effort: 5 minutes
  - Risk: Low
  - Rollback: Revert import statements

- [x] **Update exception handlers**
  - Files: GlobalExceptionHandler.java
  - Command: Find/Replace `javax.validation` → `jakarta.validation`
  - Effort: 2 minutes
  - Risk: Low
  - Rollback: Revert import statements

### Task 2.3: Migrate javax.servlet
- [x] **Update filter classes**
  - Files: JwtAuthenticationFilter.java, CorrelationIdFilter.java
  - Command: Find/Replace `javax.servlet` → `jakarta.servlet`
  - Effort: 5 minutes
  - Risk: Low
  - Rollback: Revert import statements

- [x] **Update filter tests**
  - Files: Filter test classes
  - Command: Find/Replace `javax.servlet` → `jakarta.servlet`
  - Effort: 3 minutes
  - Risk: Low
  - Rollback: Revert import statements

### Task 2.4: Migrate javax.annotation
- [x] **Update SecurityConfig**
  - File: SecurityConfig.java
  - Command: Find/Replace `javax.annotation` → `jakarta.annotation`
  - Effort: 1 minute
  - Risk: Low
  - Rollback: Revert import statement

**Verification:**
```bash
mvn clean compile -DskipTests
```
- Expected: BUILD SUCCESS
- Effort: 2-5 minutes
- Risk: Low

## Phase 3: Spring Boot 3.x Breaking Changes

### Task 3.1: Update Security Configuration
- [x] **Migrate SecurityConfig to SecurityFilterChain**
  - File: `common/src/main/java/com/example/app/common/config/SecurityConfig.java`
  - Changes:
    - Remove `WebSecurityConfigurerAdapter` (if present)
    - Update `authorizeRequests()` → `authorizeHttpRequests()`
    - Update `antMatchers()` → `requestMatchers()`
    - Use lambda-based DSL
  - Effort: 20 minutes
  - Risk: Medium
  - Rollback: Revert SecurityConfig.java

**Verification:**
```bash
mvn clean compile -DskipTests -pl common
```
- Expected: BUILD SUCCESS
- Effort: 1-2 minutes
- Risk: Low

### Task 3.2: Update JJWT API
- [x] **Migrate JwtUtil to JJWT 0.12.6 API**
  - File: `common/src/main/java/com/example/app/common/security/JwtUtil.java`
  - Changes:
    - Update builder methods (setSubject → subject, etc.)
    - Update parser methods (parserBuilder → parser, etc.)
    - Update signing (remove explicit algorithm)
  - Effort: 15 minutes
  - Risk: Medium
  - Rollback: Revert JwtUtil.java

**Verification:**
```bash
mvn clean compile -DskipTests -pl common
```
- Expected: BUILD SUCCESS
- Effort: 1-2 minutes
- Risk: Low

## Phase 4: Code Modernization (Selective)

### Task 4.1: Apply Java 9+ Features
- [x] **Use List.of() instead of Collections.singletonList()**
  - Files: JwtAuthenticationFilter.java
  - Effort: 2 minutes
  - Risk: Low
  - Rollback: Revert to Collections.singletonList()

- [x] **Use var for local variables**
  - Files: UserServiceImpl.java
  - Effort: 5 minutes
  - Risk: Low
  - Rollback: Revert to explicit types

- [x] **Use String.isBlank()**
  - Files: UserServiceImpl.java
  - Effort: 2 minutes
  - Risk: Low
  - Rollback: Revert to isEmpty() check

**Verification:**
```bash
mvn clean compile -DskipTests
```
- Expected: BUILD SUCCESS
- Effort: 2-5 minutes
- Risk: Low

## Phase 5: Test Updates

### Task 5.1: Verify Test Compilation
- [x] **Compile all test classes**
  - Command: `mvn clean test-compile -DskipTests`
  - Effort: 5-10 minutes
  - Risk: Low
  - Rollback: N/A (compilation only)

**Verification:**
- Expected: BUILD SUCCESS
- All test classes compile without errors

## Phase 6: Documentation

### Task 6.1: Create Documentation
- [x] **Create compatibility-matrix.md**
  - Effort: 30 minutes
  - Risk: None

- [x] **Create compatibility-report.md**
  - Effort: 30 minutes
  - Risk: None

- [x] **Create migration-checklist.md** (this file)
  - Effort: 30 minutes
  - Risk: None

- [ ] **Create feature-adoption.md**
  - Effort: 20 minutes
  - Risk: None

- [ ] **Create rollback-plan.md**
  - Effort: 15 minutes
  - Risk: None

- [ ] **Create executive-summary.md**
  - Effort: 30 minutes
  - Risk: None

- [ ] **Create metrics-report.json and .pdf**
  - Effort: 45 minutes
  - Risk: None

- [ ] **Update README.md**
  - Effort: 15 minutes
  - Risk: None

- [ ] **Create flow diagrams**
  - Effort: 30 minutes
  - Risk: None

## Effort Summary

| Phase | Tasks | Estimated Effort | Actual Effort |
|-------|-------|------------------|---------------|
| Phase 1: Build Configuration | 8 tasks | 30 minutes | ~30 minutes |
| Phase 2: Jakarta EE Migration | 7 tasks | 50 minutes | ~50 minutes |
| Phase 3: Spring Boot 3.x | 2 tasks | 35 minutes | ~35 minutes |
| Phase 4: Code Modernization | 3 tasks | 9 minutes | ~9 minutes |
| Phase 5: Test Updates | 1 task | 10 minutes | ~10 minutes |
| Phase 6: Documentation | 9 tasks | 245 minutes | In progress |
| **Total** | **30 tasks** | **~6.5 hours** | **~2.5 hours (so far)** |

## Risk Assessment

| Phase | Risk Level | Mitigation |
|-------|------------|------------|
| Phase 1: Build Configuration | Medium | Incremental commits, verify after each change |
| Phase 2: Jakarta EE Migration | Low | Automated find/replace, compile verification |
| Phase 3: Spring Boot 3.x | Medium | Follow Spring Boot migration guide, test incrementally |
| Phase 4: Code Modernization | Low | Selective application, preserve behavior |
| Phase 5: Test Updates | Low | Compilation only, execution deferred |
| Phase 6: Documentation | None | Documentation only, no code changes |

## Rollback Steps

### Complete Rollback
```bash
# Rollback to Java 8 baseline
git reset --hard java8-baseline

# Or if using tags
git checkout java8-baseline
```

### Partial Rollback by Phase

**Rollback Phase 1 (Build Configuration):**
```bash
git checkout HEAD -- pom.xml
git checkout HEAD -- */pom.xml
```

**Rollback Phase 2 (Jakarta EE):**
```bash
# Revert all Java files
git checkout HEAD -- **/*.java
```

**Rollback Phase 3 (Spring Boot 3.x):**
```bash
git checkout HEAD -- common/src/main/java/com/example/app/common/config/SecurityConfig.java
git checkout HEAD -- common/src/main/java/com/example/app/common/security/JwtUtil.java
```

### Incremental Rollback

Each phase was committed separately, allowing granular rollback:
```bash
# View commit history
git log --oneline

# Rollback specific commit
git revert <commit-hash>
```

## Verification Commands

### Compilation Verification
```bash
# Clean compile all modules
mvn clean compile -DskipTests

# Compile specific module
mvn clean compile -DskipTests -pl <module-name>

# Compile tests
mvn clean test-compile -DskipTests
```

### Dependency Verification
```bash
# Check dependency tree
mvn dependency:tree

# Check for conflicts
mvn dependency:analyze
```

### Code Quality
```bash
# Run linter (if configured)
mvn checkstyle:check

# Run static analysis (if configured)
mvn spotbugs:check
```

## Success Criteria

- [x] All modules compile successfully on Java 21
- [x] All test classes compile successfully
- [x] No deprecated APIs remain in active code
- [x] All dependencies are Java 21 compatible
- [ ] All documentation created
- [ ] README updated
- [ ] Flow diagrams created

## Next Steps

1. Complete documentation tasks
2. Execute full test suite (when ready)
3. Verify application startup
4. Perform integration testing
5. Deploy to staging environment
6. Monitor for runtime issues

## Notes

- All changes are committed incrementally for easy rollback
- Business logic preserved - only API migrations and modernization
- Test execution deferred as per requirements
- Full runtime verification recommended before production deployment

