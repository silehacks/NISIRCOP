#!/bin/bash

# NISIRCOP-LE API Testing Script
# This script tests all service APIs to ensure they work properly with SQLite

echo "🚀 Starting NISIRCOP-LE API Tests..."
echo "======================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Base URLs
DISCOVERY_URL="http://localhost:8761"
API_GATEWAY_URL="http://localhost:8080"
AUTH_URL="http://localhost:8081"
USER_URL="http://localhost:8085"
GEO_URL="http://localhost:8084"
INCIDENT_URL="http://localhost:8083"
ANALYTICS_URL="http://localhost:8086"
FRONTEND_URL="http://localhost:3000"

# Test results
TESTS_PASSED=0
TESTS_FAILED=0

# Function to test API endpoint
test_api() {
    local name="$1"
    local method="$2"
    local url="$3"
    local data="$4"
    local expected_status="$5"
    
    echo -n "Testing $name... "
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "%{http_code}" -o /tmp/response.json "$url")
    elif [ "$method" = "POST" ]; then
        response=$(curl -s -w "%{http_code}" -o /tmp/response.json -X POST -H "Content-Type: application/json" -d "$data" "$url")
    elif [ "$method" = "PUT" ]; then
        response=$(curl -s -w "%{http_code}" -o /tmp/response.json -X PUT -H "Content-Type: application/json" -d "$data" "$url")
    elif [ "$method" = "DELETE" ]; then
        response=$(curl -s -w "%{http_code}" -o /tmp/response.json -X DELETE "$url")
    fi
    
    http_code="${response: -3}"
    
    if [ "$http_code" = "$expected_status" ]; then
        echo -e "${GREEN}✓ PASS${NC} (HTTP $http_code)"
        ((TESTS_PASSED++))
    else
        echo -e "${RED}✗ FAIL${NC} (Expected HTTP $expected_status, got HTTP $http_code)"
        echo "Response: $(cat /tmp/response.json)"
        ((TESTS_FAILED++))
    fi
}

# Function to test API with authentication
test_api_auth() {
    local name="$1"
    local method="$2"
    local url="$3"
    local data="$4"
    local expected_status="$5"
    local token="$6"
    
    echo -n "Testing $name... "
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "%{http_code}" -o /tmp/response.json -H "Authorization: Bearer $token" "$url")
    elif [ "$method" = "POST" ]; then
        response=$(curl -s -w "%{http_code}" -o /tmp/response.json -X POST -H "Content-Type: application/json" -H "Authorization: Bearer $token" -d "$data" "$url")
    elif [ "$method" = "PUT" ]; then
        response=$(curl -s -w "%{http_code}" -o /tmp/response.json -X PUT -H "Content-Type: application/json" -H "Authorization: Bearer $token" -d "$data" "$url")
    elif [ "$method" = "DELETE" ]; then
        response=$(curl -s -w "%{http_code}" -o /tmp/response.json -X DELETE -H "Authorization: Bearer $token" "$url")
    fi
    
    http_code="${response: -3}"
    
    if [ "$http_code" = "$expected_status" ]; then
        echo -e "${GREEN}✓ PASS${NC} (HTTP $http_code)"
        ((TESTS_PASSED++))
    else
        echo -e "${RED}✗ FAIL${NC} (Expected HTTP $expected_status, got HTTP $http_code)"
        echo "Response: $(cat /tmp/response.json)"
        ((TESTS_FAILED++))
    fi
}

echo ""
echo "1. Testing Service Discovery..."
echo "=============================="
test_api "Discovery Server Health" "GET" "$DISCOVERY_URL/actuator/health" "" "200"

echo ""
echo "2. Testing Authentication Service..."
echo "==================================="
# Test login with default credentials
login_data='{"username":"admin","password":"admin123"}'
login_response=$(curl -s -X POST -H "Content-Type: application/json" -d "$login_data" "$AUTH_URL/auth/login")
echo "Login response: $login_response"

