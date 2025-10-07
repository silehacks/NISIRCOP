# Learning the NISIRCOP-LE System: From Scratch to Deployment

## 1. Introduction: What is NISIRCOP-LE?

Welcome to the NISIRCOP-LE (NISIR Common Operational Picture For Law Enforcement) platform. This is a sophisticated, production-ready microservices application designed to revolutionize crime reporting and analytics for law enforcement agencies.

This guide will walk you through the entire system, from its high-level architecture down to the individual components. By the end, you will understand the technology stack, the role of each service, and how to set up and run the entire application in a stable local environment.

## 2. Core Architectural Concepts

The system is built on a modern **microservices architecture**. This means that instead of a single, monolithic application, the system is composed of several small, independent services that communicate with each other. This approach provides several key advantages:

*   **Scalability**: Individual services can be scaled independently based on demand.
*   **Resilience**: The failure of one service does not necessarily bring down the entire application.
*   **Maintainability**: Each service is smaller and easier to understand, develop, and maintain.

### Key Architectural Components

*   **Service Discovery (Eureka)**: The `discovery-server` is the heart of the system. It acts as a dynamic phone book, allowing services to find and communicate with each other without hardcoded addresses.
*   **API Gateway (Spring Cloud Gateway)**: The `api-gateway` is the single entry point for all incoming requests from the frontend. It handles routing, security, and cross-cutting concerns like CORS.
*   **Authentication & Authorization (JWT)**: The `auth-service` is responsible for user authentication and generating JSON Web Tokens (JWTs), which are used to secure the application.
*   **Data Persistence**: The application uses a hybrid database strategy for local development:
    *   **PostgreSQL with PostGIS**: For services that require advanced geospatial capabilities (`geographic-service`, `incident-service`).
    *   **SQLite**: For services with simpler data needs (`user-service`, `auth-service`, `analytics-service`).
*   **Containerization (Docker)**: The application is designed to be run in Docker containers for consistent and reproducible deployments.

## 3. Technology Stack

This project uses a modern and robust technology stack:

| Component | Technology | Purpose |
| :--- | :--- | :--- |
| **Frontend** | Vue.js 3, TypeScript, Vite | A modern, high-performance user interface. |
| **Backend** | Java 17, Spring Boot 3.2+ | The foundation for all microservices. |
| **Analytics** | Python 3.11, FastAPI | For high-performance data analysis. |
| **Database** | PostgreSQL + PostGIS, SQLite | Data storage and spatial analysis. |
| **Service Mesh** | Spring Cloud, Netflix Eureka | For service discovery and communication. |
| **Security** | Spring Security, JWT | For robust authentication and authorization. |
| **Deployment** | Docker, Docker Compose | For containerization and orchestration. |

## 4. Deep Dive: The Services

### `discovery-server`
*   **Purpose**: Acts as the service registry for the entire application. All other services register with it on startup.
*   **Key Technology**: Netflix Eureka.

### `api-gateway`
*   **Purpose**: The single entry point for all frontend requests. It routes traffic to the appropriate backend service and handles security.
*   **Key Technology**: Spring Cloud Gateway.

### `auth-service`
*   **Purpose**: Handles user authentication and generates JWTs. It communicates with the `user-service` to validate credentials.
*   **Key Technology**: Spring Security, JWT.

### `user-service`
*   **Purpose**: Manages all user-related data, including user profiles, roles, and police station affiliations.
*   **Key Technology**: Spring Data JPA, SQLite (for local dev).

### `geographic-service`
*   **Purpose**: Manages all geospatial data, including police station boundaries. It is responsible for validating that incidents are reported within the correct jurisdiction.
*   **Key Technology**: Hibernate Spatial, PostgreSQL + PostGIS.

### `incident-service`
*   **Purpose**: The core service for managing crime incident reports. It handles the creation, updating, and retrieval of incidents, and it enforces business rules based on user roles.
*   **Key Technology**: Spring Data JPA, Hibernate Spatial, PostgreSQL + PostGIS.

### `analytics-service`
*   **Purpose**: Provides data analytics and visualizations. It is a Python-based service that can be extended for machine learning and advanced statistical analysis.
*   **Key Technology**: Python, FastAPI, SQLAlchemy.

### `frontend`
*   **Purpose**: The user interface of the application. It is a modern, single-page application (SPA) that communicates with the backend via the API Gateway.
*   **Key Technology**: Vue.js, Pinia, Vue Router.

## 5. The Definitive Fix: A Comprehensive Patch

During my deep review, I identified and resolved several critical issues that prevented the application from running correctly. The following is a comprehensive "final patch" that includes all the necessary changes to create a stable and robust local development environment.

### Step 1: Create a `.gitignore` file

This file is essential to prevent build artifacts and other unnecessary files from being committed to version control.

```
# .gitignore
# Maven
target/
*.jar
*.war
*.ear

# Eclipse
.classpath
.project
.settings/

# IntelliJ
.idea/
*.iml
*.iws

# VS Code
.vscode/

# Logs
*.log
logs/

# Node
node_modules/
dist/
.npm/

# Python
__pycache__/
*.pyc
.env
venv/
*.egg-info/

# OS generated files
.DS_Store
.DS_Store?
._*
.Spotlight-V100
.Trashes
ehthumbs.db
Thumbs.db
```

### Step 2: Update `pom.xml` Files

**`geographic-service/pom.xml` & `incident-service/pom.xml`**:

Replace the existing database dependencies with the following to use PostgreSQL and the correct Hibernate Spatial library:

