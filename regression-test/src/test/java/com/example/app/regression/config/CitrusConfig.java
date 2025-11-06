package com.example.app.regression.config;

import org.citrusframework.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Citrus framework configuration for HTTP client and messaging.
 */
@Configuration
public class CitrusConfig {

    @Value("${api.base.url:http://localhost:8080}")
    private String baseUrl;

    @Bean
    public HttpClient httpClient() {
        HttpClient client = new HttpClient();
        client.getEndpointConfiguration().setRequestUrl(baseUrl);
        return client;
    }
}

