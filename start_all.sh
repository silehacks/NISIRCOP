#!/bin/bash
set -e

# Kill any previous processes
pkill -f java || true
pkill -f uvicorn || true
pkill -f vite || true
sudo docker compose down --remove-orphans

# Start the database
sudo docker compose up -d postgres
echo "Waiting for PostgreSQL to start..."
sleep 20

# Start backend services in the background
echo "Starting Discovery Server..."
(cd /app/discovery-server && mvn spring-boot:run > /tmp/discovery.log 2>&1) &
sleep 30 # Discovery server needs more time

echo "Starting API Gateway..."
(cd /app/api-gateway && mvn spring-boot:run > /tmp/api-gateway.log 2>&1) &
sleep 15

echo "Starting Auth Service..."
(cd /app/auth-service && mvn spring-boot:run > /tmp/auth-service.log 2>&1) &
sleep 15

echo "Starting User Service..."
(cd /app/user-service && mvn spring-boot:run > /tmp/user-service.log 2>&1) &
sleep 15

echo "Starting Geographic Service..."
(cd /app/geographic-service && mvn spring-boot:run > /tmp/geographic-service.log 2>&1) &
sleep 15

echo "Starting Incident Service..."
(cd /app/incident-service && mvn spring-boot:run > /tmp/incident-service.log 2>&1) &
sleep 15

echo "Starting Analytics Service..."
(cd /app/analytics-service && uvicorn app.main:app --host 0.0.0.0 --port 8086 > /tmp/analytics-service.log 2>&1) &
sleep 10

echo "Starting Frontend..."
(cd /app/frontend && npm run dev > /tmp/frontend.log 2>&1) &
sleep 45

echo "All services started. Running verification script..."
python jules-scratch/verification/verify_all_features.py