# Extract JWT token
JWT_TOKEN=$(echo "$login_response" | grep -o '"jwt":"[^"]*"' | cut -d'"' -f4)
echo "JWT Token: ${JWT_TOKEN:0:50}..."

if [ -n "$JWT_TOKEN" ]; then
    echo -e "${GREEN}✓ Authentication successful${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}✗ Authentication failed${NC}"
    ((TESTS_FAILED++))
fi

echo ""
echo "3. Testing User Service..."
echo "========================="
test_api_auth "Get Users" "GET" "$USER_URL/users" "" "200" "$JWT_TOKEN"

# Test creating a new user (POLICE_STATION)
user_data='{
    "username": "test_station",
    "password": "test123",
    "email": "station@test.com",
    "role": "POLICE_STATION",
    "firstName": "Test",
    "lastName": "Station",
    "phone": "1234567890",
    "badgeNumber": "ST001"
}'
test_api_auth "Create Police Station User" "POST" "$USER_URL/users" "$user_data" "201" "$JWT_TOKEN"

echo ""
echo "4. Testing Geographic Service..."
echo "==============================="
test_api_auth "Get User Boundary" "GET" "$GEO_URL/geo/boundary/1" "" "200" "$JWT_TOKEN"

# Test location validation
validation_data='{
    "latitude": 9.02497,
    "longitude": 38.74689,
    "userId": 1
}'
test_api_auth "Validate Location" "POST" "$GEO_URL/geo/validate-location" "$validation_data" "200" "$JWT_TOKEN"

echo ""
echo "5. Testing Incident Service..."
echo "============================="
test_api_auth "Get Incidents" "GET" "$INCIDENT_URL/incidents" "" "200" "$JWT_TOKEN"

# Test creating a new incident
incident_data='{
    "title": "Test Incident",
    "description": "This is a test incident for API testing",
    "incidentType": "Theft",
    "priority": "Medium",
    "latitude": 9.02497,
    "longitude": 38.74689
}'
test_api_auth "Create Incident" "POST" "$INCIDENT_URL/incidents" "$incident_data" "201" "$JWT_TOKEN"

echo ""
echo "6. Testing Analytics Service..."
echo "=============================="
test_api "Analytics Health" "GET" "$ANALYTICS_URL/health" "" "200"
test_api "Incidents by Type" "GET" "$ANALYTICS_URL/analytics/incidents/count-by-type" "" "200"
test_api "Incidents by Priority" "GET" "$ANALYTICS_URL/analytics/incidents/count-by-priority" "" "200"
test_api "Incident Locations" "GET" "$ANALYTICS_URL/analytics/incidents/locations" "" "200"

echo ""
echo "7. Testing Frontend..."
echo "====================="
test_api "Frontend Health" "GET" "$FRONTEND_URL" "" "200"

echo ""
echo "8. Testing API Gateway..."
echo "========================"
test_api "API Gateway Health" "GET" "$API_GATEWAY_URL/actuator/health" "" "200"

# Test API Gateway routing
test_api_auth "API Gateway - Get Incidents" "GET" "$API_GATEWAY_URL/incidents" "" "200" "$JWT_TOKEN"
test_api_auth "API Gateway - Get Users" "GET" "$API_GATEWAY_URL/users" "" "200" "$JWT_TOKEN"

echo ""
echo "======================================"
echo "📊 Test Results Summary"
echo "======================================"
echo -e "Tests Passed: ${GREEN}$TESTS_PASSED${NC}"
echo -e "Tests Failed: ${RED}$TESTS_FAILED${NC}"
echo "Total Tests: $((TESTS_PASSED + TESTS_FAILED))"

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "\n${GREEN}🎉 All tests passed!${NC}"
    exit 0
else
    echo -e "\n${RED}❌ Some tests failed. Please check the output above.${NC}"
    exit 1
fi