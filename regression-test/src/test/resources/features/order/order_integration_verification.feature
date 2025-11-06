@regression @order @integration @verification
Feature: Order Integration Verification
  As a system
  I want to verify order creation triggers all integrations
  So that I can ensure all systems work together correctly

  Background:
    Given the API is available
    And a user with username "integrationuser" and email "integration@example.com" and password "password123"
    And I create the user
    And a product with SKU "INTEG-001" and name "Integration Product" and description "Integration test" and price 75.00
    And I create the product
    And I get inventory for product ID "$createdProductId"
    And the initial available quantity is stored as "initialAvailableQty"
    And the initial reserved quantity is stored as "initialReservedQty"

  Scenario: Verify inventory reservation after order creation
    Given an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 5
    When I create the order
    Then the order is created successfully
    When I get inventory for product ID "$createdProductId"
    Then the available quantity decreased by 5
    And the reserved quantity increased by 5

  Scenario: Verify payment creation after order creation
    Given an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 3
    When I create the order
    Then the order is created successfully
    And the order response contains payment ID
    When I get the payment by ID from order response
    Then the response status code is 200
    And the payment amount matches the order total

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

  Scenario: Verify order creation with multiple products
    Given a second product with SKU "INTEG-002" and name "Integration Product 2" and description "Second product" and price 50.00
    And I create the second product
    Given an order with multiple products for user ID "$createdUserId"
    When I create the multi-product order
    Then the order is created successfully
    And the order contains 2 products
    And inventory is reserved for all products

