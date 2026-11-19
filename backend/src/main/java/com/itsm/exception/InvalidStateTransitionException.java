package com.itsm.exception;

import com.itsm.model.enums.IncidentStatus;

/**
 * Exception thrown when an attempted incident lifecycle transition violates domain rules.
 * Enforces the State Pattern contract.
 */
public class InvalidStateTransitionException extends RuntimeException {

    private final IncidentStatus currentStatus;
    private final IncidentStatus targetStatus;

    public InvalidStateTransitionException(IncidentStatus currentStatus, IncidentStatus targetStatus) {
        super(String.format("Invalid state transition: %s → %s", currentStatus, targetStatus));
        this.currentStatus = currentStatus;
        this.targetStatus = targetStatus;
    }

    public InvalidStateTransitionException(String message) {
        super(message);
        this.currentStatus = null;
        this.targetStatus = null;
    }

    public IncidentStatus getCurrentStatus() {
        return currentStatus;
    }

    public IncidentStatus getTargetStatus() {
        return targetStatus;
    }
}
