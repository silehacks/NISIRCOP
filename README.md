NISIRCOP-LE: Crime Analytics for Ethiopian Law Enforcement
📋 Project Overview
NISIRCOP-LE (NISIR Common Operational Picture For Law Enforcement) is a comprehensive digital platform designed specifically for Ethiopian police forces to transform traditional paper-based crime reporting into a modern, data-driven intelligence system.

🎯 The Problem We Solve
Ethiopian law enforcement currently faces challenges with:

Paper-based crime reports that get lost or delayed
Manual crime mapping using physical maps and pins
Limited data analysis capabilities for strategic planning
Gut-feeling decisions instead of evidence-based policing
No centralized database of crime incidents across jurisdictions
💡 Our Solution
A web-based system that enables:

Real-time digital crime reporting with GPS coordinates
Interactive crime maps and geographic intelligence
Basic statistical analysis of crime patterns
Strict hierarchical access control matching police organizational structure
Automated boundary validation for incident reporting
🏗️ System Architecture
Technology Stack
Backend Services

Java 17 with Spring Boot 3.2+ and Spring Cloud 2023.0+
PostgreSQL 15+ with PostGIS extension for spatial data
Redis 7+ for caching and rate limiting
Python 3.11 with FastAPI for analytics service
Docker and Docker Compose for containerization
Frontend

Vue.js 3 with Composition API and TypeScript
Tailwind CSS 4+ for styling
Leaflet.js for interactive mapping
Chart.js for data visualization
Pinia for state management
Microservices Architecture
Frontend (Port 3000)
    ↓
API Gateway (Port 8080) → Redis (Rate Limiting)
    ↓
Discovery Server (Port 8761) ← Service Registry
    ↓
[Services]
├── Auth Service (8081) - Authentication & JWT
├── User Service (8085) - User management & hierarchy
├── Geographic Service (8084) - Spatial operations
├── Incident Service (8083) - Crime data collection
└── Analytics Service (8086) - Basic statistics
👮‍♂️ User Roles & Hierarchy
Strict Three-Tier System
🔴 SUPER_USER (National/Regional Administrator)

Who: Police commissioners, regional commanders
Responsibilities:
Create and manage Police Station accounts
Access all crime data across their region/country
View strategic analytics and reports
Make resource allocation decisions
Cannot create field officers directly
🟡 POLICE_STATION (Station Commander)

Who: Local police station chiefs
Responsibilities:
Create and manage Officer accounts for their station
View all crimes within station's geographic boundary
Monitor officer reporting activity
Generate local crime reports
Cannot create other police stations
🟢 OFFICER (Field Officer)

Who: Police officers on patrol duty
Responsibilities:
Report crimes using mobile devices with GPS
View only their own reported incidents
Work within assigned patrol boundaries
Cannot create any other users
User Creation Flow
BOOTSTRAP: Pre-seeded SUPER_USER (admin/admin123)
    ↓
SUPER_USER → Creates → POLICE_STATION users
    ↓
POLICE_STATION → Creates → OFFICER users
    ↓
OFFICER → No user creation rights
🗺️ Geographic Intelligence
Boundary Management
Police Station Boundaries: Geographic polygons defining jurisdiction areas
Officer Patrol Areas: Smaller polygons within station boundaries
Automatic Validation: System ensures incidents are reported within correct areas
Spatial Operations
Point-in-Polygon Checks: Validate incident locations against officer boundaries
Boundary Containment: Ensure officer areas fit within station jurisdictions
Distance Calculations: Proximity analysis for basic geographic queries
📊 Core Services Documentation
1. 🏗️ Discovery Server (Port 8761)
Purpose: Service registry and health monitoring

Tracks all running services and their locations
Provides service discovery for inter-service communication
Health monitoring and automatic failover
Dashboard available at http://localhost:8761
2. 🚪 API Gateway (Port 8080)
Purpose: Single entry point and security layer

JWT token validation for all requests
Rate limiting (10-100 requests/minute based on role)
Request routing to appropriate services
Security header injection for downstream services
3. 🔐 Auth Service (Port 8081)
Purpose: User authentication and credential management

