# Changelog

All notable changes to the NISIRCOP-LE project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- In-memory H2 database support for `geographic-service` and `incident-service` for local development.
- `spring-dotenv` dependency to `api-gateway` to load environment variables from `.env` file.

### Changed
- Updated `README.md` with manual startup instructions for all services.
- Updated `API_GUIDE.md` to reflect that the analytics service should be accessed through the API Gateway.
- Corrected database configuration for `geographic-service` and `incident-service` to use H2 for the `local` profile.
- Created `data` directories for `user-service`, `geographic-service`, and `incident-service` to allow for database creation.

### Removed
- All shell scripts from the `scripts` directory.

## [1.0.0] - Production Ready Release

### Added
- 🏗️ **Complete microservices architecture** with 7 core services
- 🔐 **Hierarchical role-based access control** (SUPER_USER, POLICE_STATION, OFFICER)
- 🗺️ **Geographic incident reporting** with boundary validation
- 📊 **Python analytics service** with FastAPI for high-performance data processing
- 🐳 **Production-ready Docker setup** with health checks and resource limits
- 🛡️ **Enhanced security configurations** with JWT authentication
- 📈 **Comprehensive monitoring** with Spring Actuator health endpoints
- 🚀 **Performance optimizations** with JVM tuning and connection pooling
- 📚 **Extensive documentation** with deployment and troubleshooting guides

### Changed
- 🔄 **Migrated from PostgreSQL to SQLite** for simplified development setup
- 🎨 **Improved configuration management** with environment-specific profiles
- 🛡️ **Tightened CORS policies** for enhanced security
- 📝 **Disabled SQL logging** in production configurations
- 🏥 **Added comprehensive health checks** for all services

### Removed
- ❌ **Frontend application** - focus on backend microservices
- ❌ **Test scripts and files** - cleaned up for production
- ❌ **Development documentation** - consolidated into main README
- ❌ **Unwanted configuration files** - streamlined setup

### Security
- 🔒 **JWT secret configuration** with environment variables
- 🛡️ **Input validation** across all API endpoints
- 🔐 **Role-based endpoint protection**
- 📝 **Audit logging** for security events
- 🚫 **SQL injection prevention** with JPA/Hibernate

### Performance
- ⚡ **JVM memory optimization** for container environments
- 🗄️ **Database connection pooling** with HikariCP
- 📊 **Efficient analytics processing** with Python FastAPI
- 🔄 **Service discovery optimization** with Eureka
- 🏥 **Health check optimization** for faster startup

### Infrastructure
- 🐳 **Multi-stage Docker builds** for optimized images
- 🔧 **Production Docker Compose** with resource limits
- 🌐 **Service networking** with dedicated Docker networks
- 📁 **Persistent data volumes** for database storage
- 🔄 **Automatic service restart** policies

## Previous Versions

### [0.x.x] - Development Versions
- Initial development and testing phases
- PostgreSQL database setup
- Basic microservices implementation
- Frontend Vue.js application
- Initial Docker containerization

---

For detailed information about upcoming releases, see our [Roadmap](README.md#roadmap).