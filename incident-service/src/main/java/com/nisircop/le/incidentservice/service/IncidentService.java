package com.nisircop.le.incidentservice.service;

import com.nisircop.le.incidentservice.client.GeoServiceClient;
import com.nisircop.le.incidentservice.client.PointValidationRequest;
import com.nisircop.le.incidentservice.client.UserServiceClient;
import com.nisircop.le.incidentservice.model.Incident;
import com.nisircop.le.incidentservice.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private GeoServiceClient geoServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    public List<Incident> getAllIncidents(Long userId, String userRole) {
        switch (userRole) {
            case "SUPER_USER":
                return incidentRepository.findAll();
            case "POLICE_STATION":
                List<Long> userIds = new ArrayList<>(userServiceClient.getOfficerIdsByStation(userId));
                userIds.add(userId);
                return incidentRepository.findByReportedByIn(userIds);
            case "OFFICER":
                return incidentRepository.findByReportedBy(userId);
            default:
                return Collections.emptyList();
        }
    }

    public Optional<Incident> getIncidentById(Long id) {
        return incidentRepository.findById(id);
    }

    public Incident createIncident(Incident incident, Long reporterId) {
        var user = userServiceClient.getUserById(reporterId);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + reporterId);
        }
        String reporterRole = user.role();

        PointValidationRequest validationRequest = new PointValidationRequest(
                reporterId,
                incident.getLatitude(),
                incident.getLongitude(),
                reporterRole
        );
        ResponseEntity<Boolean> validationResponse = geoServiceClient.validatePointInBoundary(validationRequest);

        if (!validationResponse.getStatusCode().is2xxSuccessful() || !Boolean.TRUE.equals(validationResponse.getBody())) {
            throw new RuntimeException("Incident location is outside the user's assigned boundary.");
        }

        incident.setReportedBy(reporterId);
        incident.setOccurredAt(LocalDateTime.now());
        return incidentRepository.save(incident);
    }

    public Optional<Incident> updateIncident(Long id, Incident incidentDetails, Long userId) {
        return incidentRepository.findById(id).map(incident -> {
            validateUserPermission(userId, incident, "update");
            incident.setTitle(incidentDetails.getTitle());
            incident.setDescription(incidentDetails.getDescription());
            incident.setIncidentType(incidentDetails.getIncidentType());
            incident.setPriority(incidentDetails.getPriority());
            incident.setStatus(incidentDetails.getStatus());
            incident.setLatitude(incidentDetails.getLatitude());
            incident.setLongitude(incidentDetails.getLongitude());
            return incidentRepository.save(incident);
        });
    }

    public void deleteIncident(Long id, Long userId) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with id: " + id));
        validateUserPermission(userId, incident, "delete");
        incidentRepository.deleteById(id);
    }

    private void validateUserPermission(Long userId, Incident incident, String action) {
        var user = userServiceClient.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        String userRole = user.role();

        boolean isSuperUser = "SUPER_USER".equals(userRole);
        boolean isOwner = incident.getReportedBy().equals(userId);
        boolean isStationCommander = "POLICE_STATION".equals(userRole);

        if (isSuperUser) {
            return; // Super user can do anything
        }

        if (isOwner) {
            return; // Owner can always modify their own incidents
        }

        if (isStationCommander) {
            // Check if the incident reporter is one of the station's officers
            List<Long> officerIds = userServiceClient.getOfficerIdsByStation(userId);
            if (officerIds.contains(incident.getReportedBy())) {
                return; // Station commander can modify their officers' incidents
            }
        }

        throw new RuntimeException("User does not have permission to " + action + " this incident.");
    }
}