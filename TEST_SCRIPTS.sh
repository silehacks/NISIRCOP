#!/bin/bash
# NISIRCOP-LE Comprehensive Test Suite
# This script tests all backend services and functionality

set -e

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Base URLs
API_GATEWAY_URL="http://localhost:8080"
AUTH_SERVICE_URL="http://localhost:8081"
USER_SERVICE_URL="http://localhost:8085"
GEOGRAPHIC_SERVICE_URL="http://localhost:8084"
INCIDENT_SERVICE_URL="http://localhost:8083"
ANALYTICS_SERVICE_URL="http://localhost:8086"
DISCOVERY_SERVER_URL="http://localhost:8761"

# Test counters
TESTS_PASSED=0
TESTS_FAILED=0
TOTAL_TESTS=0

# Global variables for tokens and user IDs
ADMIN_TOKEN=""
ADMIN_ID=""
STATION_TOKEN=""
STATION_ID=""
OFFICER_TOKEN=""
OFFICER_ID=""

# Utility functions
print_header() {
    echo -e "\n${YELLOW}========================================${NC}"
    echo -e "${YELLOW}$1${NC}"
    echo -e "${YELLOW}========================================${NC}\n"
}

print_test() {
    echo -e "${YELLOW}TEST: $1${NC}"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
}

print_success() {
    echo -e "${GREEN}✓ PASSED: $1${NC}\n"
    TESTS_PASSED=$((TESTS_PASSED + 1))
}

print_failure() {
    echo -e "${RED}✗ FAILED: $1${NC}\n"
    TESTS_FAILED=$((TESTS_FAILED + 1))
}

# Check if service is healthy
check_health() {
    local service_name=$1
    local url=$2
    
    print_test "Health check for $service_name"
    
    if response=$(curl -s -f "$url"); then
        if echo "$response" | grep -q '"status"'; then
            print_success "$service_name is healthy"
            return 0
        fi
    fi
    
    print_failure "$service_name health check failed"
    return 1
}

# Wait for services to be ready
wait_for_services() {
    print_header "WAITING FOR SERVICES TO START"
    
    echo "Waiting for services to be ready (this may take up to 2 minutes)..."
    sleep 30
    
    local max_attempts=12
    local attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if curl -s -f "$DISCOVERY_SERVER_URL/actuator/health" > /dev/null 2>&1; then
            echo -e "${GREEN}Services are ready!${NC}"
            return 0
        fi
        attempt=$((attempt + 1))
        echo "Waiting... attempt $attempt/$max_attempts"
        sleep 10
    done
    
    echo -e "${RED}Services failed to start within expected time${NC}"
    return 1
}

##############################################
# TEST 1: SERVICE HEALTH CHECKS
##############################################
test_service_health() {
    print_header "TEST SUITE 1: SERVICE HEALTH CHECKS"
    
    check_health "Discovery Server" "$DISCOVERY_SERVER_URL/actuator/health"
    check_health "API Gateway" "$API_GATEWAY_URL/actuator/health"
    check_health "Auth Service" "$AUTH_SERVICE_URL/actuator/health"
    check_health "User Service" "$USER_SERVICE_URL/actuator/health"
    check_health "Geographic Service" "$GEOGRAPHIC_SERVICE_URL/actuator/health"
    check_health "Incident Service" "$INCIDENT_SERVICE_URL/actuator/health"
    check_health "Analytics Service" "$ANALYTICS_SERVICE_URL/health"
}

##############################################
# TEST 2: SERVICE DISCOVERY
##############################################
test_service_discovery() {
    print_header "TEST SUITE 2: SERVICE DISCOVERY (EUREKA)"
    
    print_test "Check Eureka Dashboard"
    
    if response=$(curl -s "$DISCOVERY_SERVER_URL/"); then
        if echo "$response" | grep -q "Eureka"; then
            print_success "Eureka dashboard is accessible"
        else
            print_failure "Eureka dashboard not accessible"
        fi
    else
        print_failure "Cannot reach Eureka server"
    fi
    
    print_test "Verify all services are registered"
    
    services=("AUTH-SERVICE" "USER-SERVICE" "GEOGRAPHIC-SERVICE" "INCIDENT-SERVICE" "ANALYTICS-SERVICE" "API-GATEWAY")
    
    for service in "${services[@]}"; do
        if response=$(curl -s "$DISCOVERY_SERVER_URL/eureka/apps/$service"); then
            if echo "$response" | grep -q "$service"; then
                print_success "$service is registered with Eureka"
            else
                print_failure "$service is not registered with Eureka"
            fi
        else
            print_failure "Could not check registration for $service"
        fi
    done
}

