package com.example.app.regression.steps;

import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for admin operations.
 */
@Component
public class AdminStepDefinitions {

    @Autowired
    private BaseStepDefinitions baseSteps;

    @When("I check the health endpoint")
    public void iCheckTheHealthEndpoint() {
        Response response = baseSteps.given()
                .when()
                .get("/admin/health");
        baseSteps.setLastResponse(response);
    }

    @When("I get the metrics endpoint")
    public void iGetTheMetricsEndpoint() {
        Response response = baseSteps.given()
                .when()
                .get("/admin/metrics");
        baseSteps.setLastResponse(response);
    }

    @When("I list all users via admin endpoint with page {int} and size {int}")
    public void iListAllUsersViaAdminEndpointWithPageAndSize(int page, int size) {
        Response response = baseSteps.given()
                .queryParam("page", page)
                .queryParam("size", size)
                .when()
                .get("/admin/users");
        baseSteps.setLastResponse(response);
    }

    @When("I disable user with ID {string} via admin endpoint")
    public void iDisableUserWithIdViaAdminEndpoint(String userId) {
        String actualUserId = userId.startsWith("$") ? 
            baseSteps.getFromContext(userId.substring(1)).toString() : userId;
        Response response = baseSteps.given()
                .when()
                .put("/admin/users/" + actualUserId + "/disable");
        baseSteps.setLastResponse(response);
    }

    @Then("the health check returns status UP")
    public void theHealthCheckReturnsStatusUP() {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("status")).isEqualTo("UP");
    }

    @Then("the metrics endpoint is accessible")
    public void theMetricsEndpointIsAccessible() {
        Response response = baseSteps.getLastResponse();
        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}

