from fastapi import FastAPI, Depends
from sqlalchemy.orm import Session
from . import crud, models, schemas
from .database import SessionLocal, engine

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