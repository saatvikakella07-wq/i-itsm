package com.itsm.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for assigning an incident to a technician (manual or strategy-driven).
 */
public class AssignTechnicianRequest {

    @NotNull(message = "Technician ID is required")
    private Long technicianId;

    private String strategy; // "SKILL_BASED" or "LOAD_BASED"

    public AssignTechnicianRequest() {}

    public AssignTechnicianRequest(Long technicianId, String strategy) {
        this.technicianId = technicianId;
        this.strategy = strategy;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
}
