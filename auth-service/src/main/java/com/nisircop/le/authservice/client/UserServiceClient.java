package com.nisircop.le.authservice.client;

import com.nisircop.le.authservice.dto.UserResponseDto;
import com.nisircop.le.authservice.dto.ValidateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service") // Removed hardcoded URL to use Eureka
public interface UserServiceClient {

    @PostMapping("/users/validate")
    ResponseEntity<UserResponseDto> validateUser(@RequestBody ValidateRequest validateRequest);

    @GetMapping("/users/username/{username}")
    ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable("username") String username);
}