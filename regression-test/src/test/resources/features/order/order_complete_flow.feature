@regression @order @checkout @e2e
Feature: Complete Order Flow
  As a customer
  I want to complete a full order lifecycle
  So that I can purchase products end-to-end

  Background:
    Given the API is available
    And a user with username "e2euser" and email "e2euser@example.com" and password "password123"
    And I create the user
    And a product with SKU "E2E-001" and name "E2E Product" and description "End-to-end test product" and price 100.00
    And I create the product

  Scenario: Complete order flow from creation to confirmation
    Given an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 3
    When I create the order
    Then the order is created successfully
    And the order response contains status "PENDING"
    When I change order status to "CONFIRMED" for order ID "$createdOrderId"
    Then the response status code is 200
    And the order response contains status "CONFIRMED"
    When I change order status to "PROCESSING" for order ID "$createdOrderId"
    Then the response status code is 200
    And the order response contains status "PROCESSING"

