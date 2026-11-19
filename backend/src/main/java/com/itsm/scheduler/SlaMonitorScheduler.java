package com.itsm.scheduler;

import com.itsm.model.Incident;
import com.itsm.model.enums.NotificationType;
import com.itsm.observer.AuditLogger;
import com.itsm.observer.InAppNotifier;
import com.itsm.repository.IncidentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Intelligent Automation: Continuous SLA Compliance Monitor.
 * Periodically verifies resolution deadlines and flags SLA breaches.
 */
@Component
public class SlaMonitorScheduler {

    private static final Logger log = LoggerFactory.getLogger(SlaMonitorScheduler.class);

    private final IncidentRepository incidentRepository;
    private final InAppNotifier inAppNotifier;
    private final AuditLogger auditLogger;

    public SlaMonitorScheduler(IncidentRepository incidentRepository,
                               InAppNotifier inAppNotifier,
                               AuditLogger auditLogger) {
        this.incidentRepository = incidentRepository;
        this.inAppNotifier = inAppNotifier;
        this.auditLogger = auditLogger;
    }

    /**
     * Checks every 60 seconds for incidents that have breached their SLA resolution target.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void monitorSlaViolations() {
        LocalDateTime now = LocalDateTime.now();
        List<Incident> breached = incidentRepository.findPendingSlaViolations(now);

        for (Incident inc : breached) {
            log.warn("SLA Violated for incident: {} (Deadline: {})", inc.getIncidentNumber(), inc.getSlaResolutionDeadline());

            inc.setSlaViolated(true);
            incidentRepository.save(inc);

            // Notify stakeholders and record audit trail
            inAppNotifier.onIncidentEvent(inc, "SLA_VIOLATED", null);
            auditLogger.onIncidentEvent(inc, "SLA_VIOLATED", null);
        }
    }
}
