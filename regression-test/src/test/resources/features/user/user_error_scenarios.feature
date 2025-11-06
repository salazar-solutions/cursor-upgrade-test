@regression @user @error-handling
Feature: User Error Scenarios
  As a system
  I want to handle errors gracefully
  So that users receive appropriate error messages

  Background:
    Given the API is available
    And a user with username "existinguser" and email "existing@example.com" and password "password123"
    And I create the user

  Scenario: Create user with duplicate username
    Given a user with username "existinguser" and email "newemail@example.com" and password "password123"
    When I create the user
    Then the response status code is 409

  Scenario: Create user with duplicate email
    Given a user with username "newuser" and email "existing@example.com" and password "password123"
    When I create the user
    Then the response status code is 409

  Scenario: Get non-existent user
    When I get the user by ID "00000000-0000-0000-0000-000000000000"
    Then the response status code is 404

  Scenario: Update non-existent user
    Given a user with username "updateuser" and email "update@example.com" and password "password123"
    When I update the user with ID "00000000-0000-0000-0000-000000000000" with email "updated@example.com"
    Then the response status code is 404

  Scenario: Create user with invalid email format
    Given a user with username "invalidemail" and email "not-an-email" and password "password123"
    When I create the user
    Then the response status code is 400

  Scenario: Create user with short password
    Given a user with username "shortpass" and email "shortpass@example.com" and password "123"
    When I create the user
    Then the response status code is 400

  Scenario: Create user with empty username
    Given a user with username "" and email "emptyuser@example.com" and password "password123"
    When I create the user
    Then the response status code is 400

  Scenario: Login with invalid credentials
    When I login with username "nonexistent" and password "wrongpassword"
    Then the response status code is 422

  Scenario: Login with wrong password
    When I login with username "existinguser" and password "wrongpassword"
    Then the response status code is 422

