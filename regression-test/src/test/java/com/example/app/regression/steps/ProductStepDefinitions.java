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
 * Step definitions for product management flows.
 */
@Component
public class ProductStepDefinitions {

    @Autowired
    private BaseStepDefinitions baseSteps;

    @Given("a product with SKU {string} and name {string} and description {string} and price {double}")
    public void aProductWithSKUNameDescriptionAndPrice(String sku, String name, String description, Double price) {
        Map<String, Object> productRequest = new HashMap<>();
        productRequest.put("sku", sku);
        productRequest.put("name", name);
        productRequest.put("description", description);
        productRequest.put("price", price);
        baseSteps.storeInContext("productRequest", productRequest);
    }

    @When("I create the product")
    public void iCreateTheProduct() {
        Map<String, Object> productRequest = baseSteps.getFromContext("productRequest");
        Response response = baseSteps.given()
                .body(productRequest)
                .when()
                .post("/products");
        baseSteps.setLastResponse(response);
    }

    @When("I get the product by ID {string}")
    public void iGetTheProductById(String productId) {
        String actualProductId = productId.startsWith("$") ? 
            baseSteps.getFromContext(productId.substring(1)).toString() : productId;
        Response response = baseSteps.given()
                .when()
                .get("/products/" + actualProductId);
        baseSteps.setLastResponse(response);
    }

    @When("I search products with search term {string}, page {int}, and size {int}")
    public void iSearchProductsWithSearchTermPageAndSize(String searchTerm, int page, int size) {
        Response response = baseSteps.given()
                .queryParam("search", searchTerm)
                .queryParam("page", page)
                .queryParam("size", size)
                .when()
                .get("/products");
        baseSteps.setLastResponse(response);
    }

    @Then("the product is created successfully")
    public void theProductIsCreatedSuccessfully() {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getString("id")).isNotNull();
        baseSteps.storeInContext("createdProductId", response.jsonPath().getString("id"));
    }

    @Then("the product response contains name {string}")
    public void theProductResponseContainsName(String name) {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isIn(200, 201);
        assertThat(response.jsonPath().getString("name")).isEqualTo(name);
    }

    @Then("the product search returns at least {int} product")
    public void theProductSearchReturnsAtLeastProduct(int minCount) {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isEqualTo(200);
        int totalElements = response.jsonPath().getInt("totalElements");
        assertThat(totalElements).isGreaterThanOrEqualTo(minCount);
    }

    @Given("a second product with SKU {string} and name {string} and description {string} and price {double}")
    public void aSecondProductWithSKUNameDescriptionAndPrice(String sku, String name, String description, Double price) {
        Map<String, Object> productRequest = new HashMap<>();
        productRequest.put("sku", sku);
        productRequest.put("name", name);
        productRequest.put("description", description);
        productRequest.put("price", price);
        baseSteps.storeInContext("productRequest2", productRequest);
    }

    @When("I create the second product")
    public void iCreateTheSecondProduct() {
        Map<String, Object> productRequest = baseSteps.getFromContext("productRequest2");
        Response response = baseSteps.given()
                .body(productRequest)
                .when()
                .post("/products");
        baseSteps.setLastResponse(response);
        if (response.getStatusCode() == 201) {
            baseSteps.storeInContext("createdProductId2", response.jsonPath().getString("id"));
        }
    }
}

