package com.nisircop.le.incidentservice.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// This DTO must match the one in the Geographic Service
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointValidationRequest {
    private Long userId;
    private double latitude;
    private double longitude;
    private String userRole; // optional: SUPER_USER, POLICE_STATION, OFFICER
}