BCrypt password hashing (strength 12)
JWT token generation and validation (24-hour expiration)
Login/logout functionality
Token blacklisting for security
4. 👥 User Service (Port 8085)
Purpose: User management and hierarchical organization

Strict role-based user creation validation
Geographic boundary assignment and validation
User profile management (badge numbers, contact info)
Organizational hierarchy maintenance
5. 🗺️ Geographic Service (Port 8084)
Purpose: Spatial operations and boundary management

Point-in-polygon validation for incident reporting
Boundary containment checks
Spatial queries for crime data
Basic hotspot detection using PostGIS functions
6. 📝 Incident Service (Port 8083)
Purpose: Crime data collection and management

Digital incident reporting with GPS coordinates
Role-based data access control
Basic incident status tracking
Simple filtering and search capabilities
7. 📊 Analytics Service (Port 8086)
Purpose: Basic crime statistics and reporting

Incident counts by type, priority, and time period
Officer activity reporting
Basic temporal patterns (hourly/daily counts)
Simple aggregations for dashboard displays
8. 🖥️ Frontend (Port 3000)
Purpose: User interface for all system interactions

Interactive crime maps with Leaflet.js
Incident reporting forms with GPS capture
Role-based dashboards and reports
Real-time data visualization
🚀 Quick Start Guide
Prerequisites
Docker and Docker Compose
8GB+ RAM recommended
Ports 3000, 5432, 6379, 8080-8086, 8761 available
Installation & Startup
# 1. Clone the repository
git clone <repository-url>
cd nisircop-le

# 2. Create your environment file from the .env.example template
# (A .env.example file is provided with the project)
cp .env.example .env

# 3. IMPORTANT: Review and edit the .env file
# You must provide a secure, random string for JWT_SECRET.
# nano .env

# 4. Start the system using Docker Compose
# The --build flag is only needed the first time or after code changes.
sudo docker compose up -d --build

# 5. Check the status of the containers
sudo docker compose ps
Default Login Credentials
Admin: admin / admin123 (SUPER_USER)
Station Commander: station_commander / admin123 (POLICE_STATION)
Officer: officer_001 / admin123 (OFFICER)
Note: It is critical to change these default passwords in a production environment.

📦 Production Deployment Considerations
For a production deployment, it is crucial to manage environment variables and secrets securely. This project uses a .env file to handle sensitive configurations.

Environment File (.env)
Before starting the application, you must create a .env file in the project root. A template named .env.example is provided to guide you. This file prevents hardcoding sensitive information like database passwords or secret keys into the application's code.

🚨 Security Best Practices
Change Default Passwords: The default user accounts are for demonstration purposes only. You must change the passwords for all default users immediately upon first login in a production setting.
Secure JWT Secret: The JWT_SECRET in your .env file must be a long, random, and cryptographically strong string. Do not use the default or a simple, guessable value.
Network Security: In a production environment, expose only the necessary ports (e.g., 80/443 for the frontend and API gateway) to the public internet. Use a firewall to restrict access to internal service ports like the database or discovery server.
Do Not Commit .env: Ensure that your .env file is listed in your .gitignore file and is never committed to version control.
📈 Data Flow & Workflow
Officer Reporting Workflow
Officer responds to incident in the field
Opens mobile app and clicks "New Incident"
System auto-captures GPS location
Officer fills form: title, description, type, priority
System validates location is within officer's patrol area
Incident saved to database with timestamp and officer ID
Station Commander Monitoring
Logs into dashboard
Views all incidents within station boundary on interactive map
Checks officer activity and reporting statistics
Generates reports for higher authorities
Monitors crime patterns in their jurisdiction
Regional Administrator Oversight
Accesses regional dashboard
Views cross-station crime trends
Analyzes resource allocation needs
Makes strategic decisions based on aggregated data
🔒 Security Features
Multi-Layer Security
API Gateway: JWT validation, rate limiting, request sanitization
Service Level: Spring Security, role-based access control
Database: BCrypt password hashing, prepared statements
Geographic: Boundary-based data access control
Data Access Control
Officers: Can only access and modify their own incidents
Station Commanders: Can view all incidents within their geographic boundary
Super Users: Have access to all data across the system
🗃️ Database Schema
Core Tables
-- Users with hierarchical structure
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('SUPER_USER', 'POLICE_STATION', 'OFFICER')),
    parent_id BIGINT REFERENCES users(id),
    is_active BOOLEAN DEFAULT TRUE
);

