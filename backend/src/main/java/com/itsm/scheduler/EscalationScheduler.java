package com.itsm.scheduler;

import com.itsm.model.Incident;
import com.itsm.model.enums.IncidentStatus;
import com.itsm.model.enums.NotificationType;
import com.itsm.model.enums.Priority;
import com.itsm.observer.AuditLogger;
import com.itsm.observer.InAppNotifier;
import com.itsm.observer.SimulatedEmailNotifier;
import com.itsm.repository.IncidentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Intelligent Automation: Escalation Scheduler.
 * Periodically detects unacknowledged P1 (Critical) incidents exceeding the 5-minute
 * acknowledgment threshold and automatically triggers escalation to system administrators.
 */
@Component
public class EscalationScheduler {

    private static final Logger log = LoggerFactory.getLogger(EscalationScheduler.class);

    private final IncidentRepository incidentRepository;
    private final InAppNotifier inAppNotifier;
    private final SimulatedEmailNotifier emailNotifier;
    private final AuditLogger auditLogger;

    public EscalationScheduler(IncidentRepository incidentRepository,
                               InAppNotifier inAppNotifier,
                               SimulatedEmailNotifier emailNotifier,
                               AuditLogger auditLogger) {
        this.incidentRepository = incidentRepository;
        this.inAppNotifier = inAppNotifier;
        this.emailNotifier = emailNotifier;
        this.auditLogger = auditLogger;
    }

    /**
     * Runs every 60 seconds checking for unacknowledged P1 incidents older than 5 minutes.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkForOverdueP1Incidents() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
        List<Incident> overdue = incidentRepository.findUnacknowledgedP1Incidents(
                IncidentStatus.ASSIGNED, Priority.P1, threshold
        );

        for (Incident inc : overdue) {
            log.warn("Escalating unacknowledged critical incident: {}", inc.getIncidentNumber());

            // Dispatch escalation notification to admins
            String msg = String.format("ESCALATION: Incident %s (%s) has not been acknowledged for over 5 minutes!",
                    inc.getIncidentNumber(), inc.getTitle());
            inAppNotifier.notifyAdmins(inc, msg, NotificationType.ESCALATED);
            emailNotifier.onIncidentEvent(inc, "ESCALATED", null);

            // Record audit log
            auditLogger.onIncidentEvent(inc, "ESCALATED", null);
        }
    }
}
