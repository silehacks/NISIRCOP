# 🚀 PRODUCTION DEPLOYMENT GUIDE

## 🔒 SECURITY-FIRST DEPLOYMENT

### Step 1: Secure Configuration
```bash
# Generate secure secrets
JWT_SECRET=$(openssl rand -base64 32)
ADMIN_PASSWORD=$(openssl rand -base64 16)

# Create production environment file
cp .env.production .env.prod
```

### Step 2: Update Production Settings
```bash
# Edit .env.prod with your values
JWT_SECRET="your-generated-jwt-secret"
DEFAULT_ADMIN_PASSWORD="your-secure-admin-password"
CORS_ALLOWED_ORIGINS="https://your-domain.com"
SPRING_PROFILES_ACTIVE=prod
```

### Step 3: Production Deployment
```bash
# Deploy with production configuration
docker-compose -f docker-compose.yml -f docker-compose.prod.yml --env-file .env.prod up -d

# Verify all services are healthy
docker-compose ps
curl http://localhost:8761  # Eureka dashboard
```

## 📊 MONITORING SETUP

### Health Checks
```bash
# Check all service health
for port in 8080 8081 8083 8084 8085 8086; do
  echo "Checking port $port..."
  curl -f http://localhost:$port/actuator/health || echo "FAILED"
done
```

### Log Monitoring
```bash
# View aggregated logs
docker-compose logs -f --tail=100

# Monitor specific service
docker-compose logs -f auth-service
```

## 🔧 MAINTENANCE TASKS

### Database Backup
```bash
# Backup SQLite database
cp data/nisircop.db backups/nisircop-$(date +%Y%m%d).db

# For PostgreSQL (future)
pg_dump nisircop > backups/nisircop-$(date +%Y%m%d).sql
```

### Security Updates
```bash
# Update base images
docker-compose pull
docker-compose up -d --build

# Rotate JWT secret (if compromised)
# 1. Generate new secret
# 2. Update .env.prod
# 3. Restart all services
```

---

**Status**: ✅ READY FOR PRODUCTION DEPLOYMENT