package com.itsm.observer;

import com.itsm.model.Incident;
import com.itsm.model.SimulatedEmail;
import com.itsm.model.User;
import com.itsm.repository.SimulatedEmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer: Simulates email notifications (second notification channel).
 * Logs email output to standard console and persists records to the simulated_emails table.
 */
@Component
public class SimulatedEmailNotifier implements IncidentObserver {

    private static final Logger log = LoggerFactory.getLogger(SimulatedEmailNotifier.class);

    private final SimulatedEmailRepository emailRepository;

    public SimulatedEmailNotifier(SimulatedEmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    public void onIncidentEvent(Incident incident, String action, User actor) {
        if (incident == null) return;

        String recipientEmail = null;
        String subject = null;
        String body = null;

        if ("ASSIGNED".equals(action) && incident.getTechnician() != null) {
            recipientEmail = incident.getTechnician().getEmail();
            subject = "[I-ITSM Alert] Assigned Incident: " + incident.getIncidentNumber();
            body = String.format("Hello %s,\n\nYou have been assigned incident %s (%s).\nPriority: %s\nCategory: %s\nSLA Deadline: %s\n\nPlease log in to acknowledge.",
                    incident.getTechnician().getName(), incident.getIncidentNumber(), incident.getTitle(),
                    incident.getPriority(), incident.getCategory(), incident.getSlaResolutionDeadline());
        } else if ("RESOLVED".equals(action) && incident.getReporter() != null) {
            recipientEmail = incident.getReporter().getEmail();
            subject = "[I-ITSM Update] Resolved: " + incident.getIncidentNumber();
            body = String.format("Hello %s,\n\nYour reported incident %s has been resolved.\nResolution Details: %s\n\nPlease verify and close the ticket in your portal.",
                    incident.getReporter().getName(), incident.getIncidentNumber(), incident.getResolutionDetails());
        } else if ("ESCALATED".equals(action)) {
            recipientEmail = "admin@org.com";
            subject = "[I-ITSM ESCALATION] Critical Incident " + incident.getIncidentNumber() + " Overdue";
            body = String.format("URGENT: Incident %s (Priority %s) has not been acknowledged within SLA.\nPlease take administrative action immediately.",
                    incident.getIncidentNumber(), incident.getPriority());
        }

        if (recipientEmail != null && subject != null && body != null) {
            SimulatedEmail email = new SimulatedEmail(recipientEmail, subject, body);
            emailRepository.save(email);
            log.info("[EMAIL] To: {} | Subject: {} | Body: {}", recipientEmail, subject, body);
        }
    }
}
