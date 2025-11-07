# Regression Scenarios Validation Report

**Date**: 2025-01-27  
**Scope**: Validation of regression test scenarios and step functions against actual code functionality

## Executive Summary

This report documents discrepancies found between the regression test scenarios/step functions and the actual implementation code. Two critical mismatches were identified that would cause test failures:

1. **Payment ID Missing from Order Response** - Tests expect `paymentId` in order response, but it's not implemented
2. **Inventory Not Released on Order Cancellation** - Tests expect inventory release when orders are cancelled, but this is not implemented

---

## Critical Issues Found

### Issue #1: Payment ID Not Returned in Order Response

**Location**: 
- Test: `regression-test/src/test/resources/features/order/order_integration_verification.feature` (Scenario: "Verify payment creation after order creation")
- Step Definition: `regression-test/src/test/java/com/example/app/regression/steps/OrderStepDefinitions.java` (line 123-130)
- Implementation: `order/src/main/java/com/example/app/order/service/OrderServiceImpl.java`

**Test Expectation**:
```gherkin
Then the order response contains payment ID
When I get the payment by ID from order response
Then the payment amount matches the order total
```

The step definition expects:
```java
String paymentId = response.jsonPath().getString("paymentId");
assertThat(paymentId).isNotNull();
```

**Actual Implementation**:
- `OrderServiceImpl.createOrder()` creates a payment via `billingAdapter.createPayment()` (line 193)
- The `paymentId` is returned but **never stored** in the Order entity
- The `Order` entity (line 16-99) does **not have a `paymentId` field**
- The `OrderResponse` DTO (line 11-75) does **not have a `paymentId` field**
- The `OrderMapper` does not map any paymentId field

**Impact**: 
- Test will fail with `NullPointerException` or assertion failure
- The test cannot verify that payment was created correctly
- Integration verification scenario cannot complete

**Recommendation**:
1. Add `paymentId` field to `Order` entity
2. Add `paymentId` column to database schema
3. Store `paymentId` in `OrderServiceImpl.createOrder()` after payment creation
4. Add `paymentId` field to `OrderResponse` DTO
5. Update `OrderMapper` to include `paymentId` in response mapping

---

### Issue #2: Inventory Not Released on Order Cancellation

**Location**:
- Test: `regression-test/src/test/resources/features/order/order_integration_verification.feature` (Scenario: "Verify inventory release after order cancellation")
- Step Definitions: `regression-test/src/test/java/com/example/app/regression/steps/InventoryStepDefinitions.java` (lines 161-183)
- Implementation: `order/src/main/java/com/example/app/order/service/OrderServiceImpl.java`

**Test Expectation**:
```gherkin
When I change order status to "CANCELLED" for order ID "$createdOrderId"
Then the response status code is 200
When I get inventory for product ID "$createdProductId"
Then the reserved quantity decreased after cancellation
And the available quantity increased after cancellation
```

The step definitions verify:
- Reserved quantity decreases after cancellation
- Available quantity increases after cancellation

**Actual Implementation**:
- `OrderServiceImpl.changeOrderStatus()` (line 299-327) only:
  1. Validates status transition
  2. Records status history
  3. Updates order status
  4. Returns updated order response
  
- **Does NOT**:
  - Check if new status is `CANCELLED`
  - Retrieve order lines to get product IDs and quantities
  - Call `inventoryService.releaseInventory()` for each product
  - Release reserved inventory back to available stock

**Impact**:
- Test will fail because inventory quantities remain unchanged after cancellation
- Reserved stock is never released, leading to inventory management issues
- Business logic mismatch: documentation mentions "Stock release for cancelled orders" but it's not implemented

