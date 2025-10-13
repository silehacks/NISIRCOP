from sqlalchemy.orm import Session
from sqlalchemy import func
from . import models


def get_incident_count_by_type(db: Session):
    """
    Groups incidents by their type and returns the count for each type.
    """
    return db.query(
        models.Incident.incident_type.label("name"),
        func.count(models.Incident.id).label("count")
    ).group_by(models.Incident.incident_type).all()


def get_incident_count_by_priority(db: Session):
    """
    Groups incidents by their priority and returns the count for each priority.
    """
    return db.query(
        models.Incident.priority.label("name"),
        func.count(models.Incident.id).label("count")
    ).group_by(models.Incident.priority).all()


def get_all_incident_locations(db: Session):
    """
    Returns the latitude and longitude of all incidents.
    """
    return db.query(
        models.Incident.latitude,
        models.Incident.longitude
    ).all()