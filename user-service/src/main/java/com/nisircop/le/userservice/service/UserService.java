package com.nisircop.le.userservice.service;

import com.nisircop.le.userservice.dto.UserCreateRequest;
import com.nisircop.le.userservice.dto.UserResponseDto;
import com.nisircop.le.userservice.dto.ValidateRequest;
import com.nisircop.le.userservice.exception.ResourceNotFoundException;
import com.nisircop.le.userservice.model.User;
import com.nisircop.le.userservice.model.UserProfile;
import com.nisircop.le.userservice.model.UserRole;
import com.nisircop.le.userservice.repository.UserProfileRepository;
import com.nisircop.le.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final WKTReader wktReader = new WKTReader();

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<UserResponseDto> getUserById(Long id) {
        return userRepository.findById(id).map(this::mapToUserResponseDto);
    }

    public Optional<UserResponseDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(this::mapToUserResponseDto);
    }

    @Transactional
    public UserResponseDto createUser(UserCreateRequest request, Long creatorId) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        user.setCreatedBy(creatorId);
        user.setActive(true);

        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName(request.getFirstName());
        userProfile.setLastName(request.getLastName());
        userProfile.setPhone(request.getPhone());
        userProfile.setBadgeNumber(request.getBadgeNumber());

        Geometry boundary = parseBoundary(request.getBoundary());

        // If an OFFICER is created, inherit the boundary from the creator (POLICE_STATION)
        if (user.getRole() == UserRole.OFFICER) {
            UserProfile creatorProfile = userProfileRepository.findByUser_Id(creatorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Creator's profile not found, cannot assign boundary."));
            boundary = creatorProfile.getBoundary();
        }
        userProfile.setBoundary(boundary);

        // Set the bidirectional relationship
        user.setUserProfile(userProfile);

        User savedUser = userRepository.save(user);
        return mapToUserResponseDto(savedUser);
    }

    public Optional<UserResponseDto> validateUser(ValidateRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .map(this::mapToUserResponseDto);
    }

    public List<Long> getOfficerIdsByStation(Long stationId) {
        return userRepository.findByCreatedBy(stationId)
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    private UserResponseDto mapToUserResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setCreatedBy(user.getCreatedBy());
        dto.setActive(user.isActive());

        if (user.getUserProfile() != null) {
            dto.setFirstName(user.getUserProfile().getFirstName());
            dto.setLastName(user.getUserProfile().getLastName());
            dto.setPhone(user.getUserProfile().getPhone());
            dto.setBadgeNumber(user.getUserProfile().getBadgeNumber());
        }
        return dto;
    }

    private Geometry parseBoundary(String boundaryWkt) {
        if (boundaryWkt == null || boundaryWkt.isEmpty()) {
            return null;
        }
        try {
            return wktReader.read(boundaryWkt);
        } catch (ParseException e) {
            // This should be handled by a more specific exception
            throw new IllegalArgumentException("Invalid boundary WKT format", e);
        }
    }
}