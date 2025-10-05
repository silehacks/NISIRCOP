# NISIRCOP-LE: Crime Analytics for Ethiopian Law Enforcement

## 📋 Project Overview
**NISIRCOP-LE** (NISIR Common Operational Picture For Law Enforcement) is a comprehensive, microservices-based digital platform designed to modernize crime reporting and analysis for Ethiopian police forces. It transforms traditional, paper-based processes into a data-driven intelligence system, enabling real-time reporting, interactive mapping, and statistical analysis to support evidence-based policing.

This repository contains the full source code for all backend and frontend services required to run the NISIRCOP-LE system.

## 🏗️ System Architecture
The system is built on a microservices architecture, ensuring scalability, resilience, and maintainability. All services are containerized with Docker and orchestrated with Docker Compose.

### Technology Stack
- **Backend (Java):** Java 17, Spring Boot 3.2, Spring Cloud 2023.0
- **Backend (Python):** Python 3.11, FastAPI
- **Frontend:** Vue.js 3, Vite, TypeScript, Tailwind CSS
- **Database:** PostgreSQL 15 with PostGIS for spatial data
- **Service Discovery:** Netflix Eureka
- **API Gateway:** Spring Cloud Gateway

### Microservices
The system is composed of the following services:

| Service              | Port | Technology      | Description                                                                 |
| -------------------- | ---- | --------------- | --------------------------------------------------------------------------- |
| **Discovery Server** | 8761 | Java/Eureka     | Service registry for all backend microservices.                             |
| **API Gateway**      | 8080 | Java/Spring     | Single entry point for all client requests, routing traffic to other services. |
| **Auth Service**     | 8081 | Java/Spring     | Handles user authentication and issues JWT tokens for securing the system.  |
| **User Service**     | 8085 | Java/Spring     | Manages user data, profiles, and organizational hierarchy.                  |
| **Geographic Service**| 8084 | Java/Spring     | Provides spatial operations like boundary validation and geographic queries. |
| **Incident Service** | 8083 | Java/Spring     | Core service for creating, managing, and retrieving crime incident reports. |
| **Analytics Service**| 8086 | Python/FastAPI  | Provides statistical analysis and data aggregation for dashboards.          |
| **Frontend**         | 3000 | Vue.js/Nginx    | The web-based user interface for all system interactions.                   |
| **Database**         | 5432 | PostgreSQL/PostGIS | The central database for storing all application data.                      |

## 🚀 Quick Start Guide
This guide will walk you through setting up and running the entire NISIRCOP-LE system on your local machine using Docker.

### Prerequisites
- Docker and Docker Compose
- Git for cloning the repository
- At least 8GB of RAM is recommended

### 1. Clone the Repository
First, clone this repository to your local machine:
```sh
git clone <repository-url>
cd nisircop-le
```

### 2. Create the Environment File
The system uses a `.env` file to manage sensitive configurations like database credentials and secret keys. Create a `.env` file by copying the provided example file:
```sh
cp .env.example .env
```

### 3. Configure Environment Variables
Open the `.env` file you just created and fill in the required values. **It is critical to provide a secure, random string for `JWT_SECRET`**.

```env
# A secure, random string for signing JWT tokens.
# Example: your-super-secret-and-long-random-string
JWT_SECRET=

# Credentials for the PostgreSQL database.
# These will be used to initialize the database container.
POSTGRES_USER=nisircop_user
POSTGRES_PASSWORD=nisircop_password
POSTGRES_DB=nisircop_db
```

### 4. Build and Start the System
With the environment file configured, you can now build and start all the services using Docker Compose. The `--build` flag is only necessary the first time you run the system or after making code changes.

```sh
sudo docker compose up -d --build
```
This command will:
- Build the Docker images for all services.
- Start all the containers in the correct order based on their dependencies.
- Run the entire system in detached mode (`-d`).

### 5. Verify the System is Running
You can check the status of all running containers with the following command:
```sh
sudo docker compose ps
```
You should see all services listed with a status of `Up` or `running`.

### Accessing the Services
- **Frontend Application:** `http://localhost:3000`
- **API Gateway:** `http://localhost:8080`
- **Eureka Discovery Server Dashboard:** `http://localhost:8761`

## 🗃️ Data Models
The core data models for the application are defined in the `user-service` and `incident-service`.

### User Model (`user-service`)
- **`User`**: Represents a user account with credentials and a role (`SUPER_USER`, `POLICE_STATION`, `OFFICER`).
- **`UserProfile`**: Contains the user's personal information, badge number, and their geographic patrol boundary (as a PostGIS `Polygon`).

### Incident Model (`incident-service`)
- **`Incident`**: Represents a crime report, including its title, description, type, priority, and the GPS coordinates of where it occurred.

## 🔒 Security
- **Authentication:** Managed by the `Auth Service`, which uses JWTs.
- **Authorization:** The API Gateway will be the primary enforcer of security policies, validating JWTs for incoming requests.
- **Environment Variables:** All sensitive data (passwords, secret keys) is managed through the `.env` file and is not hardcoded in the application. Ensure your `.env` file is never committed to version control.

This new `README.md` provides a comprehensive overview of the project and clear instructions for getting started. Thank you for the opportunity to build this system with you.