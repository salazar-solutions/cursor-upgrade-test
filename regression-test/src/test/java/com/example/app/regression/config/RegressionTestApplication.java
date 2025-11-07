package com.example.app.regression.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Spring Boot application class for regression testing.
 * This application loads all modules and enables JPA repositories.
 * Step definitions are excluded from component scanning as they are managed by Cucumber SpringFactory.
 */
@SpringBootApplication(scanBasePackages = "com.example.app")
@ComponentScan(
    basePackages = "com.example.app",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "com\\.example\\.app\\.regression\\.steps\\..*"
    )
)
@EntityScan(basePackages = "com.example.app")
@EnableJpaRepositories(basePackages = "com.example.app")
public class RegressionTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegressionTestApplication.class, args);
    }
}

