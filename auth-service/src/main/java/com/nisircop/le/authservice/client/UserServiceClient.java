package com.nisircop.le.authservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "${user-service.url:http://user-service:8085}")
public interface UserServiceClient {

    @PostMapping("/users/validate")
    UserDTO validateUser(@RequestBody ValidateRequest validateRequest);

    @GetMapping("/users/username/{username}")
    UserDTO getUserByUsername(@PathVariable("username") String username);

    // DTOs for communication
    record ValidateRequest(String username, String password) {}
    record UserDTO(Long id, String username, String role, String password) {}
}