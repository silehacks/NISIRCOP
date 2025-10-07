package com.nisircop.le.userservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Logging configuration for User Service
 */
@Configuration
public class LoggingConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(LoggingConfig.class);

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                String method = request.getMethod();
                String uri = request.getRequestURI();
                String remoteAddr = request.getRemoteAddr();
                
                logger.info("User Service Request: {} {} from {}", method, uri, remoteAddr);
                return true;
            }
            
            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                      Object handler, Exception ex) {
                String method = request.getMethod();
                String uri = request.getRequestURI();
                int status = response.getStatus();
                
                if (ex != null) {
                    logger.error("User Service Response: {} {} - ERROR {} - {}", method, uri, status, ex.getMessage());
                } else {
                    logger.info("User Service Response: {} {} - {}", method, uri, status);
                }
            }
        });
    }
}