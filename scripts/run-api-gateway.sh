#!/bin/bash
# Run API Gateway
# Ensure Discovery Server is running first

echo "Starting API Gateway on port 8080..."
cd "$(dirname "$0")/../api-gateway"

# Set environment variables
export SPRING_PROFILES_ACTIVE=local
export SERVER_PORT=8080
export JWT_SECRET=${JWT_SECRET:-development-jwt-secret-key-please-change-for-production}

# Build and run
mvn spring-boot:run