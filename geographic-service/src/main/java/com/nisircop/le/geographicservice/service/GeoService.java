package com.nisircop.le.geographicservice.service;

import com.nisircop.le.geographicservice.model.UserProfile;
import com.nisircop.le.geographicservice.repository.UserProfileRepository;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeoService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * Checks if a given point is within the assigned boundary of a user.
     *
     * @param userId the ID of the user whose boundary will be checked.
     * @param point  the geographic point to check.
     * @return true if the point is within the user's boundary, false otherwise.
     */
    public boolean isPointInUserBoundary(Long userId, Point point) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found for user ID: " + userId));

        Polygon boundary = userProfile.getBoundary();
        if (boundary == null) {
            // If the user has no boundary defined, we might consider this as a failure.
            // Or, depending on business rules, it might be allowed.
            // For strict validation, we'll return false.
            return false;
        }

        // The 'within' method checks if the point is spatially within the polygon.
        return point.within(boundary);
    }
}