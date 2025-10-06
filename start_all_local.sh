#!/bin/bash
set -e

# Kill any previous processes
echo "Killing any lingering processes..."
pkill -f java || true
pkill -f uvicorn || true
pkill -f vite || true

# Create data directory if it doesn't exist
mkdir -p data

# Start backend services from the root directory
echo "Starting Discovery Server..."
mvn spring-boot:run -f discovery-server/pom.xml > /tmp/discovery.log 2>&1 &
sleep 30

echo "Starting API Gateway..."
mvn spring-boot:run -f api-gateway/pom.xml -Dspring-boot.run.profiles=dev > /tmp/api-gateway.log 2>&1 &
sleep 15

echo "Starting Auth Service..."
mvn spring-boot:run -f auth-service/pom.xml -Dspring-boot.run.profiles=dev > /tmp/auth-service.log 2>&1 &
sleep 15

echo "Starting User Service..."
mvn spring-boot:run -f user-service/pom.xml -Dspring-boot.run.profiles=dev > /tmp/user-service.log 2>&1 &
sleep 15

echo "Starting Geographic Service..."
mvn spring-boot:run -f geographic-service/pom.xml -Dspring-boot.run.profiles=dev > /tmp/geographic-service.log 2>&1 &
sleep 15

echo "Starting Incident Service..."
mvn spring-boot:run -f incident-service/pom.xml -Dspring-boot.run.profiles=dev > /tmp/incident-service.log 2>&1 &
sleep 15

echo "Starting Analytics Service..."
(cd analytics-service && uvicorn app.main:app --host 0.0.0.0 --port 8086 > /tmp/analytics-service.log 2>&1) &
sleep 10

echo "All backend services started."