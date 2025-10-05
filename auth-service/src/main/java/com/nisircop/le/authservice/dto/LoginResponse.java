package com.nisircop.le.authservice.dto;

public class LoginResponse {
    private String jwt;
    private Long id;
    private String username;
    private String role;

    public LoginResponse(String jwt, Long id, String username, String role) {
        this.jwt = jwt;
        this.id = id;
        this.username = username;
        this.role = role;
    }

    // Getters and Setters
    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}