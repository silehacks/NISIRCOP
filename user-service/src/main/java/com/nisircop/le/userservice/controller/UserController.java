package com.nisircop.le.userservice.controller;

import com.nisircop.le.userservice.dto.UserCreateRequest;
import com.nisircop.le.userservice.dto.UserResponseDto;
import com.nisircop.le.userservice.dto.ValidateRequest;
import com.nisircop.le.userservice.exception.ResourceNotFoundException;
import com.nisircop.le.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @PostMapping("/validate")
    public ResponseEntity<UserResponseDto> validateUser(@Valid @RequestBody ValidateRequest request) {
        return userService.validateUser(request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateRequest request,
                                                    @RequestHeader("X-User-Id") Long creatorId) {
        UserResponseDto createdUser = userService.createUser(request, creatorId);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/station/{stationId}/officers")
    public List<Long> getOfficerIdsByStation(@PathVariable Long stationId) {
        return userService.getOfficerIdsByStation(stationId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, 
                                                    @Valid @RequestBody UserCreateRequest request,
                                                    @RequestHeader("X-User-Id") Long updaterId) {
        UserResponseDto updatedUser = userService.updateUser(id, request, updaterId);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, 
                                         @RequestHeader("X-User-Id") Long deleterId) {
        userService.deleteUser(id, deleterId);
        return ResponseEntity.noContent().build();
    }
}