package com.itsm.service;

import com.itsm.dto.DashboardReportResponse;
import com.itsm.dto.ServiceAnalysisResponse;
import com.itsm.dto.TechnicianWorkloadResponse;
import com.itsm.model.ItService;
import com.itsm.model.enums.IncidentStatus;
import com.itsm.model.enums.Priority;
import com.itsm.repository.IncidentRepository;
import com.itsm.repository.ItServiceRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service generating operational analytics, SLA violation metrics, and executive summaries.
 */
@Service
public class ReportService {

    private final IncidentRepository incidentRepository;
    private final ItServiceRepository serviceRepository;
    private final UserService userService;

    public ReportService(IncidentRepository incidentRepository,
                         ItServiceRepository serviceRepository,
                         UserService userService) {
        this.incidentRepository = incidentRepository;
        this.serviceRepository = serviceRepository;
        this.userService = userService;
    }

    /**
     * Aggregates the 5 essential dashboard metrics required by FR-017 / Scenario 4.
     */
    public DashboardReportResponse getDashboardReport() {
        long total = incidentRepository.count();
        long resolved = incidentRepository.countByStatus(IncidentStatus.RESOLVED);
        long closed = incidentRepository.countByStatus(IncidentStatus.CLOSED);
        long open = total - (resolved + closed);
        long critical = incidentRepository.countByPriority(Priority.P1);
        long slaViolations = incidentRepository.countBySlaViolatedTrue();

        Map<String, Long> categoryMap = new HashMap<>();
        for (Object[] row : incidentRepository.countByCategory()) {
            if (row[0] != null) {
                categoryMap.put(row[0].toString(), (Long) row[1]);
            }
        }

        Map<String, Long> serviceMap = new HashMap<>();
        for (Object[] row : incidentRepository.countByService()) {
            if (row[0] != null) {
                serviceMap.put(row[0].toString(), (Long) row[1]);
            }
        }

        return new DashboardReportResponse(total, open, critical, resolved + closed, slaViolations, categoryMap, serviceMap);
    }

    public List<TechnicianWorkloadResponse> getTechnicianWorkloadReport() {
        return userService.getAllTechnicians();
    }

    public List<ServiceAnalysisResponse> getServiceAnalysisReport() {
        List<ItService> services = serviceRepository.findAll();
        Map<String, Long> serviceCounts = new HashMap<>();
        for (Object[] row : incidentRepository.countByService()) {
            if (row[0] != null) {
                serviceCounts.put(row[0].toString(), (Long) row[1]);
            }
        }

        return services.stream().map(s -> new ServiceAnalysisResponse(
                s.getId(),
                s.getName(),
                s.getCriticality(),
                serviceCounts.getOrDefault(s.getName(), 0L)
        )).toList();
    }
}