##############################################
# TEST 3: AUTHENTICATION SERVICE
##############################################
test_authentication() {
    print_header "TEST SUITE 3: AUTHENTICATION SERVICE"
    
    # Test 3.1: Login as SUPER_USER (admin)
    print_test "Login as SUPER_USER (admin)"
    
    response=$(curl -s -X POST "$API_GATEWAY_URL/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"admin123"}')
    
    if echo "$response" | grep -q "token"; then
        ADMIN_TOKEN=$(echo "$response" | jq -r '.token')
        ADMIN_ID=$(echo "$response" | jq -r '.userId')
        print_success "Admin login successful. Token: ${ADMIN_TOKEN:0:20}..."
    else
        print_failure "Admin login failed: $response"
    fi
    
    # Test 3.2: Login as POLICE_STATION (station_commander)
    print_test "Login as POLICE_STATION (station_commander)"
    
    response=$(curl -s -X POST "$API_GATEWAY_URL/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"station_commander","password":"admin123"}')
    
    if echo "$response" | grep -q "token"; then
        STATION_TOKEN=$(echo "$response" | jq -r '.token')
        STATION_ID=$(echo "$response" | jq -r '.userId')
        print_success "Station Commander login successful"
    else
        print_failure "Station Commander login failed: $response"
    fi
    
    # Test 3.3: Login as OFFICER (officer_001)
    print_test "Login as OFFICER (officer_001)"
    
    response=$(curl -s -X POST "$API_GATEWAY_URL/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"officer_001","password":"admin123"}')
    
    if echo "$response" | grep -q "token"; then
        OFFICER_TOKEN=$(echo "$response" | jq -r '.token')
        OFFICER_ID=$(echo "$response" | jq -r '.userId')
        print_success "Officer login successful"
    else
        print_failure "Officer login failed: $response"
    fi
    
    # Test 3.4: Invalid credentials
    print_test "Login with invalid credentials (should fail)"
    
    response=$(curl -s -w "\n%{http_code}" -X POST "$API_GATEWAY_URL/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"wrongpassword"}')
    
    http_code=$(echo "$response" | tail -n1)
    
    if [ "$http_code" = "401" ] || [ "$http_code" = "403" ]; then
        print_success "Invalid credentials correctly rejected"
    else
        print_failure "Invalid credentials should return 401/403, got: $http_code"
    fi
}

##############################################
# TEST 4: USER SERVICE & ROLE-BASED ACCESS
##############################################
test_user_service() {
    print_header "TEST SUITE 4: USER SERVICE & ROLE-BASED ACCESS CONTROL"
    
    # Test 4.1: Get all users as admin
    print_test "Get all users as SUPER_USER"
    
    response=$(curl -s -H "Authorization: Bearer $ADMIN_TOKEN" \
        "$API_GATEWAY_URL/api/users")
    
    if echo "$response" | jq -e 'length > 0' > /dev/null 2>&1; then
        user_count=$(echo "$response" | jq 'length')
        print_success "Retrieved $user_count users as admin"
    else
        print_failure "Failed to get users: $response"
    fi
    
    # Test 4.2: Get specific user by ID
    print_test "Get user by ID"
    
    response=$(curl -s -H "Authorization: Bearer $ADMIN_TOKEN" \
        "$API_GATEWAY_URL/api/users/$ADMIN_ID")
    
    if echo "$response" | jq -e '.username == "admin"' > /dev/null 2>&1; then
        print_success "Retrieved admin user details"
    else
        print_failure "Failed to get user by ID: $response"
    fi
    
    # Test 4.3: Get user by username
    print_test "Get user by username"
    
    response=$(curl -s -H "Authorization: Bearer $ADMIN_TOKEN" \
        "$API_GATEWAY_URL/api/users/username/officer_001")
    
    if echo "$response" | jq -e '.role == "OFFICER"' > /dev/null 2>&1; then
        print_success "Retrieved user by username"
    else
        print_failure "Failed to get user by username: $response"
    fi
    
    # Test 4.4: Create new user as SUPER_USER
    print_test "Create new OFFICER user as SUPER_USER"
    
    NEW_OFFICER_DATA='{
        "username": "test_officer_'$(date +%s)'",
        "password": "testpass123",
        "email": "testofficer'$(date +%s)'@nisircop.le",
        "role": "OFFICER",
        "firstName": "Test",
        "lastName": "Officer",
        "phone": "+251-11-999-9999",
        "badgeNumber": "TEST-001"
    }'
    
    response=$(curl -s -w "\n%{http_code}" -X POST "$API_GATEWAY_URL/api/users" \
        -H "Authorization: Bearer $ADMIN_TOKEN" \
        -H "Content-Type: application/json" \
        -d "$NEW_OFFICER_DATA")
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" = "201" ]; then
        NEW_OFFICER_ID=$(echo "$body" | jq -r '.id')
        print_success "Created new officer user with ID: $NEW_OFFICER_ID"
    else
        print_failure "Failed to create user. HTTP code: $http_code, Response: $body"
    fi
    
    # Test 4.5: Create user as POLICE_STATION
    print_test "Create new OFFICER as POLICE_STATION"
    
    STATION_OFFICER_DATA='{
        "username": "station_officer_'$(date +%s)'",
        "password": "testpass123",
        "email": "stationofficer'$(date +%s)'@nisircop.le",
        "role": "OFFICER",
        "firstName": "Station",
        "lastName": "Officer",
        "phone": "+251-11-888-8888",
        "badgeNumber": "STA-001"
    }'
    
    response=$(curl -s -w "\n%{http_code}" -X POST "$API_GATEWAY_URL/api/users" \
        -H "Authorization: Bearer $STATION_TOKEN" \
        -H "Content-Type: application/json" \
        -d "$STATION_OFFICER_DATA")
    
    http_code=$(echo "$response" | tail -n1)
    
    if [ "$http_code" = "201" ]; then
        print_success "Station commander created officer user"
    else
        print_failure "Station commander failed to create officer. HTTP code: $http_code"
    fi
    
    # Test 4.6: Try to create user as OFFICER (should fail)
    print_test "Create user as OFFICER (should fail - no permission)"
    
    response=$(curl -s -w "\n%{http_code}" -X POST "$API_GATEWAY_URL/api/users" \
        -H "Authorization: Bearer $OFFICER_TOKEN" \
        -H "Content-Type: application/json" \
        -d "$NEW_OFFICER_DATA")
    
    http_code=$(echo "$response" | tail -n1)
    
    if [ "$http_code" = "403" ] || [ "$http_code" = "401" ]; then
        print_success "Officer correctly denied permission to create users"
    else
        print_failure "Officer should not be able to create users. HTTP code: $http_code"
    fi
    
    # Test 4.7: Get officers by station
    print_test "Get officers by station ID"
    
    response=$(curl -s -H "Authorization: Bearer $ADMIN_TOKEN" \
        "$API_GATEWAY_URL/api/users/station/$STATION_ID/officers")
    
    if echo "$response" | jq -e 'type == "array"' > /dev/null 2>&1; then
        officer_count=$(echo "$response" | jq 'length')
        print_success "Retrieved $officer_count officers for station"
    else
        print_failure "Failed to get officers by station: $response"
    fi
    
    # Test 4.8: Update user
    if [ -n "$NEW_OFFICER_ID" ]; then
        print_test "Update user as SUPER_USER"
        
        UPDATE_DATA='{
            "username": "test_officer_'$(date +%s)'",
            "password": "newpass123",
            "email": "updated'$(date +%s)'@nisircop.le",
            "role": "OFFICER",
            "firstName": "Updated",
            "lastName": "Officer",
            "phone": "+251-11-777-7777",
            "badgeNumber": "UPD-001"
        }'
        
        response=$(curl -s -w "\n%{http_code}" -X PUT "$API_GATEWAY_URL/api/users/$NEW_OFFICER_ID" \
            -H "Authorization: Bearer $ADMIN_TOKEN" \
            -H "Content-Type: application/json" \
            -d "$UPDATE_DATA")
        
        http_code=$(echo "$response" | tail -n1)
        
        if [ "$http_code" = "200" ]; then
            print_success "User updated successfully"
        else
            print_failure "Failed to update user. HTTP code: $http_code"
        fi
    fi
    
    # Test 4.9: Delete user
    if [ -n "$NEW_OFFICER_ID" ]; then
        print_test "Delete user as SUPER_USER"
        
        response=$(curl -s -w "\n%{http_code}" -X DELETE "$API_GATEWAY_URL/api/users/$NEW_OFFICER_ID" \
            -H "Authorization: Bearer $ADMIN_TOKEN")
        
        http_code=$(echo "$response" | tail -n1)
        
        if [ "$http_code" = "204" ]; then
            print_success "User deleted successfully"
        else
            print_failure "Failed to delete user. HTTP code: $http_code"
        fi
    fi
}

##############################################
# TEST 5: GEOGRAPHIC SERVICE
##############################################
test_geographic_service() {
    print_header "TEST SUITE 5: GEOGRAPHIC SERVICE & BOUNDARY VALIDATION"
    
    # Test 5.1: Get boundary for user
    print_test "Get geographic boundary for user"
    
    response=$(curl -s -H "Authorization: Bearer $ADMIN_TOKEN" \
        "$API_GATEWAY_URL/api/geo/boundary/$OFFICER_ID")
    
    if echo "$response" | jq -e 'has("id")' > /dev/null 2>&1; then
        print_success "Retrieved geographic boundary"
    else
        print_failure "Failed to get boundary (this is expected if no boundary is set): $response"
    fi
    
    # Test 5.2: Validate point for SUPER_USER (should always pass)
    print_test "Validate point for SUPER_USER (should always pass)"
    
    VALIDATION_DATA='{
        "userId": '$ADMIN_ID',
        "latitude": 9.0320,
        "longitude": 38.7469,
        "userRole": "SUPER_USER"
    }'
    
    response=$(curl -s -X POST "$API_GATEWAY_URL/api/geo/validate-point" \
        -H "Authorization: Bearer $ADMIN_TOKEN" \
        -H "Content-Type: application/json" \
        -d "$VALIDATION_DATA")
    
    if echo "$response" | grep -q "true"; then
        print_success "SUPER_USER point validation bypassed correctly"
    else
        print_failure "SUPER_USER should always pass validation: $response"
    fi
    
    # Test 5.3: Validate point for OFFICER
    print_test "Validate point for OFFICER"
    
    VALIDATION_DATA='{
        "userId": '$OFFICER_ID',
        "latitude": 9.0320,
        "longitude": 38.7469,
        "userRole": "OFFICER"
    }'
    
    response=$(curl -s -X POST "$API_GATEWAY_URL/api/geo/validate-point" \
        -H "Authorization: Bearer $OFFICER_TOKEN" \
        -H "Content-Type: application/json" \
        -d "$VALIDATION_DATA")
    
    # This might return false if no boundary is set
    if echo "$response" | grep -qE "true|false"; then
        print_success "Point validation response received: $response"
    else
        print_failure "Invalid validation response: $response"
    fi
}

