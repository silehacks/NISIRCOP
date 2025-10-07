#!/bin/bash
# Run Discovery Server (Eureka)
# This must be started first before other services

echo "Starting Discovery Server on port 8761..."
cd "$(dirname "$0")/../discovery-server"

# Set environment variables
export SPRING_PROFILES_ACTIVE=local
export SERVER_PORT=8761

# Build and run
mvn spring-boot:run