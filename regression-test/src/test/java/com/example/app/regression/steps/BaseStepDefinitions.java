package com.example.app.regression.steps;

import io.cucumber.java.en.Given;
import io.cucumber.spring.ScenarioScope;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Base step definitions providing common functionality for all test steps.
 */
@Component
@ScenarioScope
public class BaseStepDefinitions {

    @Value("${api.base.url:http://localhost:8080}")
    private String baseUrl;

    @Value("${api.base.path:/api/v1}")
    private String basePath;

    private Response lastResponse;
    private Map<String, Object> scenarioContext = new HashMap<>();

    public void setupRestAssured() {
        RestAssured.baseURI = baseUrl;
        RestAssured.basePath = basePath;
    }

    public RequestSpecification given() {
        setupRestAssured();
        return RestAssured.given()
                .contentType("application/json")
                .accept("application/json");
    }

    public Response getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(Response response) {
        this.lastResponse = response;
    }

    public void storeInContext(String key, Object value) {
        scenarioContext.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getFromContext(String key) {
        return (T) scenarioContext.get(key);
    }

    public void clearContext() {
        scenarioContext.clear();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getBasePath() {
        return basePath;
    }

    @Given("the API is available")
    public void theAPIIsAvailable() {
        setupRestAssured();
        // Verify API is accessible by checking health endpoint
        Response response = given()
                .when()
                .get("/admin/health");
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("API is not available. Health check failed.");
        }
    }
}

