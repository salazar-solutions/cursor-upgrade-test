# Regression Test Mismatch Analysis

**Date**: 2025-01-27  
**Analysis Scope**: Root cause analysis of mismatches between regression tests and upgraded application logic  
**Profile**: `regression` (with `local`)  
**Spring Boot Version**: 3.2.5  
**Java Version**: 21

## Executive Summary

This report provides detailed root cause analysis of mismatches identified between regression test scenarios and the actual application implementation. The analysis distinguishes between business logic changes introduced during the upgrade and test definitions that are outdated or expect functionality that was never implemented.

### Key Findings

- **Total Mismatches Identified**: 2 critical issues
- **Root Cause Distribution**:
  - **Test Definition Outdated**: 2 (100%)
  - **Business Logic Change**: 0 (0%)
  - **Test Data Invalid**: 0 (0%)
  - **API Mismatch**: 0 (0%)

**Conclusion**: All mismatches are due to test definitions expecting functionality that was never implemented in the codebase, not due to business logic changes during the upgrade.

---

## Failing Scenarios Table

| Scenario Name | Feature File | Root Cause | Impacted Modules | Severity | Suggested Fix |
|--------------|--------------|------------|------------------|----------|---------------|
| Verify payment creation after order creation | `order/order_integration_verification.feature` | **Test Definition Outdated** | `order` | **CRITICAL** | Implement paymentId in Order entity/response |
| Verify inventory release after order cancellation | `order/order_integration_verification.feature` | **Test Definition Outdated** | `order`, `inventory` | **CRITICAL** | Implement inventory release on cancellation |

---

## Detailed Mismatch Analysis

### Mismatch #1: Payment ID Missing from Order Response

#### Scenario Details

**Feature File**: `regression-test/src/test/resources/features/order/order_integration_verification.feature`  
**Scenario**: "Verify payment creation after order creation"  
**Tags**: `@regression @order @integration @verification`

**Test Steps**:
```gherkin
Scenario: Verify payment creation after order creation
  Given an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 3
  When I create the order
  Then the order is created successfully
  And the order response contains payment ID
  When I get the payment by ID from order response
  Then the response status code is 200
  And the payment amount matches the order total
```

#### Root Cause Analysis

**Category**: **Test Definition Outdated**

**Evidence**:
1. **Test Expectation**: Step definition `OrderStepDefinitions.theOrderResponseContainsPaymentId()` (lines 117-124) expects `paymentId` field in order response JSON
2. **Actual Implementation**: 
   - `OrderServiceImpl.createOrder()` creates payment via `billingAdapter.createPayment()` (line 193)
   - Payment ID is returned as `UUID` but **never stored** in Order entity
   - `Order` entity has no `paymentId` field
   - `OrderResponse` DTO has no `paymentId` field
   - `OrderMapper` does not map paymentId

**Code Evidence**:
```java
// OrderServiceImpl.java line 193
UUID paymentId = billingAdapter.createPayment(savedOrder.getId(), totalAmount);
// paymentId is never stored or returned
```

```java
// OrderResponse.java - No paymentId field
public class OrderResponse {
    private String id;
    private String userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<OrderLineResponse> orderLines;
    private Instant createdAt;
    private Instant updatedAt;
    // No paymentId field
}
```

**Database Schema Evidence**:
```sql
-- db/patches/001_create_schema.sql
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    total_amount DECIMAL(19,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
-- No payment_id column
```

**Historical Context**: 
- This functionality was likely planned but never implemented
- The test was written expecting this feature to exist
- No evidence of this feature being removed during the upgrade
- Payment is created but not linked back to the order in the response

**Impact Assessment**:
- **Severity**: CRITICAL
- **Impacted Modules**: `order` module (Order entity, OrderResponse DTO, OrderServiceImpl, OrderMapper)
- **Test Failure**: Assertion failure - `paymentId` is null
- **Business Impact**: Cannot verify payment was created correctly, integration verification incomplete

#### Suggested Fix

**Option A: Implement Missing Functionality (RECOMMENDED)**

1. **Database Migration**:
   ```sql
   ALTER TABLE cursordb.orders ADD COLUMN payment_id UUID;
   CREATE INDEX idx_order_payment_id ON cursordb.orders(payment_id);
   ```

2. **Update Order Entity** (`order/src/main/java/com/example/app/order/entity/Order.java`):
   ```java
   @Column(name = "payment_id", columnDefinition = "UUID")
   private UUID paymentId;
   
   // Add getter and setter
   public UUID getPaymentId() { return paymentId; }
   public void setPaymentId(UUID paymentId) { this.paymentId = paymentId; }
   ```

