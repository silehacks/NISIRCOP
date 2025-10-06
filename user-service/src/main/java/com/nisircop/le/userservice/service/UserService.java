package com.nisircop.le.userservice.service;

import com.nisircop.le.userservice.dto.ValidateRequest;
import com.nisircop.le.userservice.model.User;
import com.nisircop.le.userservice.model.UserProfile;
import com.nisircop.le.userservice.model.UserRole;
import com.nisircop.le.userservice.repository.UserProfileRepository;
import com.nisircop.le.userservice.repository.UserRepository;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User createUser(User user, UserProfile userProfile, Geometry boundary, Long creatorId) {
        if (user.getRole() == UserRole.OFFICER) {
            UserProfile creatorProfile = userProfileRepository.findByUser_Id(creatorId)
                    .orElseThrow(() -> new RuntimeException("Creator's profile not found, cannot assign boundary."));
            boundary = creatorProfile.getBoundary();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedBy(creatorId);
        User savedUser = userRepository.save(user);

        userProfile.setUser(savedUser);
        userProfile.setBoundary(boundary);
        userProfileRepository.save(userProfile);

        savedUser.setUserProfile(userProfile);
        return savedUser;
    }

    public Optional<User> validateUser(ValidateRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()));
    }

    public List<Long> getOfficerIdsByStation(Long stationId) {
        return userRepository.findByCreatedBy(stationId)
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }
}