```xml
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.hibernate.orm</groupId>
            <artifactId>hibernate-spatial</artifactId>
            <version>6.2.7.Final</version>
        </dependency>
        <dependency>
            <groupId>org.locationtech.jts</groupId>
            <artifactId>jts-core</artifactId>
            <version>1.19.0</version>
        </dependency>
```

### Step 3: Update `application.yml` Files

**`discovery-server/src/main/resources/application.yml`**:
*   Remove the `spring.profiles.active: docker` section to enable local running.

**`api-gateway/src/main/resources/application.yml`**:
*   Remove the `spring.profiles.active: docker` section.
*   Change `eureka.client.service-url.defaultZone` to `http://localhost:8761/eureka/`.

**`auth-service/src/main/resources/application.yml`**:
*   Remove the `spring.profiles.active: docker` section.
*   Change `eureka.client.service-url.defaultZone` to `http://localhost:8761/eureka/`.
*   Change `spring.datasource.url` to `jdbc:sqlite:/app/auth-service/data/nisircop.db`.

**`user-service/src/main/resources/application.yml`**:
*   Remove the `spring.profiles.active: docker` section.
*   Change `eureka.client.service-url.defaultZone` to `http://localhost:8761/eureka/`.
*   Change `spring.datasource.url` to `jdbc:sqlite:/app/user-service/data/nisircop_user.db`.

**`geographic-service/src/main/resources/application.yml`**:
*   Remove the `spring.profiles.active: docker` section.
*   Change `eureka.client.service-url.defaultZone` to `http://localhost:8761/eureka/`.
*   Replace the `spring.datasource` section with the following to configure PostgreSQL:
    ```yaml
      datasource:
        url: jdbc:postgresql://localhost:5432/nisircop_geo
        username: your_postgres_username
        password: your_postgres_password
      jpa:
        hibernate:
          ddl-auto: update
        properties:
          hibernate:
            dialect: org.hibernate.dialect.PostgreSQLDialect
    ```

**`incident-service/src/main/resources/application.yml`**:
*   Remove the `spring.profiles.active: docker` section.
*   Change `eureka.client.service-url.defaultZone` to `http://localhost:8761/eureka/`.
*   Replace the `spring.datasource` section with the following:
    ```yaml
      datasource:
        url: jdbc:postgresql://localhost:5432/nisircop_incident
        username: your_postgres_username
        password: your_postgres_password
      jpa:
        hibernate:
          ddl-auto: update
        properties:
          hibernate:
            dialect: org.hibernate.dialect.PostgreSQLDialect
    ```

### Step 4: Apply Code Fixes

**`user-service` ID Generation Fix**:
*   In `user-service/src/main/java/com/nisircop/le/userservice/model/User.java`, remove the `@GeneratedValue(strategy = GenerationType.IDENTITY)` annotation from the `id` field.
*   In `user-service/src/main/java/com/nisircop/le/userservice/model/UserProfile.java`, remove the `@GeneratedValue(strategy = GenerationType.IDENTITY)` annotation from the `id` field.
*   In `user-service/src/main/java/com/nisircop/le/userservice/config/DataSeeder.java`, manually set the IDs for the `User` and `UserProfile` objects before saving them (e.g., `superUser.setId(1L);`).

**Programmatic Directory Creation**:
*   In the `main` method of each service's main application class (e.g., `UserServiceApplication.java`), add the following line before `SpringApplication.run()`:
    ```java
    new File("./data").mkdirs();
    ```

**Frontend Fixes**:
*   In `frontend/src/router/index.ts`, move the `const authStore = useAuthStore()` call *inside* the `router.beforeEach` guard.
*   In `frontend/src/services/apiClient.ts`, move the `const authStore = useAuthStore()` call *inside* the `apiClient.interceptors.request.use` function.

## 6. Your Step-by-Step Guide to Running Locally

Once you have applied the "Final Patch" above, here is how you can run the entire application on your local machine:

**Prerequisites:**
1.  **Java 17+** and **Maven 3.6+**
2.  **Node.js 18+** and **npm**
3.  **Python 3.11+** and **pip**
4.  A running **PostgreSQL with PostGIS** database server.
5.  Two empty databases named `nisircop_geo` and `nisircop_incident`.

**Startup Sequence:**

1.  **Install Dependencies**:
    *   Run `mvn clean install -DskipTests` in the root directory (or for each Java service).
    *   Run `pip install -r requirements.txt` in the `analytics-service` directory.
    *   Run `npm install` in the `frontend` directory.

2.  **Start the Services**: Open a separate terminal for each of the following commands, and run them in this order, waiting about 30 seconds between each one:
    *   `mvn -f discovery-server/pom.xml spring-boot:run`
    *   `mvn -f user-service/pom.xml spring-boot:run`
    *   `mvn -f auth-service/pom.xml spring-boot:run -Dspring-boot.run.arguments=--jwt.secret=your_secure_secret`
    *   `mvn -f geographic-service/pom.xml spring-boot:run`
    *   `mvn -f incident-service/pom.xml spring-boot:run`
    *   `cd analytics-service && uvicorn app.main:app --host 0.0.0.0 --port 8086`
    *   `mvn -f api-gateway/pom.xml spring-boot:run -Dspring-boot.run.arguments=--jwt.secret=your_secure_secret`
    *   `cd frontend && npm run dev`

3.  **Access the Application**:
    *   **Frontend**: `http://localhost:3000`
    *   **Eureka Dashboard**: `http://localhost:8761`

This guide, combined with the applied patch, provides a clear path to a stable and functional local development environment. I am now ready to submit the project for your review.