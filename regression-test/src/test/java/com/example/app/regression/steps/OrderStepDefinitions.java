package com.example.app.regression.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for order management flows.
 */
@Component
public class OrderStepDefinitions {

    @Autowired
    private BaseStepDefinitions baseSteps;

    @Given("an order for user ID {string} with product ID {string} and quantity {int}")
    public void anOrderForUserIdWithProductIdAndQuantity(String userId, String productId, int quantity) {
        String actualUserId = userId.startsWith("$") ? 
            baseSteps.getFromContext(userId.substring(1)).toString() : userId;
        String actualProductId = productId.startsWith("$") ? 
            baseSteps.getFromContext(productId.substring(1)).toString() : productId;
        
        Map<String, Object> orderLine = new HashMap<>();
        orderLine.put("productId", actualProductId);
        orderLine.put("quantity", quantity);
        
        List<Map<String, Object>> orderLines = new ArrayList<>();
        orderLines.add(orderLine);
        
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("userId", actualUserId);
        orderRequest.put("orderLines", orderLines);
        
        baseSteps.storeInContext("orderRequest", orderRequest);
    }

    @When("I create the order")
    public void iCreateTheOrder() {
        Map<String, Object> orderRequest = baseSteps.getFromContext("orderRequest");
        Response response = baseSteps.given()
                .body(orderRequest)
                .when()
                .post("/orders");
        baseSteps.setLastResponse(response);
    }

    @When("I get the order by ID {string}")
    public void iGetTheOrderById(String orderId) {
        String actualOrderId = orderId.startsWith("$") ? 
            baseSteps.getFromContext(orderId.substring(1)).toString() : orderId;
        Response response = baseSteps.given()
                .when()
                .get("/orders/" + actualOrderId);
        baseSteps.setLastResponse(response);
    }

    @When("I get orders for user ID {string} with status {string}")
    public void iGetOrdersForUserIdWithStatus(String userId, String status) {
        String actualUserId = userId.startsWith("$") ? 
            baseSteps.getFromContext(userId.substring(1)).toString() : userId;
        Response response = baseSteps.given()
                .queryParam("userId", actualUserId)
                .queryParam("status", status)
                .when()
                .get("/orders");
        baseSteps.setLastResponse(response);
    }

    @When("I change order status to {string} for order ID {string}")
    public void iChangeOrderStatusToForOrderId(String status, String orderId) {
        String actualOrderId = orderId.startsWith("$") ? 
            baseSteps.getFromContext(orderId.substring(1)).toString() : orderId;
        Map<String, Object> statusRequest = new HashMap<>();
        statusRequest.put("status", status);
        
        Response response = baseSteps.given()
                .body(statusRequest)
                .when()
                .post("/orders/" + actualOrderId + "/status");
        baseSteps.setLastResponse(response);
    }

    @Then("the order is created successfully")
    public void theOrderIsCreatedSuccessfully() {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getString("id")).isNotNull();
        baseSteps.storeInContext("createdOrderId", response.jsonPath().getString("id"));
    }

    @Then("the order response contains status {string}")
    public void theOrderResponseContainsStatus(String status) {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isIn(200, 201);
        assertThat(response.jsonPath().getString("status")).isEqualTo(status);
    }

    @Then("the order list contains at least {int} order")
    public void theOrderListContainsAtLeastOrder(int minCount) {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isEqualTo(200);
        int totalElements = response.jsonPath().getInt("totalElements");
        assertThat(totalElements).isGreaterThanOrEqualTo(minCount);
    }

    @Then("the order response contains payment ID")
    public void theOrderResponseContainsPaymentId() {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isIn(200, 201);
        String paymentId = response.jsonPath().getString("paymentId");
        assertThat(paymentId).isNotNull();
        baseSteps.storeInContext("orderPaymentId", paymentId);
    }

    @When("I get the payment by ID from order response")
    public void iGetThePaymentByIdFromOrderResponse() {
        String paymentId = baseSteps.getFromContext("orderPaymentId");
        Response response = baseSteps.given()
                .when()
                .get("/billing/payments/" + paymentId);
        baseSteps.setLastResponse(response);
    }

    @Then("the payment amount matches the order total")
    public void thePaymentAmountMatchesTheOrderTotal() {
        Response paymentResponse = baseSteps.getLastResponse();
        assertThat(paymentResponse.getStatusCode()).isEqualTo(200);
        
        // Get order total from context or from order response
        String orderId = baseSteps.getFromContext("createdOrderId");
        Response orderResponse = baseSteps.given()
                .when()
                .get("/orders/" + orderId);
        
        double orderTotal = orderResponse.jsonPath().getDouble("totalAmount");
        double paymentAmount = paymentResponse.jsonPath().getDouble("amount");
        assertThat(paymentAmount).isEqualTo(orderTotal);
    }

    @Given("an order with multiple products for user ID {string}")
    public void anOrderWithMultipleProductsForUserId(String userId) {
        String actualUserId = userId.startsWith("$") ? 
            baseSteps.getFromContext(userId.substring(1)).toString() : userId;
        
        // Get product IDs from context
        String productId1 = baseSteps.getFromContext("createdProductId");
        String productId2 = baseSteps.getFromContext("createdProductId2");
        
        List<Map<String, Object>> orderLines = new ArrayList<>();
        
        Map<String, Object> orderLine1 = new HashMap<>();
        orderLine1.put("productId", productId1);
        orderLine1.put("quantity", 2);
        orderLines.add(orderLine1);
        
        Map<String, Object> orderLine2 = new HashMap<>();
        orderLine2.put("productId", productId2);
        orderLine2.put("quantity", 1);
        orderLines.add(orderLine2);
        
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("userId", actualUserId);
        orderRequest.put("orderLines", orderLines);
        
        baseSteps.storeInContext("orderRequest", orderRequest);
    }

    @When("I create the multi-product order")
    public void iCreateTheMultiProductOrder() {
        Map<String, Object> orderRequest = baseSteps.getFromContext("orderRequest");
        Response response = baseSteps.given()
                .body(orderRequest)
                .when()
                .post("/orders");
        baseSteps.setLastResponse(response);
    }

    @Then("the order contains {int} products")
    public void theOrderContainsProducts(int productCount) {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isIn(200, 201);
        int actualCount = response.jsonPath().getList("orderLines").size();
        assertThat(actualCount).isEqualTo(productCount);
    }

    @Then("inventory is reserved for all products")
    public void inventoryIsReservedForAllProducts() {
        Response orderResponse = baseSteps.getLastResponse();
        List<Map<String, Object>> orderLines = orderResponse.jsonPath().getList("orderLines");
        
        for (Map<String, Object> line : orderLines) {
            String productId = line.get("productId").toString();
            Response inventoryResponse = baseSteps.given()
                    .when()
                    .get("/inventory/" + productId);
            assertThat(inventoryResponse.getStatusCode()).isEqualTo(200);
            int reservedQty = inventoryResponse.jsonPath().getInt("reservedQuantity");
            assertThat(reservedQty).isGreaterThan(0);
        }
    }

    @Given("a status change request to {string} for order ID {string}")
    public void aStatusChangeRequestToForOrderId(String status, String orderId) {
        // This step is for documentation, actual status change happens in the When step
        baseSteps.storeInContext("statusChangeRequest", status);
    }
}