**Recommendation**:
1. Modify `OrderServiceImpl.changeOrderStatus()` to detect CANCELLED status
2. When status changes to CANCELLED:
   - Retrieve all order lines for the order
   - For each order line, call `inventoryService.releaseInventory()` with the reserved quantity
   - Handle any exceptions appropriately (log but don't fail the status change)
3. Consider adding transaction rollback if inventory release fails (or handle gracefully)

---

## Additional Observations

### Status Transition Validation

**Status**: ✅ **MATCHES**

The test scenarios for status transitions match the implementation:
- `order_lifecycle_complete.feature` correctly tests:
  - PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED
  - PENDING → CANCELLED
  - CONFIRMED → CANCELLED
  - Cannot cancel DELIVERED (expects 422)

The `isValidStatusTransition()` method (line 346-366) correctly implements these rules.

### Order Creation Flow

**Status**: ✅ **MOSTLY MATCHES** (except paymentId issue)

The order creation flow matches expectations:
- ✅ Validates products exist
- ✅ Reserves inventory for all products
- ✅ Creates payment record
- ✅ Creates order with PENDING status
- ✅ Sends notification
- ❌ **Missing**: Returns paymentId in response

### Inventory Reservation

**Status**: ✅ **MATCHES**

Inventory reservation during order creation works correctly:
- `OrderServiceImpl.createOrder()` calls `inventoryService.reserveInventory()` for each product
- Test scenarios correctly verify inventory changes

---

## Test Data Validation

### Context Variable Usage

**Status**: ✅ **MATCHES**

Step definitions correctly use context variables:
- `$createdUserId`, `$createdProductId`, `$createdOrderId` are properly resolved
- Context variable resolution in step definitions matches the pattern used

### API Endpoints

**Status**: ✅ **MATCHES**

All API endpoints used in tests match the controller definitions:
- `/orders` (POST) - Create order
- `/orders/{id}` (GET) - Get order
- `/orders` (GET) - List orders with filters
- `/orders/{id}/status` (POST) - Change status
- `/inventory/{productId}` (GET) - Get inventory
- `/billing/payments/{id}` (GET) - Get payment

---

## Summary of Mismatches

| Issue | Severity | Test File | Implementation File | Status |
|-------|----------|-----------|-------------------|--------|
| Payment ID missing from order response | **CRITICAL** | `order_integration_verification.feature` | `OrderServiceImpl.java` | ❌ **MISMATCH** |
| Inventory not released on cancellation | **CRITICAL** | `order_integration_verification.feature` | `OrderServiceImpl.java` | ❌ **MISMATCH** |
| Status transitions | Low | `order_lifecycle_complete.feature` | `OrderServiceImpl.java` | ✅ **MATCHES** |
| Order creation flow | Medium | Multiple feature files | `OrderServiceImpl.java` | ⚠️ **PARTIAL** |
| Inventory reservation | Low | `order_integration_verification.feature` | `OrderServiceImpl.java` | ✅ **MATCHES** |

---

## Recommendations

### High Priority Fixes

1. **Add Payment ID to Order Response**
   - Database migration: Add `payment_id` column to `orders` table
   - Entity: Add `paymentId` field to `Order` entity
   - DTO: Add `paymentId` field to `OrderResponse`
   - Service: Store `paymentId` after payment creation
   - Mapper: Include `paymentId` in response mapping

2. **Implement Inventory Release on Cancellation**
   - Service: Add logic to `changeOrderStatus()` to release inventory when status is CANCELLED
   - Retrieve order lines and release inventory for each product
   - Handle edge cases (already cancelled, no reserved inventory, etc.)

### Medium Priority

1. **Add Integration Tests** for the new functionality
2. **Update Documentation** to reflect actual behavior
3. **Consider Event-Driven Approach** for order cancellation to decouple inventory release

### Low Priority

1. Review other feature files for similar mismatches
2. Add validation for edge cases in step definitions
3. Consider adding more comprehensive error scenario tests

---

## Files Requiring Changes

### Implementation Files
1. `order/src/main/java/com/example/app/order/entity/Order.java` - Add paymentId field
2. `order/src/main/java/com/example/app/order/dto/OrderResponse.java` - Add paymentId field
3. `order/src/main/java/com/example/app/order/service/OrderServiceImpl.java` - Store paymentId and release inventory on cancellation
4. `order/src/main/java/com/example/app/order/mapper/OrderMapper.java` - Map paymentId
5. `db/patches/004_add_payment_id_to_orders.sql` - New migration script

### Test Files
- No changes needed to test files - they correctly test the expected behavior

---

## Conclusion

The regression test scenarios are well-designed and test important integration points. However, two critical mismatches were identified where the tests expect functionality that is not implemented in the code:

1. **Payment ID tracking** - The code creates payments but doesn't expose the payment ID in the order response
2. **Inventory release on cancellation** - The code allows order cancellation but doesn't release reserved inventory

These issues need to be addressed either by:
- **Option A**: Implementing the missing functionality in the code (recommended)
- **Option B**: Updating the tests to match current implementation (not recommended, as it reduces test coverage)

**Recommendation**: Implement the missing functionality to match the test expectations, as these represent important business requirements (payment tracking and inventory management).

