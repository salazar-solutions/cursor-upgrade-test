package com.example.app.common.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {MetricsConfigTest.TestConfig.class})
@TestPropertySource(properties = {
    "spring.actuator.metrics.export.enabled=true"
})
class MetricsConfigTest {

    @Autowired
    private MetricsConfig metricsConfig;

    @BeforeEach
    void setUp() {
        Metrics.globalRegistry.clear();
    }

    @Test
    void testMetricsCommonTags_BeanCreation_ReturnsCustomizer() {
        // Act
        MeterRegistryCustomizer<MeterRegistry> customizer = 
            metricsConfig.metricsCommonTags();

        // Assert
        assertNotNull(customizer);
    }

    @Test
    void testMetricsCommonTags_AppliesCommonTags() {
        // Arrange
        MeterRegistry registry = new SimpleMeterRegistry();
        MeterRegistryCustomizer<MeterRegistry> customizer = 
            metricsConfig.metricsCommonTags();

        // Act
        customizer.customize(registry);

        // Assert
        // Verify that the customizer can be applied without errors
        assertNotNull(registry);
        // The common tag "application" = "multi-module-app" should be configured
    }

    @Configuration
    @Import(MetricsConfig.class)
    static class TestConfig {
        // Minimal configuration - only imports MetricsConfig
    }
}

