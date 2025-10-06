from sqlalchemy import Column, Integer, String, Float, DateTime
from .database import Base
import datetime


class Incident(Base):
    __tablename__ = "incidents"

    id = Column(Integer, primary_key=True, index=True)
    title = Column(String, nullable=False)
    description = Column(String)
    incident_type = Column(String, index=True)
    priority = Column(String, index=True)
    latitude = Column(Float, nullable=False)
    longitude = Column(Float, nullable=False)
    reported_by = Column(Integer, nullable=False)
    occurred_at = Column(DateTime, default=datetime.datetime.utcnow, nullable=False)