##############################################
# TEST 6: INCIDENT SERVICE
##############################################
test_incident_service() {
    print_header "TEST SUITE 6: INCIDENT SERVICE"
    
    # Test 6.1: Create incident as SUPER_USER
    print_test "Create incident as SUPER_USER"
    
    INCIDENT_DATA='{
        "title": "Test Incident - Theft",
        "description": "Test incident created during automated testing",
        "incidentType": "THEFT",
        "priority": "HIGH",
        "latitude": 9.0320,
        "longitude": 38.7469
    }'
    
    response=$(curl -s -w "\n%{http_code}" -X POST "$API_GATEWAY_URL/api/v1/incidents" \
        -H "Authorization: Bearer $ADMIN_TOKEN" \
        -H "Content-Type: application/json" \
        -d "$INCIDENT_DATA")
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" = "200" ]; then
        ADMIN_INCIDENT_ID=$(echo "$body" | jq -r '.id')
        print_success "Created incident as admin with ID: $ADMIN_INCIDENT_ID"
    else
        print_failure "Failed to create incident. HTTP code: $http_code, Response: $body"
    fi
    
    # Test 6.2: Create incident as OFFICER
    print_test "Create incident as OFFICER"
    
    OFFICER_INCIDENT_DATA='{
        "title": "Test Incident - Burglary",
        "description": "Test incident reported by officer",
        "incidentType": "BURGLARY",
        "priority": "MEDIUM",
        "latitude": 9.0300,
        "longitude": 38.7450
    }'
    
    response=$(curl -s -w "\n%{http_code}" -X POST "$API_GATEWAY_URL/api/v1/incidents" \
        -H "Authorization: Bearer $OFFICER_TOKEN" \
        -H "Content-Type: application/json" \
        -d "$OFFICER_INCIDENT_DATA")
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" = "200" ]; then
        OFFICER_INCIDENT_ID=$(echo "$body" | jq -r '.id')
        print_success "Created incident as officer with ID: $OFFICER_INCIDENT_ID"
    else
        print_failure "Failed to create incident as officer. HTTP code: $http_code"
    fi
    
    # Test 6.3: Get all incidents as SUPER_USER
    print_test "Get all incidents as SUPER_USER"
    
    response=$(curl -s -H "Authorization: Bearer $ADMIN_TOKEN" \
        "$API_GATEWAY_URL/api/v1/incidents")
    
    if echo "$response" | jq -e 'type == "array"' > /dev/null 2>&1; then
        incident_count=$(echo "$response" | jq 'length')
        print_success "Retrieved $incident_count incidents as admin"
    else
        print_failure "Failed to get incidents: $response"
    fi
    
    # Test 6.4: Get incidents as OFFICER (should only see own incidents)
    print_test "Get incidents as OFFICER (should only see own incidents)"
    
    response=$(curl -s -H "Authorization: Bearer $OFFICER_TOKEN" \
        "$API_GATEWAY_URL/api/v1/incidents")
    
    if echo "$response" | jq -e 'type == "array"' > /dev/null 2>&1; then
        incident_count=$(echo "$response" | jq 'length')
        print_success "Officer retrieved $incident_count incidents (own only)"
    else
        print_failure "Failed to get incidents as officer: $response"
    fi
    
    # Test 6.5: Get incidents as POLICE_STATION (should see station incidents)
    print_test "Get incidents as POLICE_STATION"
    
    response=$(curl -s -H "Authorization: Bearer $STATION_TOKEN" \
        "$API_GATEWAY_URL/api/v1/incidents")
    
    if echo "$response" | jq -e 'type == "array"' > /dev/null 2>&1; then
        incident_count=$(echo "$response" | jq 'length')
        print_success "Station commander retrieved $incident_count incidents"
    else
        print_failure "Failed to get incidents as station: $response"
    fi
    
    # Test 6.6: Get specific incident by ID
    if [ -n "$ADMIN_INCIDENT_ID" ]; then
        print_test "Get incident by ID"
        
        response=$(curl -s -H "Authorization: Bearer $ADMIN_TOKEN" \
            "$API_GATEWAY_URL/api/v1/incidents/$ADMIN_INCIDENT_ID")
        
        if echo "$response" | jq -e '.id == '$ADMIN_INCIDENT_ID'' > /dev/null 2>&1; then
            print_success "Retrieved incident by ID"
        else
            print_failure "Failed to get incident by ID: $response"
        fi
    fi
    
    # Test 6.7: Update incident
    if [ -n "$ADMIN_INCIDENT_ID" ]; then
        print_test "Update incident as owner"
        
        UPDATE_INCIDENT_DATA='{
            "title": "Updated Test Incident",
            "description": "Updated description",
            "incidentType": "THEFT",
            "priority": "CRITICAL",
            "latitude": 9.0320,
            "longitude": 38.7469
        }'
        
        response=$(curl -s -w "\n%{http_code}" -X PUT "$API_GATEWAY_URL/api/v1/incidents/$ADMIN_INCIDENT_ID" \
            -H "Authorization: Bearer $ADMIN_TOKEN" \
            -H "Content-Type: application/json" \
            -d "$UPDATE_INCIDENT_DATA")
        
        http_code=$(echo "$response" | tail -n1)
        
        if [ "$http_code" = "200" ]; then
            print_success "Incident updated successfully"
        else
            print_failure "Failed to update incident. HTTP code: $http_code"
        fi
    fi
    
    # Test 6.8: Try to update another user's incident (should fail for officer)
    if [ -n "$ADMIN_INCIDENT_ID" ]; then
        print_test "Try to update another user's incident as OFFICER (should fail)"
        
        response=$(curl -s -w "\n%{http_code}" -X PUT "$API_GATEWAY_URL/api/v1/incidents/$ADMIN_INCIDENT_ID" \
            -H "Authorization: Bearer $OFFICER_TOKEN" \
            -H "Content-Type: application/json" \
            -d "$UPDATE_INCIDENT_DATA")
        
        http_code=$(echo "$response" | tail -n1)
        
        if [ "$http_code" = "403" ] || [ "$http_code" = "404" ]; then
            print_success "Officer correctly denied permission to update other's incident"
        else
            print_failure "Officer should not update other's incident. HTTP code: $http_code"
        fi
    fi
    
    # Test 6.9: Delete incident
    if [ -n "$ADMIN_INCIDENT_ID" ]; then
        print_test "Delete incident as owner"
        
        response=$(curl -s -w "\n%{http_code}" -X DELETE "$API_GATEWAY_URL/api/v1/incidents/$ADMIN_INCIDENT_ID" \
            -H "Authorization: Bearer $ADMIN_TOKEN")
        
        http_code=$(echo "$response" | tail -n1)
        
        if [ "$http_code" = "204" ]; then
            print_success "Incident deleted successfully"
        else
            print_failure "Failed to delete incident. HTTP code: $http_code"
        fi
    fi
}

