package com.nisircop.le.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

/**
 * Logging configuration for API Gateway
 */
@Configuration
public class LoggingConfig {

    private static final Logger logger = LoggerFactory.getLogger(LoggingConfig.class);

    @Bean
    @Order(-1)
    public GlobalFilter loggingFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();
            String method = exchange.getRequest().getMethod().toString();
            String remoteAddress = exchange.getRequest().getRemoteAddress() != null ? 
                exchange.getRequest().getRemoteAddress().toString() : "unknown";
            
            logger.info("API Gateway Request: {} {} from {}", method, path, remoteAddress);
            
            return chain.filter(exchange)
                .doOnSuccess(aVoid -> logger.info("API Gateway Response: {} {} - SUCCESS", method, path))
                .doOnError(throwable -> logger.error("API Gateway Response: {} {} - ERROR: {}", 
                    method, path, throwable.getMessage()));
        };
    }
}