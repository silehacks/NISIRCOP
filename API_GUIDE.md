# NISIRCOP-LE API Complete Guide
## For Developers and Integration

---

## Quick Start

**Base URL**: `http://localhost:8080` (API Gateway)

**Authentication**: All protected endpoints require JWT token in header:
```
Authorization: Bearer <your-jwt-token>
```

**Headers Injected by Gateway**:
- `X-User-Id`: Current user's ID
- `X-User-Role`: Current user's role (SUPER_USER, POLICE_STATION, OFFICER)

---

## 1. Authentication Service

### POST /auth/login
Authenticate user and receive JWT token.

**Endpoint**: `POST /auth/login`

**Headers**: None (public endpoint)

**Request Body**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Success Response** (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJyb2xlIjoiU1VQRVJfVVNFUiIsImlhdCI6MTcwOTgxMjgwMCwiZXhwIjoxNzA5ODE2NDAwfQ.xyz...",
  "userId": 1,
  "username": "admin",
  "role": "SUPER_USER"
}
```

**Error Response** (401 Unauthorized):
```json
{
  "error": "Invalid credentials"
}
```

**cURL Example**:
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**JavaScript Example**:
```javascript
const response = await fetch('http://localhost:8080/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'admin',
    password: 'admin123'
  })
});

const data = await response.json();
const token = data.token;
// Store token for subsequent requests
localStorage.setItem('authToken', token);
```

---

## 2. User Management APIs

### GET /api/users
Get all users (filtered based on role).

**Endpoint**: `GET /api/users`

**Headers**: 
```
Authorization: Bearer <token>
```

**Query Parameters**: None

**Success Response** (200 OK):
```json
[
  {
    "id": 1,
    "username": "admin",
    "email": "admin@nisircop.le",
    "role": "SUPER_USER",
    "firstName": "System",
    "lastName": "Administrator",
    "phone": "+251-11-123-4567",
    "badgeNumber": "ADMIN-001",
    "createdBy": null,
    "active": true
  },
  {
    "id": 2,
    "username": "station_commander",
    "email": "station@nisircop.le",
    "role": "POLICE_STATION",
    "firstName": "Station",
    "lastName": "Commander",
    "phone": "+251-11-234-5678",
    "badgeNumber": "STATION-001",
    "createdBy": 1,
    "active": true
  }
]
```

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN"
```

---

### GET /api/users/{id}
Get user by ID.

**Endpoint**: `GET /api/users/{id}`

**Path Parameters**:
- `id` (integer, required): User ID

**Success Response** (200 OK):
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@nisircop.le",
  "role": "SUPER_USER",
  "firstName": "System",
  "lastName": "Administrator",
  "phone": "+251-11-123-4567",
  "badgeNumber": "ADMIN-001",
  "createdBy": null,
  "active": true
}
```

**Error Response** (404 Not Found):
```json
{
  "error": "User not found with id: 999"
}
```

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

### GET /api/users/username/{username}
Get user by username.

**Endpoint**: `GET /api/users/username/{username}`

**Path Parameters**:
- `username` (string, required): Username

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/users/username/admin \
  -H "Authorization: Bearer $TOKEN"
```

---

### POST /api/users
Create new user.

**Endpoint**: `POST /api/users`

**Headers**: 
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body**:
```json
{
  "username": "new_officer",
  "password": "securepass123",
  "email": "newofficer@nisircop.le",
  "role": "OFFICER",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+251-11-555-1234",
  "badgeNumber": "OFF-100"
}
```

**Field Descriptions**:
- `username` (string, required): Unique username (3-50 characters)
- `password` (string, required): User password (min 8 characters)
- `email` (string, required): Valid email address (unique)
- `role` (string, required): One of: SUPER_USER, POLICE_STATION, OFFICER
- `firstName` (string, required): First name
- `lastName` (string, required): Last name
- `phone` (string, optional): Phone number
- `badgeNumber` (string, optional): Badge/ID number

