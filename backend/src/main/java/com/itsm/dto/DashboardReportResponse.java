package com.itsm.dto;

import java.util.Map;

/**
 * DTO returned by GET /api/reports/dashboard for the Admin Executive Dashboard.
 */
public class DashboardReportResponse {

    private long totalIncidents;
    private long openIncidents;
    private long criticalIncidents;
    private long resolvedIncidents;
    private long slaViolations;
    private Map<String, Long> incidentsByCategory;
    private Map<String, Long> incidentsByService;

    public DashboardReportResponse() {}

    public DashboardReportResponse(long totalIncidents, long openIncidents, long criticalIncidents,
                                   long resolvedIncidents, long slaViolations,
                                   Map<String, Long> incidentsByCategory,
                                   Map<String, Long> incidentsByService) {
        this.totalIncidents = totalIncidents;
        this.openIncidents = openIncidents;
        this.criticalIncidents = criticalIncidents;
        this.resolvedIncidents = resolvedIncidents;
        this.slaViolations = slaViolations;
        this.incidentsByCategory = incidentsByCategory;
        this.incidentsByService = incidentsByService;
    }

    public long getTotalIncidents() {
        return totalIncidents;
    }

    public void setTotalIncidents(long totalIncidents) {
        this.totalIncidents = totalIncidents;
    }

    public long getOpenIncidents() {
        return openIncidents;
    }

    public void setOpenIncidents(long openIncidents) {
        this.openIncidents = openIncidents;
    }

    public long getCriticalIncidents() {
        return criticalIncidents;
    }

    public void setCriticalIncidents(long criticalIncidents) {
        this.criticalIncidents = criticalIncidents;
    }

    public long getResolvedIncidents() {
        return resolvedIncidents;
    }

    public void setResolvedIncidents(long resolvedIncidents) {
        this.resolvedIncidents = resolvedIncidents;
    }

    public long getSlaViolations() {
        return slaViolations;
    }

    public void setSlaViolations(long slaViolations) {
        this.slaViolations = slaViolations;
    }

    public Map<String, Long> getIncidentsByCategory() {
        return incidentsByCategory;
    }

    public void setIncidentsByCategory(Map<String, Long> incidentsByCategory) {
        this.incidentsByCategory = incidentsByCategory;
    }

    public Map<String, Long> getIncidentsByService() {
        return incidentsByService;
    }

    public void setIncidentsByService(Map<String, Long> incidentsByService) {
        this.incidentsByService = incidentsByService;
    }
}
