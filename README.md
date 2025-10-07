# NISIRCOP-LE: Crime Analytics Platform for Ethiopian Law Enforcement

<div align="center">

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/java-17+-red.svg)
![Python](https://img.shields.io/badge/python-3.11+-green.svg)
![Docker](https://img.shields.io/badge/docker-compose-blue.svg)
![Status](https://img.shields.io/badge/status-production--ready-brightgreen.svg)

**A comprehensive digital platform transforming crime reporting and intelligence for Ethiopian police forces**

</div>

## 🎯 Overview

NISIRCOP-LE (NISIR Common Operational Picture For Law Enforcement) is a production-ready microservices platform designed to modernize crime reporting, analysis, and intelligence for Ethiopian law enforcement agencies. It transforms traditional paper-based processes into a sophisticated digital ecosystem that enables real-time monitoring, data-driven decision making, and strategic crime prevention.

### Key Value Propositions

- **🚀 Real-time Crime Intelligence**: Instant incident reporting with GPS coordinates and automated analytics
- **🗺️ Interactive Crime Mapping**: Geospatial visualization with hotspot analysis and boundary validation
- **📊 Advanced Analytics**: Python-powered data analysis with machine learning capabilities
- **🔐 Hierarchical Security**: Role-based access control mirroring police organizational structure
- **⚡ High Performance**: Optimized microservices architecture with health monitoring
- **🐳 Production Ready**: Containerized deployment with comprehensive monitoring and logging

## 🏗️ System Architecture

### Technology Stack

| Component | Technology | Purpose |
|-----------|------------|---------|
| **Backend** | Java 17, Spring Boot 3.2+ | Microservices foundation |
| **Analytics** | Python 3.11, FastAPI | High-performance data processing |
| **Database** | SQLite (dev), PostgreSQL (prod) | Data persistence |
| **Service Discovery** | Netflix Eureka | Dynamic service registration |
| **API Gateway** | Spring Cloud Gateway | Unified entry point & routing |
| **Security** | JWT, Spring Security | Authentication & authorization |
| **Containerization** | Docker, Docker Compose | Deployment & orchestration |
| **Monitoring** | Spring Actuator | Health checks & metrics |

### Microservices Architecture

```
┌─────────────────┐    ┌─────────────────┐
│   Frontend      │◄───┤  API Gateway    │
│   (Removed)     │    │   Port: 8080    │
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
- **Permissions**: Full system access, can create POLICE_STATION accounts
- **Capabilities**: 
  - View all crime data across jurisdictions
  - Access strategic analytics and reports
  - Report incidents anywhere (bypasses boundary validation)
  - Manage system-wide configurations

### 🏢 POLICE_STATION (Station Commander)
- **Permissions**: Station-level management and oversight
- **Capabilities**:
  - Create and manage OFFICER accounts within station
  - View all incidents within station's geographic boundary
  - Edit/delete incidents reported by their officers
  - Access station-level analytics and reports

### 👮 OFFICER (Field Officer)
- **Permissions**: Field reporting and personal data access
- **Capabilities**:
  - Report new incidents from the field
  - View only their own incident reports
  - Access mobile-optimized interface
  - Cannot create users or access others' data

## 🚀 Quick Start Guide

### Prerequisites

Ensure your system has the following installed:

- **Docker Engine** 20.10+ and **Docker Compose** 2.0+
- **Git** for cloning the repository
- **Minimum 4GB RAM** and **2 CPU cores** for optimal performance
- **10GB free disk space** for containers and data

### Installation

#### 1. Clone Repository
```bash
git clone https://github.com/your-org/nisircop-le.git
cd nisircop-le
```

#### 2. Environment Configuration
```bash
# Copy and configure environment variables
cp .env.example .env

# Edit .env file with your specific configuration
# CRITICAL: Change JWT_SECRET for production!
vim .env
```

**⚠️ Production Security**: Generate a secure JWT secret:
```bash
openssl rand -base64 32
```

#### 3. Create Data Directory
```bash
mkdir -p data
chmod 755 data
```

### Deployment Options

#### Option A: Development Environment
```bash
# Start all services with development settings
docker-compose up --build -d

# View logs
docker-compose logs -f
```

#### Option B: Production Environment
```bash
# Start with production optimizations
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up --build -d

# Enable resource limits and health checks
docker-compose ps
```

#### Option C: Individual Service Development
```bash
# Start only core infrastructure
docker-compose up -d discovery-server

# Scale specific services
docker-compose up -d auth-service user-service
```

### 4. Verify Deployment

#### Health Checks
```bash
# Check all services status
docker-compose ps

# Verify service health
curl http://localhost:8761/actuator/health  # Discovery Server
curl http://localhost:8080/actuator/health  # API Gateway
curl http://localhost:8081/actuator/health  # Auth Service
```

#### Service Discovery
Visit http://localhost:8761 to see all registered services in Eureka dashboard.

## 🔧 Configuration Management

### Environment Variables

| Variable | Description | Default | Production Notes |
|----------|-------------|---------|------------------|
| `JWT_SECRET` | JWT signing key | `dev-secret` | ⚠️ **Must change for production** |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `docker` | Use `prod` for production |
| `DB_PATH` | Database file path | `./data/nisircop.db` | Ensure persistent volume |
| `EUREKA_SERVER_URL` | Service discovery URL | Auto-configured | Network-specific |

### Production Configuration Files

#### Spring Boot Profiles
- **Development**: Default logging, SQL debugging enabled
- **Docker**: Container-optimized settings
- **Production**: Performance tuned, minimal logging

#### Database Configuration
```yaml
# Development (SQLite)
spring:
  datasource:
    url: jdbc:sqlite:./data/nisircop.db
    driver-class-name: org.sqlite.JDBC

# Production (PostgreSQL + PostGIS)
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/nisircop
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

## 🛡️ Security Features

### Authentication & Authorization
- **JWT Token-based Authentication**: Stateless, scalable authentication
- **Role-based Access Control (RBAC)**: Hierarchical permission system
- **Automatic Token Refresh**: Seamless user experience
- **Session Management**: Secure token storage and cleanup

### API Security
- **CORS Configuration**: Restricted cross-origin requests
- **Input Validation**: Comprehensive request validation
- **SQL Injection Prevention**: JPA/Hibernate protection
- **Rate Limiting Ready**: Configurable request throttling

### Data Protection
- **Boundary Validation**: Geographic access control
- **Audit Logging**: User action tracking
- **Error Sanitization**: Secure error responses
- **Encrypted Communications**: HTTPS-ready configuration

## 📊 Monitoring & Observability

### Health Monitoring
All services expose health endpoints via Spring Actuator:

```bash
# Service health status
GET /actuator/health

# Detailed metrics
GET /actuator/metrics

# Service information
GET /actuator/info

# Eureka registration status
GET /actuator/eureka
```

### Logging Strategy
- **Structured Logging**: JSON format for log aggregation
- **Log Levels**: Configurable per environment
- **Security Logging**: Authentication and authorization events
- **Performance Logging**: Request timing and resource usage

### Docker Health Checks
```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 5
  start_period: 60s
```

## 🚀 Production Deployment

### Resource Requirements

#### Minimum Production Setup
- **CPU**: 4 cores
- **Memory**: 8GB RAM
- **Storage**: 50GB SSD
- **Network**: 100Mbps

#### Recommended Production Setup
- **CPU**: 8 cores
- **Memory**: 16GB RAM
- **Storage**: 200GB NVMe SSD
- **Network**: 1Gbps
- **Load Balancer**: Nginx/HAProxy

### Production Checklist

#### Security
- [ ] Change default JWT_SECRET
- [ ] Enable HTTPS/TLS termination
- [ ] Configure firewall rules
- [ ] Set up VPN access
- [ ] Enable audit logging
- [ ] Configure backup encryption

#### Performance
- [ ] Enable JVM performance tuning
- [ ] Configure connection pooling
- [ ] Set up database indexing
- [ ] Enable response caching
- [ ] Configure load balancing

#### Reliability
- [ ] Set up data backups
- [ ] Configure monitoring alerts
- [ ] Test disaster recovery
- [ ] Implement log rotation
- [ ] Set up service redundancy

### Scaling Strategies

#### Horizontal Scaling
```bash
# Scale specific services
docker-compose up -d --scale auth-service=3
docker-compose up -d --scale incident-service=2
```

#### Database Scaling
- **Read Replicas**: For analytics workloads
- **Connection Pooling**: HikariCP optimization
- **Indexing Strategy**: Geographic and temporal indexes

## 🔍 API Documentation

### Core Endpoints

#### Authentication
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "secure_password"
}
```

#### User Management
```http
GET    /api/users                 # List users (role-based filtering)
POST   /api/users                 # Create user (hierarchy enforced)
PUT    /api/users/{id}             # Update user
DELETE /api/users/{id}             # Delete user
```

#### Incident Management
```http
GET    /api/incidents              # List incidents (boundary filtered)
POST   /api/incidents              # Report incident
PUT    /api/incidents/{id}         # Update incident
DELETE /api/incidents/{id}         # Delete incident
```

#### Analytics
```http
GET    /api/analytics/summary      # Crime statistics
GET    /api/analytics/hotspots     # Geographic hotspots
GET    /api/analytics/trends       # Temporal patterns
```

### Default Credentials

| Role | Username | Password | Access Level |
|------|----------|----------|--------------|
| SUPER_USER | `admin` | `admin123` | Full system access |
| POLICE_STATION | `station_commander` | `admin123` | Station management |
| OFFICER | `officer_001` | `admin123` | Field reporting |

**⚠️ CRITICAL**: Change these credentials immediately in production!

## 🧪 Testing Strategy

### Automated Testing
```bash
# Run all tests
./scripts/run-tests.sh

# Unit tests
mvn test

# Integration tests
mvn integration-test

# API tests
./scripts/api-test.sh
```

### Manual Testing Checklist

#### Authentication Flow
- [ ] Login with each role type
- [ ] JWT token validation
- [ ] Session timeout handling
- [ ] Logout functionality

#### User Management
- [ ] Role-based user creation
- [ ] Hierarchy enforcement
- [ ] Permission validation
- [ ] Profile management

#### Incident Reporting
- [ ] Geographic validation
- [ ] Form validation
- [ ] File uploads
- [ ] Search and filtering

#### Analytics Dashboard
- [ ] Chart rendering
- [ ] Data accuracy
- [ ] Real-time updates
- [ ] Export functionality

## 🔧 Troubleshooting

### Common Issues

#### Service Discovery Problems
```bash
# Check Eureka dashboard
curl http://localhost:8761/

# Verify service registration
docker-compose logs discovery-server

# Restart discovery service
docker-compose restart discovery-server
```

#### Database Connection Issues
```bash
# Check database file permissions
ls -la data/

# Verify SQLite integrity
sqlite3 data/nisircop.db "PRAGMA integrity_check;"

# Reset database
rm data/nisircop.db && docker-compose restart
```

#### Performance Issues
```bash
# Check container resources
docker stats

# Review JVM memory usage
docker-compose logs auth-service | grep -i "heap\|memory"

# Monitor database queries
tail -f data/query.log
```

### Log Analysis
```bash
# Service-specific logs
docker-compose logs -f [service-name]

# Error aggregation
docker-compose logs | grep -i "error\|exception"

# Performance monitoring
docker-compose logs | grep -i "slow\|timeout"
```

## 📈 Performance Optimization

### JVM Tuning
```bash
# Production JVM settings (in docker-compose.prod.yml)
JAVA_OPTS: "-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### Database Optimization
```sql
-- Create performance indexes
CREATE INDEX idx_incidents_location ON incidents(latitude, longitude);
CREATE INDEX idx_incidents_date ON incidents(occurred_at);
CREATE INDEX idx_users_role ON users(role);
```

### Caching Strategy
- **Application Level**: Spring Cache abstraction
- **Database Level**: Query result caching
- **HTTP Level**: Response caching headers

## 🚀 Migration from Development to Production

### Database Migration
```bash
# Export development data
./scripts/export-dev-data.sh

# Set up PostgreSQL production database
docker run -d --name postgres-prod \
  -e POSTGRES_DB=nisircop \
  -e POSTGRES_USER=nisircop \
  -e POSTGRES_PASSWORD=secure_password \
  -p 5432:5432 \
  postgis/postgis:15-3.3

# Import data to production
./scripts/import-prod-data.sh
```

### Configuration Updates
```bash
# Update application.yml for production
sed -i 's/sqlite/postgresql/g' */src/main/resources/application.yml

# Update docker-compose for production database
cp docker-compose.prod-db.yml docker-compose.override.yml
```

## 📚 Additional Resources

### Documentation
- [API Reference](./docs/api-reference.md)
- [Deployment Guide](./docs/deployment-guide.md)
- [Security Best Practices](./docs/security-guide.md)
- [Monitoring Setup](./docs/monitoring-guide.md)

### Community
- **Issues**: [GitHub Issues](https://github.com/your-org/nisircop-le/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-org/nisircop-le/discussions)
- **Contributing**: [Contributing Guidelines](./CONTRIBUTING.md)

### Support
- **Email**: support@nisircop-le.com
- **Documentation**: [User Manual](./docs/user-manual.pdf)
- **Training**: [Video Tutorials](https://training.nisircop-le.com)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🎯 Roadmap

### Current Version (v1.0.0)
- ✅ Core microservices architecture
- ✅ Role-based access control
- ✅ Crime incident reporting
- ✅ Basic analytics dashboard
- ✅ SQLite development setup
- ✅ Docker containerization

### Future Enhancements (v2.0.0)
- 🔄 PostgreSQL + PostGIS migration
- 🔄 Advanced geospatial analytics
- 🔄 Mobile application (iOS/Android)
- 🔄 Real-time notifications
- 🔄 Machine learning predictions
- 🔄 Integration with external systems

### Enterprise Features (v3.0.0)
- 🔄 Multi-tenant architecture
- 🔄 Advanced reporting engine
- 🔄 Workflow management
- 🔄 Document management
- 🔄 Advanced security features
- 🔄 Third-party integrations

---

<div align="center">

**Built with ❤️ for Ethiopian Law Enforcement**

*Transforming crime fighting through technology*

</div>