**Success Response** (201 Created):
```json
{
  "id": 4,
  "username": "new_officer",
  "email": "newofficer@nisircop.le",
  "role": "OFFICER",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+251-11-555-1234",
  "badgeNumber": "OFF-100",
  "createdBy": 1,
  "active": true
}
```

**Error Response** (400 Bad Request):
```json
{
  "error": "Username already exists"
}
```

**Permission Rules**:
- SUPER_USER: Can create any role
- POLICE_STATION: Can only create OFFICER
- OFFICER: Cannot create users (403 Forbidden)

**cURL Example**:
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "new_officer",
    "password": "securepass123",
    "email": "newofficer@nisircop.le",
    "role": "OFFICER",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+251-11-555-1234",
    "badgeNumber": "OFF-100"
  }'
```

---

### PUT /api/users/{id}
Update existing user.

**Endpoint**: `PUT /api/users/{id}`

**Path Parameters**:
- `id` (integer, required): User ID to update

**Request Body**: Same as POST /api/users

**Permission Rules**:
- SUPER_USER: Can update any user
- POLICE_STATION: Can update officers they created
- OFFICER: Can update own profile only

**Success Response** (200 OK): Returns updated user object

**cURL Example**:
```bash
curl -X PUT http://localhost:8080/api/users/4 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "updated_officer",
    "email": "updated@nisircop.le",
    "role": "OFFICER",
    "firstName": "Updated",
    "lastName": "Name",
    "phone": "+251-11-555-9999",
    "badgeNumber": "OFF-100"
  }'
```

---

### DELETE /api/users/{id}
Delete user.

**Endpoint**: `DELETE /api/users/{id}`

**Path Parameters**:
- `id` (integer, required): User ID to delete

**Permission Rules**:
- SUPER_USER: Can delete any user
- POLICE_STATION: Can delete officers they created
- OFFICER: Cannot delete users (403 Forbidden)

**Success Response** (204 No Content): Empty response

**cURL Example**:
```bash
curl -X DELETE http://localhost:8080/api/users/4 \
  -H "Authorization: Bearer $TOKEN"
```

---

### GET /api/users/station/{stationId}/officers
Get all officers created by a police station.

**Endpoint**: `GET /api/users/station/{stationId}/officers`

**Path Parameters**:
- `stationId` (integer, required): Police station user ID

**Success Response** (200 OK):
```json
[3, 4, 5, 6]
```

**Returns**: Array of officer user IDs

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/users/station/2/officers \
  -H "Authorization: Bearer $TOKEN"
```

---

## 3. Incident Management APIs

### POST /api/v1/incidents
Create new incident report.

**Endpoint**: `POST /api/v1/incidents`

**Headers**: 
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body**:
```json
{
  "title": "Smartphone Theft at Central Market",
  "description": "iPhone 14 Pro stolen from vendor stall. Suspect fled on motorcycle. Witness available.",
  "incidentType": "THEFT",
  "priority": "HIGH",
  "latitude": 9.0320,
  "longitude": 38.7469
}
```

**Field Descriptions**:
- `title` (string, required): Brief incident title (max 255 chars)
- `description` (string, optional): Detailed description
- `incidentType` (string, required): Type of crime
  - THEFT, BURGLARY, ASSAULT, VANDALISM, DRUG_OFFENSE, TRAFFIC, HOMICIDE, ROBBERY, DOMESTIC_VIOLENCE, OTHER
- `priority` (string, required): Incident priority
  - LOW, MEDIUM, HIGH, CRITICAL
- `latitude` (number, required): GPS latitude (-90 to 90)
- `longitude` (number, required): GPS longitude (-180 to 180)

**Success Response** (200 OK):
```json
{
  "id": 1,
  "title": "Smartphone Theft at Central Market",
  "description": "iPhone 14 Pro stolen from vendor stall. Suspect fled on motorcycle. Witness available.",
  "incidentType": "THEFT",
  "priority": "HIGH",
  "location": {
    "type": "Point",
    "coordinates": [38.7469, 9.0320]
  },
  "reportedBy": 3,
  "occurredAt": "2025-10-07T10:30:00"
}
```

**Error Response** (400 Bad Request):
```json
{
  "error": "Incident location is outside the user's assigned boundary."
}
```

