package com.nisircop.le.geographicservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GeographicServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeographicServiceApplication.class, args);
    }

}