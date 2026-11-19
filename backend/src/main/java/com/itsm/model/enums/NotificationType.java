package com.itsm.model.enums;

/**
 * Types of system event notifications sent to employees, technicians, and administrators.
 */
public enum NotificationType {
    ASSIGNED,
    SLA_WARNING,
    SLA_VIOLATED,
    RESOLVED,
    ESCALATED,
    CLOSED
}
