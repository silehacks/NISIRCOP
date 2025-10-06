package com.nisircop.le.geographicservice.service;

import com.nisircop.le.geographicservice.model.UserProfile;
import com.nisircop.le.geographicservice.repository.UserProfileRepository;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GeoService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    public Optional<UserProfile> getBoundaryByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId);
    }

    public boolean isPointInBoundary(Long userId, Point point) {
        return userProfileRepository.findByUserId(userId)
                .map(profile -> profile.getBoundary() != null && profile.getBoundary().contains(point))
                .orElse(false); // Or handle as an error if the user must have a boundary
    }
}