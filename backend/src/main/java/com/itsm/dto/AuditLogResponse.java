package com.itsm.dto;

import com.itsm.model.AuditLog;
import com.itsm.model.enums.IncidentStatus;
import java.time.LocalDateTime;

/**
 * DTO representing an immutable audit log entry.
 */
public class AuditLogResponse {

    private Long id;
    private Long incidentId;
    private Long userId;
    private String userName;
    private String action;
    private IncidentStatus previousStatus;
    private IncidentStatus newStatus;
    private String description;
    private LocalDateTime timestamp;

    public AuditLogResponse() {}

    public static AuditLogResponse fromEntity(AuditLog log) {
        if (log == null) return null;
        AuditLogResponse dto = new AuditLogResponse();
        dto.setId(log.getId());
        if (log.getIncident() != null) {
            dto.setIncidentId(log.getIncident().getId());
        }
        if (log.getUser() != null) {
            dto.setUserId(log.getUser().getId());
            dto.setUserName(log.getUser().getName());
        } else {
            dto.setUserName("System Automated Agent");
        }
        dto.setAction(log.getAction());
        dto.setPreviousStatus(log.getPreviousStatus());
        dto.setNewStatus(log.getNewStatus());
        dto.setDescription(log.getDescription());
        dto.setTimestamp(log.getTimestamp());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(Long incidentId) {
        this.incidentId = incidentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public IncidentStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(IncidentStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public IncidentStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(IncidentStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
