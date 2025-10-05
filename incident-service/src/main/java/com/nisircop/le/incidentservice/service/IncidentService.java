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
        var user = userServiceClient.getUserById(reporterId);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + reporterId);
        }
        String reporterRole = user.getRole();

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

    public Optional<Incident> updateIncident(Long id, Incident incidentDetails) {
        return incidentRepository.findById(id).map(incident -> {
            incident.setTitle(incidentDetails.getTitle());
            incident.setDescription(incidentDetails.getDescription());
            incident.setIncidentType(incidentDetails.getIncidentType());
            incident.setPriority(incidentDetails.getPriority());
            incident.setLatitude(incidentDetails.getLatitude());
            incident.setLongitude(incidentDetails.getLongitude());
            return incidentRepository.save(incident);
        });
    }

    public void deleteIncident(Long id) {
        incidentRepository.deleteById(id);
    }
}