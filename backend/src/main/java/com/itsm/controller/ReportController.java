package com.itsm.controller;

import com.itsm.dto.DashboardReportResponse;
import com.itsm.dto.ServiceAnalysisResponse;
import com.itsm.dto.TechnicianWorkloadResponse;
import com.itsm.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller exposing analytical and reporting endpoints for administrators.
 */
@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * GET /api/reports/dashboard : Summary metrics (Total, Open, Critical, Resolved, SLA Violations).
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardReportResponse> getDashboardReport() {
        return ResponseEntity.ok(reportService.getDashboardReport());
    }

    /**
     * GET /api/reports/technician-workload : Active incident distribution per technician.
     */
    @GetMapping("/technician-workload")
    public ResponseEntity<List<TechnicianWorkloadResponse>> getTechnicianWorkload() {
        return ResponseEntity.ok(reportService.getTechnicianWorkloadReport());
    }

    /**
     * GET /api/reports/service-analysis : Incident density across organizational IT services.
     */
    @GetMapping("/service-analysis")
    public ResponseEntity<List<ServiceAnalysisResponse>> getServiceAnalysis() {
        return ResponseEntity.ok(reportService.getServiceAnalysisReport());
    }
}
