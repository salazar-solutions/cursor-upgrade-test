# Code Metrics Report

Generated: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")

## Overview

This report provides comprehensive code metrics for the entire codebase.

## Total Lines of Code

| Category | Lines | Files |
|----------|-------|-------|
| **Java Source Code (Total)** | **7,810** | **131** |
| └─ Main Source Code | 3,342 | 83 |
| └─ Test Code | 4,468 | 48 |
| XML Configuration | 1,834 | 10 |
| Properties Files | 261 | 25 |
| SQL Scripts | 169 | 8 |
| Markdown Documentation | 1,293 | 12 |
| **GRAND TOTAL** | **11,367** | **186** |

## Code Distribution

### By Module (Java Code Only)

| Module | Main Source | Test Code | Total Lines | Main Files | Test Files |
|--------|-------------|-----------|-------------|------------|------------|
| **admin** | 115 | 343 | 458 | 3 | 5 |
| **billing** | 239 | 459 | 698 | 8 | 6 |
| **common** | 572 | 744 | 1,316 | 12 | 8 |
| **inventory** | 301 | 441 | 742 | 10 | 5 |
| **notifications** | 109 | 180 | 289 | 4 | 4 |
| **order** | 814 | 718 | 1,532 | 17 | 5 |
| **payment** | 256 | 402 | 658 | 8 | 4 |
| **product** | 384 | 483 | 867 | 8 | 5 |
| **user** | 552 | 698 | 1,250 | 13 | 6 |
| **TOTAL** | **3,342** | **4,468** | **7,810** | **83** | **48** |

## Key Metrics

### Code Quality Indicators

- **Test Coverage Ratio**: 57.2% (4,468 test lines / 7,810 total Java lines)
- **Test-to-Source Ratio**: 1.34:1 (4,468 test lines / 3,342 source lines)
- **Average Lines per File**: 59.6 lines (7,810 lines / 131 files)
- **Average Lines per Main File**: 40.3 lines (3,342 lines / 83 files)
- **Average Lines per Test File**: 93.1 lines (4,468 lines / 48 files)

### Module Size Rankings (by Total Lines)

1. **order** - 1,532 lines (largest module)
2. **common** - 1,316 lines
3. **user** - 1,250 lines
4. **product** - 867 lines
5. **billing** - 698 lines
6. **payment** - 658 lines
7. **inventory** - 742 lines
8. **admin** - 458 lines
9. **notifications** - 289 lines (smallest module)

### Code Distribution

- **Production Code**: 3,342 lines (42.8% of Java code)
- **Test Code**: 4,468 lines (57.2% of Java code)
- **Configuration**: 2,095 lines (XML + Properties)
- **Database Scripts**: 169 lines
- **Documentation**: 1,293 lines

## File Type Breakdown

| File Type | Count | Lines |
|-----------|-------|-------|
| Java (.java) | 131 | 7,810 |
| XML (.xml) | 10 | 1,834 |
| Properties (.properties) | 25 | 261 |
| SQL (.sql) | 8 | 169 |
| Markdown (.md) | 12 | 1,293 |
| **Total** | **186** | **11,367** |

## Summary

- **Total Source Code Lines**: 7,810 lines of Java
- **Total Project Lines**: 11,367 lines (including config, SQL, and docs)
- **Code Files**: 131 Java files across 9 modules
- **Test Coverage**: Strong test coverage with 57% of code being tests
- **Project Structure**: Well-organized multi-module Maven project

