package com.itsm.repository;

import com.itsm.model.Incident;
import com.itsm.model.User;
import com.itsm.model.enums.IncidentCategory;
import com.itsm.model.enums.IncidentStatus;
import com.itsm.model.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository interface for Incidents with JPA Specification querying support.
 */
@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long>, JpaSpecificationExecutor<Incident> {

    Optional<Incident> findByIncidentNumber(String incidentNumber);

    List<Incident> findByReporter(User reporter);

    List<Incident> findByTechnician(User technician);

    List<Incident> findByStatus(IncidentStatus status);

    List<Incident> findByPriority(Priority priority);

    // Duplicate detection helper: find recent open incidents matching service and category within 24 hours
    @Query("SELECT i FROM Incident i WHERE i.service.id = :serviceId AND i.category = :category " +
           "AND i.createdAt >= :since AND i.status NOT IN ('RESOLVED', 'CLOSED') ORDER BY i.createdAt ASC")
    List<Incident> findPotentialDuplicates(@Param("serviceId") Long serviceId,
                                          @Param("category") IncidentCategory category,
                                          @Param("since") LocalDateTime since);

    // For Auto-Escalation: unacknowledged P1 incidents assigned for more than threshold
    @Query("SELECT i FROM Incident i WHERE i.status = :status AND i.priority = :priority " +
           "AND i.createdAt <= :deadline")
    List<Incident> findUnacknowledgedP1Incidents(@Param("status") IncidentStatus status,
                                               @Param("priority") Priority priority,
                                               @Param("deadline") LocalDateTime deadline);

    // SLA Violation check: not resolved/closed past resolution deadline
    @Query("SELECT i FROM Incident i WHERE i.slaResolutionDeadline <= :now " +
           "AND i.status NOT IN ('RESOLVED', 'CLOSED') AND i.slaViolated = false")
    List<Incident> findPendingSlaViolations(@Param("now") LocalDateTime now);

    // Dashboard metrics
    long countByStatus(IncidentStatus status);
    long countByPriority(Priority priority);
    long countBySlaViolatedTrue();

    @Query("SELECT i.category, COUNT(i) FROM Incident i GROUP BY i.category")
    List<Object[]> countByCategory();

    @Query("SELECT i.service.name, COUNT(i) FROM Incident i GROUP BY i.service.name")
    List<Object[]> countByService();
}
