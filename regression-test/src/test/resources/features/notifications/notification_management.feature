@regression @notifications @notification-management
Feature: Notification Management
  As a notification service
  I want to send notifications
  So that users are informed of important events

  Background:
    Given the API is available
    And a user with username "notifyuser" and email "notifyuser@example.com" and password "password123"
    And I create the user

  Scenario: Send order created notification
    Given a notification request for user ID "$createdUserId" with type "ORDER_CREATED" and message "Your order has been created"
    When I send the notification
    Then the notification is sent successfully

  Scenario: Send order status changed notification
    Given a notification request for user ID "$createdUserId" with type "ORDER_STATUS_CHANGED" and message "Your order status has been updated"
    When I send the notification
    Then the notification is sent successfully

  Scenario: Send payment processed notification
    Given a notification request for user ID "$createdUserId" with type "PAYMENT_PROCESSED" and message "Your payment has been processed"
    When I send the notification
    Then the notification is sent successfully

