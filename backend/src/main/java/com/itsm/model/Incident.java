package com.itsm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.itsm.model.enums.IncidentCategory;
import com.itsm.model.enums.IncidentStatus;
import com.itsm.model.enums.Priority;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Central aggregate entity representing an IT incident in the I-ITSM system.
 * Encapsulates the complete lifecycle: classification, priority calculation,
 * technician assignment, investigation, resolution, and closure.
 */
@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "incident_number", nullable = false, unique = true, length = 50)
    private String incidentNumber;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private IncidentCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private IncidentStatus status = IncidentStatus.NEW;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Priority priority = Priority.P4;

    @Column(nullable = false)
    private Integer impact; // 1 to 3

    @Column(nullable = false)
    private Integer urgency; // 1 to 3

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "technician_id")
    private User technician;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", nullable = false)
    private ItService service;

    @Column(name = "sla_response_deadline")
    private LocalDateTime slaResponseDeadline;

    @Column(name = "sla_resolution_deadline")
    private LocalDateTime slaResolutionDeadline;

    @Column(name = "sla_violated", nullable = false)
    private Boolean slaViolated = false;

    @Column(name = "investigation_notes", columnDefinition = "TEXT")
    private String investigationNotes;

    @Column(name = "resolution_details", columnDefinition = "TEXT")
    private String resolutionDetails;

    @Column(name = "potential_duplicate", nullable = false)
    private Boolean potentialDuplicate = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_incident_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "parentIncident"})
    private Incident parentIncident;

    @Column(name = "feedback")
    private Integer feedback; // 1 to 5 rating

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    public Incident() {}

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = IncidentStatus.NEW;
        }
        if (this.slaViolated == null) {
            this.slaViolated = false;
        }
        if (this.potentialDuplicate == null) {
            this.potentialDuplicate = false;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIncidentNumber() {
        return incidentNumber;
    }

    public void setIncidentNumber(String incidentNumber) {
        this.incidentNumber = incidentNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IncidentCategory getCategory() {
        return category;
    }

    public void setCategory(IncidentCategory category) {
        this.category = category;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Integer getImpact() {
        return impact;
    }

    public void setImpact(Integer impact) {
        this.impact = impact;
    }

    public Integer getUrgency() {
        return urgency;
    }

    public void setUrgency(Integer urgency) {
        this.urgency = urgency;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public User getTechnician() {
        return technician;
    }

    public void setTechnician(User technician) {
        this.technician = technician;
    }

    public ItService getService() {
        return service;
    }

    public void setService(ItService service) {
        this.service = service;
    }

    public LocalDateTime getSlaResponseDeadline() {
        return slaResponseDeadline;
    }

    public void setSlaResponseDeadline(LocalDateTime slaResponseDeadline) {
        this.slaResponseDeadline = slaResponseDeadline;
    }

    public LocalDateTime getSlaResolutionDeadline() {
        return slaResolutionDeadline;
    }

    public void setSlaResolutionDeadline(LocalDateTime slaResolutionDeadline) {
        this.slaResolutionDeadline = slaResolutionDeadline;
    }

    public Boolean getSlaViolated() {
        return slaViolated;
    }

    public void setSlaViolated(Boolean slaViolated) {
        this.slaViolated = slaViolated;
    }

    public String getInvestigationNotes() {
        return investigationNotes;
    }

    public void setInvestigationNotes(String investigationNotes) {
        this.investigationNotes = investigationNotes;
    }

    public String getResolutionDetails() {
        return resolutionDetails;
    }

    public void setResolutionDetails(String resolutionDetails) {
        this.resolutionDetails = resolutionDetails;
    }

    public Boolean getPotentialDuplicate() {
        return potentialDuplicate;
    }

    public void setPotentialDuplicate(Boolean potentialDuplicate) {
        this.potentialDuplicate = potentialDuplicate;
    }

    public Incident getParentIncident() {
        return parentIncident;
    }

    public void setParentIncident(Incident parentIncident) {
        this.parentIncident = parentIncident;
    }

    public Integer getFeedback() {
        return feedback;
    }

    public void setFeedback(Integer feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }
}
