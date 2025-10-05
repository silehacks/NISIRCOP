package com.nisircop.le.incidentservice.repository;

import com.nisircop.le.incidentservice.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
}