3. **Update OrderResponse DTO** (`order/src/main/java/com/example/app/order/dto/OrderResponse.java`):
   ```java
   private String paymentId;
   
   // Add getter and setter
   public String getPaymentId() { return paymentId; }
   public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
   ```

4. **Update OrderServiceImpl** (`order/src/main/java/com/example/app/order/service/OrderServiceImpl.java`):
   ```java
   // After line 193
   UUID paymentId = billingAdapter.createPayment(savedOrder.getId(), totalAmount);
   savedOrder.setPaymentId(paymentId);
   savedOrder = orderRepository.save(savedOrder);
   ```

5. **Update OrderMapper** (`order/src/main/java/com/example/app/order/mapper/OrderMapper.java`):
   ```java
   @Mapping(target = "paymentId", expression = "java(order.getPaymentId() != null ? order.getPaymentId().toString() : null)")
   OrderResponse toResponse(Order order);
   ```

**Option B: Update Test to Match Current Implementation (NOT RECOMMENDED)**
- Remove the step that expects paymentId in order response
- Retrieve payment ID via a separate API call to billing service
- **Downside**: Reduces test coverage and integration verification

**Recommendation**: **Option A** - Implement the missing functionality as it represents an important business requirement (payment tracking).

---

### Mismatch #2: Inventory Not Released on Order Cancellation

#### Scenario Details

**Feature File**: `regression-test/src/test/resources/features/order/order_integration_verification.feature`  
**Scenario**: "Verify inventory release after order cancellation"  
**Tags**: `@regression @order @integration @verification`

**Test Steps**:
```gherkin
Scenario: Verify inventory release after order cancellation
  Given an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 4
  When I create the order
  And I get inventory for product ID "$createdProductId"
  And the reserved quantity after order is stored as "reservedAfterOrder"
  When I change order status to "CANCELLED" for order ID "$createdOrderId"
  Then the response status code is 200
  When I get inventory for product ID "$createdProductId"
  Then the reserved quantity decreased after cancellation
  And the available quantity increased after cancellation
```

#### Root Cause Analysis

**Category**: **Test Definition Outdated**

**Evidence**:
1. **Test Expectation**: Step definitions `InventoryStepDefinitions.theReservedQuantityDecreasedAfterCancellation()` and `theAvailableQuantityIncreasedAfterCancellation()` (lines 122-136) expect inventory quantities to change after order cancellation
2. **Actual Implementation**: 
   - `OrderServiceImpl.changeOrderStatus()` (lines 299-327) only:
     - Validates status transition
     - Records status history
     - Updates order status
     - Returns updated order response
   - **Does NOT** check if status is CANCELLED
   - **Does NOT** retrieve order lines
   - **Does NOT** call `inventoryService.releaseInventory()`

**Code Evidence**:
```java
// OrderServiceImpl.java lines 299-327
@Override
public OrderResponse changeOrderStatus(UUID orderId, OrderStatusChangeRequest request) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

    OrderStatus currentStatus = order.getStatus();
    OrderStatus newStatus = request.getStatus();

    // Validate status transition
    if (!isValidStatusTransition(currentStatus, newStatus)) {
        throw new BusinessException(
            String.format("Invalid status transition from %s to %s", currentStatus, newStatus)
        );
    }

    // Record status history
    OrderStatusHistory history = new OrderStatusHistory();
    history.setOrderId(orderId);
    history.setFromStatus(currentStatus);
    history.setToStatus(newStatus);
    orderStatusHistoryRepository.save(history);

    order.setStatus(newStatus);
    Order updatedOrder = orderRepository.save(order);

    List<OrderLine> orderLines = orderLineRepository.findByOrderId(orderId);
    OrderResponse response = orderMapper.toResponse(updatedOrder);
    response.setOrderLines(orderMapper.toOrderLineResponseList(orderLines));
    return response;
    // NO INVENTORY RELEASE LOGIC
}
```

**Documentation Evidence**:
- `InventoryServiceImpl` documentation mentions "Stock release for cancelled orders" (line 25)
- This suggests the feature was planned but never implemented

**Historical Context**:
- This functionality was likely planned but never implemented
- The test was written expecting this feature to exist
- No evidence of this feature being removed during the upgrade
- Documentation suggests this was an intended feature

