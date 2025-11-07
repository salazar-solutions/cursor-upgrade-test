package com.example.app.regression.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for notification flows.
 */
public class NotificationStepDefinitions {

    @Autowired
    private BaseStepDefinitions baseSteps;

    @Given("a notification request for user ID {string} with type {string} and message {string}")
    public void aNotificationRequestForUserIdWithTypeAndMessage(String userId, String type, String message) {
        String actualUserId = userId.startsWith("$") ? 
            baseSteps.getFromContext(userId.substring(1)).toString() : userId;
        
        Map<String, Object> notificationRequest = new HashMap<>();
        notificationRequest.put("userId", actualUserId);
        notificationRequest.put("type", type);
        notificationRequest.put("message", message);
        
        baseSteps.storeInContext("notificationRequest", notificationRequest);
    }

    @When("I send the notification")
    public void iSendTheNotification() {
        Map<String, Object> notificationRequest = baseSteps.getFromContext("notificationRequest");
        Response response = baseSteps.given()
                .body(notificationRequest)
                .when()
                .post("/notifications/send");
        baseSteps.setLastResponse(response);
    }

    @Then("the notification is sent successfully")
    public void theNotificationIsSentSuccessfully() {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("status")).isEqualTo("sent");
    }
}

