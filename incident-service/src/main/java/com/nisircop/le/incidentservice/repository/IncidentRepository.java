package com.nisircop.le.incidentservice.repository;

import com.nisircop.le.incidentservice.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByReportedBy(Long reportedBy);
    List<Incident> findByReportedByIn(List<Long> reportedBy);
}