package com.itsm.dto;

import com.itsm.model.Notification;
import com.itsm.model.enums.NotificationType;
import java.time.LocalDateTime;

/**
 * DTO representing an in-app notification.
 */
public class NotificationResponse {

    private Long id;
    private Long userId;
    private Long incidentId;
    private String message;
    private NotificationType type;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponse() {}

    public static NotificationResponse fromEntity(Notification notification) {
        if (notification == null) return null;
        NotificationResponse dto = new NotificationResponse();
        dto.setId(notification.getId());
        if (notification.getUser() != null) {
            dto.setUserId(notification.getUser().getId());
        }
        if (notification.getIncident() != null) {
            dto.setIncidentId(notification.getIncident().getId());
        }
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(Long incidentId) {
        this.incidentId = incidentId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
