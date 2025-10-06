package com.nisircop.le.incidentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "geographic-service", path = "/geo")
public interface GeoServiceClient {

    @PostMapping("/validate-point")
    ResponseEntity<Boolean> validatePointInBoundary(@RequestBody PointValidationRequest request);
}