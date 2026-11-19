package com.itsm.service;

import com.itsm.exception.InvalidStateTransitionException;
import com.itsm.exception.UnauthorizedException;
import com.itsm.model.Incident;
import com.itsm.model.User;
import com.itsm.model.enums.IncidentStatus;
import com.itsm.model.enums.Role;
import com.itsm.observer.AuditLogger;
import com.itsm.observer.IncidentObserver;
import com.itsm.repository.IncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * State Pattern implementation managing valid incident lifecycle transitions.
 * Enforces business constraints, authorization, audit logging, and observer notifications.
 *
 * Valid lifecycle:
 * NEW -> CLASSIFIED -> ASSIGNED -> ACKNOWLEDGED -> IN_PROGRESS -> RESOLVED -> [VERIFIED] -> CLOSED
 */
@Service
public class IncidentStateService {

    private final IncidentRepository incidentRepository;
    private final AuditLogger auditLogger;
    private final List<IncidentObserver> observers;

    // Allowed transition map: key = Current State, value = Set of Valid Next States
    private static final Map<IncidentStatus, Set<IncidentStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(IncidentStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(IncidentStatus.NEW, Set.of(IncidentStatus.CLASSIFIED));
        ALLOWED_TRANSITIONS.put(IncidentStatus.CLASSIFIED, Set.of(IncidentStatus.ASSIGNED));
        ALLOWED_TRANSITIONS.put(IncidentStatus.ASSIGNED, Set.of(IncidentStatus.ACKNOWLEDGED));
        ALLOWED_TRANSITIONS.put(IncidentStatus.ACKNOWLEDGED, Set.of(IncidentStatus.IN_PROGRESS));
        ALLOWED_TRANSITIONS.put(IncidentStatus.IN_PROGRESS, Set.of(IncidentStatus.RESOLVED));
        ALLOWED_TRANSITIONS.put(IncidentStatus.RESOLVED, Set.of(IncidentStatus.VERIFIED, IncidentStatus.CLOSED));
        ALLOWED_TRANSITIONS.put(IncidentStatus.VERIFIED, Set.of(IncidentStatus.CLOSED));
        ALLOWED_TRANSITIONS.put(IncidentStatus.CLOSED, Collections.emptySet()); // Terminal state
    }

    public IncidentStateService(IncidentRepository incidentRepository,
                                AuditLogger auditLogger,
                                List<IncidentObserver> observers) {
        this.incidentRepository = incidentRepository;
        this.auditLogger = auditLogger;
        this.observers = observers;
    }

    /**
     * Validates whether a transition from currentStatus to targetStatus is permitted.
     */
    public boolean isValidTransition(IncidentStatus currentStatus, IncidentStatus targetStatus) {
        Set<IncidentStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Collections.emptySet());
        return allowed.contains(targetStatus);
    }

    /**
     * Executes a state transition with role validation, audit logging, and observer dispatching.
     *
     * @param incident The incident entity.
     * @param targetStatus The requested new status.
     * @param actor The user requesting the transition.
     * @param notes Optional notes regarding this transition.
     * @return The updated Incident entity.
     */
    @Transactional
    public Incident transition(Incident incident, IncidentStatus targetStatus, User actor, String notes) {
        IncidentStatus currentStatus = incident.getStatus();

        // 1. Validate state progression
        if (!isValidTransition(currentStatus, targetStatus)) {
            throw new InvalidStateTransitionException(currentStatus, targetStatus);
        }

        // 2. Validate role authorization for specific transitions
        validateRoleForTransition(incident, targetStatus, actor);

        // 3. Update status and timestamps
        incident.setStatus(targetStatus);

        if (targetStatus == IncidentStatus.RESOLVED) {
            incident.setResolvedAt(LocalDateTime.now());
        } else if (targetStatus == IncidentStatus.CLOSED) {
            incident.setClosedAt(LocalDateTime.now());
        }

        Incident saved = incidentRepository.save(incident);

        // 4. Audit Log recording
        auditLogger.logStatusTransition(saved, currentStatus, targetStatus, actor, notes);

        // 5. Fire observers
        String eventName = targetStatus.name();
        for (IncidentObserver observer : observers) {
            try {
                observer.onIncidentEvent(saved, eventName, actor);
            } catch (Exception e) {
                // Log and continue ensuring state change succeeds
            }
        }

        return saved;
    }

    private void validateRoleForTransition(Incident incident, IncidentStatus targetStatus, User actor) {
        if (actor == null) return; // System action allowed

        // Admins can perform any permitted state transition
        if (actor.getRole() == Role.ADMIN) return;

        switch (targetStatus) {
            case ACKNOWLEDGED, IN_PROGRESS, RESOLVED -> {
                // Only assigned technician can investigate and resolve
                if (actor.getRole() != Role.TECHNICIAN) {
                    throw new UnauthorizedException("Only technicians can move incidents to " + targetStatus);
                }
                if (incident.getTechnician() != null && !incident.getTechnician().getId().equals(actor.getId())) {
                    throw new UnauthorizedException("You are not the technician assigned to this incident.");
                }
            }
            case VERIFIED, CLOSED -> {
                // Only reporting employee or admin can verify / close
                if (actor.getRole() != Role.EMPLOYEE && actor.getRole() != Role.ADMIN) {
                    throw new UnauthorizedException("Only the reporter can verify or close an incident.");
                }
                if (actor.getRole() == Role.EMPLOYEE && !incident.getReporter().getId().equals(actor.getId())) {
                    throw new UnauthorizedException("You can only close incidents reported by yourself.");
                }
            }
            default -> {}
        }
    }
}
