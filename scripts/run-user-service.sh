#!/bin/bash
# Run User Service
# Ensure Discovery Server is running first

echo "Starting User Service on port 8085..."
cd "$(dirname "$0")/../user-service"

# Set environment variables
export SPRING_PROFILES_ACTIVE=local
export SERVER_PORT=8085

# Build and run
mvn spring-boot:run