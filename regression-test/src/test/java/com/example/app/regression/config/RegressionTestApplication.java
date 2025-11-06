package com.example.app.regression.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Spring Boot application class for regression testing.
 * This application loads all modules and enables JPA repositories.
 */
@SpringBootApplication(scanBasePackages = "com.example.app")
@EntityScan(basePackages = "com.example.app")
@EnableJpaRepositories(basePackages = "com.example.app")
public class RegressionTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegressionTestApplication.class, args);
    }
}

