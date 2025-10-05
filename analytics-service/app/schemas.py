from pydantic import BaseModel


class IncidentCount(BaseModel):
    name: str
    count: int

    class Config:
        orm_mode = True