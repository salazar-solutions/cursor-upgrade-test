# Rollback Plan

## Overview

This document provides exact git commands to revert all changes made during the Java 8 to Java 21 migration. The migration was designed with incremental commits to enable granular rollback.

## Complete Rollback

### Option 1: Reset to Baseline Tag

If a baseline tag was created before migration:

```bash
# View available tags
git tag -l

# Reset to Java 8 baseline (DESTRUCTIVE - use with caution)
git reset --hard java8-baseline

# Or checkout the baseline tag (creates detached HEAD)
git checkout java8-baseline
```

### Option 2: Reset to Specific Commit

```bash
# Find the commit hash before migration started
git log --oneline

# Reset to that commit (replace <commit-hash> with actual hash)
git reset --hard <commit-hash>
```

### Option 3: Revert All Migration Commits

```bash
# View commit history
git log --oneline

# Revert commits in reverse order (newest first)
git revert <newest-commit-hash>
git revert <next-commit-hash>
# ... continue for all migration commits
```

## Partial Rollback by Phase

### Phase 1: Rollback Build Configuration Changes

```bash
# Revert root pom.xml
git checkout HEAD -- pom.xml

# Revert all module pom.xml files
git checkout HEAD -- common/pom.xml
git checkout HEAD -- user/pom.xml
git checkout HEAD -- product/pom.xml
git checkout HEAD -- inventory/pom.xml
git checkout HEAD -- order/pom.xml
git checkout HEAD -- payment/pom.xml
git checkout HEAD -- billing/pom.xml
git checkout HEAD -- notifications/pom.xml
git checkout HEAD -- admin/pom.xml
git checkout HEAD -- regression-test/pom.xml
```

### Phase 2: Rollback Jakarta EE Migration

```bash
# Revert all Java source files (main)
git checkout HEAD -- **/src/main/java/**/*.java

# Revert all Java test files
git checkout HEAD -- **/src/test/java/**/*.java
```

**Note:** This will revert ALL Java file changes, including Phase 3 and 4 changes. For more granular control, see specific file rollback below.

### Phase 3: Rollback Spring Boot 3.x Changes

```bash
# Revert SecurityConfig
git checkout HEAD -- common/src/main/java/com/example/app/common/config/SecurityConfig.java

# Revert JwtUtil
git checkout HEAD -- common/src/main/java/com/example/app/common/security/JwtUtil.java
```

### Phase 4: Rollback Code Modernization

```bash
# Revert UserServiceImpl
git checkout HEAD -- user/src/main/java/com/example/app/user/service/UserServiceImpl.java

# Revert JwtAuthenticationFilter
git checkout HEAD -- common/src/main/java/com/example/app/common/filter/JwtAuthenticationFilter.java
```

## Granular File Rollback

### Build Configuration Files

```bash
# Root POM
git checkout HEAD -- pom.xml

# Specific module POMs
git checkout HEAD -- <module-path>/pom.xml
```

### Source Files by Package

```bash
# Revert all entity classes
git checkout HEAD -- **/entity/**/*.java

# Revert all domain classes
git checkout HEAD -- **/domain/**/*.java

# Revert all controllers
git checkout HEAD -- **/controller/**/*.java

# Revert all filters
git checkout HEAD -- **/filter/**/*.java

# Revert all config classes
git checkout HEAD -- **/config/**/*.java
```

### Specific Files

```bash
# Revert specific file
git checkout HEAD -- <file-path>

# Example: Revert SecurityConfig only
git checkout HEAD -- common/src/main/java/com/example/app/common/config/SecurityConfig.java
```

## Documentation Rollback

If documentation files need to be removed:

```bash
# Remove documentation files
rm compatibility-matrix.md
rm compatibility-report.md
rm migration-checklist.md
rm feature-adoption.md
rm rollback-plan.md
rm executive-summary.md
rm metrics-report.json
rm metrics-report.pdf

# Or revert if they were committed
git checkout HEAD -- compatibility-matrix.md
git checkout HEAD -- compatibility-report.md
# ... etc
```

## Verification After Rollback

### Verify Java Version

```bash
# Check Java version in pom.xml
grep "java.version" pom.xml
# Should show: <java.version>1.8</java.version>

# Verify Maven compilation
mvn clean compile -DskipTests
```

### Verify Dependencies

