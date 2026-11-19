package com.itsm.dto;

import java.util.List;

/**
 * DTO for reporting technician workload and active incident allocation.
 */
public class TechnicianWorkloadResponse {

    private Long technicianId;
    private Long userId;
    private String name;
    private String email;
    private List<String> skills;
    private Integer currentWorkload;
    private Boolean isAvailable;

    public TechnicianWorkloadResponse() {}

    public TechnicianWorkloadResponse(Long technicianId, Long userId, String name, String email,
                                      List<String> skills, Integer currentWorkload, Boolean isAvailable) {
        this.technicianId = technicianId;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.skills = skills;
        this.currentWorkload = currentWorkload;
        this.isAvailable = isAvailable;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public Integer getCurrentWorkload() {
        return currentWorkload;
    }

    public void setCurrentWorkload(Integer currentWorkload) {
        this.currentWorkload = currentWorkload;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean available) {
        isAvailable = available;
    }
}
