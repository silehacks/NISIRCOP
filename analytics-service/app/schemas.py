from pydantic import BaseModel


class IncidentCount(BaseModel):
    name: str
    count: int

    class Config:
        orm_mode = True


class IncidentLocation(BaseModel):
    latitude: float
    longitude: float

    class Config:
        orm_mode = True