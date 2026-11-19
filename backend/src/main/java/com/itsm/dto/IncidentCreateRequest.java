package com.itsm.dto;

import com.itsm.model.enums.IncidentCategory;
import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for creating a new IT incident.
 */
public class IncidentCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private IncidentCategory category;

    @NotNull(message = "Affected service ID is required")
    private Long serviceId;

    @NotNull(message = "Impact level is required")
    @Min(value = 1, message = "Impact must be between 1 (Low) and 3 (High)")
    @Max(value = 3, message = "Impact must be between 1 (Low) and 3 (High)")
    private Integer impact;

    @NotNull(message = "Urgency level is required")
    @Min(value = 1, message = "Urgency must be between 1 (Low) and 3 (High)")
    @Max(value = 3, message = "Urgency must be between 1 (Low) and 3 (High)")
    private Integer urgency;

    public IncidentCreateRequest() {}

    public IncidentCreateRequest(String title, String description, IncidentCategory category,
                                 Long serviceId, Integer impact, Integer urgency) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.serviceId = serviceId;
        this.impact = impact;
        this.urgency = urgency;
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

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
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
}
