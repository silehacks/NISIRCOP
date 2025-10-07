#!/bin/bash
# Run Frontend (Vue.js)
# Ensure API Gateway is running on port 8080

echo "Starting Frontend Development Server on port 3000..."
cd "$(dirname "$0")/../frontend"

# Set environment variable for API URL
export VITE_API_URL=http://localhost:8080

# Run development server
npm run dev