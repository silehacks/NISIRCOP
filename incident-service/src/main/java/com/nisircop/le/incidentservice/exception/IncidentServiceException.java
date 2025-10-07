package com.nisircop.le.incidentservice.exception;

/**
 * Custom exception for Incident Service specific errors
 */
public class IncidentServiceException extends RuntimeException {
    
    private final String errorCode;
    
    public IncidentServiceException(String message) {
        super(message);
        this.errorCode = "INCIDENT_SERVICE_ERROR";
    }
    
    public IncidentServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public IncidentServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "INCIDENT_SERVICE_ERROR";
    }
    
    public IncidentServiceException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}