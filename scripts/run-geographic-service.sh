#!/bin/bash
# Run Geographic Service
# Ensure Discovery Server is running first

echo "Starting Geographic Service on port 8084..."
cd "$(dirname "$0")/../geographic-service"

# Set environment variables
export SPRING_PROFILES_ACTIVE=local
export SERVER_PORT=8084

# Build and run
mvn spring-boot:run