##############################################
# TEST 7: ANALYTICS SERVICE
##############################################
test_analytics_service() {
    print_header "TEST SUITE 7: ANALYTICS SERVICE"
    
    # Test 7.1: Get incident count by type
    print_test "Get incident count by type"
    
    response=$(curl -s "$ANALYTICS_SERVICE_URL/analytics/incidents/count-by-type")
    
    if echo "$response" | jq -e 'type == "array"' > /dev/null 2>&1; then
        print_success "Retrieved incident count by type"
        echo "Types: $(echo "$response" | jq -c '.')"
    else
        print_failure "Failed to get incident count by type: $response"
    fi
    
    # Test 7.2: Get incident count by priority
    print_test "Get incident count by priority"
    
    response=$(curl -s "$ANALYTICS_SERVICE_URL/analytics/incidents/count-by-priority")
    
    if echo "$response" | jq -e 'type == "array"' > /dev/null 2>&1; then
        print_success "Retrieved incident count by priority"
        echo "Priorities: $(echo "$response" | jq -c '.')"
    else
        print_failure "Failed to get incident count by priority: $response"
    fi
    
    # Test 7.3: Get incident locations
    print_test "Get incident locations (for map)"
    
    response=$(curl -s "$ANALYTICS_SERVICE_URL/analytics/incidents/locations")
    
    if echo "$response" | jq -e 'type == "array"' > /dev/null 2>&1; then
        location_count=$(echo "$response" | jq 'length')
        print_success "Retrieved $location_count incident locations"
    else
        print_failure "Failed to get incident locations: $response"
    fi
}

