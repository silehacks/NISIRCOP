package com.nisircop.le.apigateway.exception;

/**
 * Custom exception for API Gateway specific errors
 */
public class GatewayException extends RuntimeException {
    
    private final String errorCode;
    
    public GatewayException(String message) {
        super(message);
        this.errorCode = "GATEWAY_ERROR";
    }
    
    public GatewayException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public GatewayException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "GATEWAY_ERROR";
    }
    
    public GatewayException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}