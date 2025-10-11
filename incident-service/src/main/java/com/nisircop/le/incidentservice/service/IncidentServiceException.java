package com.nisircop.le.incidentservice.service;

public class IncidentServiceException extends RuntimeException {

    private final String errorCode;

    public IncidentServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}