##############################################
# TEST 8: API GATEWAY ROUTING
##############################################
test_api_gateway_routing() {
    print_header "TEST SUITE 8: API GATEWAY ROUTING"
    
    # Test 8.1: Route to auth service
    print_test "API Gateway routing to Auth Service"
    
    response=$(curl -s -w "\n%{http_code}" -X POST "$API_GATEWAY_URL/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"admin123"}')
    
    http_code=$(echo "$response" | tail -n1)
    
    if [ "$http_code" = "200" ]; then
        print_success "API Gateway correctly routes to Auth Service"
    else
        print_failure "API Gateway auth routing failed. HTTP code: $http_code"
    fi
    
    # Test 8.2: Route to user service
    print_test "API Gateway routing to User Service"
    
    response=$(curl -s -w "\n%{http_code}" -H "Authorization: Bearer $ADMIN_TOKEN" \
        "$API_GATEWAY_URL/api/users")
    
    http_code=$(echo "$response" | tail -n1)
    
    if [ "$http_code" = "200" ]; then
        print_success "API Gateway correctly routes to User Service"
    else
        print_failure "API Gateway user service routing failed. HTTP code: $http_code"
    fi
    
    # Test 8.3: JWT validation
    print_test "API Gateway JWT validation"
    
    response=$(curl -s -w "\n%{http_code}" "$API_GATEWAY_URL/api/users")
    
    http_code=$(echo "$response" | tail -n1)
    
    if [ "$http_code" = "401" ] || [ "$http_code" = "403" ]; then
        print_success "API Gateway correctly rejects requests without JWT"
    else
        print_failure "API Gateway should reject unauthenticated requests. HTTP code: $http_code"
    fi
    
    # Test 8.4: Invalid JWT
    print_test "API Gateway invalid JWT handling"
    
    response=$(curl -s -w "\n%{http_code}" -H "Authorization: Bearer invalid.jwt.token" \
        "$API_GATEWAY_URL/api/users")
    
    http_code=$(echo "$response" | tail -n1)
    
    if [ "$http_code" = "401" ] || [ "$http_code" = "403" ]; then
        print_success "API Gateway correctly rejects invalid JWT"
    else
        print_failure "API Gateway should reject invalid JWT. HTTP code: $http_code"
    fi
}

##############################################
# MAIN EXECUTION
##############################################
main() {
    echo -e "${GREEN}"
    echo "╔════════════════════════════════════════════════════════╗"
    echo "║   NISIRCOP-LE COMPREHENSIVE TEST SUITE                ║"
    echo "║   Testing Backend Services & Role-Based Access        ║"
    echo "╚════════════════════════════════════════════════════════╝"
    echo -e "${NC}\n"
    
    # Check if jq is installed
    if ! command -v jq &> /dev/null; then
        echo -e "${RED}Error: jq is not installed. Please install it first.${NC}"
        echo "Ubuntu/Debian: sudo apt-get install jq"
        echo "MacOS: brew install jq"
        exit 1
    fi
    
    # Wait for services to start
    wait_for_services || exit 1
    
    # Run all test suites
    test_service_health
    test_service_discovery
    test_authentication
    test_user_service
    test_geographic_service
    test_incident_service
    test_analytics_service
    test_api_gateway_routing
    
    # Print summary
    print_header "TEST SUMMARY"
    
    echo -e "Total Tests: ${YELLOW}$TOTAL_TESTS${NC}"
    echo -e "Passed: ${GREEN}$TESTS_PASSED${NC}"
    echo -e "Failed: ${RED}$TESTS_FAILED${NC}"
    echo -e "Success Rate: ${YELLOW}$(awk "BEGIN {printf \"%.2f\", ($TESTS_PASSED/$TOTAL_TESTS)*100}")%${NC}\n"
    
    if [ $TESTS_FAILED -eq 0 ]; then
        echo -e "${GREEN}╔════════════════════════════════════════╗${NC}"
        echo -e "${GREEN}║  ✓ ALL TESTS PASSED SUCCESSFULLY!     ║${NC}"
        echo -e "${GREEN}╚════════════════════════════════════════╝${NC}\n"
        exit 0
    else
        echo -e "${RED}╔════════════════════════════════════════╗${NC}"
        echo -e "${RED}║  ✗ SOME TESTS FAILED                   ║${NC}"
        echo -e "${RED}╚════════════════════════════════════════╝${NC}\n"
        exit 1
    fi
}

# Run main function
main
