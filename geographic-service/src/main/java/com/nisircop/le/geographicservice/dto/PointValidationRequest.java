package com.nisircop.le.geographicservice.dto;

public class PointValidationRequest {
    private Long userId;
    private double latitude;
    private double longitude;
    private String userRole;

    public PointValidationRequest(Long userId, double latitude, double longitude, String userRole) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userRole = userRole;
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

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}