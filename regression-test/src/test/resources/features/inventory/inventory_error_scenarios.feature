@regression @inventory @error-handling
Feature: Inventory Error Scenarios
  As a system
  I want to handle inventory errors gracefully
  So that stock operations are validated correctly

  Background:
    Given the API is available
    And a product with SKU "LIMITED-001" and name "Limited Stock" and description "Low stock product" and price 50.00
    And I create the product

  Scenario: Get inventory for non-existent product
    When I get inventory for product ID "00000000-0000-0000-0000-000000000000"
    Then the response status code is 404

  Scenario: Reserve more inventory than available
    Given I get inventory for product ID "$createdProductId"
    And the available quantity is stored as "availableQty"
    When I reserve 999999 units of inventory for product ID "$createdProductId"
    Then the response status code is 422

  Scenario: Release more inventory than reserved
    When I release 999999 units of inventory for product ID "$createdProductId"
    Then the response status code is 422

  Scenario: Reserve with negative quantity
    Given a reserve request with quantity -5 for product ID "$createdProductId"
    When I attempt to reserve with invalid quantity
    Then the response status code is 400

