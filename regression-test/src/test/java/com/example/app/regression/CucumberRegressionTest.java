package com.example.app.regression;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/**
 * Cucumber regression test runner.
 * Tests are only executed when the regression profile is active.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/cucumber.html, json:target/cucumber-reports/cucumber.json, junit:target/cucumber-reports/cucumber.xml")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.example.app.regression.steps")
@ConfigurationParameter(key = Constants.FILTER_TAGS_PROPERTY_NAME, value = "@regression")
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "regression")
public class CucumberRegressionTest {
    // Test runner class - configuration is done via annotations
}

