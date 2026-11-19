package com.itsm.repository;

import com.itsm.model.AuditLog;
import com.itsm.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository interface for immutable AuditLog entries.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByIncidentOrderByTimestampAsc(Incident incident);
    List<AuditLog> findByIncidentIdOrderByTimestampAsc(Long incidentId);
}
