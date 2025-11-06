@regression @billing @payment
Feature: Payment Processing
  As a payment processor
  I want to process payments
  So that orders can be paid for

  Background:
    Given the API is available
    And a user with username "paymentuser" and email "paymentuser@example.com" and password "password123"
    And I create the user
    And a product with SKU "PAY-001" and name "Payment Product" and description "For payment testing" and price 75.00
    And I create the product
    And an order for user ID "$createdUserId" with product ID "$createdProductId" and quantity 2
    And I create the order

  Scenario: Create a payment for an order
    Given a payment request for order ID "$createdOrderId" with amount 150.00 and payment method "CREDIT_CARD"
    When I create the payment
    Then the payment is created successfully
    And the payment response contains status "PROCESSING"

  Scenario: Get payment by ID
    Given a payment request for order ID "$createdOrderId" with amount 150.00 and payment method "CREDIT_CARD"
    When I create the payment
    And I get the payment by ID "$createdPaymentId"
    Then the response status code is 200
    And the payment response contains status "PROCESSING"

