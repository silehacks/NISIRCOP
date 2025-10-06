package com.nisircop.le.incidentservice.controller;

import com.nisircop.le.incidentservice.dto.IncidentCreateRequest;
import com.nisircop.le.incidentservice.model.Incident;
import com.nisircop.le.incidentservice.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    @GetMapping
    public List<Incident> getAllIncidents(@RequestHeader("X-User-Id") Long userId, @RequestHeader("X-User-Role") String userRole) {
        return incidentService.getAllIncidents(userId, userRole);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> getIncidentById(@PathVariable Long id) {
        return incidentService.getIncidentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Incident> createIncident(@RequestBody IncidentCreateRequest request,
                                                   @RequestHeader("X-User-Id") Long reporterId,
                                                   @RequestHeader("X-User-Role") String reporterRole) {
        try {
            Incident createdIncident = incidentService.createIncident(request, reporterId, reporterRole);
            return ResponseEntity.ok(createdIncident);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Incident> updateIncident(@PathVariable Long id,
                                                   @RequestBody IncidentCreateRequest request,
                                                   @RequestHeader("X-User-Id") Long userId) {
        return incidentService.updateIncident(id, request, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        try {
            incidentService.deleteIncident(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}