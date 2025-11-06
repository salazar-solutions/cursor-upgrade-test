@regression @product @product-management
Feature: Product Management
  As a product manager
  I want to manage products
  So that I can maintain the product catalog

  Background:
    Given the API is available

  Scenario: Create a new product
    Given a product with SKU "SKU-001" and name "Test Product" and description "Test Description" and price 99.99
    When I create the product
    Then the product is created successfully
    And the product response contains name "Test Product"

  Scenario: Get product by ID
    Given a product with SKU "SKU-002" and name "Another Product" and description "Another Description" and price 149.99
    When I create the product
    And I get the product by ID "$createdProductId"
    Then the response status code is 200
    And the product response contains name "Another Product"

  Scenario: Search products
    Given a product with SKU "SKU-003" and name "Searchable Product" and description "This is searchable" and price 79.99
    When I create the product
    And I search products with search term "Searchable", page 0, and size 10
    Then the response status code is 200
    And the product search returns at least 1 product

