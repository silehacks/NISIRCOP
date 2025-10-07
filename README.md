# NISIRCOP-LE: Crime Analytics Platform for Ethiopian Law Enforcement

<div align="center">

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/java-17+-red.svg)
![Python](https://img.shields.io/badge/python-3.11+-green.svg)
![Status](https://img.shields.io/badge/status-development-yellow.svg)

**A comprehensive digital platform transforming crime reporting and intelligence for Ethiopian police forces**

</div>

## 🎯 Overview

NISIRCOP-LE (NISIR Common Operational Picture For Law Enforcement) is a microservices platform designed to modernize crime reporting, analysis, and intelligence for Ethiopian law enforcement agencies. It transforms traditional paper-based processes into a sophisticated digital ecosystem that enables real-time monitoring, data-driven decision making, and strategic crime prevention.

### Key Value Propositions

- **🚀 Real-time Crime Intelligence**: Instant incident reporting with GPS coordinates and automated analytics.
- **🗺️ Interactive Crime Mapping**: Geospatial visualization with hotspot analysis and boundary validation.
- **📊 Advanced Analytics**: Python-powered data analysis with machine learning capabilities.
- **🔐 Hierarchical Security**: Role-based access control mirroring police organizational structure.
- **⚡ High Performance**: Optimized microservices architecture with health monitoring.

## 🏗️ System Architecture

### Technology Stack

| Component | Technology | Purpose |
|-----------|------------|---------|
| **Frontend** | Vue.js 3, TypeScript, Vite | Modern reactive user interface |
| **UI Framework** | Tailwind CSS | Responsive design system |
| **State Management** | Pinia | Frontend state management |
| **Routing** | Vue Router | Client-side navigation |
| **HTTP Client** | Axios | API communication |
| **Maps** | Leaflet | Geographic visualization |
| **Charts** | Chart.js | Data visualization |
| **Backend** | Java 17, Spring Boot 3.2+ | Microservices foundation |
| **Analytics** | Python 3.11, FastAPI | High-performance data processing |
| **Database** | PostgreSQL + PostGIS | Data persistence with geospatial support |
| **Service Discovery**| Netflix Eureka | Dynamic service registration |
| **API Gateway** | Spring Cloud Gateway | Unified entry point & routing |
| **Security** | JWT, Spring Security | Authentication & authorization |
| **Monitoring** | Spring Actuator | Health checks & metrics |

### Microservices Architecture

```
┌─────────────────┐    ┌─────────────────┐
│   Frontend      │◄───┤  API Gateway    │
│   (Vue.js)      │    │   Port: 8080    │
│   Port: 3000    │    │                 │
└─────────────────┘    └─────────────────┘
                                │
                       ┌─────────────────┐
                       │ Discovery Server│
                       │   Port: 8761    │
                       └─────────────────┘
                                │
          ┌─────────────────────┼─────────────────────┐
          │                     │                     │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Auth Service   │    │  User Service   │    │Geographic Serv. │
│   Port: 8081    │    │   Port: 8085    │    │   Port: 8084    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
          │                     │                     │
          └─────────────────────┼─────────────────────┘
                                │
          ┌─────────────────────┼─────────────────────┐
          │                     │                     │
┌─────────────────┐    ┌─────────────────┐    
│Incident Service │    │Analytics Service│    
│   Port: 8083    │    │   Port: 8086    │    
└─────────────────┘    └─────────────────┘    
```

## 👮‍♂️ User Roles & Hierarchy

### 🏛️ SUPER_USER (National/Regional Administrator)
- **Permissions**: Full system access, can create POLICE_STATION accounts.
- **Capabilities**: 
  - View all crime data across jurisdictions.
  - Access strategic analytics and reports.
  - Report incidents anywhere (bypasses boundary validation).
  - Manage system-wide configurations.

### 🏢 POLICE_STATION (Station Commander)
- **Permissions**: Station-level management and oversight.
- **Capabilities**:
  - Create and manage OFFICER accounts within the station.
  - View all incidents within the station's geographic boundary.
  - Edit/delete incidents reported by their officers.
  - Access station-level analytics and reports.

### 👮 OFFICER (Field Officer)
- **Permissions**: Field reporting and personal data access.
- **Capabilities**:
  - Report new incidents from the field.
  - View only their own incident reports.
  - Access mobile-optimized interface.
  - Cannot create users or access others' data.

## Microservices Overview

The backend is composed of seven distinct microservices, each with a specific responsibility.

-   **Discovery Server (Port 8761)**: Using Netflix Eureka, this service allows all other services to find and communicate with each other dynamically. It acts as a service registry.
-   **API Gateway (Port 8080)**: Built with Spring Cloud Gateway, this is the single entry point for all client requests. It handles routing, rate limiting, and security (JWT validation).
-   **Auth Service (Port 8081)**: Manages user authentication. It validates credentials by communicating with the User Service and issues JWTs upon successful login.
-   **User Service (Port 8085)**: Responsible for all user-related data, including user accounts, profiles, and the enforcement of the hierarchical role-based access control (RBAC) rules.
-   **Geographic Service (Port 8084)**: Performs geospatial operations, such as validating that an incident's location is within an officer's assigned boundary, using the JTS Topology Suite.
-   **Incident Service (Port 8083)**: Manages the creation, retrieval, updating, and deletion of crime incident reports. It enforces business rules based on user roles and geographic boundaries.
-   **Analytics Service (Port 8086)**: A Python-based FastAPI service that provides aggregated data and statistics for dashboards and reports by directly querying the database.

## Security Architecture

Security is implemented in multiple layers, with JWT (JSON Web Tokens) being the core of the authentication and authorization strategy.

### Authentication Flow

1.  The user provides their credentials to the **Auth Service** via the API Gateway.
2.  The **Auth Service** delegates credential validation to the **User Service**.
3.  If the credentials are valid, the **Auth Service** generates a JWT containing the user's ID, username, and role.
4.  The client stores this token and includes it in the `Authorization` header for all subsequent requests to protected endpoints.
5.  The **API Gateway** intercepts each request, validates the JWT, and, if valid, forwards the request to the appropriate microservice, injecting the user's ID and role as headers for downstream authorization.

```
┌──────────┐           ┌──────────────┐          ┌──────────────┐          ┌─────────────┐
│  Client  │──Login──>│ API Gateway  │──Validate──>│ Auth Service │──GetUser──>│ User Service│
└──────────┘           └──────┬───────┘          └──────┬───────┘          └─────────────┘
     ▲                      │                      │
     │                      │                      │
   Token                  Token                  Token
     │                      │                      │
     └──────────────────────┴──────────────────────┘
```

### JWT Token Security

-   **Stateless**: The server does not need to store session information, making the system easier to scale.
-   **Signature**: Tokens are signed using HMAC-SHA256, ensuring they cannot be tampered with.
-   **Expiration**: Tokens have a limited lifespan, reducing the risk of compromised tokens.
-   **Payload**: The token payload carries essential, non-sensitive user information like `userId` and `role`, which services use to make authorization decisions.

## Architecture Principles

- **Microservices**: The system is built on a microservices architecture, where each service is independent, singularly focused, and can be developed and deployed individually.
- **Service Discovery**: Services dynamically register with a central Eureka server, allowing them to locate and communicate with each other without hardcoded addresses.
- **API Gateway**: A single API Gateway provides a unified entry point for all client requests, handling routing, security, and cross-cutting concerns.
- **Database per Service**: Each microservice is responsible for its own data, promoting loose coupling and allowing services to evolve independently.
- **Stateless Services**: Backend services are stateless, with authentication managed via JWT tokens, which enables easier scaling and load balancing.

## Database Design

### Entity-Relationship Diagram
```
┌─────────────────┐
│     Users       │
├─────────────────┤
│ id (PK)         │
│ username        │
│ password        │
│ email           │
│ role            │
│ created_by (FK) │──┐
│ is_active       │  │
└────────┬────────┘  │
         │           │
         │ 1:1       │ Self-reference
         │           │
┌────────▼────────┐  │
│  User Profiles  │  │
├─────────────────┤  │
│ id (PK)         │  │
│ user_id (FK)    │  │
│ first_name      │  │
│ last_name       │  │
│ phone           │  │
│ badge_number    │  │
│ boundary (GEOM) │  │
└─────────────────┘  │
                     │
                     │
┌─────────────────┐  │
│   Incidents     │  │
├─────────────────┤  │
│ id (PK)         │  │
│ title           │  │
│ description     │  │
│ incident_type   │  │
│ priority        │  │
│ location (GEOM) │  │
│ reported_by (FK)│──┘
│ occurred_at     │
└─────────────────┘
```

### PostgreSQL Schema
The following SQL commands can be used to set up the database schema. The services, using JPA, will create these tables on startup, but this provides a reference.

**Users Table**
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_by INTEGER,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
```

**User Profiles Table**
```sql
CREATE TABLE user_profiles (
    id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(20),
    badge_number VARCHAR(20),
    boundary GEOMETRY(POLYGON, 4326),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_userprofiles_userid ON user_profiles(user_id);
CREATE SPATIAL INDEX idx_userprofiles_boundary ON user_profiles(boundary);
```

**Incidents Table**
```sql
CREATE TABLE incidents (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    incident_type VARCHAR(50) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    location GEOMETRY(POINT, 4326) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    reported_by INTEGER NOT NULL,
    occurred_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reported_by) REFERENCES users(id)
);

CREATE INDEX idx_incidents_type ON incidents(incident_type);
CREATE INDEX idx_incidents_priority ON incidents(priority);
CREATE INDEX idx_incidents_reporter ON incidents(reported_by);
CREATE INDEX idx_incidents_date ON incidents(occurred_at);
CREATE SPATIAL INDEX idx_incidents_location ON incidents(location);
```

## 🚀 Local Development Setup

This guide provides instructions for setting up and running the entire NISIRCOP-LE platform on your local machine for development purposes.

### Prerequisites

- **Git**: For cloning the repository.
- **Java 17+**: OpenJDK 17 or a later version.
- **Maven 3.6+**: For building the Java microservices.
- **Node.js 18+**: For running the frontend application.
- **npm 8+**: For frontend dependency management.
- **Python 3.11+**: For the Analytics Service.
- **PostgreSQL 14+**: The database for all services.
- **PostGIS 3+**: The geospatial extension for PostgreSQL.
- A code editor or IDE, such as Visual Studio Code or IntelliJ IDEA.
- At least **8GB of RAM** and **4 CPU cores** are recommended for a smooth experience.

### 1. Clone the Repository
```bash
git clone https://github.com/your-org/nisircop-le.git
cd nisircop-le
```

### 2. Setup PostgreSQL and PostGIS

You need a PostgreSQL server with the PostGIS extension enabled.

#### Using a Local Installation:

1.  **Install PostgreSQL**: Follow the official instructions for your operating system from [postgresql.org](https://www.postgresql.org/download/).
2.  **Install PostGIS**: Follow the official instructions for your version of PostgreSQL from [postgis.net/install](https://postgis.net/install/).
3.  **Create the database**:
    ```bash
    psql -U postgres
    CREATE DATABASE nisircop_le;
    \c nisircop_le
    CREATE EXTENSION postgis;
    \q
    ```
4.  **Configure Environment Variables**:
    Create a `.env` file in the project root by copying the example template:
    ```bash
    cp .env.example .env
    ```
    Open the newly created `.env` file and update the placeholder values with your local PostgreSQL credentials and a secure JWT secret.

    **Important**: It is strongly recommended to add the `.env` file to your project's `.gitignore` to avoid committing sensitive credentials to source control.

### 3. Build and Run the Backend Services

You will need to open a separate terminal for each of the 7 backend services.

**Build All Java Services:**
Before running the services, you must build each Java service individually using Maven. From the project root, run the following commands:
```bash
cd discovery-server && mvn clean install && cd ..
cd api-gateway && mvn clean install && cd ..
cd auth-service && mvn clean install && cd ..
cd user-service && mvn clean install && cd ..
cd geographic-service && mvn clean install && cd ..
cd incident-service && mvn clean install && cd ..
```

**Service Startup Order:**

It is crucial to start the services in the following order to ensure proper dependency registration with the Discovery Server.

1.  **Terminal 1: Discovery Server (Port 8761)**
    ```bash
    cd discovery-server
    mvn spring-boot:run
    ```
    *Wait for the "Started EurekaServerApplication" log message before starting other services.*

2.  **Terminal 2: API Gateway (Port 8080)**
    ```bash
    cd api-gateway
    mvn spring-boot:run
    ```

3.  **Terminal 3: Auth Service (Port 8081)**
    ```bash
    cd auth-service
    mvn spring-boot:run
    ```

4.  **Terminal 4: User Service (Port 8085)**
    ```bash
    cd user-service
    mvn spring-boot:run
    ```

5.  **Terminal 5: Geographic Service (Port 8084)**
    ```bash
    cd geographic-service
    mvn spring-boot:run
    ```

6.  **Terminal 6: Incident Service (Port 8083)**
    ```bash
    cd incident-service
    mvn spring-boot:run
    ```

7.  **Terminal 7: Analytics Service (Port 8086)**
    ```bash
    cd analytics-service
    pip install -r requirements.txt
    uvicorn main:app --host 0.0.0.0 --port 8086 --reload
    ```

### 4. Run the Frontend Application

1.  **Terminal 8: Frontend (Port 3000)**
    ```bash
    cd frontend
    npm install
    npm run dev
    ```

### 5. Verify the Setup

-   **Eureka Dashboard**: Open [http://localhost:8761](http://localhost:8761) to see all registered backend services.
-   **Frontend Application**: Open [http://localhost:3000](http://localhost:3000) to access the user interface.
-   **API Gateway**: The gateway is available at [http://localhost:8080](http://localhost:8080).

### Default Credentials

| Role | Username | Password |
| :--- | :--- | :--- |
| SUPER_USER | `admin` | `admin123` |
| POLICE_STATION | `station_commander`| `admin123` |
| OFFICER | `officer_001` | `admin123` |

**Note**: These credentials will be seeded into the `user-service` database on its first startup.

## 📖 User Guide

### Officer
- **Primary Tasks**: Report incidents and view their own reports.
- **Key Actions**:
    - Log in and view a personal dashboard.
    - Create a new incident report with details and a GPS location. The location must be within their assigned boundary.
    - View, edit, and delete their own incidents.

### Police Station Commander
- **Primary Tasks**: Manage officers and monitor all incidents within their station's jurisdiction.
- **Key Actions**:
    - Create and manage `OFFICER` accounts for their station.
    - View all incidents reported by their officers.
    - Edit or delete incidents reported by their officers.
    - View station-level analytics and crime heatmaps.

### Super User (Administrator)
- **Primary Tasks**: Full system oversight, user management, and national-level analytics.
- **Key Actions**:
    - Create and manage `POLICE_STATION` and other `SUPER_USER` accounts.
    - View, edit, and delete any user or incident across the entire system.
    - Access system-wide analytics, national crime maps, and reports.
    - Report incidents anywhere, with no geographic boundary restrictions.

## 🧪 Testing the Application

### Backend Health Checks

You can check the health of each microservice using its actuator endpoint:
```bash
# API Gateway
curl http://localhost:8080/actuator/health

# Auth Service
curl http://localhost:8081/actuator/health

# User Service
curl http://localhost:8085/actuator/health

# Geographic Service
curl http://localhost:8084/actuator/health

# Incident Service
curl http://localhost:8083/actuator/health

# Analytics Service
curl http://localhost:8086/health
```

### Frontend Testing

1.  **Login**: Open the application at [http://localhost:3000](http://localhost:3000) and log in with each of the default users to test their roles.
2.  **Create an Incident**: Log in as an `OFFICER` or `POLICE_STATION` and create a new incident report. Verify the form submission works and the new incident appears in the list.
3.  **View Analytics**: Log in as a `SUPER_USER` or `POLICE_STATION` and navigate to the analytics dashboard to see if data is displayed correctly.
4.  **User Management**: Log in as a `SUPER_USER` and create a new `POLICE_STATION` user. Then, log in as that new user and create an `OFFICER`.

## 📄 API Reference

All endpoints are accessed through the API Gateway at `http://localhost:8080`. All protected endpoints require a JWT token in the `Authorization: Bearer <token>` header.

### Authentication API

#### **POST** `/auth/login`
Authenticates a user and returns a JWT token. This is a public endpoint.

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "username": "admin",
  "role": "SUPER_USER"
}
```

### User Management API

#### **POST** `/api/users`
Creates a new user. Permissions are enforced based on the requester's role.

**Request Body:**
```json
{
  "username": "new_officer",
  "password": "securePassword123",
  "email": "new.officer@example.com",
  "role": "OFFICER",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "123-456-7890",
  "badgeNumber": "OFFICER_123"
}
```

**Success Response (201 Created):** Returns the newly created user object.

#### **GET** `/api/users`
Retrieves a list of users. The results are filtered based on the requester's role and hierarchy.

#### **GET** `/api/users/{id}`
Retrieves a specific user by their ID.

### Incident Management API

#### **POST** `/api/incidents`
Creates a new incident report. The location is validated against the user's assigned geographic boundary unless the user is a `SUPER_USER`.

**Request Body:**
```json
{
  "title": "Theft at Merkato",
  "description": "A smartphone was stolen from a pedestrian.",
  "incidentType": "THEFT",
  "priority": "HIGH",
  "latitude": 9.0320,
  "longitude": 38.7469
}
```

**Success Response (201 Created):** Returns the newly created incident object.

#### **GET** `/api/incidents`
Retrieves a list of incidents. The results are filtered based on the user's role and geographic boundary.
-   `SUPER_USER`: Sees all incidents.
-   `POLICE_STATION`: Sees incidents from their station's officers.
-   `OFFICER`: Sees only their own reported incidents.

### Geographic API

#### **POST** `/api/geo/validate-point`
Validates if a given coordinate point is within a user's assigned boundary.

**Request Body:**
```json
{
  "userId": 123,
  "latitude": 9.0320,
  "longitude": 38.7469,
  "userRole": "OFFICER"
}
```
**Success Response (200 OK):** Returns `true` or `false`.

### Analytics API

#### **GET** `/api/analytics/summary`
Retrieves a summary of crime statistics, such as counts by type and priority.

**Success Response (200 OK):**
```json
{
  "totalIncidents": 150,
  "byType": {
    "THEFT": 45,
    "ASSAULT": 30
  },
  "byPriority": {
    "HIGH": 60,
    "MEDIUM": 70
  }
}
```

## 🎨 Frontend Architecture

The frontend is a modern single-page application (SPA) built with Vue.js 3 and TypeScript.

-   **Build Tool**: Vite provides a fast development server and optimized production builds.
-   **UI Framework**: Tailwind CSS is used for a utility-first, responsive design system.
-   **State Management**: Pinia serves as the centralized state management solution.
-   **Routing**: Vue Router handles all client-side navigation.
-   **API Communication**: Axios is configured for making requests to the backend API Gateway.

### Directory Structure
```
frontend/
├── src/
│   ├── assets/         # Static assets like images and CSS
│   ├── components/     # Reusable Vue components
│   ├── services/       # API client and service-specific helpers
│   ├── stores/         # Pinia state management modules
│   ├── views/          # Page-level components for each route
│   ├── App.vue         # The root Vue component
│   └── main.ts         # Application entry point
├── index.html          # The main HTML file
└── vite.config.ts      # Vite build and server configuration
```

## 🔧 Configuration Details

### Environment Variables
The entire application is configured using a `.env` file in the project root. This file should not be committed to source control.

**Key Variables:**
| Variable | Description | Example |
| :--- | :--- | :--- |
| `DB_HOST` | The hostname of the PostgreSQL database. | `localhost` |
| `DB_PORT` | The port of the PostgreSQL database. | `5432` |
| `DB_NAME` | The name of the database to use. | `nisircop_le` |
| `DB_USERNAME`| The username for the database connection. | `postgres` |
| `DB_PASSWORD`| The password for the database connection. | `your_password` |
| `JWT_SECRET` | A secret key for signing JWTs. | `a-long-and-secure-secret-key` |

The `spring-dotenv` library is used in the Java services to automatically load these variables. The Python services use `python-dotenv`.

## ⚙️ Troubleshooting

### Common Local Setup Issues

**"Could not resolve placeholder '...'" error in a Spring Boot service:**
-   **Cause**: The service cannot find a required environment variable.
-   **Solution**: Ensure you have created a `.env` file in the project root and that it contains all the necessary variables (`DB_HOST`, `JWT_SECRET`, etc.).

**"Connection refused" error when a service tries to connect to the database:**
-   **Cause**: The PostgreSQL server is not running, or the connection details in your `.env` file are incorrect.
-   **Solution**:
    1.  Verify that your local PostgreSQL server is running.
    2.  Double-check the `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, and `DB_PASSWORD` values in your `.env` file.

**A service fails to register with Eureka:**
-   **Cause**: The Discovery Server is not running or is not accessible.
-   **Solution**:
    1.  Make sure you started the **Discovery Server** first and gave it time to initialize.
    2.  Check the `discovery-server.log` for any startup errors.
    3.  Verify that no other process is using port `8761`.

**Frontend shows a blank page or API errors in the browser console:**
-   **Cause**: The frontend application cannot connect to the API Gateway.
-   **Solution**:
    1.  Ensure the **API Gateway** is running on port `8080`.
    2.  Check the `api-gateway.log` for startup errors. The gateway will fail to start if it cannot connect to the Discovery Server.

---

<div align="center">

**Built with ❤️ for Ethiopian Law Enforcement**

*Transforming crime fighting through technology*

</div>