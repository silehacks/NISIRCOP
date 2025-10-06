# SQLite Migration Guide for NISIRCOP-LE

## Overview
This document outlines the migration from PostgreSQL to SQLite for development purposes. All services have been updated to use SQLite as the primary database.

## Changes Made

### 1. Database Configuration
- **All Java services** now use SQLite instead of PostgreSQL
- **Analytics service** (Python) updated to use SQLite
- **Docker Compose** updated to remove PostgreSQL dependency
- **Shared data directory** (`./data/`) for all services

### 2. Service Updates

#### Java Services
- Updated `application.yml` files to use SQLite JDBC driver
- Updated `pom.xml` files to include SQLite dependencies
- Removed PostgreSQL dependencies
- Added Hibernate SQLite dialect support

#### Analytics Service (Python)
- Updated `requirements.txt` to remove PostgreSQL dependencies
- Modified `database.py` to use SQLite connection
- Updated database URL configuration

### 3. Data Model Changes
- **Removed case management features** (status fields)
- **Focused on incident reporting only** as per requirements
- **Maintained geospatial capabilities** with SQLite

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL, -- SUPER_USER, POLICE_STATION, OFFICER
    created_by INTEGER,
    is_active BOOLEAN DEFAULT 1
);
```

### User Profiles Table
```sql
CREATE TABLE user_profiles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(20),
    badge_number VARCHAR(20),
    boundary TEXT, -- WKT format for geospatial data
    user_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Incidents Table
```sql
CREATE TABLE incidents (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    incident_type VARCHAR(50),
    priority VARCHAR(20) NOT NULL, -- High, Medium, Low
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,
    reported_by INTEGER NOT NULL,
    occurred_at DATETIME NOT NULL,
    FOREIGN KEY (reported_by) REFERENCES users(id)
);
```

## User Roles & Permissions

### SUPER_USER
- **Can create and manage** `POLICE_STATION` accounts
- **Has access to all** crime data across all jurisdictions
- **Can view high-level** strategic analytics and reports
- **Can report incidents** anywhere (bypasses boundary validation)

### POLICE_STATION
- **Can create and manage** `OFFICER` accounts within their station
- **Can view all incidents** reported within their station's geographic boundary
- **Can edit and delete** incidents reported by their officers
- **Can monitor local** crime reports and officer activity

### OFFICER
- **Can report new incidents** from the field
- **Can only view incidents** they have personally reported
- **Cannot create other users** or view data outside their own reports

## Testing

### API Testing
Run the comprehensive API test suite:
```bash
./test-apis.sh
```

This script tests:
- Service discovery
- Authentication
- User management
- Geographic services
- Incident management
- Analytics services
- API Gateway routing

### Frontend Testing
Open `test-frontend.html` in a browser to test:
- All UI components
- Button functionality
- Form validation
- Role-based access
- Responsive design
- API integration

### Manual Testing Checklist

#### Authentication
- [ ] Login with default credentials works
- [ ] JWT token is properly stored
- [ ] Logout clears session
- [ ] 401 errors redirect to login

#### User Management
- [ ] SUPER_USER can create POLICE_STATION users
- [ ] POLICE_STATION can create OFFICER users
- [ ] OFFICER cannot create users
- [ ] User search and filtering works
- [ ] User edit/delete works

#### Incident Management
- [ ] Incident reporting modal opens on map click
- [ ] Form validation works properly
- [ ] Boundary validation works (except for SUPER_USER)
- [ ] Incident list loads and displays correctly
- [ ] Search and filtering works
- [ ] Edit/delete permissions work correctly
- [ ] "View on Map" focuses map on incident

#### Analytics
- [ ] Analytics dashboard loads
- [ ] Charts render properly (Chart.js)
- [ ] Hotspot/heatmap toggle works
- [ ] Data updates in real-time

#### Geographic Features
- [ ] Map loads with Leaflet
- [ ] User boundaries display correctly
- [ ] Location validation works
- [ ] Heatmap overlay functions

#### Responsive Design
- [ ] Mobile menu works
- [ ] Tables scroll horizontally on mobile
- [ ] Modals are responsive
- [ ] All buttons are accessible

## Default Credentials

- **SUPER_USER**: `admin` / `admin123`
- **POLICE_STATION**: `station_commander` / `admin123`
- **OFFICER**: `officer_001` / `admin123`

## File Structure

```
/workspace/
├── data/                          # SQLite database files
│   └── nisircop.db
├── analytics-service/
│   ├── app/
│   │   ├── database.py           # SQLite configuration
│   │   ├── models.py             # Updated models
│   │   └── main.py               # FastAPI app
│   └── requirements.txt          # Updated dependencies
├── auth-service/
│   ├── src/main/resources/
│   │   └── application.yml       # SQLite config
│   └── pom.xml                   # SQLite dependencies
├── user-service/
│   ├── src/main/resources/
│   │   └── application.yml       # SQLite config
│   └── pom.xml                   # SQLite dependencies
├── geographic-service/
│   ├── src/main/resources/
│   │   └── application.yml       # SQLite config
│   └── pom.xml                   # SQLite dependencies
├── incident-service/
│   ├── src/main/resources/
│   │   └── application.yml       # SQLite config
│   └── pom.xml                   # SQLite dependencies
├── frontend/
│   └── src/                      # Vue.js frontend
├── docker-compose.yml            # Updated for SQLite
├── test-apis.sh                  # API test script
└── test-frontend.html            # Frontend test suite
```

## Troubleshooting

### Common Issues

1. **Database file permissions**
   - Ensure the `data/` directory is writable
   - Check file permissions on `nisircop.db`

2. **Service startup order**
   - Discovery server must start first
   - Other services depend on discovery server

3. **Port conflicts**
   - Ensure all required ports are available
   - Check for conflicting services

4. **SQLite limitations**
   - No concurrent writes (acceptable for development)
   - Limited geospatial functions (basic support only)

### Logs
Check service logs for detailed error information:
```bash
docker compose logs [service-name]
```

## Performance Notes

- SQLite is suitable for development and testing
- For production, consider PostgreSQL with PostGIS for full geospatial support
- Current setup prioritizes simplicity and ease of development
- All geospatial operations use basic coordinate validation

## Security Considerations

- Change default passwords in production
- Use proper JWT secret generation
- Implement proper boundary validation
- Add rate limiting and input sanitization