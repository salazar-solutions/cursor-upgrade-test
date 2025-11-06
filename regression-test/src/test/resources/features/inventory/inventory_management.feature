@regression @inventory @inventory-management
Feature: Inventory Management
  As an inventory manager
  I want to manage inventory
  So that I can track stock levels and reservations

  Background:
    Given the API is available
    And a product with SKU "INV-001" and name "Inventory Product" and description "For inventory testing" and price 50.00
    And I create the product

  Scenario: Get inventory for a product
    When I get inventory for product ID "$createdProductId"
    Then the response status code is 200
    And the inventory response contains available quantity

  Scenario: Reserve inventory
    When I reserve 5 units of inventory for product ID "$createdProductId"
    Then the response status code is 200
    And the inventory reservation is successful

  Scenario: Release inventory
    Given I reserve 10 units of inventory for product ID "$createdProductId"
    When I release 5 units of inventory for product ID "$createdProductId"
    Then the response status code is 200
    And the inventory release is successful

