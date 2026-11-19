package com.itsm.observer;

import com.itsm.model.AuditLog;
import com.itsm.model.Incident;
import com.itsm.model.User;
import com.itsm.repository.AuditLogRepository;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer: Persists immutable audit trail records upon any incident modification.
 */
@Component
public class AuditLogger implements IncidentObserver {

    private final AuditLogRepository auditLogRepository;

    public AuditLogger(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void onIncidentEvent(Incident incident, String action, User actor) {
        if (incident == null) return;

        String description = buildDescription(incident, action, actor);

        AuditLog log = new AuditLog(
                incident,
                actor,
                action,
                null, // Previous and target statuses are explicitly set in state transition if applicable
                incident.getStatus(),
                description
        );
        auditLogRepository.save(log);
    }

    /**
     * Overloaded method to record explicit status transition events.
     */
    public void logStatusTransition(Incident incident, com.itsm.model.enums.IncidentStatus oldStatus,
                                    com.itsm.model.enums.IncidentStatus newStatus, User actor, String notes) {
        String desc = String.format("Status changed from %s to %s by %s.%s",
                oldStatus, newStatus,
                actor != null ? actor.getName() : "System",
                notes != null && !notes.isBlank() ? " Notes: " + notes : "");

        AuditLog log = new AuditLog(incident, actor, "STATUS_CHANGED", oldStatus, newStatus, desc);
        auditLogRepository.save(log);
    }

    private String buildDescription(Incident incident, String action, User actor) {
        String actorName = actor != null ? actor.getName() : "System Automated Service";
        return switch (action) {
            case "CREATED" -> "Incident reported by " + actorName + " with initial priority " + incident.getPriority();
            case "ASSIGNED" -> "Assigned to technician " + (incident.getTechnician() != null ? incident.getTechnician().getName() : "None") + " by " + actorName;
            case "ESCALATED" -> "Incident auto-escalated to administrators due to acknowledgment timeout.";
            case "SLA_VIOLATED" -> "Incident flagged for SLA violation as resolution target was exceeded.";
            case "POTENTIAL_DUPLICATE" -> "Flagged as potential duplicate of parent incident #" +
                    (incident.getParentIncident() != null ? incident.getParentIncident().getIncidentNumber() : "N/A");
            default -> action + " performed on incident " + incident.getIncidentNumber() + " by " + actorName;
        };
    }
}
