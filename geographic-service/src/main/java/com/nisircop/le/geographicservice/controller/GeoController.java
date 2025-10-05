package com.nisircop.le.geographicservice.controller;

import com.nisircop.le.geographicservice.dto.PointValidationRequest;
import com.nisircop.le.geographicservice.model.UserProfile;
import com.nisircop.le.geographicservice.service.GeoService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/geo")
public class GeoController {

    @Autowired
    private GeoService geoService;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @GetMapping("/boundary/{userId}")
    public ResponseEntity<UserProfile> getBoundaryByUserId(@PathVariable Long userId) {
        return geoService.getBoundaryByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/validate-point")
    public ResponseEntity<Boolean> validatePointInBoundary(@RequestBody PointValidationRequest request) {
        Point point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));

        // Bypass validation for SUPER_USER, as they are not confined to a boundary
        if ("SUPER_USER".equals(request.getUserRole())) {
            return ResponseEntity.ok(true);
        }

        boolean isValid = geoService.isPointInBoundary(request.getUserId(), point);
        return ResponseEntity.ok(isValid);
    }
}