-- User profiles with geographic boundaries
CREATE TABLE user_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    badge_number VARCHAR(50),
    boundary GEOMETRY(POLYGON, 4326)
);

-- Crime incidents
CREATE TABLE incidents (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    incident_type VARCHAR(50),
    priority VARCHAR(20) NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(10, 8) NOT NULL,
    reported_by BIGINT REFERENCES users(id),
    occurred_at TIMESTAMP NOT NULL
);
🌍 Ethiopian Context
Localization Features
Addis Ababa Center: Default map center at [9.1450, 38.7667]
Ethiopian Time Zone: UTC+3 (East Africa Time)
Local Phone Numbers: Support for Ethiopian format (+251XXXXXXXXX)
Police Ranks: Customizable to Ethiopian police hierarchy
Geographic Coverage
Designed for urban crime analysis in Ethiopian cities
Support for multiple police stations across regions
Scalable to cover entire country as needed
📊 Analytics & Reporting
Basic Statistical Features
Incident Counts: By type, priority, time period
Officer Activity: Reporting frequency and patterns
Geographic Distribution: Basic heat maps of incident density
Temporal Patterns: Hourly, daily, monthly trends
Available Reports
Daily Activity Reports: Summary of incidents by station
Officer Performance: Basic reporting statistics
Crime Type Analysis: Distribution of incident types
Priority Analysis: Breakdown by urgency levels
🔧 Development & Customization
Local Development Setup
# Start infrastructure services
docker-compose up postgres redis discovery-server

# Run services individually for development
cd auth-service && ./mvnw spring-boot:run
cd user-service && ./mvnw spring-boot:run
# ... repeat for other services

# Run frontend
cd frontend && npm run dev
API Documentation
Swagger UI: Available at each service's /swagger-ui.html endpoint
API Gateway: http://localhost:8080/swagger-ui.html
Analytics API: http://localhost:8086/docs
🚨 Troubleshooting
Common Issues
# Services not starting
docker-compose logs [service-name]

# Database connection issues
docker-compose exec postgres psql -U nisircop_user -d nisircop_db -c "SELECT 1;"

# Frontend not loading
curl http://localhost:8080/actuator/health
Health Checks
All services provide health endpoints:

Spring Boot Services: /actuator/health
Analytics Service: /health
Frontend: Nginx status page
📈 Performance Targets
System Performance
Data Collection: Support for 1000+ monthly incident reports
Response Time: <200ms for API calls, <2s for complex queries
Availability: 99.9% uptime with health monitoring
Concurrent Users: Support for 50+ simultaneous officers reporting
Geographic Performance
Spatial Queries: Optimized with PostGIS indexes
Boundary Validation: Real-time point-in-polygon checks
Map Rendering: Efficient tile loading and caching
🔮 Future Enhancements
Potential Extensions
Mobile App: Native Android/iOS applications for field officers
Advanced Analytics: Machine learning for crime prediction
Integration: APIs for other government systems
Real-time Notifications: Alert system for critical incidents
Evidence Management: Digital evidence upload and tracking
🤝 Contributing
Development Guidelines
Follow Spring Boot best practices for backend services
Use Vue 3 Composition API patterns for frontend
Maintain PostgreSQL + PostGIS spatial query standards
Ensure role-based access control in all features
📄 License
This project is licensed under the MIT License - see the LICENSE file for details.

🙏 Acknowledgments
Ethiopian Federal Police for domain expertise and requirements
PostGIS Community for spatial database capabilities
Spring Cloud for microservices infrastructure
Vue.js Community for frontend framework and ecosystem
NISIRCOP-LE - Transforming Ethiopian law enforcement through digital crime intelligence and data-driven policing for safer communities.
