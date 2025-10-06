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

        user.setUserProfile(userProfile);
        userProfile.setUser(user);

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

    @Transactional
    public UserResponseDto updateUser(Long id, UserCreateRequest request, Long updaterId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        validateUserUpdatePermission(updaterId, user);
        
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        if (user.getUserProfile() != null) {
            user.getUserProfile().setFirstName(request.getFirstName());
            user.getUserProfile().setLastName(request.getLastName());
            user.getUserProfile().setPhone(request.getPhone());
            user.getUserProfile().setBadgeNumber(request.getBadgeNumber());
        }
        
        User savedUser = userRepository.save(user);
        return mapToUserResponseDto(savedUser);
    }

    @Transactional
    public void deleteUser(Long id, Long deleterId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        validateUserDeletePermission(deleterId, user);
        
        userRepository.deleteById(id);
    }

    private void validateUserUpdatePermission(Long updaterId, User targetUser) {
        User updater = userRepository.findById(updaterId)
                .orElseThrow(() -> new ResourceNotFoundException("Updater not found with id: " + updaterId));
        
        String updaterRole = updater.getRole().name();
        
        if ("SUPER_USER".equals(updaterRole)) {
            return;
        }
        
        if ("POLICE_STATION".equals(updaterRole) && 
            targetUser.getCreatedBy() != null && 
            targetUser.getCreatedBy().equals(updaterId)) {
            return;
        }
        
        if (updaterId.equals(targetUser.getId())) {
            return;
        }
        
        throw new RuntimeException("User does not have permission to update this user.");
    }

    private void validateUserDeletePermission(Long deleterId, User targetUser) {
        User deleter = userRepository.findById(deleterId)
                .orElseThrow(() -> new ResourceNotFoundException("Deleter not found with id: " + deleterId));
        
        String deleterRole = deleter.getRole().name();
        
        if ("SUPER_USER".equals(deleterRole)) {
            return;
        }
        
        if ("POLICE_STATION".equals(deleterRole) && 
            targetUser.getCreatedBy() != null && 
            targetUser.getCreatedBy().equals(deleterId)) {
            return;
        }
        
        throw new RuntimeException("User does not have permission to delete this user.");
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
}