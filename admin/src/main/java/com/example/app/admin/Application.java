package com.example.app.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Spring Boot application class.
 */
@SpringBootApplication(scanBasePackages = "com.example.app")
@EnableJpaRepositories(basePackages = "com.example.app")
@EntityScan(basePackages = "com.example.app")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

