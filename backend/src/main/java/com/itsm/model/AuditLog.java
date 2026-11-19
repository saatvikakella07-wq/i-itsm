package com.itsm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.itsm.model.enums.IncidentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Immutable audit trail record logging every state transition, assignment,
 * or administrative action performed on an incident.
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Incident incident;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user; // null if performed by system/scheduler

    @Column(nullable = false, length = 100)
    private String action;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 50)
    private IncidentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", length = 50)
    private IncidentStatus newStatus;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public AuditLog() {}

    public AuditLog(Incident incident, User user, String action, IncidentStatus previousStatus,
                    IncidentStatus newStatus, String description) {
        this.incident = incident;
        this.user = user;
        this.action = action;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
