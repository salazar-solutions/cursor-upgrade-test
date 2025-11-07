@regression @notifications @error-handling
Feature: Notification Error Scenarios
  As a system
  I want to handle notification errors gracefully
  So that notification operations are validated correctly

  Background:
    Given the API is available
    And a user with username "notifyerroruser" and email "notifyerror@example.com" and password "password123"
    And I create the user

  Scenario: Send notification with empty message
    Given a notification request for user ID "$createdUserId" with type "ORDER_CREATED" and message ""
    When I send the notification
    Then the response status code is 400

  Scenario: Send notification for non-existent user
    Given a notification request for user ID "00000000-0000-0000-0000-000000000000" with type "ORDER_CREATED" and message "Test message"
    When I send the notification
    Then the response status code is 404

  Scenario: Send notification with invalid type
    Given a notification request for user ID "$createdUserId" with type "INVALID_TYPE" and message "Test message"
    When I send the notification
    Then the response status code is 422

