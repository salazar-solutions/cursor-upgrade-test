@regression @order @error-handling
Feature: Order Error Scenarios
  As a system
  I want to handle order errors gracefully
  So that order operations are validated correctly

  Background:
    Given the API is available
    And a user with username "ordererroruser" and email "ordererror@example.com" and password "password123"
    And I create the user

  Scenario: Create order with non-existent product
    Given an order for user ID "$createdUserId" with product ID "00000000-0000-0000-0000-000000000000" and quantity 1
    When I create the order
    Then the response status code is 404

  Scenario: Create order with non-existent user
    Given a product with SKU "ORD-ERR-001" and name "Order Error Product" and description "Test" and price 25.00
    And I create the product
    Given an order for user ID "00000000-0000-0000-0000-000000000000" with product ID "$createdProductId" and quantity 1
    When I create the order
    Then the response status code is 404

  Scenario: Get non-existent order
    When I get the order by ID "00000000-0000-0000-0000-000000000000"
    Then the response status code is 404

  Scenario: Change status of non-existent order
    Given a status change request to "CONFIRMED" for order ID "00000000-0000-0000-0000-000000000000"
    When I change order status to "CONFIRMED" for order ID "00000000-0000-0000-0000-000000000000"
    Then the response status code is 404

  Scenario: Invalid order status transition
    Given a product with SKU "STATUS-ERR-001" and name "Status Error Product" and description "Test" and price 30.00
    And I create the product
    And an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 1
    And I create the order
    When I change order status to "DELIVERED" for order ID "$createdOrderId"
    Then the response status code is 422

  Scenario: Create order with zero quantity
    Given a product with SKU "ZERO-QTY-001" and name "Zero Qty Product" and description "Test" and price 20.00
    And I create the product
    Given an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 0
    When I create the order
    Then the response status code is 400

  Scenario: Create order with negative quantity
    Given a product with SKU "NEG-QTY-001" and name "Negative Qty Product" and description "Test" and price 15.00
    And I create the product
    Given an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity -1
    When I create the order
    Then the response status code is 400

