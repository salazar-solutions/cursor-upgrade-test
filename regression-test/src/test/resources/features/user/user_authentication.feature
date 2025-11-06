@regression @user @authentication @login
Feature: User Authentication
  As a user
  I want to authenticate
  So that I can access protected resources

  Background:
    Given the API is available
    And a user with username "authuser" and email "authuser@example.com" and password "password123"
    And I create the user

  Scenario: Successful login with valid credentials
    When I login with username "authuser" and password "password123"
    Then the authentication is successful and I receive a token

  Scenario: Failed login with invalid password
    When I login with username "authuser" and password "wrongpassword"
    Then the response status code is 422

