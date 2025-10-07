package com.nisircop.le.incidentservice.service;

import com.nisircop.le.incidentservice.client.GeoServiceClient;
import com.nisircop.le.incidentservice.client.PointValidationRequest;
import com.nisircop.le.incidentservice.client.UserServiceClient;
import com.nisircop.le.incidentservice.dto.IncidentCreateRequest;
import com.nisircop.le.incidentservice.exception.IncidentServiceException;
import com.nisircop.le.incidentservice.model.Incident;
import com.nisircop.le.incidentservice.repository.IncidentRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final GeoServiceClient geoServiceClient;
    private final UserServiceClient userServiceClient;

    public IncidentService(IncidentRepository incidentRepository,
                          GeoServiceClient geoServiceClient,
                          UserServiceClient userServiceClient) {
        this.incidentRepository = incidentRepository;
        this.geoServiceClient = geoServiceClient;
        this.userServiceClient = userServiceClient;
    }

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Transactional(readOnly = true)
    public List<Incident> getAllIncidents(Long userId, String userRole) {
        return switch (userRole) {
            case "SUPER_USER" -> incidentRepository.findAll();
            case "POLICE_STATION" -> {
                List<Long> userIds = new ArrayList<>(userServiceClient.getOfficerIdsByStation(userId));
                userIds.add(userId);
                yield incidentRepository.findByReportedByIn(userIds);
            }
            case "OFFICER" -> incidentRepository.findByReportedBy(userId);
            default -> Collections.emptyList();
        };
    }

    @Transactional(readOnly = true)
    public Optional<Incident> getIncidentById(Long id) {
        return incidentRepository.findById(id);
    }

    @Transactional
    public Incident createIncident(IncidentCreateRequest request, Long reporterId, String reporterRole) {
        Point point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
        validatePointInBoundary(reporterId, reporterRole, point);

        Incident incident = new Incident();
        incident.setTitle(request.getTitle());
        incident.setDescription(request.getDescription());
        incident.setIncidentType(request.getIncidentType());
        incident.setPriority(request.getPriority());
        incident.setLocation(point);
        incident.setReportedBy(reporterId);
        // occurredAt is set by @CreationTimestamp

        return incidentRepository.save(incident);
    }

    @Transactional
    public Optional<Incident> updateIncident(Long id, IncidentCreateRequest request, Long userId) {
        return incidentRepository.findById(id).map(incident -> {
            validateUserPermission(userId, incident, "update");

            Point point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
            // Assuming role is not needed for update, as only owner or super_user can update.
            // If boundary validation is needed on update, the role would be required.
            // validatePointInBoundary(incident.getReportedBy(), "UNKNOWN", point);

            incident.setTitle(request.getTitle());
            incident.setDescription(request.getDescription());
            incident.setIncidentType(request.getIncidentType());
            incident.setPriority(request.getPriority());
            incident.setLocation(point);

            return incidentRepository.save(incident);
        });
    }

    @Transactional
    public void deleteIncident(Long id, Long userId) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentServiceException("Incident not found with id: " + id, "INCIDENT_NOT_FOUND"));
        validateUserPermission(userId, incident, "delete");
        incidentRepository.delete(incident);
    }

    private void validatePointInBoundary(Long userId, String userRole, Point point) {
        PointValidationRequest validationRequest = new PointValidationRequest(
                userId,
                point.getY(), // latitude
                point.getX(), // longitude
                userRole
        );
        ResponseEntity<Boolean> validationResponse = geoServiceClient.validatePointInBoundary(validationRequest);

        if (validationResponse.getBody() == null || !validationResponse.getBody()) {
            throw new IncidentServiceException("Incident location is outside the user's assigned boundary.", "LOCATION_OUT_OF_BOUNDS");
        }
    }

    private void validateUserPermission(Long userId, Incident incident, String action) {
        UserServiceClient.UserDTO user = Optional.ofNullable(userServiceClient.getUserById(userId))
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        String userRole = user.role();

        if ("SUPER_USER".equals(userRole)) return;
        if (incident.getReportedBy().equals(userId)) return;

        if ("POLICE_STATION".equals(userRole)) {
            List<Long> officerIds = userServiceClient.getOfficerIdsByStation(userId);
            if (officerIds.contains(incident.getReportedBy())) return;
        }

        throw new IncidentServiceException("User does not have permission to " + action + " this incident.", "INSUFFICIENT_PERMISSIONS");
    }
}