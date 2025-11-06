@regression @order @lifecycle @e2e
Feature: Complete Order Lifecycle
  As a customer
  I want to see the complete order lifecycle
  So that I can track my order from creation to delivery

  Background:
    Given the API is available
    And a user with username "lifecycleuser" and email "lifecycle@example.com" and password "password123"
    And I create the user
    And a product with SKU "LIFECYCLE-001" and name "Lifecycle Product" and description "Full lifecycle test" and price 100.00
    And I create the product

  Scenario: Complete order lifecycle PENDING to DELIVERED
    Given an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 2
    When I create the order
    Then the order is created successfully
    And the order response contains status "PENDING"
    
    When I change order status to "CONFIRMED" for order ID "$createdOrderId"
    Then the response status code is 200
    And the order response contains status "CONFIRMED"
    
    When I change order status to "PROCESSING" for order ID "$createdOrderId"
    Then the response status code is 200
    And the order response contains status "PROCESSING"
    
    When I change order status to "SHIPPED" for order ID "$createdOrderId"
    Then the response status code is 200
    And the order response contains status "SHIPPED"
    
    When I change order status to "DELIVERED" for order ID "$createdOrderId"
    Then the response status code is 200
    And the order response contains status "DELIVERED"

  Scenario: Order cancellation from PENDING status
    Given an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 1
    When I create the order
    Then the order is created successfully
    And the order response contains status "PENDING"
    
    When I change order status to "CANCELLED" for order ID "$createdOrderId"
    Then the response status code is 200
    And the order response contains status "CANCELLED"

  Scenario: Order cancellation from CONFIRMED status
    Given an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 1
    When I create the order
    And I change order status to "CONFIRMED" for order ID "$createdOrderId"
    When I change order status to "CANCELLED" for order ID "$createdOrderId"
    Then the response status code is 200
    And the order response contains status "CANCELLED"

  Scenario: Cannot cancel DELIVERED order
    Given an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 1
    When I create the order
    And I change order status to "CONFIRMED" for order ID "$createdOrderId"
    And I change order status to "PROCESSING" for order ID "$createdOrderId"
    And I change order status to "SHIPPED" for order ID "$createdOrderId"
    And I change order status to "DELIVERED" for order ID "$createdOrderId"
    When I change order status to "CANCELLED" for order ID "$createdOrderId"
    Then the response status code is 422

