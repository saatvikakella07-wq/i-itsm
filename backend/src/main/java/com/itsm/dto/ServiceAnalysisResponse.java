package com.itsm.dto;

/**
 * DTO for analyzing incident distribution per IT service.
 */
public class ServiceAnalysisResponse {

    private Long serviceId;
    private String serviceName;
    private Integer criticality;
    private long totalIncidents;

    public ServiceAnalysisResponse() {}

    public ServiceAnalysisResponse(Long serviceId, String serviceName, Integer criticality, long totalIncidents) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.criticality = criticality;
        this.totalIncidents = totalIncidents;
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

    public Integer getCriticality() {
        return criticality;
    }

    public void setCriticality(Integer criticality) {
        this.criticality = criticality;
    }

    public long getTotalIncidents() {
        return totalIncidents;
    }

    public void setTotalIncidents(long totalIncidents) {
        this.totalIncidents = totalIncidents;
    }
}
