from fastapi import FastAPI, Depends, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session
from . import crud, models, schemas
from .database import SessionLocal, engine
import os
import logging

# Optional Eureka client
try:
    from py_eureka_client import eureka_client
except Exception:
    eureka_client = None

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

models.Base.metadata.create_all(bind=engine)

app = FastAPI(title="NISIRCOP Analytics Service", version="1.0.0")

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Configure appropriately for production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Dependency
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


@app.get("/health")
def health_check():
    return {"status": "ok"}


@app.get("/analytics/incidents/count-by-type", response_model=list[schemas.IncidentCount])
def get_incident_count_by_type(db: Session = Depends(get_db)):
    try:
        return crud.get_incident_count_by_type(db)
    except Exception as e:
        logger.error(f"Error getting incident count by type: {e}")
        raise HTTPException(status_code=500, detail="Internal server error")


@app.get("/analytics/incidents/count-by-priority", response_model=list[schemas.IncidentCount])
def get_incident_count_by_priority(db: Session = Depends(get_db)):
    try:
        return crud.get_incident_count_by_priority(db)
    except Exception as e:
        logger.error(f"Error getting incident count by priority: {e}")
        raise HTTPException(status_code=500, detail="Internal server error")


@app.get("/analytics/incidents/locations", response_model=list[schemas.IncidentLocation])
def get_incident_locations(db: Session = Depends(get_db)):
    try:
        return crud.get_all_incident_locations(db)
    except Exception as e:
        logger.error(f"Error getting incident locations: {e}")
        raise HTTPException(status_code=500, detail="Internal server error")


@app.on_event("startup")
def register_with_eureka():
    """Register this service with a Eureka server if configured via EUREKA_SERVER_URL env var."""
    eureka_server = os.getenv("EUREKA_SERVER_URL")
    if not eureka_server or eureka_client is None:
        logger.info("Eureka client not available or server URL not configured")
        return

    # Build instance metadata
    app_name = os.getenv("EUREKA_APP_NAME", "ANALYTICS-SERVICE")
    instance_port = int(os.getenv("EUREKA_INSTANCE_PORT", os.getenv("PORT", "8086")))
    instance_host = os.getenv("EUREKA_INSTANCE_HOST", "")

    try:
        # Use the high-level init function which starts a background thread
        eureka_client.init(eureka_server=eureka_server,
                           app_name=app_name,
                           instance_port=instance_port,
                           instance_host=instance_host)
        # store a marker that we initialized
        app.state.eureka_initialized = True
        logger.info(f"EUREKA: initialized client for {app_name} -> {eureka_server}")
    except Exception as e:
        # don't crash the app if registration fails; check logs
        app.state.eureka_initialized = False
        logger.error(f"EUREKA: init failed: {e}")


@app.on_event("shutdown")
def unregister_from_eureka():
    if eureka_client is not None and getattr(app.state, "eureka_initialized", False):
        try:
            eureka_client.stop()
            logger.info("EUREKA: stopped client")
        except Exception as e:
            logger.error(f"EUREKA: stop failed: {e}")