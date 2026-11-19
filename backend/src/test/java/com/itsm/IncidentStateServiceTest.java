package com.itsm;

import com.itsm.exception.InvalidStateTransitionException;
import com.itsm.model.AuditLog;
import com.itsm.model.Incident;
import com.itsm.model.User;
import com.itsm.model.enums.IncidentStatus;
import com.itsm.model.enums.Role;
import com.itsm.observer.AuditLogger;
import com.itsm.repository.AuditLogRepository;
import com.itsm.repository.IncidentRepository;
import com.itsm.service.IncidentStateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.FluentQuery;

import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying State Pattern enforcement and transition constraints.
 * Uses lightweight dynamic proxy stubs for repository calls, fully compatible with all JDK versions.
 */
class IncidentStateServiceTest {

    private IncidentRepository incidentRepository;
    private AuditLogger auditLogger;
    private IncidentStateService stateService;

    @BeforeEach
    void setUp() {
        // Pure Java dynamic proxy for IncidentRepository.save
        incidentRepository = (IncidentRepository) Proxy.newProxyInstance(
                IncidentRepository.class.getClassLoader(),
                new Class<?>[]{IncidentRepository.class},
                (proxy, method, methodArgs) -> {
                    if ("save".equals(method.getName()) && methodArgs != null && methodArgs.length > 0) {
                        return methodArgs[0];
                    }
                    return null;
                }
        );

        AuditLogRepository auditRepo = (AuditLogRepository) Proxy.newProxyInstance(
                AuditLogRepository.class.getClassLoader(),
                new Class<?>[]{AuditLogRepository.class},
                (proxy, method, methodArgs) -> {
                    if ("save".equals(method.getName()) && methodArgs != null && methodArgs.length > 0) {
                        return methodArgs[0];
                    }
                    return null;
                }
        );

        auditLogger = new AuditLogger(auditRepo);
        stateService = new IncidentStateService(incidentRepository, auditLogger, Collections.emptyList());
    }

    @Test
    @DisplayName("Should permit valid lifecycle: NEW -> CLASSIFIED -> ASSIGNED -> ACKNOWLEDGED -> IN_PROGRESS -> RESOLVED -> CLOSED")
    void testValidLifecycleProgression() {
        User admin = new User("Admin", "admin@org.com", "pass", Role.ADMIN);
        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.NEW);

        // 1. NEW -> CLASSIFIED
        assertTrue(stateService.isValidTransition(IncidentStatus.NEW, IncidentStatus.CLASSIFIED));
        Incident step1 = stateService.transition(incident, IncidentStatus.CLASSIFIED, admin, "Classified");
        assertEquals(IncidentStatus.CLASSIFIED, step1.getStatus());

        // 2. CLASSIFIED -> ASSIGNED
        assertTrue(stateService.isValidTransition(IncidentStatus.CLASSIFIED, IncidentStatus.ASSIGNED));
        Incident step2 = stateService.transition(step1, IncidentStatus.ASSIGNED, admin, "Assigned");
        assertEquals(IncidentStatus.ASSIGNED, step2.getStatus());

        // 3. ASSIGNED -> ACKNOWLEDGED
        assertTrue(stateService.isValidTransition(IncidentStatus.ASSIGNED, IncidentStatus.ACKNOWLEDGED));
        Incident step3 = stateService.transition(step2, IncidentStatus.ACKNOWLEDGED, admin, "Acknowledged");
        assertEquals(IncidentStatus.ACKNOWLEDGED, step3.getStatus());

        // 4. ACKNOWLEDGED -> IN_PROGRESS
        assertTrue(stateService.isValidTransition(IncidentStatus.ACKNOWLEDGED, IncidentStatus.IN_PROGRESS));
        Incident step4 = stateService.transition(step3, IncidentStatus.IN_PROGRESS, admin, "Investigation started");
        assertEquals(IncidentStatus.IN_PROGRESS, step4.getStatus());

        // 5. IN_PROGRESS -> RESOLVED
        assertTrue(stateService.isValidTransition(IncidentStatus.IN_PROGRESS, IncidentStatus.RESOLVED));
        Incident step5 = stateService.transition(step4, IncidentStatus.RESOLVED, admin, "Resolved");
        assertEquals(IncidentStatus.RESOLVED, step5.getStatus());

        // 6. RESOLVED -> CLOSED
        assertTrue(stateService.isValidTransition(IncidentStatus.RESOLVED, IncidentStatus.CLOSED));
        Incident step6 = stateService.transition(step5, IncidentStatus.CLOSED, admin, "Closed");
        assertEquals(IncidentStatus.CLOSED, step6.getStatus());
    }

    @Test
    @DisplayName("Should throw InvalidStateTransitionException when skipping states (e.g., NEW -> CLOSED)")
    void testInvalidTransitionNewToClosed() {
        User admin = new User("Admin", "admin@org.com", "pass", Role.ADMIN);
        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.NEW);

        assertFalse(stateService.isValidTransition(IncidentStatus.NEW, IncidentStatus.CLOSED));

        InvalidStateTransitionException ex = assertThrows(InvalidStateTransitionException.class, () ->
                stateService.transition(incident, IncidentStatus.CLOSED, admin, "Direct close attempted"));

        assertTrue(ex.getMessage().contains("Invalid state transition: NEW → CLOSED"));
    }

    @Test
    @DisplayName("Should reject backward transition (e.g., RESOLVED -> NEW)")
    void testInvalidTransitionResolvedToNew() {
        User admin = new User("Admin", "admin@org.com", "pass", Role.ADMIN);
        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.RESOLVED);

        assertThrows(InvalidStateTransitionException.class, () ->
                stateService.transition(incident, IncidentStatus.NEW, admin, "Reset attempt"));
    }

    @Test
    @DisplayName("Should reject modification after terminal CLOSED state")
    void testCannotTransitionFromClosed() {
        User admin = new User("Admin", "admin@org.com", "pass", Role.ADMIN);
        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.CLOSED);

        assertThrows(InvalidStateTransitionException.class, () ->
                stateService.transition(incident, IncidentStatus.IN_PROGRESS, admin, "Reopen attempt"));
    }
}
