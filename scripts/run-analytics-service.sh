#!/bin/bash
# Run Analytics Service (Python/FastAPI)
# Ensure Discovery Server is running first

echo "Starting Analytics Service on port 8086..."
cd "$(dirname "$0")/../analytics-service"

# Set environment variables
export DATABASE_URL=sqlite:///./data/nisircop.db
export EUREKA_SERVER_URL=http://localhost:8761/eureka
export EUREKA_APP_NAME=ANALYTICS-SERVICE

# Add local bin to PATH for uvicorn
export PATH=$PATH:$HOME/.local/bin

# Run with uvicorn
python3 -m uvicorn app.main:app --host 0.0.0.0 --port 8086 --reload