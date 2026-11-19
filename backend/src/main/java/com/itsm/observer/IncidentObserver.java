package com.itsm.observer;

import com.itsm.model.Incident;
import com.itsm.model.User;

/**
 * Observer Pattern Interface: Contract for decoupled subscribers that react to incident events.
 * Implementations process audit logging, notifications, and email simulation.
 */
public interface IncidentObserver {

    /**
     * Callback invoked whenever a significant incident lifecycle event occurs.
     *
     * @param incident The incident instance subject to the event.
     * @param action The event action name (e.g., CREATED, STATUS_CHANGED, ASSIGNED, ESCALATED).
     * @param actor The user responsible for the action (or null if system-triggered).
     */
    void onIncidentEvent(Incident incident, String action, User actor);
}
