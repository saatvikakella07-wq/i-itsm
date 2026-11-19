package com.itsm.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for resolving an incident by technician.
 */
public class ResolveIncidentRequest {

    @NotBlank(message = "Resolution details are required")
    private String resolutionDetails;

    public ResolveIncidentRequest() {}

    public ResolveIncidentRequest(String resolutionDetails) {
        this.resolutionDetails = resolutionDetails;
    }

    public String getResolutionDetails() {
        return resolutionDetails;
    }

    public void setResolutionDetails(String resolutionDetails) {
        this.resolutionDetails = resolutionDetails;
    }
}
