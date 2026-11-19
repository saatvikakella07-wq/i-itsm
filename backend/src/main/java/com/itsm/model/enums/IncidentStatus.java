package com.itsm.model.enums;

/**
 * Represents the lifecycle stages of an IT incident from submission to closure.
 * Managed and strictly transitioned through the State Pattern (IncidentStateService).
 */
public enum IncidentStatus {
    NEW,
    CLASSIFIED,
    ASSIGNED,
    ACKNOWLEDGED,
    IN_PROGRESS,
    RESOLVED,
    VERIFIED,
    CLOSED
}
