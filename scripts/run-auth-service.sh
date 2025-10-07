#!/bin/bash
# Run Authentication Service
# Ensure Discovery Server is running first

echo "Starting Auth Service on port 8081..."
cd "$(dirname "$0")/../auth-service"

# Set environment variables
export SPRING_PROFILES_ACTIVE=local
export SERVER_PORT=8081
export JWT_SECRET=${JWT_SECRET:-development-jwt-secret-key-please-change-for-production}

# Build and run
mvn spring-boot:run