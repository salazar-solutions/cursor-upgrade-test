package com.example.app.regression.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for inventory management flows.
 */
@Component
public class InventoryStepDefinitions {

    @Autowired
    private BaseStepDefinitions baseSteps;

    @When("I get inventory for product ID {string}")
    public void iGetInventoryForProductId(String productId) {
        String actualProductId = productId.startsWith("$") ? 
            baseSteps.getFromContext(productId.substring(1)).toString() : productId;
        Response response = baseSteps.given()
                .when()
                .get("/inventory/" + actualProductId);
        baseSteps.setLastResponse(response);
    }

    @When("I reserve {int} units of inventory for product ID {string}")
    public void iReserveUnitsOfInventoryForProductId(int quantity, String productId) {
        String actualProductId = productId.startsWith("$") ? 
            baseSteps.getFromContext(productId.substring(1)).toString() : productId;
        Map<String, Object> reserveRequest = new HashMap<>();
        reserveRequest.put("quantity", quantity);
        reserveRequest.put("orderId", java.util.UUID.randomUUID().toString());
        
        Response response = baseSteps.given()
                .body(reserveRequest)
                .when()
                .post("/inventory/" + actualProductId + "/reserve");
        baseSteps.setLastResponse(response);
    }

    @When("I release {int} units of inventory for product ID {string}")
    public void iReleaseUnitsOfInventoryForProductId(int quantity, String productId) {
        String actualProductId = productId.startsWith("$") ? 
            baseSteps.getFromContext(productId.substring(1)).toString() : productId;
        Map<String, Object> releaseRequest = new HashMap<>();
        releaseRequest.put("quantity", quantity);
        releaseRequest.put("orderId", java.util.UUID.randomUUID().toString());
        
        Response response = baseSteps.given()
                .body(releaseRequest)
                .when()
                .post("/inventory/" + actualProductId + "/release");
        baseSteps.setLastResponse(response);
    }

    @Then("the inventory response contains available quantity")
    public void theInventoryResponseContainsAvailableQuantity() {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getInt("availableQuantity")).isGreaterThanOrEqualTo(0);
    }

    @Then("the inventory reservation is successful")
    public void theInventoryReservationIsSuccessful() {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getInt("reservedQuantity")).isGreaterThanOrEqualTo(0);
    }

    @Then("the inventory release is successful")
    public void theInventoryReleaseIsSuccessful() {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    @Given("the available quantity is stored as {string}")
    public void theAvailableQuantityIsStoredAs(String key) {
        Response response = baseSteps.getLastResponse();
        int availableQty = response.jsonPath().getInt("availableQuantity");
        baseSteps.storeInContext(key, availableQty);
    }

    @Given("the initial reserved quantity is stored as {string}")
    public void theInitialReservedQuantityIsStoredAs(String key) {
        Response response = baseSteps.getLastResponse();
        int reservedQty = response.jsonPath().getInt("reservedQuantity");
        baseSteps.storeInContext(key, reservedQty);
    }

    @Then("the available quantity decreased by {int}")
    public void theAvailableQuantityDecreasedBy(int decreaseAmount) {
        Response response = baseSteps.getLastResponse();
        int currentAvailable = response.jsonPath().getInt("availableQuantity");
        Integer initialAvailable = baseSteps.getFromContext("initialAvailableQty");
        assertThat(initialAvailable - currentAvailable).isEqualTo(decreaseAmount);
    }

    @Then("the reserved quantity increased by {int}")
    public void theReservedQuantityIncreasedBy(int increaseAmount) {
        Response response = baseSteps.getLastResponse();
        int currentReserved = response.jsonPath().getInt("reservedQuantity");
        Integer initialReserved = baseSteps.getFromContext("initialReservedQty");
        assertThat(currentReserved - initialReserved).isEqualTo(increaseAmount);
    }

    @Given("the reserved quantity after order is stored as {string}")
    public void theReservedQuantityAfterOrderIsStoredAs(String key) {
        Response response = baseSteps.getLastResponse();
        int reservedQty = response.jsonPath().getInt("reservedQuantity");
        baseSteps.storeInContext(key, reservedQty);
    }

    @Then("the reserved quantity decreased after cancellation")
    public void theReservedQuantityDecreasedAfterCancellation() {
        Response response = baseSteps.getLastResponse();
        int currentReserved = response.jsonPath().getInt("reservedQuantity");
        Integer reservedAfterOrder = baseSteps.getFromContext("reservedAfterOrder");
        assertThat(currentReserved).isLessThan(reservedAfterOrder);
    }

    @Then("the available quantity increased after cancellation")
    public void theAvailableQuantityIncreasedAfterCancellation() {
        Response response = baseSteps.getLastResponse();
        int currentAvailable = response.jsonPath().getInt("availableQuantity");
        Integer initialAvailable = baseSteps.getFromContext("initialAvailableQty");
        assertThat(currentAvailable).isGreaterThan(initialAvailable);
    }

    @Given("a reserve request with quantity {int} for product ID {string}")
    public void aReserveRequestWithQuantityForProductId(int quantity, String productId) {
        Map<String, Object> reserveRequest = new HashMap<>();
        reserveRequest.put("quantity", quantity);
        reserveRequest.put("orderId", java.util.UUID.randomUUID().toString());
        baseSteps.storeInContext("reserveRequest", reserveRequest);
        baseSteps.storeInContext("reserveProductId", productId.startsWith("$") ? 
            baseSteps.getFromContext(productId.substring(1)).toString() : productId);
    }

    @When("I attempt to reserve with invalid quantity")
    public void iAttemptToReserveWithInvalidQuantity() {
        Map<String, Object> reserveRequest = baseSteps.getFromContext("reserveRequest");
        String productId = baseSteps.getFromContext("reserveProductId");
        Response response = baseSteps.given()
                .body(reserveRequest)
                .when()
                .post("/inventory/" + productId + "/reserve");
        baseSteps.setLastResponse(response);
    }
}

