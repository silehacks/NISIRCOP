package com.nisircop.le.authservice.controller;

import com.nisircop.le.authservice.client.UserServiceClient;
import com.nisircop.le.authservice.dto.LoginRequest;
import com.nisircop.le.authservice.dto.LoginResponse;
import com.nisircop.le.authservice.dto.UserDTO;
import com.nisircop.le.authservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserServiceClient userServiceClient;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        UserDTO user = userServiceClient.getUserByUsername(loginRequest.getUsername());
        String jwt = jwtUtil.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(jwt, user.getId(), user.getUsername(), user.getRole()));
    }
}