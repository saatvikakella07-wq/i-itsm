package com.itsm.dto;

import com.itsm.model.Incident;
import com.itsm.model.enums.IncidentCategory;
import com.itsm.model.enums.IncidentStatus;
import com.itsm.model.enums.Priority;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing an incident response returned to callers.
 */
public class IncidentResponse {

    private Long id;
    private String incidentNumber;
    private String title;
    private String description;
    private IncidentCategory category;
    private IncidentStatus status;
    private Priority priority;
    private Integer impact;
    private Integer urgency;
    private Long reporterId;
    private String reporterName;
    private String reporterEmail;
    private Long technicianId;
    private String technicianName;
    private Long serviceId;
    private String serviceName;
    private Integer serviceCriticality;
    private LocalDateTime slaResponseDeadline;
    private LocalDateTime slaResolutionDeadline;
    private Boolean slaViolated;
    private String investigationNotes;
    private String resolutionDetails;
    private Boolean potentialDuplicate;
    private Long parentIncidentId;
    private Integer feedback;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;

    public IncidentResponse() {}

    public static IncidentResponse fromEntity(Incident incident) {
        if (incident == null) return null;
        IncidentResponse dto = new IncidentResponse();
        dto.setId(incident.getId());
        dto.setIncidentNumber(incident.getIncidentNumber());
        dto.setTitle(incident.getTitle());
        dto.setDescription(incident.getDescription());
        dto.setCategory(incident.getCategory());
        dto.setStatus(incident.getStatus());
        dto.setPriority(incident.getPriority());
        dto.setImpact(incident.getImpact());
        dto.setUrgency(incident.getUrgency());

        if (incident.getReporter() != null) {
            dto.setReporterId(incident.getReporter().getId());
            dto.setReporterName(incident.getReporter().getName());
            dto.setReporterEmail(incident.getReporter().getEmail());
        }

        if (incident.getTechnician() != null) {
            dto.setTechnicianId(incident.getTechnician().getId());
            dto.setTechnicianName(incident.getTechnician().getName());
        }

        if (incident.getService() != null) {
            dto.setServiceId(incident.getService().getId());
            dto.setServiceName(incident.getService().getName());
            dto.setServiceCriticality(incident.getService().getCriticality());
        }

        dto.setSlaResponseDeadline(incident.getSlaResponseDeadline());
        dto.setSlaResolutionDeadline(incident.getSlaResolutionDeadline());
        dto.setSlaViolated(incident.getSlaViolated());
        dto.setInvestigationNotes(incident.getInvestigationNotes());
        dto.setResolutionDetails(incident.getResolutionDetails());
        dto.setPotentialDuplicate(incident.getPotentialDuplicate());

        if (incident.getParentIncident() != null) {
            dto.setParentIncidentId(incident.getParentIncident().getId());
        }

        dto.setFeedback(incident.getFeedback());
        dto.setCreatedAt(incident.getCreatedAt());
        dto.setResolvedAt(incident.getResolvedAt());
        dto.setClosedAt(incident.getClosedAt());
        return dto;
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

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public void setReporterEmail(String reporterEmail) {
        this.reporterEmail = reporterEmail;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Integer getServiceCriticality() {
        return serviceCriticality;
    }

    public void setServiceCriticality(Integer serviceCriticality) {
        this.serviceCriticality = serviceCriticality;
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

    public Long getParentIncidentId() {
        return parentIncidentId;
    }

    public void setParentIncidentId(Long parentIncidentId) {
        this.parentIncidentId = parentIncidentId;
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
