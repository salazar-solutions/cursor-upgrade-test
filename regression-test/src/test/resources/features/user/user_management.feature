@regression @user @user-management
Feature: User Management
  As a system administrator
  I want to manage users
  So that I can maintain user accounts in the system

  Background:
    Given the API is available

  Scenario: Create a new user
    Given a user with username "testuser1" and email "testuser1@example.com" and password "password123"
    When I create the user
    Then the user is created successfully
    And the user response contains username "testuser1"

  Scenario: Get user by ID
    Given a user with username "testuser2" and email "testuser2@example.com" and password "password123"
    When I create the user
    And I get the user by ID "$createdUserId"
    Then the response status code is 200
    And the user response contains username "testuser2"

  Scenario: Update user
    Given a user with username "testuser3" and email "testuser3@example.com" and password "password123"
    When I create the user
    And I update the user with ID "$createdUserId" with email "updated@example.com"
    Then the response status code is 200
    And the user response contains username "testuser3"

  Scenario: Get all users with pagination
    When I get all users with page 0 and size 10
    Then the response status code is 200
    And the user list contains at least 0 user

