package com.itsm.observer;

import com.itsm.model.Incident;
import com.itsm.model.Notification;
import com.itsm.model.User;
import com.itsm.model.enums.NotificationType;
import com.itsm.model.enums.Role;
import com.itsm.repository.NotificationRepository;
import com.itsm.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Concrete Observer: Dispatches persistent in-app notifications to relevant stakeholders
 * (assigned technician, incident reporter, or system administrators).
 */
@Component
public class InAppNotifier implements IncidentObserver {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public InAppNotifier(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void onIncidentEvent(Incident incident, String action, User actor) {
        if (incident == null) return;

        switch (action) {
            case "ASSIGNED" -> notifyAssignedTechnician(incident);
            case "RESOLVED" -> notifyReporter(incident, "Your incident " + incident.getIncidentNumber() + " has been resolved.", NotificationType.RESOLVED);
            case "CLOSED" -> notifyAssignedTechnicianAndReporter(incident, "Incident " + incident.getIncidentNumber() + " has been closed.", NotificationType.CLOSED);
            case "ESCALATED" -> notifyAdmins(incident, "ESCALATION: Incident " + incident.getIncidentNumber() + " not acknowledged within SLA target!", NotificationType.ESCALATED);
            case "SLA_WARNING" -> notifyAssignedTechnician(incident, "SLA WARNING: Incident " + incident.getIncidentNumber() + " is approaching resolution deadline.", NotificationType.SLA_WARNING);
            case "SLA_VIOLATED" -> {
                notifyAdmins(incident, "SLA VIOLATION: Incident " + incident.getIncidentNumber() + " has breached SLA resolution deadline!", NotificationType.SLA_VIOLATED);
                notifyReporter(incident, "Update: SLA deadline for incident " + incident.getIncidentNumber() + " was violated. Support management has been alerted.", NotificationType.SLA_VIOLATED);
            }
            default -> {}
        }
    }

    private void notifyAssignedTechnician(Incident incident) {
        if (incident.getTechnician() != null) {
            Notification notif = new Notification(
                    incident.getTechnician(),
                    incident,
                    "New incident assigned: " + incident.getIncidentNumber() + " - " + incident.getTitle(),
                    NotificationType.ASSIGNED
            );
            notificationRepository.save(notif);
        }
    }

    private void notifyAssignedTechnician(Incident incident, String message, NotificationType type) {
        if (incident.getTechnician() != null) {
            Notification notif = new Notification(incident.getTechnician(), incident, message, type);
            notificationRepository.save(notif);
        }
    }

    private void notifyReporter(Incident incident, String message, NotificationType type) {
        if (incident.getReporter() != null) {
            Notification notif = new Notification(incident.getReporter(), incident, message, type);
            notificationRepository.save(notif);
        }
    }

    private void notifyAssignedTechnicianAndReporter(Incident incident, String message, NotificationType type) {
        notifyReporter(incident, message, type);
        if (incident.getTechnician() != null) {
            notifyAssignedTechnician(incident, message, type);
        }
    }

    public void notifyAdmins(Incident incident, String message, NotificationType type) {
        List<User> admins = userRepository.findByRoleAndIsActiveTrue(Role.ADMIN);
        for (User admin : admins) {
            Notification notif = new Notification(admin, incident, message, type);
            notificationRepository.save(notif);
        }
    }
}
