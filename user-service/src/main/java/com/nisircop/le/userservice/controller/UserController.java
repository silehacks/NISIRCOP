package com.nisircop.le.userservice.controller;

import com.nisircop.le.userservice.dto.UserCreateRequest;
import com.nisircop.le.userservice.dto.ValidateRequest;
import com.nisircop.le.userservice.model.User;
import com.nisircop.le.userservice.model.UserProfile;
import com.nisircop.le.userservice.model.UserRole;
import com.nisircop.le.userservice.service.UserService;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    private final WKTReader wktReader = new WKTReader();

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/validate")
    public ResponseEntity<User> validateUser(@RequestBody ValidateRequest request) {
        return userService.validateUser(request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserCreateRequest request, @RequestHeader("X-User-Id") Long creatorId) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));

        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName(request.getFirstName());
        userProfile.setLastName(request.getLastName());
        userProfile.setPhone(request.getPhone());
        userProfile.setBadgeNumber(request.getBadgeNumber());

        Geometry boundary = null;
        if (request.getBoundary() != null && !request.getBoundary().isEmpty()) {
            try {
                boundary = wktReader.read(request.getBoundary());
            } catch (ParseException e) {
                return ResponseEntity.badRequest().body(null); // Or handle more gracefully
            }
        }

        User createdUser = userService.createUser(user, userProfile, boundary, creatorId);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/station/{stationId}/officers")
    public List<Long> getOfficerIdsByStation(@PathVariable Long stationId) {
        return userService.getOfficerIdsByStation(stationId);
    }
}