@regression @admin @admin-operations
Feature: Admin Operations
  As an administrator
  I want to perform administrative tasks
  So that I can monitor and manage the system

  Background:
    Given the API is available

  Scenario: Health check
    When I check the health endpoint
    Then the response status code is 200
    And the health check returns status UP

  Scenario: Get metrics endpoint
    When I get the metrics endpoint
    Then the response status code is 200
    And the metrics endpoint is accessible

  Scenario: List all users via admin endpoint
    When I list all users via admin endpoint with page 0 and size 10
    Then the response status code is 200
    And the user list contains at least 0 user

  Scenario: Disable user via admin endpoint
    Given a user with username "disableuser" and email "disableuser@example.com" and password "password123"
    And I create the user
    When I disable user with ID "$createdUserId" via admin endpoint
    Then the response status code is 200

