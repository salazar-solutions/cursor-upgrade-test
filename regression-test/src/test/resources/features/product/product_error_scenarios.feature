@regression @product @error-handling
Feature: Product Error Scenarios
  As a system
  I want to handle product errors gracefully
  So that users receive appropriate error messages

  Background:
    Given the API is available

  Scenario: Get non-existent product
    When I get the product by ID "00000000-0000-0000-0000-000000000000"
    Then the response status code is 404

  Scenario: Create product with negative price
    Given a product with SKU "NEG-001" and name "Negative Price" and description "Test" and price -10.00
    When I create the product
    Then the response status code is 400

  Scenario: Create product with empty name
    Given a product with SKU "EMPTY-001" and name "" and description "Test" and price 10.00
    When I create the product
    Then the response status code is 400

  Scenario: Search products with special characters
    When I search products with search term "!@#$%^&*()", page 0, and size 10
    Then the response status code is 200
    And the product search returns at least 0 product

