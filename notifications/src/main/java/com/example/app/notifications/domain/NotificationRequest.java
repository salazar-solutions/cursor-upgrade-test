package com.example.app.notifications.domain;

import javax.validation.constraints.NotBlank;

/**
 * Request model for sending a notification.
 */
public class NotificationRequest {
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Message is required")
    private String message;

    @NotBlank(message = "Type is required")
    private String type;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

