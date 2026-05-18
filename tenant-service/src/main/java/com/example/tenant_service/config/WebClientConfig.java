package com.example.tenant_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced // Esto es vital para que busque el "auth-service" en Eureka
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}