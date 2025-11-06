package com.example.app.regression.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for billing and payment flows.
 */
@Component
public class BillingStepDefinitions {

    @Autowired
    private BaseStepDefinitions baseSteps;

    @Given("a payment request for order ID {string} with amount {double} and payment method {string}")
    public void aPaymentRequestForOrderIdWithAmountAndPaymentMethod(String orderId, Double amount, String paymentMethod) {
        String actualOrderId = orderId.startsWith("$") ? 
            baseSteps.getFromContext(orderId.substring(1)).toString() : orderId;
        
        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("orderId", actualOrderId);
        paymentRequest.put("amount", amount);
        paymentRequest.put("paymentMethod", paymentMethod);
        
        baseSteps.storeInContext("paymentRequest", paymentRequest);
    }

    @When("I create the payment")
    public void iCreateThePayment() {
        Map<String, Object> paymentRequest = baseSteps.getFromContext("paymentRequest");
        Response response = baseSteps.given()
                .body(paymentRequest)
                .when()
                .post("/billing/payments");
        baseSteps.setLastResponse(response);
    }

    @When("I get the payment by ID {string}")
    public void iGetThePaymentById(String paymentId) {
        String actualPaymentId = paymentId.startsWith("$") ? 
            baseSteps.getFromContext(paymentId.substring(1)).toString() : paymentId;
        Response response = baseSteps.given()
                .when()
                .get("/billing/payments/" + actualPaymentId);
        baseSteps.setLastResponse(response);
    }

    @Then("the payment is created successfully")
    public void thePaymentIsCreatedSuccessfully() {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getString("id")).isNotNull();
        baseSteps.storeInContext("createdPaymentId", response.jsonPath().getString("id"));
    }

    @Then("the payment response contains status {string}")
    public void thePaymentResponseContainsStatus(String status) {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isIn(200, 201);
        assertThat(response.jsonPath().getString("status")).isEqualTo(status);
    }
}

