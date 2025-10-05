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

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    public Optional<Incident> getIncidentById(Long id) {
        return incidentRepository.findById(id);
    }

    public Incident createIncident(Incident incident, Long reporterId) {
        // 1. Validate the location using the Geographic Service
        // fetch reporter role from user-service
        String reporterRole = "";
        try {
            var user = userServiceClient.getUserById(reporterId);
            reporterRole = user != null ? user.getRole() : "";
        } catch (Exception ignored) {
            // If the user-service call fails, proceed with empty role which will be treated normally by geo service
        }

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

        // 2. If valid, set additional details and save the incident
        incident.setReportedBy(reporterId);
        incident.setOccurredAt(LocalDateTime.now()); // Or use a timestamp from the request
        return incidentRepository.save(incident);
    }
}