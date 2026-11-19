package com.itsm.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * DTO for closing an incident and providing feedback by employee.
 */
public class CloseIncidentRequest {

    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer feedback;

    public CloseIncidentRequest() {}

    public CloseIncidentRequest(Integer feedback) {
        this.feedback = feedback;
    }

    public Integer getFeedback() {
        return feedback;
    }

    public void setFeedback(Integer feedback) {
        this.feedback = feedback;
    }
}
