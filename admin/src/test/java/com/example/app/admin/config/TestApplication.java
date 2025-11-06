package com.example.app.admin.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/**
 * Test application class for integration tests.
 * Excludes UserController and User-related services to avoid dependency on UserMapper during tests.
 */
@SpringBootApplication
@ComponentScan(
        basePackages = "com.example.app"
)
@EnableJpaRepositories(basePackages = "com.example.app")
@EntityScan(basePackages = "com.example.app")
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
