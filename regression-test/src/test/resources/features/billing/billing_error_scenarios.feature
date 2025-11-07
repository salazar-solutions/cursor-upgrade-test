@regression @billing @error-handling
Feature: Billing Error Scenarios
  As a system
  I want to handle billing errors gracefully
  So that payment operations are validated correctly

  Background:
    Given the API is available
    And a user with username "billingerroruser" and email "billingerror@example.com" and password "password123"
    And I create the user
    And the user is created successfully

  Scenario: Get non-existent payment
    When I get the payment by ID "00000000-0000-0000-0000-000000000000"
    Then the response status code is 404

  Scenario: Create payment with negative amount
    Given a product with SKU "PAY-ERR-001" and name "Payment Error Product" and description "Test" and price 50.00
    And I create the product
    And an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 1
    And I create the order
    Given a payment request for order ID "$createdOrderId" with amount -100.00 and payment method "CREDIT_CARD"
    When I create the payment
    Then the response status code is 400

  Scenario: Create payment with zero amount
    Given a product with SKU "PAY-ZERO-001" and name "Zero Payment Product" and description "Test" and price 50.00
    And I create the product
    And an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 1
    And I create the order
    Given a payment request for order ID "$createdOrderId" with amount 0.00 and payment method "CREDIT_CARD"
    When I create the payment
    Then the response status code is 400

  Scenario: Create payment for non-existent order
    Given a payment request for order ID "00000000-0000-0000-0000-000000000000" with amount 100.00 and payment method "CREDIT_CARD"
    When I create the payment
    Then the response status code is 404

