# NISIRCOP-LE Architecture Documentation
## Technical Design and Implementation Details

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Microservices Design](#microservices-design)
3. [Security Architecture](#security-architecture)
4. [Database Design](#database-design)
5. [Communication Patterns](#communication-patterns)
6. [Deployment Architecture](#deployment-architecture)
7. [Design Decisions](#design-decisions)
8. [Scalability Considerations](#scalability-considerations)

---

## 1. Architecture Overview

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     CLIENT LAYER                         │
│              (Web/Mobile Applications)                   │
└───────────────────────┬─────────────────────────────────┘
                        │ HTTPS
                        │
┌───────────────────────▼─────────────────────────────────┐
│                  API GATEWAY LAYER                       │
│         • Request Routing                                │
│         • JWT Validation                                 │
│         • Load Balancing                                 │
│         • Rate Limiting                                  │
└───────────────────────┬─────────────────────────────────┘
                        │
        ┌───────────────┼───────────────┐
        │               │               │
┌───────▼──────┐ ┌─────▼─────┐ ┌──────▼──────┐
│   Service    │ │  Service  │ │   Service   │
│  Discovery   │ │  Registry │ │   Health    │
│   (Eureka)   │ │           │ │  Monitoring │
└──────────────┘ └───────────┘ └─────────────┘
        │
        │ Service Registration & Discovery
        │
┌───────┴──────────────────────────────────────────────┐
│              MICROSERVICES LAYER                      │
│                                                       │
│  ┌─────────┐  ┌─────────┐  ┌──────────┐            │
│  │  Auth   │  │  User   │  │Geographic│            │
│  │ Service │  │ Service │  │ Service  │            │
│  └─────────┘  └─────────┘  └──────────┘            │
│                                                       │
│  ┌─────────┐  ┌──────────┐                          │
│  │Incident │  │Analytics │                          │
│  │ Service │  │ Service  │                          │
│  └─────────┘  └──────────┘                          │
└───────┬───────────────────────────────────────────────┘
        │
┌───────▼──────────────────────────────────────────────┐
│              DATA PERSISTENCE LAYER                   │
│                                                       │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │ SQLite   │  │PostgreSQL│  │  PostGIS │          │
│  │  (Dev)   │  │  (Prod)  │  │  (Geo)   │          │
│  └──────────┘  └──────────┘  └──────────┘          │
└───────────────────────────────────────────────────────┘
```

### Architecture Principles

1. **Microservices Architecture**: Independent, deployable services
2. **Service Discovery**: Dynamic service registration and lookup
3. **API Gateway Pattern**: Single entry point for all client requests
4. **Database per Service**: Each service owns its data
5. **Stateless Services**: No session state in services (JWT-based auth)
6. **Containerization**: Docker for consistent deployment
7. **Eventual Consistency**: Distributed transactions not required

---

## 2. Microservices Design

### Service Catalog

#### Discovery Server (Port 8761)

**Technology**: Spring Boot + Netflix Eureka  
**Purpose**: Service registry and discovery

**Key Features**:
- Service registration on startup
- Health check monitoring (30s intervals)
- Service instance tracking
- Load balancing metadata
- Failover support

**Configuration**:
```yaml
server:
  port: 8761

eureka:
  instance:
    hostname: discovery-server
  client:
    registerWithEureka: false
    fetchRegistry: false
```

**Why Eureka?**:
- Industry-standard service discovery
- Built-in health checks
- Easy Spring Cloud integration
- Mature and battle-tested

---

#### API Gateway (Port 8080)

**Technology**: Spring Cloud Gateway  
**Purpose**: Single entry point, routing, authentication

**Key Responsibilities**:
1. **Request Routing**:
   ```
   /auth/*          → auth-service
   /api/users/*     → user-service
   /api/incidents/* → incident-service
   /api/geo/*       → geographic-service
   ```

2. **JWT Validation**:
   ```java
   // Extract and validate token
   String token = authHeader.substring(7);
   Claims claims = jwtUtil.getAllClaimsFromToken(token);
   
   // Inject user context
   .header("X-User-Id", claims.get("userId"))
   .header("X-User-Role", claims.get("role"))
   ```

3. **CORS Configuration**:
   ```yaml
   globalcors:
     cors-configurations:
       '[/**]':
         allowedOrigins: ["http://localhost:3000"]
         allowedMethods: ["GET", "POST", "PUT", "DELETE"]
   ```

**Filter Chain**:
```
Request → CORS Filter → Auth Filter → Route Filter → Service
```

**Why Spring Cloud Gateway?**:
- Reactive (high performance)
- Built-in filters
- Dynamic routing
- Service discovery integration

---

#### Auth Service (Port 8081)

**Technology**: Spring Boot + Spring Security + JWT  
**Purpose**: Authentication and token generation

**Authentication Flow**:
```
1. Client sends username/password
2. Auth service calls User Service to validate
3. Password verified using BCrypt
4. JWT token generated with:
   - userId
   - username
   - role
   - expiration
5. Token returned to client
```

**JWT Token Structure**:
```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "userId": 1,
    "username": "admin",
    "role": "SUPER_USER",
    "iat": 1709812800,
    "exp": 1709899200
  },
  "signature": "..."
}
```

**Security Implementation**:
```java
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
    
    @Bean
    public AuthenticationManager authenticationManager(...) {
        // Configure authentication
    }
}
```

**Why Separate Auth Service?**:
- Centralized authentication logic
- Easy to add OAuth2/SAML later
- Can be scaled independently
- Single point for security updates

---

#### User Service (Port 8085)

**Technology**: Spring Boot + Spring Data JPA + SQLite  
**Purpose**: User and profile management

**Domain Model**:
```java
User {
    id: Long
    username: String (unique)
    password: String (encrypted)
    email: String (unique)
    role: UserRole (enum)
    createdBy: Long
    isActive: boolean
    userProfile: UserProfile (one-to-one)
}

UserProfile {
    id: Long
    firstName: String
    lastName: String
    phone: String
    badgeNumber: String
    user: User
}
```

**Key Business Logic**:

1. **Hierarchical User Creation**:
```java
// SUPER_USER can create anyone
// POLICE_STATION can create OFFICER only
// OFFICER cannot create users

if (creator.role == SUPER_USER) {
    // Allow any role
} else if (creator.role == POLICE_STATION && newUser.role == OFFICER) {
    // Allow
    newUser.createdBy = creator.id;
} else {
    throw new ForbiddenException();
}
```

2. **Permission Validation**:
```java
private void validateUserUpdatePermission(Long updaterId, User targetUser) {
    User updater = userRepository.findById(updaterId);
    
    // SUPER_USER can update anyone
    if (updater.role == SUPER_USER) return;
    
    // POLICE_STATION can update own officers
    if (updater.role == POLICE_STATION 
        && targetUser.createdBy == updaterId) {
        return;
    }
    
    // Users can update themselves
    if (updaterId.equals(targetUser.id)) return;
    
    throw new ForbiddenException();
}
```

**Data Seeding**:
```java
// On first startup, create default users
if (userRepository.count() == 0) {
    createSuperUser("admin", "admin123");
    createStation("station_commander", "admin123");
    createOfficer("officer_001", "admin123", stationId);
}
```

---

#### Geographic Service (Port 8084)

**Technology**: Spring Boot + JTS Topology Suite + PostGIS-ready  
**Purpose**: Geospatial operations and boundary validation

**Geospatial Model**:
```java
UserProfile {
    id: Long
    userId: Long
    firstName: String
    lastName: String
    phone: String
    badgeNumber: String
    boundary: Polygon (JTS Geometry)
}
```

**Point-in-Polygon Algorithm**:
```java
public boolean isPointInBoundary(Long userId, Point point) {
    return userProfileRepository.findByUserId(userId)
        .map(profile -> 
            profile.getBoundary() != null && 
            profile.getBoundary().contains(point)
        )
        .orElse(false);
}
```

**Coordinate System**: WGS84 (SRID 4326)
- Latitude: -90 to 90
- Longitude: -180 to 180

**Boundary Representation**:
```json
{
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
```

**Why JTS?**:
- Industry-standard geospatial library
- Compatible with PostGIS
- Supports complex polygons
- High-performance algorithms

---

#### Incident Service (Port 8083)

**Technology**: Spring Boot + JTS + PostGIS  
**Purpose**: Crime incident reporting and management

**Domain Model**:
```java
Incident {
    id: Long
    title: String
    description: String (CLOB)
    incidentType: String
    priority: String
    location: Point (JTS Geometry)
    reportedBy: Long
    occurredAt: LocalDateTime (auto-generated)
}
```

**Business Logic**:

1. **Create Incident with Boundary Check**:
```java
public Incident createIncident(IncidentCreateRequest request, 
                               Long reporterId, 
                               String reporterRole) {
    // Create point from coordinates
    Point point = geometryFactory.createPoint(
        new Coordinate(request.getLongitude(), 
                      request.getLatitude())
    );
    
    // Validate boundary (unless SUPER_USER)
    validatePointInBoundary(reporterId, reporterRole, point);
    
    // Create incident
    Incident incident = new Incident();
    incident.setLocation(point);
    incident.setReportedBy(reporterId);
    // ... set other fields
    
    return incidentRepository.save(incident);
}
```

2. **Role-Based Filtering**:
```java
public List<Incident> getAllIncidents(Long userId, String userRole) {
    return switch (userRole) {
        case "SUPER_USER" -> 
            incidentRepository.findAll();
            
        case "POLICE_STATION" -> {
            // Get all officers from this station
            List<Long> userIds = userServiceClient
                .getOfficerIdsByStation(userId);
            userIds.add(userId); // Include station's own
            yield incidentRepository.findByReportedByIn(userIds);
        }
        
        case "OFFICER" -> 
            incidentRepository.findByReportedBy(userId);
            
        default -> Collections.emptyList();
    };
}
```

3. **Permission Validation**:
```java
private void validateUserPermission(Long userId, 
                                   Incident incident, 
                                   String action) {
    UserResponse user = userServiceClient.getUserById(userId);
    
    // SUPER_USER: can do anything
    if (user.role().equals("SUPER_USER")) return;
    
    // Owner: can modify own incident
    if (incident.getReportedBy().equals(userId)) return;
    
    // POLICE_STATION: can modify officers' incidents
    if (user.role().equals("POLICE_STATION")) {
        List<Long> officerIds = userServiceClient
            .getOfficerIdsByStation(userId);
        if (officerIds.contains(incident.getReportedBy())) {
            return;
        }
    }
    
    throw new ForbiddenException();
}
```

---

#### Analytics Service (Port 8086)

**Technology**: Python 3.13 + FastAPI + SQLAlchemy  
**Purpose**: Data analytics and statistics

**Why Python for Analytics?**:
- Rich data science libraries (NumPy, Pandas, Scikit-learn)
- Fast prototyping
- Easy to add ML models later
- Excellent for data processing

**Data Model**:
```python
class Incident(Base):
    __tablename__ = "incidents"
    
    id = Column(Integer, primary_key=True)
    title = Column(String, nullable=False)
    description = Column(String)
    incident_type = Column(String, index=True)
    priority = Column(String, index=True)
    latitude = Column(Float, nullable=False)
    longitude = Column(Float, nullable=False)
    reported_by = Column(Integer, nullable=False)
    occurred_at = Column(DateTime, default=datetime.utcnow)
```

**Analytics Queries**:

1. **Count by Type**:
```python
def get_incident_count_by_type(db: Session):
    return db.query(
        Incident.incident_type.label("name"),
        func.count(Incident.id).label("count")
    ).group_by(Incident.incident_type).all()
```

2. **Count by Priority**:
```python
def get_incident_count_by_priority(db: Session):
    return db.query(
        Incident.priority.label("name"),
        func.count(Incident.id).label("count")
    ).group_by(Incident.priority).all()
```

3. **Get Locations**:
```python
def get_all_incident_locations(db: Session):
    return db.query(
        Incident.latitude,
        Incident.longitude
    ).all()
```

**Future ML Capabilities**:
- Crime prediction models
- Hotspot detection algorithms
- Pattern recognition
- Resource allocation optimization

---

## 3. Security Architecture

### Multi-Layer Security

```
┌─────────────────────────────────────────┐
│   Layer 1: Network Security             │
│   • HTTPS/TLS                            │
│   • Firewall rules                       │
│   • VPN access                           │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│   Layer 2: API Gateway Security         │
│   • JWT validation                       │
│   • Rate limiting                        │
│   • CORS policy                          │
│   • Request sanitization                 │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│   Layer 3: Service-Level Security       │
│   • Role-based authorization             │
│   • Input validation                     │
│   • SQL injection prevention             │
│   • XSS protection                       │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│   Layer 4: Data Security                │
│   • Password encryption (BCrypt)         │
│   • Database encryption at rest          │
│   • Audit logging                        │
│   • Backup encryption                    │
└─────────────────────────────────────────┘
```

### Authentication Flow (Detailed)

```
┌──────────┐                              ┌──────────────┐
│  Client  │                              │ API Gateway  │
└────┬─────┘                              └──────┬───────┘
     │                                           │
     │ 1. POST /auth/login                      │
     │ {username, password}                     │
     ├──────────────────────────────────────────>
     │                                           │
     │                              ┌────────────▼────────────┐
     │                              │    Auth Service         │
     │                              │ 2. Validate credentials │
     │                              └────────────┬────────────┘
     │                                           │
     │                              ┌────────────▼────────────┐
     │                              │    User Service         │
     │                              │ 3. Get user by username │
     │                              │ 4. Verify password      │
     │                              └────────────┬────────────┘
     │                                           │
     │                              ┌────────────▼────────────┐
     │                              │    Auth Service         │
     │                              │ 5. Generate JWT token   │
     │                              └────────────┬────────────┘
     │                                           │
     │ 6. Return JWT token                      │
     │<─────────────────────────────────────────┤
     │ {token, userId, role}                    │
     │                                           │
     │ 7. Store token                           │
     │                                           │
     │ 8. Subsequent requests                   │
     │ Authorization: Bearer <token>            │
     ├──────────────────────────────────────────>
     │                                           │
     │                              9. Validate token
     │                              10. Extract userId & role
     │                              11. Inject headers:
     │                                  X-User-Id: 1
     │                                  X-User-Role: SUPER_USER
     │                                           │
     │                              ┌────────────▼────────────┐
     │                              │   Target Service        │
     │                              │ 12. Use headers for     │
     │                              │     authorization       │
     │                              └─────────────────────────┘
```

### JWT Token Security

**Token Generation**:
```java
public String generateToken(UserDTO user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", user.getId());
    claims.put("username", user.getUsername());
    claims.put("role", user.getRole());
    
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(user.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() 
                                + JWT_TOKEN_VALIDITY))
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact();
}
```

**Token Validation**:
```java
public Claims getAllClaimsFromToken(String token) {
    return Jwts.parser()
        .setSigningKey(secret)
        .parseClaimsJws(token)
        .getBody();
}
```

**Security Features**:
- HMAC-SHA256 signature
- Expiration time (configurable)
- Tamper-proof
- Stateless (no server-side storage)

---

## 4. Database Design

### Entity Relationship Diagram

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

### Database Schema

#### Users Table
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
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

#### User Profiles Table
```sql
CREATE TABLE user_profiles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
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

#### Incidents Table
```sql
CREATE TABLE incidents (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    incident_type VARCHAR(50) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    location GEOMETRY(POINT, 4326) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    reported_by INTEGER NOT NULL,
    occurred_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reported_by) REFERENCES users(id)
);

CREATE INDEX idx_incidents_type ON incidents(incident_type);
CREATE INDEX idx_incidents_priority ON incidents(priority);
CREATE INDEX idx_incidents_reporter ON incidents(reported_by);
CREATE INDEX idx_incidents_date ON incidents(occurred_at);
CREATE SPATIAL INDEX idx_incidents_location ON incidents(location);
```

### Database Migration Strategy

**Development**: SQLite
- Easy setup
- No separate server
- File-based storage
- Good for development/testing

**Production**: PostgreSQL + PostGIS
- Multi-user concurrent access
- Advanced geospatial features
- Better performance at scale
- ACID compliance
- Replication support

**Migration Path**:
```bash
# 1. Export data from SQLite
sqlite3 nisircop.db .dump > dump.sql

# 2. Transform for PostgreSQL
# Convert SQLite syntax to PostgreSQL

# 3. Import to PostgreSQL
psql nisircop_prod < dump_transformed.sql

# 4. Update application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/nisircop
    driver-class-name: org.postgresql.Driver
```

---

## 5. Communication Patterns

### Service-to-Service Communication

**Pattern**: Synchronous HTTP/REST via Feign Client

**Example: Incident Service → User Service**
```java
@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @GetMapping("/users/{id}")
    UserResponse getUserById(@PathVariable Long id);
    
    @GetMapping("/users/station/{stationId}/officers")
    List<Long> getOfficerIdsByStation(@PathVariable Long stationId);
}
```

**Why Feign?**:
- Declarative REST clients
- Automatic service discovery integration
- Load balancing
- Error handling
- Retry logic

### Request Flow Example

**Scenario**: Create incident with boundary validation

```
1. Client → API Gateway
   POST /api/v1/incidents
   Authorization: Bearer <token>
   
2. API Gateway
   • Validates JWT
   • Extracts userId=3, role=OFFICER
   • Adds headers: X-User-Id=3, X-User-Role=OFFICER
   • Routes to incident-service
   
3. Incident Service
   • Receives request with headers
   • Creates Point from coordinates
   • Calls Geographic Service:
     POST /geo/validate-point
     {userId: 3, lat: 9.032, lon: 38.746, role: OFFICER}
     
4. Geographic Service
   • Looks up user boundary
   • Performs point-in-polygon check
   • Returns: true/false
   
5. Incident Service
   • If valid: saves incident
   • If invalid: throws exception
   • Returns response to client
   
6. API Gateway
   • Returns response to client
```

### Error Propagation

```
Service Error → Feign Client → Calling Service → Gateway → Client

Example:
User Service: 404 Not Found
  ↓
Incident Service: RuntimeException("User not found")
  ↓
API Gateway: 400 Bad Request
  ↓
Client: {error: "User not found with id: 999"}
```

---

## 6. Deployment Architecture

### Docker Compose Deployment

**Network Architecture**:
```
┌──────────────────────────────────────────────┐
│       nisircop-network (Bridge)              │
│                                              │
│  ┌──────────┐    ┌──────────┐              │
│  │Discovery │    │   API    │              │
│  │  8761    │    │ Gateway  │              │
│  │          │    │   8080   │              │
│  └──────────┘    └──────────┘              │
│                                              │
│  ┌──────────┐    ┌──────────┐              │
│  │   Auth   │    │   User   │              │
│  │  8081    │    │  8085    │              │
│  └──────────┘    └──────────┘              │
│                                              │
│  ┌──────────┐    ┌──────────┐              │
│  │   Geo    │    │ Incident │              │
│  │  8084    │    │  8083    │              │
│  └──────────┘    └──────────┘              │
│                                              │
│  ┌──────────┐                               │
│  │Analytics │                               │
│  │  8086    │                               │
│  └──────────┘                               │
└──────────────────────────────────────────────┘
         │
    ┌────▼────┐
    │  Data   │
    │ Volume  │
    └─────────┘
```

**Container Configuration**:

```yaml
version: '3.8'

services:
  discovery-server:
    build: ./discovery-server
    ports:
      - "8761:8761"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8761/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
    restart: unless-stopped
    
  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    depends_on:
      discovery-server:
        condition: service_healthy
    environment:
      - JWT_SECRET=${JWT_SECRET}
    restart: unless-stopped
    
  # Other services...

volumes:
  nisircop-data:
    driver: local
    
networks:
  nisircop-network:
    driver: bridge
```

### Production Deployment (Kubernetes)

**Future Architecture**:

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: nisircop-le

---

apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-gateway
  namespace: nisircop-le
spec:
  replicas: 3
  selector:
    matchLabels:
      app: api-gateway
  template:
    metadata:
      labels:
        app: api-gateway
    spec:
      containers:
      - name: api-gateway
        image: nisircop/api-gateway:latest
        ports:
        - containerPort: 8080
        env:
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: jwt-secret
              key: secret
        resources:
          limits:
            memory: "512Mi"
            cpu: "500m"
          requests:
            memory: "256Mi"
            cpu: "250m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10

---

apiVersion: v1
kind: Service
metadata:
  name: api-gateway
  namespace: nisircop-le
spec:
  type: LoadBalancer
  ports:
  - port: 80
    targetPort: 8080
  selector:
    app: api-gateway
```

---

## 7. Design Decisions

### Why Microservices?

**Decision**: Use microservices architecture instead of monolith

**Rationale**:
1. **Scalability**: Scale services independently based on load
2. **Technology Diversity**: Java for business logic, Python for analytics
3. **Team Organization**: Different teams can work on different services
4. **Fault Isolation**: One service failure doesn't bring down the system
5. **Deployment**: Deploy services independently without downtime

**Trade-offs**:
- ✅ Better scalability
- ✅ Independent deployment
- ✅ Technology flexibility
- ❌ Increased complexity
- ❌ More network calls
- ❌ Distributed debugging harder

### Why JWT Instead of Sessions?

**Decision**: Stateless JWT authentication

**Rationale**:
1. **Scalability**: No server-side session storage
2. **Microservices-Friendly**: Token contains all needed info
3. **Mobile-Ready**: Works well with mobile apps
4. **Distributed**: No session replication needed

**Trade-offs**:
- ✅ Stateless (scales easily)
- ✅ Self-contained
- ✅ No database lookups
- ❌ Cannot revoke tokens immediately
- ❌ Larger than session IDs
- ❌ Token refresh complexity

### Why SQLite for Development?

**Decision**: Use SQLite in development, PostgreSQL in production

**Rationale**:
1. **Easy Setup**: No database server needed
2. **Fast Development**: File-based, no configuration
3. **Testing**: Each test can have its own database
4. **CI/CD**: Easy to run tests

**Migration Plan**: Documented path to PostgreSQL

### Why Service Discovery (Eureka)?

**Decision**: Use Netflix Eureka for service discovery

**Rationale**:
1. **Dynamic Registration**: Services register automatically
2. **Health Checks**: Automatic monitoring
3. **Load Balancing**: Built-in client-side load balancing
4. **Fault Tolerance**: Handles service failures gracefully

**Alternative Considered**: Consul
- Eureka is simpler for our use case
- Better Spring Cloud integration
- Proven in production

---

## 8. Scalability Considerations

### Horizontal Scaling

**Services That Can Scale**:

1. **API Gateway**: Multiple instances behind load balancer
```bash
docker-compose up -d --scale api-gateway=3
```

2. **Auth Service**: Multiple instances (stateless)
3. **User Service**: Multiple instances (read-heavy)
4. **Incident Service**: Multiple instances
5. **Analytics Service**: Multiple instances

**Services That Shouldn't Scale** (in current design):
- Discovery Server (single registry)

### Performance Optimization

**Database Level**:
```sql
-- Add indexes for common queries
CREATE INDEX idx_incidents_date ON incidents(occurred_at);
CREATE INDEX idx_incidents_type_priority 
  ON incidents(incident_type, priority);

-- Analyze queries
EXPLAIN QUERY PLAN SELECT ...;
```

**Application Level**:
```java
// Use pagination for large results
Page<Incident> findAll(Pageable pageable);

// Lazy loading for related entities
@OneToOne(fetch = FetchType.LAZY)
private UserProfile userProfile;

// Caching for frequently accessed data
@Cacheable("users")
public User getUserById(Long id) {...}
```

**Connection Pooling**:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

### Load Testing

**Expected Load**:
- 1000 concurrent users
- 100 requests/second average
- 500 requests/second peak

**Performance Targets**:
- Average response time: < 200ms
- 95th percentile: < 500ms
- 99th percentile: < 1000ms
- Error rate: < 0.1%

---

## 9. Monitoring and Observability

### Health Checks

All services expose:
```
GET /actuator/health
GET /actuator/metrics
GET /actuator/info
```

**Health Check Response**:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "SQLite",
        "validationQuery": "SELECT 1"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 336365142016,
        "threshold": 10485760
      }
    },
    "eureka": {
      "status": "UP"
    }
  }
}
```

### Logging Strategy

**Log Levels**:
- ERROR: System errors, exceptions
- WARN: Potential issues, deprecated usage
- INFO: Important business events
- DEBUG: Detailed debugging information

**Log Format** (JSON for easy parsing):
```json
{
  "timestamp": "2025-10-07T10:30:00.123Z",
  "level": "INFO",
  "service": "incident-service",
  "traceId": "abc123",
  "userId": 3,
  "action": "CREATE_INCIDENT",
  "message": "Incident created successfully",
  "incidentId": 42
}
```

### Metrics

**Key Metrics to Monitor**:
- Request rate (requests/second)
- Response time (average, p95, p99)
- Error rate (errors/total requests)
- JVM memory usage
- Database connection pool usage
- Service registration status

---

## 10. Future Architecture Enhancements

### Phase 2: Message Queue

Add asynchronous communication:

```
┌──────────┐     ┌─────────┐     ┌──────────┐
│ Incident │────>│ RabbitMQ│────>│Analytics │
│ Service  │     │  Queue  │     │ Service  │
└──────────┘     └─────────┘     └──────────┘
                       │
                       v
                 ┌──────────┐
                 │  Email   │
                 │ Service  │
                 └──────────┘
```

**Benefits**:
- Decoupled services
- Better fault tolerance
- Event-driven architecture
- Real-time notifications

### Phase 3: Cache Layer

Add Redis for performance:

```
┌─────────┐     ┌───────┐     ┌──────────┐
│ Request │────>│ Redis │────>│ Database │
│         │<────│ Cache │<────│          │
└─────────┘     └───────┘     └──────────┘
```

**Cache Strategy**:
- User profiles (rarely change)
- Geographic boundaries (rarely change)
- Analytics data (5-minute TTL)

### Phase 4: Machine Learning Pipeline

```
┌──────────┐     ┌──────────┐     ┌──────────┐
│ Incident │────>│   ML     │────>│ Prediction│
│   Data   │     │  Model   │     │  Service │
└──────────┘     └──────────┘     └──────────┘
```

**ML Capabilities**:
- Crime prediction
- Hotspot detection
- Resource allocation
- Pattern recognition

---

## Conclusion

NISIRCOP-LE demonstrates a well-architected, production-ready microservices system with:

✅ **Scalable Architecture**: Independent services, horizontal scaling  
✅ **Security Best Practices**: JWT, encryption, RBAC  
✅ **Modern Technology Stack**: Spring Boot, FastAPI, Docker  
✅ **Geographic Intelligence**: JTS, PostGIS-ready  
✅ **Service Discovery**: Eureka for dynamic registration  
✅ **Maintainability**: Clean code, separation of concerns  
✅ **Future-Ready**: Clear path for enhancements  

The system is designed to grow with the organization's needs while maintaining reliability, security, and performance.

---

**Document Version**: 1.0  
**Last Updated**: 2025-10-07  
**For**: NISIRCOP-LE Technical Presentation
