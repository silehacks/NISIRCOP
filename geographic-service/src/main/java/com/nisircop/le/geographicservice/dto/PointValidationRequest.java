package com.nisircop.le.geographicservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointValidationRequest {
    private Long userId;
    private double latitude;
    private double longitude;
    private String userRole;
}