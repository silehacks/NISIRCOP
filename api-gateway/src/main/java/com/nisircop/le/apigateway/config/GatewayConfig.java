package com.nisircop.le.apigateway.config;

import com.nisircop.le.apigateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private AuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**")
                        .uri("lb://auth-service"))
                .route("user-service", r -> r.path("/users/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://user-service"))
                .route("geographic-service", r -> r.path("/geo/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://geographic-service"))
                .route("incident-service", r -> r.path("/incidents/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://incident-service"))
                .route("analytics-service", r -> r.path("/analytics/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://analytics-service"))
                .build();
    }
}