**Geographic Validation**:
- SUPER_USER: Can report anywhere (validation bypassed)
- POLICE_STATION: Must be within station boundary
- OFFICER: Must be within assigned boundary

**cURL Example**:
```bash
curl -X POST http://localhost:8080/api/v1/incidents \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Smartphone Theft at Central Market",
    "description": "iPhone 14 Pro stolen from vendor stall",
    "incidentType": "THEFT",
    "priority": "HIGH",
    "latitude": 9.0320,
    "longitude": 38.7469
  }'
```

**JavaScript Example**:
```javascript
const createIncident = async (incidentData) => {
  const response = await fetch('http://localhost:8080/api/v1/incidents', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(incidentData)
  });
  
  if (!response.ok) {
    throw new Error('Failed to create incident');
  }
  
  return await response.json();
};
```

---

### GET /api/v1/incidents
Get all incidents (role-filtered).

**Endpoint**: `GET /api/v1/incidents`

**Headers**: 
```
Authorization: Bearer <token>
```

**Success Response** (200 OK):
```json
[
  {
    "id": 1,
    "title": "Smartphone Theft",
    "description": "Details...",
    "incidentType": "THEFT",
    "priority": "HIGH",
    "location": {
      "type": "Point",
      "coordinates": [38.7469, 9.0320]
    },
    "reportedBy": 3,
    "occurredAt": "2025-10-07T10:30:00"
  }
]
```

**Filtering Logic**:
- **SUPER_USER**: Returns ALL incidents in system
- **POLICE_STATION**: Returns incidents from station officers
- **OFFICER**: Returns ONLY incidents reported by the officer

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/v1/incidents \
  -H "Authorization: Bearer $TOKEN"
```

---

### GET /api/v1/incidents/{id}
Get specific incident by ID.

**Endpoint**: `GET /api/v1/incidents/{id}`

**Path Parameters**:
- `id` (integer, required): Incident ID

**Success Response** (200 OK): Returns incident object

**Error Response** (404 Not Found):
```json
{
  "error": "Incident not found"
}
```

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/v1/incidents/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

### PUT /api/v1/incidents/{id}
Update existing incident.

**Endpoint**: `PUT /api/v1/incidents/{id}`

**Path Parameters**:
- `id` (integer, required): Incident ID

**Request Body**: Same as POST /api/v1/incidents

**Permission Rules**:
- SUPER_USER: Can update any incident
- POLICE_STATION: Can update incidents from their officers
- OFFICER: Can update only own incidents

**Success Response** (200 OK): Returns updated incident

**cURL Example**:
```bash
curl -X PUT http://localhost:8080/api/v1/incidents/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Title",
    "description": "Updated details",
    "incidentType": "THEFT",
    "priority": "CRITICAL",
    "latitude": 9.0320,
    "longitude": 38.7469
  }'
```

---

### DELETE /api/v1/incidents/{id}
Delete incident.

**Endpoint**: `DELETE /api/v1/incidents/{id}`

**Path Parameters**:
- `id` (integer, required): Incident ID

**Permission Rules**:
- SUPER_USER: Can delete any incident
- POLICE_STATION: Can delete incidents from their officers
- OFFICER: Can delete only own incidents

**Success Response** (204 No Content): Empty response

**cURL Example**:
```bash
curl -X DELETE http://localhost:8080/api/v1/incidents/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 4. Geographic Service APIs

### GET /api/geo/boundary/{userId}
Get geographic boundary for user.

**Endpoint**: `GET /api/geo/boundary/{userId}`

**Path Parameters**:
- `userId` (integer, required): User ID

**Success Response** (200 OK):
```json
{
  "id": 1,
  "userId": 3,
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+251-11-345-6789",
  "badgeNumber": "OFF-001",
  "boundary": {
    "type": "Polygon",
    "coordinates": [
      [
        [38.7469, 9.0320],
        [38.7500, 9.0320],
        [38.7500, 9.0350],
        [38.7469, 9.0350],
        [38.7469, 9.0320]
      ]
    ]
  }
}
```

