#!/bin/bash
# Run Incident Service
# Ensure Discovery Server is running first

echo "Starting Incident Service on port 8083..."
cd "$(dirname "$0")/../incident-service"

# Set environment variables
export SPRING_PROFILES_ACTIVE=local
export SERVER_PORT=8083

# Build and run
mvn spring-boot:run