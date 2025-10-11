package com.nisircop.le.incidentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "user-service", path = "/users")
public interface UserServiceClient {

    @GetMapping("/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);

    @GetMapping("/station/{stationId}/officers")
    List<Long> getOfficerIdsByStation(@PathVariable("stationId") Long stationId);
}