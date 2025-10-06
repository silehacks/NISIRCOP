package com.nisircop.le.incidentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentCreateRequest {
    private String title;
    private String description;
    private String incidentType;
    private String priority;
    private double latitude;
    private double longitude;
}