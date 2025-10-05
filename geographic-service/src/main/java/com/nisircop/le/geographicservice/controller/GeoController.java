package com.nisircop.le.geographicservice.controller;

import com.nisircop.le.geographicservice.dto.PointValidationRequest;
import com.nisircop.le.geographicservice.service.GeoService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/geo")
public class GeoController {

    @Autowired
    private GeoService geoService;

    // GeometryFactory is thread-safe and can be reused.
    // SRID 4326 is for WGS 84, the standard for GPS.
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @PostMapping("/validate/point-in-boundary")
    public ResponseEntity<Boolean> validatePointInBoundary(@RequestBody PointValidationRequest request) {
        if (request.getUserId() == null) {
            return ResponseEntity.badRequest().body(false);
        }

        try {
            Point point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
            boolean isValid = geoService.isPointInUserBoundary(request.getUserId(), point);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            // Log the exception
            return ResponseEntity.status(500).body(false);
        }
    }
}