package com.example.app.regression.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Cucumber Spring configuration for integration with Spring Boot.
 * Tests are only enabled when the regression profile is active.
 */
@CucumberContextConfiguration
@SpringBootTest(
    classes = RegressionTestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@ActiveProfiles({"local", "regression"})
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "regression")
public class CucumberSpringConfiguration {
    // Configuration class for Cucumber Spring integration
    // This class is automatically picked up by Cucumber Spring
}

