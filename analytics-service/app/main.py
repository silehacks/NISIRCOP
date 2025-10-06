from fastapi import FastAPI, Depends
from sqlalchemy.orm import Session
from . import crud, models, schemas
from .database import SessionLocal, engine
import os

# Optional Eureka client
try:
    from py_eureka_client import eureka_client
except Exception:
    eureka_client = None

models.Base.metadata.create_all(bind=engine)

app = FastAPI()


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
    return crud.get_incident_count_by_type(db)


@app.get("/analytics/incidents/count-by-priority", response_model=list[schemas.IncidentCount])
def get_incident_count_by_priority(db: Session = Depends(get_db)):
    return crud.get_incident_count_by_priority(db)


@app.get("/analytics/incidents/locations", response_model=list[schemas.IncidentLocation])
def get_incident_locations(db: Session = Depends(get_db)):
    return crud.get_all_incident_locations(db)


@app.on_event("startup")
def register_with_eureka():
    """Register this service with a Eureka server if configured via EUREKA_SERVER_URL env var."""
    eureka_server = os.getenv("EUREKA_SERVER_URL")
    if not eureka_server or eureka_client is None:
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
        print(f"EUREKA: initialized client for {app_name} -> {eureka_server}")
    except Exception as e:
        # don't crash the app if registration fails; check logs
        app.state.eureka_initialized = False
        print("EUREKA: init failed:", e)


@app.on_event("shutdown")
def unregister_from_eureka():
    if eureka_client is not None and getattr(app.state, "eureka_initialized", False):
        try:
            eureka_client.stop()
            print("EUREKA: stopped client")
        except Exception as e:
            print("EUREKA: stop failed", e)