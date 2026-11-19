package com.itsm.dto;

import com.itsm.model.enums.IncidentStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for advancing or updating incident lifecycle status.
 */
public class StatusUpdateRequest {

    @NotNull(message = "New status is required")
    private IncidentStatus newStatus;

    private String notes;

    public StatusUpdateRequest() {}

    public StatusUpdateRequest(IncidentStatus newStatus, String notes) {
        this.newStatus = newStatus;
        this.notes = notes;
    }

    public IncidentStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(IncidentStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