```bash
# Check Spring Boot version
grep "spring-boot.version" pom.xml
# Should show: <spring-boot.version>2.7.18</spring-boot.version>

# Check dependency tree
mvn dependency:tree | head -20
```

### Verify Source Code

```bash
# Check for javax.* imports (should be present)
grep -r "import javax\." --include="*.java" | head -5

# Check for jakarta.* imports (should NOT be present)
grep -r "import jakarta\." --include="*.java" | head -5
```

## Rollback Scenarios

### Scenario 1: Complete Rollback Needed

**Situation:** Migration failed, need to restore Java 8 baseline completely.

**Commands:**
```bash
# Option A: Reset to tag (if exists)
git reset --hard java8-baseline

# Option B: Reset to commit before migration
git log --oneline
git reset --hard <pre-migration-commit-hash>

# Verify
mvn clean compile -DskipTests
```

### Scenario 2: Partial Rollback - Build Issues

**Situation:** Build configuration issues, but code changes are fine.

**Commands:**
```bash
# Revert only POM files
git checkout HEAD -- pom.xml
git checkout HEAD -- */pom.xml

# Verify build
mvn clean compile -DskipTests
```

### Scenario 3: Partial Rollback - Security Config Issues

**Situation:** Security configuration not working, need to revert only SecurityConfig.

**Commands:**
```bash
# Revert SecurityConfig
git checkout HEAD -- common/src/main/java/com/example/app/common/config/SecurityConfig.java

# Verify compilation
mvn clean compile -DskipTests -pl common
```

### Scenario 4: Partial Rollback - JJWT API Issues

**Situation:** JWT functionality broken, need to revert JwtUtil.

**Commands:**
```bash
# Revert JwtUtil
git checkout HEAD -- common/src/main/java/com/example/app/common/security/JwtUtil.java

# Also revert JJWT version in common/pom.xml
git checkout HEAD -- common/pom.xml

# Verify compilation
mvn clean compile -DskipTests -pl common
```

## Rollback Best Practices

### Before Rollback

1. **Document the Issue**
   - Note what's not working
   - Identify which phase/component is affected
   - Determine if partial rollback is sufficient

2. **Create Backup Branch**
   ```bash
   # Create backup of current state
   git branch backup-before-rollback
   ```

3. **Verify Current State**
   ```bash
   # Check current branch and status
   git status
   git log --oneline -5
   ```

### During Rollback

1. **Incremental Rollback**
   - Start with smallest possible rollback
   - Test after each rollback step
   - Only rollback what's necessary

2. **Verify After Each Step**
   ```bash
   # After each rollback, verify
   mvn clean compile -DskipTests
   ```

### After Rollback

1. **Verify Functionality**
   ```bash
   # Compile
   mvn clean compile -DskipTests
   
   # Compile tests
   mvn clean test-compile -DskipTests
   ```

2. **Document Rollback**
   - Note what was rolled back
   - Document why rollback was needed
   - Plan for re-attempting migration

## Re-attempting Migration

After rollback, if re-attempting migration:

1. **Identify Root Cause**
   - Analyze why rollback was needed
   - Fix underlying issues
   - Update migration plan if needed

2. **Incremental Approach**
   - Apply changes in smaller increments
   - Test after each increment
   - Commit frequently

3. **Use Feature Branches**
   ```bash
   # Create feature branch for re-attempt
   git checkout -b java21-migration-v2
   
   # Apply changes incrementally
   # Test and commit frequently
   ```

## Emergency Rollback

For production emergencies:

```bash
# Fastest rollback - reset to baseline
git reset --hard java8-baseline

# Or revert last commit
git revert HEAD

# Force push (if necessary, coordinate with team)
git push --force origin <branch-name>
```

**Warning:** Force push should be coordinated with the team and only used in emergencies.

## Rollback Checklist

- [ ] Identify scope of rollback (complete vs partial)
- [ ] Create backup branch
- [ ] Document reason for rollback
- [ ] Execute rollback commands
- [ ] Verify compilation
- [ ] Verify test compilation
- [ ] Test basic functionality
- [ ] Document rollback in commit message
- [ ] Notify team of rollback
- [ ] Plan re-attempt if needed

## Summary

The migration was designed with rollback in mind:
- Incremental commits enable granular rollback
- Each phase can be rolled back independently
- Complete rollback is always possible
- Documentation changes can be reverted separately

Use the most specific rollback possible to minimize impact and preserve working changes.

