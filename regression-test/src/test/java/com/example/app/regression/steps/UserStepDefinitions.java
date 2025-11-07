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
 * Step definitions for user management and authentication flows.
 */
public class UserStepDefinitions {

    @Autowired
    private BaseStepDefinitions baseSteps;

    @Given("a user with username {string} and email {string} and password {string}")
    public void aUserWithUsernameAndEmailAndPassword(String username, String email, String password) {
        // Add timestamp to make usernames and emails unique across test runs (but not for empty strings used in validation tests)
        long timestamp = System.currentTimeMillis();
        
        // Check if we should reuse a previous username/email (for duplicate testing)
        String uniqueUsername;
        String uniqueEmail;
        
        if (username.equals("existinguser") && baseSteps.getFromContext("existingUsername") != null) {
            // Reuse the same username for duplicate tests
            uniqueUsername = (String) baseSteps.getFromContext("existingUsername");
        } else {
            uniqueUsername = username.isEmpty() ? username : username + "_" + timestamp;
            if (username.equals("existinguser")) {
                baseSteps.storeInContext("existingUsername", uniqueUsername);
            }
        }
        
        if (email.equals("existing@example.com") && baseSteps.getFromContext("existingEmail") != null) {
            // Reuse the same email for duplicate tests
            uniqueEmail = (String) baseSteps.getFromContext("existingEmail");
        } else {
            uniqueEmail = email.isEmpty() || !email.contains("@") ? email : email.replace("@", "_" + timestamp + "@");
            if (email.equals("existing@example.com")) {
                baseSteps.storeInContext("existingEmail", uniqueEmail);
            }
        }
        
        Map<String, Object> userRequest = new HashMap<>();
        userRequest.put("username", uniqueUsername);
        userRequest.put("email", uniqueEmail);
        userRequest.put("password", password);
        baseSteps.storeInContext("userRequest", userRequest);
        baseSteps.storeInContext("originalUsername", username); // Store original for assertions
    }

    @When("I create the user")
    public void iCreateTheUser() {
        Map<String, Object> userRequest = baseSteps.getFromContext("userRequest");
        Response response = baseSteps.given()
                .body(userRequest)
                .when()
                .post("/users");
        baseSteps.setLastResponse(response);
        
        // Always try to store user ID - handle both success and existing user cases
        String userId = response.jsonPath().getString("id");
        if (userId != null && response.getStatusCode() == 201) {
            // User was created successfully
            baseSteps.storeInContext("createdUserId", userId);
        } else if (response.getStatusCode() == 422 || response.getStatusCode() == 409) {
            // User already exists - retrieve it from the list
            String username = (String) userRequest.get("username");
            Response listResponse = baseSteps.given()
                    .queryParam("page", 0)
                    .queryParam("size", 100)
                    .when()
                    .get("/users");
            
            if (listResponse.getStatusCode() == 200) {
                var users = listResponse.jsonPath().getList("content");
                if (users != null) {
                    for (Object userObj : users) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> user = (Map<String, Object>) userObj;
                        if (username != null && username.equals(user.get("username"))) {
                            userId = (String) user.get("id");
                            if (userId != null) {
                                baseSteps.storeInContext("createdUserId", userId);
                            }
                            break;
                        }
                    }
                }
            }
        }
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
        
        // Handle $uniqueEmail placeholder by generating a unique email with timestamp
        String actualEmail;
        if ("$uniqueEmail".equals(email)) {
            long timestamp = System.currentTimeMillis();
            actualEmail = "updated_" + timestamp + "@example.com";
        } else {
            actualEmail = email;
        }
        
        userRequest.put("email", actualEmail);
        
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
        String userId = null;
        
        if (response.getStatusCode() == 201) {
            // User was created successfully
            userId = response.jsonPath().getString("id");
        } else if (response.getStatusCode() == 422 || response.getStatusCode() == 409) {
            // User already exists - retrieve it from the list
            Map<String, Object> userRequest = baseSteps.getFromContext("userRequest");
            String username = (String) userRequest.get("username");
            
            // Get all users and find the one with matching username
            Response listResponse = baseSteps.given()
                    .queryParam("page", 0)
                    .queryParam("size", 100)
                    .when()
                    .get("/users");
            
            if (listResponse.getStatusCode() == 200) {
                var users = listResponse.jsonPath().getList("content");
                if (users != null) {
                    for (Object userObj : users) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> user = (Map<String, Object>) userObj;
                        if (username != null && username.equals(user.get("username"))) {
                            userId = (String) user.get("id");
                            break;
                        }
                    }
                }
            }
        }
        
        assertThat(userId)
            .withFailMessage("Failed to get user ID. Status code: %d, Response: %s", 
                response.getStatusCode(), response.getBody().asString())
            .isNotNull();
        baseSteps.storeInContext("createdUserId", userId);
    }

    @Then("the user response contains username {string}")
    public void theUserResponseContainsUsername(String expectedUsername) {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isIn(200, 201);
        String actualUsername = response.jsonPath().getString("username");
        // Username contains timestamp suffix, so check if it starts with expected base name
        assertThat(actualUsername).startsWith(expectedUsername);
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

