# NISIRCOP-LE: Crime Analytics for Ethiopian Law Enforcement

## 📋 Project Overview
NISIRCOP-LE (NISIR Common Operational Picture For Law Enforcement) is a comprehensive digital platform designed specifically for Ethiopian police forces. It transforms traditional, paper-based crime reporting into a modern, data-driven intelligence system, enabling real-time monitoring, analysis, and strategic decision-making.

### The Problem We Solve
Ethiopian law enforcement currently faces challenges with:
- Paper-based crime reports that get lost, are hard to aggregate, or are delayed.
- Manual crime mapping using physical maps and pins, which is inefficient and lacks analytical depth.
- Limited data analysis capabilities, making it difficult to identify crime patterns and plan strategic interventions.
- Decisions often based on intuition rather than on concrete, evidence-based insights.
- No centralized, real-time database of crime incidents across different jurisdictions.

### 💡 Our Solution
NISIRCOP-LE is a web-based microservices application that provides:
- **Real-time digital crime reporting** with precise GPS coordinates.
- **Interactive crime maps** for geographic intelligence and visualization.
- **Powerful analytics** to identify crime hotspots, trends, and patterns.
- **Strict hierarchical access control** that mirrors the police organizational structure.
- **Automated boundary validation** to ensure incidents are reported within the correct jurisdiction.

---

## 🏗️ System Architecture

### Technology Stack
- **Backend Services:** Java 17, Spring Boot 3.2+, Spring Cloud
- **Database:** PostgreSQL 15+ with PostGIS for spatial data capabilities.
- **Analytics Service:** Python 3.11 with FastAPI for high-performance data analysis.
- **Frontend:** Vue.js 3 (Composition API, TypeScript), Pinia for state management, and Tailwind CSS for styling.
- **Mapping:** Leaflet.js for interactive maps.
- **Data Visualization:** Chart.js for creating insightful dashboards.
- **Containerization:** Docker and Docker Compose for streamlined deployment.

### Microservices
The system is composed of the following microservices:
- **Frontend (Port 3000):** The main user interface, built with Vue.js.
- **API Gateway (Port 8080):** A single entry point for all client requests, handling routing and security.
- **Discovery Server (Port 8761):** A Eureka server for service registration and discovery.
- **Auth Service (Port 8081):** Manages user authentication and JWT generation.
- **User Service (Port 8085):** Handles user profiles, roles, and hierarchical relationships.
- **Geographic Service (Port 8084):** Manages spatial data, including user boundaries and location validation.
- **Incident Service (Port 8083):** Manages the core functionality of creating, reading, updating, and deleting crime incidents.
- **Analytics Service (Port 8086):** Provides data aggregation and analytics for crime trends.

---

## 👮‍♂️ User Roles & Hierarchy
The system enforces a strict three-tier user hierarchy to ensure data security and appropriate access levels.

- **SUPER_USER (National/Regional Administrator):**
  - Can create and manage `POLICE_STATION` accounts.
  - Has access to all crime data across all jurisdictions.
  - Can view high-level strategic analytics and reports.
  - **Note:** `SUPER_USER` accounts can report incidents anywhere, bypassing the usual geographic boundary checks. This is an intentional design choice for administrative purposes.

- **POLICE_STATION (Station Commander):**
  - Can create and manage `OFFICER` accounts within their station.
  - Can view all incidents reported within their station's geographic boundary.
  - Can edit and delete incidents reported by their officers.
  - Can monitor local crime reports and officer activity.

- **OFFICER (Field Officer):**
  - Can report new incidents from the field.
  - Can only view incidents they have personally reported.
  - Cannot create other users or view data outside their own reports.

---

## 🚀 Quick Start Guide

### Prerequisites
- Docker and Docker Compose
- Java 17+
- Maven 3.6+
- Node.js 18+ and npm
- Python 3.11+ and pip

### Installation & Startup

**1. Clone the Repository**
```bash
git clone <repository-url>
cd nisircop-le
```

**2. Set Up Environment Variables**
Create a `.env` file from the provided template. This file contains essential configuration, including database credentials and a secure JWT secret.
```bash
cp .env.example .env
```
**Important:** The default `JWT_SECRET` in the `.env` file is for development only. For production, generate a new, secure secret using a tool like `openssl rand -base64 32`.

**3. Run the Application**

**Option A: Using Docker Compose (Recommended)**
This is the simplest way to get the entire application running.
```bash
sudo docker compose up --build -d
```
All services, including the database, will be started in detached mode.

**Option B: Local Development Setup**
If you encounter issues with Docker (e.g., rate limits), you can run the services locally.

*   **Start the Database:**
    ```bash
    sudo docker compose up -d postgres
    ```

*   **Start Backend Services (in separate terminals):**
    ```bash
    # Discovery Server
    cd discovery-server && mvn spring-boot:run &

    # API Gateway
    cd ../api-gateway && mvn spring-boot:run &

    # Auth Service
    cd ../auth-service && mvn spring-boot:run &

    # User Service
    cd ../user-service && mvn spring-boot:run &

    # Geographic Service
    cd ../geographic-service && mvn spring-boot:run &

    # Incident Service
    cd ../incident-service && mvn spring-boot:run &

    # Analytics Service
    cd ../analytics-service
    pip install -r requirements.txt
    uvicorn app.main:app --host 0.0.0.0 --port 8086 &
    ```

*   **Start the Frontend:**
    ```bash
    cd ../frontend
    npm install
    npm run dev
    ```

**4. Access the Application**
Once all services are running, you can access the application in your browser at `http://localhost:3000`.

### Default Login Credentials
- **SUPER_USER:** `admin` / `admin123`
- **POLICE_STATION:** `station_commander` / `admin123`
- **OFFICER:** `officer_001` / `admin123`

**Note:** It is critical to change these default passwords in a production environment.

---

### 🚨 Troubleshooting
- **Connection Errors:** If services fail to connect, ensure the `discovery-server` is running and that all services have successfully registered. Check the log files for each service for detailed error messages.
- **Frontend Issues:** If the frontend is not loading correctly, check the browser's developer console for errors. Ensure the API gateway is running and that the proxy configuration in `vite.config.ts` is correct.
- **Database Problems:** Use `sudo docker compose logs postgres` to check the database logs. Ensure the credentials in your `.env` file match those in the `docker-compose.yml` file.