**Impact Assessment**:
- **Severity**: CRITICAL
- **Impacted Modules**: `order` module (OrderServiceImpl), `inventory` module (InventoryService)
- **Test Failure**: Assertion failure - inventory quantities remain unchanged
- **Business Impact**: Reserved stock is never released, leading to inventory management issues and stock unavailability

#### Suggested Fix

**Option A: Implement Missing Functionality (RECOMMENDED)**

1. **Update OrderServiceImpl** (`order/src/main/java/com/example/app/order/service/OrderServiceImpl.java`):
   ```java
   @Override
   public OrderResponse changeOrderStatus(UUID orderId, OrderStatusChangeRequest request) {
       Order order = orderRepository.findById(orderId)
           .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

       OrderStatus currentStatus = order.getStatus();
       OrderStatus newStatus = request.getStatus();

       // Validate status transition
       if (!isValidStatusTransition(currentStatus, newStatus)) {
           throw new BusinessException(
               String.format("Invalid status transition from %s to %s", currentStatus, newStatus)
           );
       }

       // If status is changing to CANCELLED, release inventory
       if (newStatus == OrderStatus.CANCELLED && currentStatus != OrderStatus.CANCELLED) {
           List<OrderLine> orderLines = orderLineRepository.findByOrderId(orderId);
           for (OrderLine orderLine : orderLines) {
               try {
                   ReleaseRequest releaseRequest = new ReleaseRequest();
                   releaseRequest.setQuantity(orderLine.getQuantity());
                   inventoryService.releaseInventory(orderLine.getProductId(), releaseRequest);
               } catch (Exception e) {
                   // Log error but don't fail the status change
                   // In production, this might trigger a compensation transaction or alert
                   System.err.println("Failed to release inventory for product " + 
                       orderLine.getProductId() + ": " + e.getMessage());
               }
           }
       }

       // Record status history
       OrderStatusHistory history = new OrderStatusHistory();
       history.setOrderId(orderId);
       history.setFromStatus(currentStatus);
       history.setToStatus(newStatus);
       orderStatusHistoryRepository.save(history);

       order.setStatus(newStatus);
       Order updatedOrder = orderRepository.save(order);

       List<OrderLine> orderLines = orderLineRepository.findByOrderId(orderId);
       OrderResponse response = orderMapper.toResponse(updatedOrder);
       response.setOrderLines(orderMapper.toOrderLineResponseList(orderLines));
       return response;
   }
   ```

2. **Add Import**:
   ```java
   import com.example.app.inventory.domain.ReleaseRequest;
   ```

**Option B: Update Test to Match Current Implementation (NOT RECOMMENDED)**
- Remove the steps that verify inventory release on cancellation
- **Downside**: Loses important business logic validation, inventory management issues will persist

**Recommendation**: **Option A** - Implement the missing functionality as it represents critical business logic (inventory management).

**Additional Considerations**:
- Consider transaction boundaries: Should inventory release failure rollback the status change?
- Consider idempotency: What happens if status is changed to CANCELLED multiple times?
- Consider partial failures: What if some products fail to release inventory?

---

## Minor Issues (Non-Critical)

### Issue #3: PaymentRequest.paymentMethod Field

**Location**:
- Step Definition: `regression-test/src/test/java/com/example/app/regression/steps/BillingStepDefinitions.java` (line 32)
- DTO: `billing/src/main/java/com/example/app/billing/domain/PaymentRequest.java`

**Issue**:
- Step definition includes `paymentMethod` field in payment request
- PaymentRequest DTO does not have this field
- Field is ignored by Jackson deserialization (no validation error)

**Impact**: MINOR - Test sends unused field, but doesn't cause test failure

**Fix Recommendation**: Remove `paymentMethod` from step definition to match actual DTO structure

---

### Issue #4: ReserveRequest/ReleaseRequest.orderId Field

**Location**:
- Step Definitions: `regression-test/src/test/java/com/example/app/regression/steps/InventoryStepDefinitions.java` (lines 41, 56)
- DTOs: `inventory/src/main/java/com/example/app/inventory/domain/ReserveRequest.java`, `ReleaseRequest.java`

**Issue**:
- Step definitions include `orderId` field in reserve/release requests
- ReserveRequest and ReleaseRequest DTOs do not have this field
- Field is ignored by Jackson deserialization (no validation error)

**Impact**: MINOR - Test sends unused field, but doesn't cause test failure

