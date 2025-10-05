package com.nisircop.le.incidentservice.client;

// This DTO must match the one in the Geographic Service
public class PointValidationRequest {
    private Long userId;
    private double latitude;
    private double longitude;
    private String role; // optional: SUPER_USER, POLICE_STATION, OFFICER

    public PointValidationRequest(Long userId, double latitude, double longitude) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PointValidationRequest(Long userId, double latitude, double longitude, String role) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.role = role;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}