**Error Response** (404 Not Found):
```json
{
  "error": "No boundary found for user"
}
```

**cURL Example**:
```bash
curl -X GET http://localhost:8080/api/geo/boundary/3 \
  -H "Authorization: Bearer $TOKEN"
```

---

### POST /api/geo/validate-point
Validate if point is within user's boundary.

**Endpoint**: `POST /api/geo/validate-point`

**Request Body**:
```json
{
  "userId": 3,
  "latitude": 9.0320,
  "longitude": 38.7469,
  "userRole": "OFFICER"
}
```

**Field Descriptions**:
- `userId` (integer, required): User ID to check
- `latitude` (number, required): Point latitude
- `longitude` (number, required): Point longitude
- `userRole` (string, required): User role

**Success Response** (200 OK):
```json
true
```
or
```json
false
```

**Validation Logic**:
- SUPER_USER: Always returns `true` (no boundary check)
- POLICE_STATION/OFFICER: Checks if point is within assigned polygon

**cURL Example**:
```bash
curl -X POST http://localhost:8080/api/geo/validate-point \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 3,
    "latitude": 9.0320,
    "longitude": 38.7469,
    "userRole": "OFFICER"
  }'
```

---

## 5. Analytics Service APIs

**Note**: Analytics service runs on separate port (8086) and doesn't require authentication (internal service).

### GET /analytics/incidents/count-by-type
Get incident count grouped by type.

**Endpoint**: `GET http://localhost:8086/analytics/incidents/count-by-type`

**Success Response** (200 OK):
```json
[
  {
    "name": "THEFT",
    "count": 45
  },
  {
    "name": "BURGLARY",
    "count": 23
  },
  {
    "name": "ASSAULT",
    "count": 18
  }
]
```

**Use Case**: Display bar chart of crime types

**cURL Example**:
```bash
curl -X GET http://localhost:8086/analytics/incidents/count-by-type
```

---

### GET /analytics/incidents/count-by-priority
Get incident count grouped by priority.

**Endpoint**: `GET http://localhost:8086/analytics/incidents/count-by-priority`

**Success Response** (200 OK):
```json
[
  {
    "name": "HIGH",
    "count": 30
  },
  {
    "name": "MEDIUM",
    "count": 25
  },
  {
    "name": "LOW",
    "count": 15
  },
  {
    "name": "CRITICAL",
    "count": 10
  }
]
```

**Use Case**: Display pie chart of incident priorities

**cURL Example**:
```bash
curl -X GET http://localhost:8086/analytics/incidents/count-by-priority
```

---

### GET /analytics/incidents/locations
Get all incident locations for mapping.

**Endpoint**: `GET http://localhost:8086/analytics/incidents/locations`

**Success Response** (200 OK):
```json
[
  {
    "latitude": 9.0320,
    "longitude": 38.7469
  },
  {
    "latitude": 9.0310,
    "longitude": 38.7450
  },
  {
    "latitude": 9.0330,
    "longitude": 38.7480
  }
]
```

**Use Case**: Display crime heat map or markers on map

**cURL Example**:
```bash
curl -X GET http://localhost:8086/analytics/incidents/locations
```

---

## 6. Error Handling

### Standard Error Response Format

```json
{
  "timestamp": "2025-10-07T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/users"
}
```

### HTTP Status Codes

| Code | Meaning | When It Occurs |
|------|---------|----------------|
| 200 | OK | Successful GET, PUT |
| 201 | Created | Successful POST (user/incident created) |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Invalid input data, validation errors |
| 401 | Unauthorized | Missing or invalid JWT token |
| 403 | Forbidden | Insufficient permissions for action |
| 404 | Not Found | Resource doesn't exist |
| 500 | Internal Server Error | Server-side error |

---

## 7. Complete Integration Example

### Full Workflow: Login → Create User → Create Incident

