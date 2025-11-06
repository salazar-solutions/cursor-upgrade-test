package com.example.app.user.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Test application class for integration tests.
 */
@SpringBootApplication(scanBasePackages = "com.example.app")
@EnableJpaRepositories(basePackages = "com.example.app")
@EntityScan(basePackages = "com.example.app")
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}

