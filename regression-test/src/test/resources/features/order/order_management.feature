@regression @order @order-management
Feature: Order Management
  As a customer
  I want to manage orders
  So that I can place and track my orders

  Background:
    Given the API is available
    And a user with username "orderuser" and email "orderuser@example.com" and password "password123"
    And I create the user
    And a product with SKU "ORD-001" and name "Order Product" and description "For order testing" and price 25.00
    And I create the product

  Scenario: Create a new order
    Given an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 2
    When I create the order
    Then the order is created successfully
    And the order response contains status "PENDING"

  Scenario: Get order by ID
    Given an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 1
    When I create the order
    And I get the order by ID "$createdOrderId"
    Then the response status code is 200
    And the order response contains status "PENDING"

  Scenario: Get orders for a user
    Given an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 1
    When I create the order
    And I get orders for user ID "$createdUserId" with status "PENDING"
    Then the response status code is 200
    And the order list contains at least 1 order

  Scenario: Change order status
    Given an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 1
    When I create the order
    And I change order status to "CONFIRMED" for order ID "$createdOrderId"
    Then the response status code is 200
    And the order response contains status "CONFIRMED"