```javascript
// 1. Login
const loginResponse = await fetch('http://localhost:8080/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'admin',
    password: 'admin123'
  })
});

const { token } = await loginResponse.json();

// 2. Create new officer
const userResponse = await fetch('http://localhost:8080/api/users', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'new_officer_123',
    password: 'secure123',
    email: 'officer123@nisircop.le',
    role: 'OFFICER',
    firstName: 'Jane',
    lastName: 'Smith',
    phone: '+251-11-999-8888',
    badgeNumber: 'OFF-123'
  })
});

const newUser = await userResponse.json();
console.log('Created user:', newUser.id);

// 3. Create incident (as the officer)
const incidentResponse = await fetch('http://localhost:8080/api/v1/incidents', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    title: 'Burglary at Residential Area',
    description: 'Multiple houses broken into during night',
    incidentType: 'BURGLARY',
    priority: 'HIGH',
    latitude: 9.0320,
    longitude: 38.7469
  })
});

const newIncident = await incidentResponse.json();
console.log('Created incident:', newIncident.id);

// 4. Get analytics data
const analyticsResponse = await fetch('http://localhost:8086/analytics/incidents/count-by-type');
const analytics = await analyticsResponse.json();
console.log('Crime statistics:', analytics);
```

---

## 8. Rate Limiting & Best Practices

### Best Practices

1. **Store tokens securely**
   - Use httpOnly cookies or secure localStorage
   - Never expose tokens in URLs

2. **Handle token expiration**
   ```javascript
   if (response.status === 401) {
     // Token expired, redirect to login
     window.location.href = '/login';
   }
   ```

3. **Validate input client-side**
   - Check required fields
   - Validate email format
   - Validate coordinate ranges

4. **Use appropriate HTTP methods**
   - GET: Retrieve data (idempotent)
   - POST: Create new resources
   - PUT: Update existing resources
   - DELETE: Remove resources

5. **Handle errors gracefully**
   ```javascript
   try {
     const response = await fetch(url, options);
     if (!response.ok) {
       const error = await response.json();
       console.error('API Error:', error.message);
     }
   } catch (error) {
     console.error('Network Error:', error);
   }
   ```

---

## 9. Postman Collection

### Import these endpoints into Postman:

1. Create new collection: "NISIRCOP-LE"
2. Add environment variables:
   - `base_url`: http://localhost:8080
   - `analytics_url`: http://localhost:8086
   - `token`: (will be set after login)

3. Add pre-request script for authenticated requests:
```javascript
pm.request.headers.add({
  key: 'Authorization',
  value: 'Bearer ' + pm.environment.get('token')
});
```

4. Add test script for login to save token:
```javascript
if (pm.response.code === 200) {
  const jsonData = pm.response.json();
  pm.environment.set('token', jsonData.token);
}
```

---

## 10. WebSocket Support (Future)

Planned for real-time notifications:

```javascript
// Future implementation
const ws = new WebSocket('ws://localhost:8080/ws');

ws.on('incident-created', (data) => {
  console.log('New incident:', data);
  // Update UI in real-time
});
```

---

## Appendix: Quick Reference

### All Endpoints Summary

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /auth/login | Login | No |
| GET | /api/users | Get all users | Yes |
| GET | /api/users/{id} | Get user by ID | Yes |
| GET | /api/users/username/{username} | Get user by username | Yes |
| POST | /api/users | Create user | Yes |
| PUT | /api/users/{id} | Update user | Yes |
| DELETE | /api/users/{id} | Delete user | Yes |
| GET | /api/users/station/{id}/officers | Get station officers | Yes |
| POST | /api/v1/incidents | Create incident | Yes |
| GET | /api/v1/incidents | Get incidents | Yes |
| GET | /api/v1/incidents/{id} | Get incident | Yes |
| PUT | /api/v1/incidents/{id} | Update incident | Yes |
| DELETE | /api/v1/incidents/{id} | Delete incident | Yes |
| GET | /api/geo/boundary/{userId} | Get boundary | Yes |
| POST | /api/geo/validate-point | Validate location | Yes |
| GET | /analytics/incidents/count-by-type | Type statistics | No |
| GET | /analytics/incidents/count-by-priority | Priority stats | No |
| GET | /analytics/incidents/locations | Map locations | No |

---

**Need Help?** Check the main PROJECT_PRESENTATION.md for architecture details and user guides.