**Fix Recommendation**: Remove `orderId` from step definitions to match actual DTO structure

---

## Business Logic Changes Analysis

### No Business Logic Changes Identified

**Analysis**: All mismatches are due to test definitions expecting functionality that was never implemented, not due to business logic changes during the upgrade.

**Evidence**:
- No functionality was removed from the codebase
- No API contracts were changed
- No DTO structures were modified in ways that break tests
- All mismatches are "missing functionality" rather than "changed functionality"

**Conclusion**: The upgrade did not introduce breaking changes. The test failures are due to tests expecting features that were planned but never implemented.

---

## Test Artifact Upgrade Recommendations

### High Priority

1. **Fix Order Integration Verification Feature**
   - Update scenarios to match current implementation OR
   - Implement missing functionality (recommended)

2. **Update Step Definitions**
   - Remove unused fields (`paymentMethod`, `orderId`) from request payloads
   - Align with actual DTO structures

### Medium Priority

3. **Re-enable Citrus Tests**
   - Review Citrus 4.0.0 API documentation
   - Update test implementations
   - Re-enable for orchestration validation

4. **Complete User Disable Implementation**
   - Implement AdminUserController.disableUser() functionality
   - Update test expectations

### Low Priority

5. **Add Missing Test Coverage**
   - Bulk operations
   - Concurrent operations
   - Performance/load scenarios

---

## Test Data Validation Results

### SQL Test Data Scripts

**Status**: ✅ **VALID**

**Analysis**:
- SQL scripts in `db/patches/` contain valid test data
- Data format matches application expectations (UUIDs, timestamps, decimal amounts)
- No conflicts with feature file test data (feature files create data dynamically)
- Test data is properly structured and can be used for manual testing

**Recommendation**: No changes needed to SQL test data scripts.

### Feature File Test Data

**Status**: ✅ **VALID**

**Analysis**:
- Feature files create test data dynamically during test execution
- No hardcoded dependencies on SQL script data
- Context variables are properly used to pass data between steps
- Test data format matches DTO expectations

**Recommendation**: No changes needed to feature file test data usage.

---

## Reproducibility and Environment Assumptions

### Environment Requirements

**Database**:
- PostgreSQL 13+ required
- Database: `devdb`
- Schema: `cursordb`
- User: `devuser`
- Test data scripts should be executed before running tests (optional, tests create their own data)

**Application**:
- Spring Boot 3.2.5
- Java 21
- Base URL: `http://localhost:8080` (configurable via `api.base.url`)
- Base Path: `/api/v1` (configurable via `api.base.path`)

**Test Execution**:
- Profile: `regression` (with `local`)
- Maven command: `mvn test -pl regression-test -Dspring.profiles.active=regression`
- Test framework: Cucumber with JUnit 5
- Context variables are scenario-scoped (cleared between scenarios)

### Known Limitations

1. **Citrus Tests**: All commented out, not executable
2. **User Disable**: Admin endpoint returns placeholder, functionality not implemented
3. **Payment Method**: PaymentRequest doesn't validate `paymentMethod` field (ignored if sent)
4. **Order Payment ID**: Not returned in OrderResponse (causes test failure)
5. **Inventory Release**: Not triggered on order cancellation (causes test failure)

---

## Summary and Next Steps

### Summary

- **Total Mismatches**: 2 critical issues
- **Root Cause**: Test definitions expect functionality that was never implemented
- **Impact**: 2 test scenarios will fail
- **Fix Complexity**: Medium (requires code changes and database migration)

### Immediate Actions

1. **Implement Payment ID in Order Response** (CRITICAL)
   - Add database column
   - Update entity, DTO, service, and mapper
   - Estimated effort: 2-4 hours

2. **Implement Inventory Release on Cancellation** (CRITICAL)
   - Update OrderServiceImpl.changeOrderStatus()
   - Add inventory release logic
   - Estimated effort: 2-3 hours

3. **Clean Up Step Definitions** (OPTIONAL)
   - Remove unused fields from request payloads
   - Estimated effort: 30 minutes

### Long-term Recommendations

1. **Re-enable Citrus Tests** for orchestration validation
2. **Complete User Disable Implementation**
3. **Add Missing Test Coverage** for edge cases and performance scenarios
4. **Document Test Data Strategy** (dynamic vs. static)

---

**Report Generated**: 2025-01-27  
**Validation Method**: Static code analysis (no test execution)  
**Next Steps**: Implement fixes for critical mismatches, then re-run regression tests
