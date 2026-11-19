package com.itsm.controller;

import com.itsm.dto.*;
import com.itsm.exception.ResourceNotFoundException;
import com.itsm.exception.UnauthorizedException;
import com.itsm.model.AuditLog;
import com.itsm.model.Incident;
import com.itsm.model.User;
import com.itsm.model.enums.IncidentCategory;
import com.itsm.model.enums.IncidentStatus;
import com.itsm.model.enums.Priority;
import com.itsm.model.enums.Role;
import com.itsm.repository.AuditLogRepository;
import com.itsm.repository.UserRepository;
import com.itsm.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller exposing REST endpoints for the complete IT incident lifecycle:
 * reporting, filtering, status transition, assignment, investigation, and closure.
 */
@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public IncidentController(IncidentService incidentService,
                              UserRepository userRepository,
                              AuditLogRepository auditLogRepository) {
        this.incidentService = incidentService;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    private User getCurrentUser(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new UnauthorizedException("User is not authenticated.");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));
    }

    /**
     * POST /api/incidents : Reports a new incident (Employees only).
     */
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<IncidentResponse> createIncident(@Valid @RequestBody IncidentCreateRequest request,
                                                           Authentication auth) {
        User user = getCurrentUser(auth);
        Incident incident = incidentService.createIncident(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(IncidentResponse.fromEntity(incident));
    }

    /**
     * GET /api/incidents : Multi-criteria search and filtered incident retrieval.
     */
    @GetMapping
    public ResponseEntity<List<IncidentResponse>> getIncidents(
            @RequestParam(required = false) IncidentStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) IncidentCategory category,
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) Long technicianId,
            @RequestParam(required = false) Long reporterId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String q,
            Authentication auth) {

        User currentUser = getCurrentUser(auth);

        // Role-based visibility scoping:
        // Employees only see their own submitted incidents unless admin
        if (currentUser.getRole() == Role.EMPLOYEE) {
            reporterId = currentUser.getId();
        }

        List<Incident> incidents = incidentService.searchIncidents(
                status, priority, category, serviceId, technicianId, reporterId, from, to, q
        );

        List<IncidentResponse> response = incidents.stream()
                .map(IncidentResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/incidents/{id} : Retrieves full details of an incident including complete audit history.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getIncidentById(@PathVariable Long id) {
        Incident incident = incidentService.getIncidentById(id);
        List<AuditLog> auditLogs = auditLogRepository.findByIncidentOrderByTimestampAsc(incident);

        Map<String, Object> result = new HashMap<>();
        result.put("incident", IncidentResponse.fromEntity(incident));
        result.put("auditHistory", auditLogs.stream().map(AuditLogResponse::fromEntity).toList());

        return ResponseEntity.ok(result);
    }

    /**
     * PATCH /api/incidents/{id}/status : Updates the lifecycle status of an incident.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<IncidentResponse> updateStatus(@PathVariable Long id,
                                                         @Valid @RequestBody StatusUpdateRequest request,
                                                         Authentication auth) {
        User user = getCurrentUser(auth);
        Incident updated = incidentService.updateStatus(id, request.getNewStatus(), request.getNotes(), user);
        return ResponseEntity.ok(IncidentResponse.fromEntity(updated));
    }

    /**
     * PATCH /api/incidents/{id}/resolve : Marks an incident as RESOLVED with resolution details (Technicians).
     */
    @PatchMapping("/{id}/resolve")
    public ResponseEntity<IncidentResponse> resolveIncident(@PathVariable Long id,
                                                            @Valid @RequestBody ResolveIncidentRequest request,
                                                            Authentication auth) {
        User user = getCurrentUser(auth);
        Incident updated = incidentService.resolveIncident(id, request.getResolutionDetails(), user);
        return ResponseEntity.ok(IncidentResponse.fromEntity(updated));
    }

    /**
     * PATCH /api/incidents/{id}/close : Closes an incident with optional feedback (Reporter/Employee).
     */
    @PatchMapping("/{id}/close")
    public ResponseEntity<IncidentResponse> closeIncident(@PathVariable Long id,
                                                          @RequestBody(required = false) CloseIncidentRequest request,
                                                          Authentication auth) {
        User user = getCurrentUser(auth);
        Integer feedback = request != null ? request.getFeedback() : null;
        Incident updated = incidentService.closeIncident(id, feedback, user);
        return ResponseEntity.ok(IncidentResponse.fromEntity(updated));
    }

    /**
     * PATCH /api/incidents/{id}/assign : Assigns or reassigns an incident to a technician (Admin).
     */
    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IncidentResponse> assignTechnician(@PathVariable Long id,
                                                             @Valid @RequestBody AssignTechnicianRequest request,
                                                             Authentication auth) {
        User user = getCurrentUser(auth);
        Incident updated = incidentService.assignTechnician(id, request.getTechnicianId(), request.getStrategy(), user);
        return ResponseEntity.ok(IncidentResponse.fromEntity(updated));
    }
}
