package com.nisircop.le.incidentservice.controller;

import com.nisircop.le.incidentservice.dto.IncidentCreateRequest;
import com.nisircop.le.incidentservice.model.Incident;
import com.nisircop.le.incidentservice.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incidents")
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    @GetMapping
    public List<Incident> getAllIncidents() {
        return incidentService.getAllIncidents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> getIncidentById(@PathVariable Long id) {
        return incidentService.getIncidentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Incident> createIncident(@RequestBody IncidentCreateRequest request, @RequestHeader("X-User-Id") Long reporterId) {
        Incident incident = new Incident();
        incident.setTitle(request.getTitle());
        incident.setDescription(request.getDescription());
        incident.setIncidentType(request.getIncidentType());
        incident.setPriority(request.getPriority());
        incident.setLatitude(request.getLatitude());
        incident.setLongitude(request.getLongitude());

        try {
            Incident createdIncident = incidentService.createIncident(incident, reporterId);
            return ResponseEntity.ok(createdIncident);
        } catch (RuntimeException e) {
            // In a real app, you'd have more specific exception handling
            return ResponseEntity.badRequest().body(null);
        }
    }
}