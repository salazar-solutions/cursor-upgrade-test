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
 * Step definitions for user management and authentication flows.
 */
@Component
public class UserStepDefinitions {

    @Autowired
    private BaseStepDefinitions baseSteps;

    @Given("a user with username {string} and email {string} and password {string}")
    public void aUserWithUsernameAndEmailAndPassword(String username, String email, String password) {
        Map<String, Object> userRequest = new HashMap<>();
        userRequest.put("username", username);
        userRequest.put("email", email);
        userRequest.put("password", password);
        baseSteps.storeInContext("userRequest", userRequest);
    }

    @When("I create the user")
    public void iCreateTheUser() {
        Map<String, Object> userRequest = baseSteps.getFromContext("userRequest");
        Response response = baseSteps.given()
                .body(userRequest)
                .when()
                .post("/users");
        baseSteps.setLastResponse(response);
    }

    @When("I get the user by ID {string}")
    public void iGetTheUserById(String userId) {
        String actualUserId = userId.startsWith("$") ? 
            baseSteps.getFromContext(userId.substring(1)).toString() : userId;
        Response response = baseSteps.given()
                .when()
                .get("/users/" + actualUserId);
        baseSteps.setLastResponse(response);
    }

    @When("I update the user with ID {string} with email {string}")
    public void iUpdateTheUserWithIdWithEmail(String userId, String email) {
        String actualUserId = userId.startsWith("$") ? 
            baseSteps.getFromContext(userId.substring(1)).toString() : userId;
        Map<String, Object> userRequest = baseSteps.getFromContext("userRequest");
        if (userRequest == null) {
            userRequest = new HashMap<>();
        }
        userRequest.put("email", email);
        
        Response response = baseSteps.given()
                .body(userRequest)
                .when()
                .put("/users/" + actualUserId);
        baseSteps.setLastResponse(response);
    }

    @When("I login with username {string} and password {string}")
    public void iLoginWithUsernameAndPassword(String username, String password) {
        Map<String, Object> authRequest = new HashMap<>();
        authRequest.put("username", username);
        authRequest.put("password", password);
        
        Response response = baseSteps.given()
                .body(authRequest)
                .when()
                .post("/users/auth/login");
        baseSteps.setLastResponse(response);
        
        // Store token if login successful
        if (response.getStatusCode() == 200) {
            String token = response.jsonPath().getString("token");
            baseSteps.storeInContext("authToken", token);
        }
    }

    @When("I get all users with page {int} and size {int}")
    public void iGetAllUsersWithPageAndSize(int page, int size) {
        Response response = baseSteps.given()
                .queryParam("page", page)
                .queryParam("size", size)
                .when()
                .get("/users");
        baseSteps.setLastResponse(response);
    }

    @Then("the user is created successfully")
    public void theUserIsCreatedSuccessfully() {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getString("id")).isNotNull();
        baseSteps.storeInContext("createdUserId", response.jsonPath().getString("id"));
    }

    @Then("the user response contains username {string}")
    public void theUserResponseContainsUsername(String username) {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isIn(200, 201);
        assertThat(response.jsonPath().getString("username")).isEqualTo(username);
    }

    @Then("the authentication is successful and I receive a token")
    public void theAuthenticationIsSuccessfulAndIReceiveAToken() {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("token")).isNotNull();
    }

    @Then("the response status code is {int}")
    public void theResponseStatusCodeIs(int statusCode) {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isEqualTo(statusCode);
    }

    @Then("the user list contains at least {int} user")
    public void theUserListContainsAtLeastUser(int minCount) {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isEqualTo(200);
        int totalElements = response.jsonPath().getInt("totalElements");
        assertThat(totalElements).isGreaterThanOrEqualTo(minCount);
    }
}

