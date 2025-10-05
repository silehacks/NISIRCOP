package com.nisircop.le.userservice.service;

import com.nisircop.le.userservice.model.User;
import com.nisircop.le.userservice.model.UserProfile;
import com.nisircop.le.userservice.model.UserRole;
import com.nisircop.le.userservice.repository.UserProfileRepository;
import com.nisircop.le.userservice.repository.UserRepository;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final WKTReader wktReader = new WKTReader();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User createUser(User user, UserProfile userProfile, String boundaryWkt, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        validateUserCreation(creator, user.getRole());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setParent(creator);
        User savedUser = userRepository.save(user);

        if (boundaryWkt != null && !boundaryWkt.isEmpty()) {
            try {
                Polygon boundary = (Polygon) wktReader.read(boundaryWkt);
                userProfile.setBoundary(boundary);
            } catch (ParseException e) {
                throw new RuntimeException("Invalid WKT format for boundary", e);
            }
        }

        userProfile.setUser(savedUser);
        userProfileRepository.save(userProfile);

        return savedUser;
    }

    private void validateUserCreation(User creator, UserRole roleToCreate) {
        UserRole creatorRole = creator.getRole();
        boolean isAllowed = switch (creatorRole) {
            case SUPER_USER -> roleToCreate == UserRole.POLICE_STATION;
            case POLICE_STATION -> roleToCreate == UserRole.OFFICER;
            default -> false;
        };
        if (!isAllowed) {
            throw new RuntimeException("User with role " + creatorRole + " cannot create user with role " + roleToCreate);
        }
    }

    // This bean needs to be configured in a SecurityConfig class
    // For now, we assume it's available.
}