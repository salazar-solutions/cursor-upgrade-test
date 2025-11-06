package com.example.app.payment.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Test application class for integration tests.
 * Scans payment and required packages.
 */
@SpringBootApplication
@ComponentScan(
    basePackages = {"com.example.app.payment"},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.example\\.app\\.order\\.service\\..*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.example\\.app\\.user\\.service\\..*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.example\\.app\\.user\\.controller\\..*")
    }
)
@EnableJpaRepositories(basePackages = {"com.example.app.payment", "com.example.app.user", "com.example.app.order"})
@EntityScan(basePackages = {"com.example.app.payment", "com.example.app.user", "com.example.app.order"})
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}

