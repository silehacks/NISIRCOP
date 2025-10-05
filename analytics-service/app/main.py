from fastapi import FastAPI

app = FastAPI(
    title="NISIRCOP Analytics Service",
    description="This service provides basic statistical analysis of crime data.",
    version="0.1.0",
)

@app.get("/health", tags=["Monitoring"])
def read_health():
    """
    Health check endpoint to confirm the service is running.
    """
    return {"status": "UP"}

@app.get("/", tags=["General"])
def read_root():
    """
    Root endpoint for the Analytics Service.
    """
    return {"message": "Welcome to the NISIRCOP Analytics Service"}