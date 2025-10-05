package com.nisircop.le.authservice.client;

import com.nisircop.le.authservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/users/username/{username}")
    UserDTO getUserByUsername(@PathVariable("username") String username);
}