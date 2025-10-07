# NISIRCOP-LE Quick Reference Guide

## Service Ports

| Service | Port | URL | Health Check |
|---------|------|-----|--------------|
| Frontend | 3000 | http://localhost:3000 | - |
| API Gateway | 8080 | http://localhost:8080 | http://localhost:8080/actuator/health |
| Discovery Server | 8761 | http://localhost:8761 | http://localhost:8761/actuator/health |
| Auth Service | 8081 | http://localhost:8081 | http://localhost:8081/actuator/health |
| Incident Service | 8083 | http://localhost:8083 | http://localhost:8083/actuator/health |
| Geographic Service | 8084 | http://localhost:8084 | http://localhost:8084/actuator/health |
| User Service | 8085 | http://localhost:8085 | http://localhost:8085/actuator/health |
| Analytics Service | 8086 | http://localhost:8086 | http://localhost:8086/health |

## API Routes

All routes go through the API Gateway (port 8080):

| Endpoint | Service | Authentication Required |
|----------|---------|------------------------|
| `/auth/**` | Auth Service | No |
| `/users/**` | User Service | Yes |
| `/geo/**` | Geographic Service | Yes |
| `/incidents/**` | Incident Service | Yes |
| `/analytics/**` | Analytics Service | Yes |

## Default Credentials

| Role | Username | Password |
|------|----------|----------|
| SUPER_USER | admin | admin123 |
| POLICE_STATION | station_commander | admin123 |
| OFFICER | officer_001 | admin123 |

⚠️ **Change these in production!**

## Startup Order (Local Development)

1. **Discovery Server** (Port 8761) - Wait 30 seconds
2. **API Gateway** (Port 8080)
3. **Auth Service** (Port 8081)
4. **User Service** (Port 8085)
5. **Geographic Service** (Port 8084)
6. **Incident Service** (Port 8083)
7. **Analytics Service** (Port 8086)
8. **Frontend** (Port 3000)

## Quick Commands

### Docker Deployment
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Rebuild and restart
docker-compose up --build -d
```

### Local Development
```bash
# Start individual service
./scripts/run-[service-name].sh

# Example: Start discovery server
./scripts/run-discovery-server.sh

# Frontend
./scripts/run-frontend.sh
```

### Health Checks
```bash
# Check all services
curl http://localhost:8761  # Eureka - see all services
curl http://localhost:8080/actuator/health  # API Gateway
curl http://localhost:8081/actuator/health  # Auth Service
curl http://localhost:8085/actuator/health  # User Service
curl http://localhost:8084/actuator/health  # Geographic Service
curl http://localhost:8083/actuator/health  # Incident Service
curl http://localhost:8086/health           # Analytics Service
```

### Test API
```bash
# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Get users (requires token)
curl http://localhost:8080/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Create incident (requires token)
curl -X POST http://localhost:8080/incidents \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Incident",
    "description": "Test Description",
    "latitude": 9.0320,
    "longitude": 38.7469,
    "occurredAt": "2025-10-07T10:00:00"
  }'
```

## Environment Variables

### Backend Services
```bash
JWT_SECRET=your-secret-key
SPRING_PROFILES_ACTIVE=local  # or docker
DB_PATH=./data/nisircop.db
EUREKA_SERVER_URL=http://localhost:8761/eureka
```

### Frontend
```bash
VITE_API_URL=http://localhost:8080
```

## Common Issues & Solutions

### Issue: Service won't start
- Check if port is already in use: `netstat -tuln | grep PORT_NUMBER`
- Kill process on port: `kill -9 $(lsof -t -i:PORT_NUMBER)`

### Issue: Service not registered with Eureka
- Wait 30 seconds after starting Discovery Server
- Check Eureka dashboard: http://localhost:8761
- Verify EUREKA_SERVER_URL environment variable

### Issue: Frontend can't connect to backend
- Verify API Gateway is running: `curl http://localhost:8080/actuator/health`
- Check CORS configuration in API Gateway
- Verify VITE_API_URL in frontend/.env.local

### Issue: Database errors
- Check data directory exists: `ls -la data/`
- Verify permissions: `chmod 755 data/`
- Initialize database: `rm data/nisircop.db` (will recreate on startup)

## Development Workflow

### Making Changes

1. **Frontend Changes:**
   ```bash
   cd frontend
   # Edit files - changes hot-reload automatically
   npm run build  # Build for production
   ```

2. **Backend Changes:**
   ```bash
   cd [service-name]
   # Edit Java files
   mvn clean install  # Rebuild
   # Restart the service
   ```

3. **Testing:**
   ```bash
   # Test frontend
   cd frontend && npm run dev
   
   # Test backend
   mvn test
   
   # Integration test
   # Start all services and test via browser at http://localhost:3000
   ```

## Useful Links

- **Frontend:** http://localhost:3000
- **Eureka Dashboard:** http://localhost:8761
- **API Gateway:** http://localhost:8080
- **GitHub Repository:** https://github.com/silehacks/NISIRCOP

## Support

For issues or questions:
1. Check logs: `docker-compose logs [service-name]` or check terminal output
2. Verify service health: `curl http://localhost:PORT/actuator/health`
3. Check Eureka: http://localhost:8761
4. Review documentation: README.md