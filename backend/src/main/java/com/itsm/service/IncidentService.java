package com.itsm.service;

import com.itsm.dto.IncidentCreateRequest;
import com.itsm.exception.ResourceNotFoundException;
import com.itsm.exception.UnauthorizedException;
import com.itsm.model.*;
import com.itsm.model.enums.IncidentCategory;
import com.itsm.model.enums.IncidentStatus;
import com.itsm.model.enums.Priority;
import com.itsm.model.enums.Role;
import com.itsm.observer.AuditLogger;
import com.itsm.observer.IncidentObserver;
import com.itsm.repository.*;
import com.itsm.strategy.AssignmentStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service managing incident creation, prioritization, SLA assignment, duplicate detection,
 * technician assignment strategies, and resolution tracking.
 */
@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;
    private final TechnicianRepository technicianRepository;
    private final ItServiceRepository serviceRepository;
    private final SlaPolicyRepository slaRepository;
    private final PriorityService priorityService;
    private final IncidentStateService stateService;
    private final AssignmentStrategy skillBasedAssignment;
    private final AssignmentStrategy loadBasedAssignment;
    private final AuditLogger auditLogger;
    private final List<IncidentObserver> observers;

    public IncidentService(IncidentRepository incidentRepository,
                           UserRepository userRepository,
                           TechnicianRepository technicianRepository,
                           ItServiceRepository serviceRepository,
                           SlaPolicyRepository slaRepository,
                           PriorityService priorityService,
                           IncidentStateService stateService,
                           @Qualifier("skillBasedAssignment") AssignmentStrategy skillBasedAssignment,
                           @Qualifier("loadBasedAssignment") AssignmentStrategy loadBasedAssignment,
                           AuditLogger auditLogger,
                           List<IncidentObserver> observers) {
        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
        this.technicianRepository = technicianRepository;
        this.serviceRepository = serviceRepository;
        this.slaRepository = slaRepository;
        this.priorityService = priorityService;
        this.stateService = stateService;
        this.skillBasedAssignment = skillBasedAssignment;
        this.loadBasedAssignment = loadBasedAssignment;
        this.auditLogger = auditLogger;
        this.observers = observers;
    }

    /**
     * Reports and processes a new incident through automated classification,
     * priority scoring, SLA computation, duplicate detection, and technician assignment.
     */
    @Transactional
    public Incident createIncident(IncidentCreateRequest req, Long reporterId) {
        // Step 1: Validate reporter and IT service
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Reporter not found with ID: " + reporterId));

        ItService service = serviceRepository.findById(req.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("IT Service not found with ID: " + req.getServiceId()));

        // Step 2: Intelligent Priority Calculation (Score = Impact × Urgency × Criticality)
        Priority priority = priorityService.calculatePriority(req.getImpact(), req.getUrgency(), service.getCriticality());

        // Step 3: Compute SLA Deadlines from SLA Policy
        SlaPolicy sla = slaRepository.findByPriority(priority)
                .orElseGet(() -> getDefaultSlaPolicy(priority));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime responseDeadline = now.plusMinutes(sla.getResponseMinutes());
        LocalDateTime resolutionDeadline = now.plusMinutes(sla.getResolutionMinutes());

        // Step 4: Construct Incident Entity with unique incident number
        Incident incident = new Incident();
        incident.setTitle(req.getTitle());
        incident.setDescription(req.getDescription());
        incident.setCategory(req.getCategory());
        incident.setImpact(req.getImpact());
        incident.setUrgency(req.getUrgency());
        incident.setReporter(reporter);
        incident.setService(service);
        incident.setPriority(priority);
        incident.setSlaResponseDeadline(responseDeadline);
        incident.setSlaResolutionDeadline(resolutionDeadline);
        incident.setStatus(IncidentStatus.NEW);

        // Preliminary save to generate database ID for incident number
        incident.setIncidentNumber("INC-TMP-" + System.currentTimeMillis());
        Incident saved = incidentRepository.save(incident);
        saved.setIncidentNumber(String.format("INC-%04d", saved.getId()));

        // Step 5: Duplicate Detection (Intelligent Feature)
        // Searches for active incidents with same service and category reported in the last 24 hours
        LocalDateTime duplicateWindow = now.minusHours(24);
        List<Incident> duplicates = incidentRepository.findPotentialDuplicates(service.getId(), req.getCategory(), duplicateWindow);
        // Exclude the current incident
        duplicates = duplicates.stream().filter(i -> !i.getId().equals(saved.getId())).toList();

        if (!duplicates.isEmpty()) {
            saved.setPotentialDuplicate(true);
            saved.setParentIncident(duplicates.get(0));
            auditLogger.onIncidentEvent(saved, "POTENTIAL_DUPLICATE", reporter);
        }

        // Advance to CLASSIFIED
        saved.setStatus(IncidentStatus.CLASSIFIED);

        // Step 6: Dynamic Technician Assignment using Strategy Pattern
        List<Technician> availableTechnicians = technicianRepository.findByIsAvailableTrue();
        Optional<Technician> assignedTechnician = skillBasedAssignment.assign(saved, availableTechnicians);

        if (assignedTechnician.isPresent()) {
            Technician tech = assignedTechnician.get();
            saved.setTechnician(tech.getUser());
            saved.setStatus(IncidentStatus.ASSIGNED);

            // Increment technician workload
            tech.setCurrentWorkload(tech.getCurrentWorkload() + 1);
            technicianRepository.save(tech);
        }

        Incident finalSaved = incidentRepository.save(saved);

        // Step 7: Record Audit Log and Notify Observers
        auditLogger.onIncidentEvent(finalSaved, "CREATED", reporter);
        if (finalSaved.getTechnician() != null) {
            for (IncidentObserver obs : observers) {
                obs.onIncidentEvent(finalSaved, "ASSIGNED", reporter);
            }
        }

        return finalSaved;
    }

    /**
     * Retrieves an incident by its unique ID.
     */
    public Incident getIncidentById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with ID: " + id));
    }

    /**
     * Multi-criteria incident search supporting filtering by status, priority, category,
     * service, technician, reporter, date range, and keyword query.
     */
    public List<Incident> searchIncidents(IncidentStatus status,
                                          Priority priority,
                                          IncidentCategory category,
                                          Long serviceId,
                                          Long technicianId,
                                          Long reporterId,
                                          LocalDateTime fromDate,
                                          LocalDateTime toDate,
                                          String query) {
        Specification<Incident> spec = IncidentSpecification.filter(
                status, priority, category, serviceId, technicianId, reporterId, fromDate, toDate, query
        );
        return incidentRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    /**
     * Updates the status of an incident adhering to the State Pattern rules.
     */
    @Transactional
    public Incident updateStatus(Long incidentId, IncidentStatus newStatus, String notes, User actor) {
        Incident incident = getIncidentById(incidentId);
        return stateService.transition(incident, newStatus, actor, notes);
    }

    /**
     * Resolves an incident, recording technician findings and updating active workloads.
     */
    @Transactional
    public Incident resolveIncident(Long incidentId, String resolutionDetails, User actor) {
        Incident incident = getIncidentById(incidentId);

        if (actor.getRole() != Role.TECHNICIAN && actor.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only technicians can resolve incidents.");
        }
        if (actor.getRole() == Role.TECHNICIAN && incident.getTechnician() != null
                && !incident.getTechnician().getId().equals(actor.getId())) {
            throw new UnauthorizedException("You are not assigned to resolve this incident.");
        }

        incident.setResolutionDetails(resolutionDetails);

        // Transition through State Machine: IN_PROGRESS -> RESOLVED
        Incident updated = stateService.transition(incident, IncidentStatus.RESOLVED, actor, "Resolved: " + resolutionDetails);

        // Decrement technician active workload
        if (updated.getTechnician() != null) {
            technicianRepository.findByUserId(updated.getTechnician().getId()).ifPresent(tech -> {
                tech.setCurrentWorkload(Math.max(0, tech.getCurrentWorkload() - 1));
                technicianRepository.save(tech);
            });
        }

        return updated;
    }

    /**
     * Closes an incident and records employee feedback rating.
     */
    @Transactional
    public Incident closeIncident(Long incidentId, Integer feedback, User actor) {
        Incident incident = getIncidentById(incidentId);
        if (feedback != null) {
            incident.setFeedback(feedback);
        }
        return stateService.transition(incident, IncidentStatus.CLOSED, actor,
                feedback != null ? "Closed with rating " + feedback + "/5" : "Incident verified and closed");
    }

    /**
     * Manually or programmatically reassigns an incident to a technician.
     */
    @Transactional
    public Incident assignTechnician(Long incidentId, Long technicianUserId, String strategyName, User actor) {
        Incident incident = getIncidentById(incidentId);

        // Workload adjustment for previous technician
        if (incident.getTechnician() != null) {
            technicianRepository.findByUserId(incident.getTechnician().getId()).ifPresent(oldTech -> {
                oldTech.setCurrentWorkload(Math.max(0, oldTech.getCurrentWorkload() - 1));
                technicianRepository.save(oldTech);
            });
        }

        // New technician assignment
        User newTechUser = userRepository.findById(technicianUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Technician user not found with ID: " + technicianUserId));

        Technician newTech = technicianRepository.findByUserId(technicianUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Technician record not found for user: " + technicianUserId));

        incident.setTechnician(newTechUser);
        if (incident.getStatus() == IncidentStatus.NEW || incident.getStatus() == IncidentStatus.CLASSIFIED) {
            incident.setStatus(IncidentStatus.ASSIGNED);
        }

        newTech.setCurrentWorkload(newTech.getCurrentWorkload() + 1);
        technicianRepository.save(newTech);

        Incident saved = incidentRepository.save(incident);

        auditLogger.onIncidentEvent(saved, "ASSIGNED", actor);
        for (IncidentObserver obs : observers) {
            obs.onIncidentEvent(saved, "ASSIGNED", actor);
        }

        return saved;
    }

    private SlaPolicy getDefaultSlaPolicy(Priority priority) {
        return switch (priority) {
            case P1 -> new SlaPolicy(Priority.P1, 5, 30);
            case P2 -> new SlaPolicy(Priority.P2, 15, 120);
            case P3 -> new SlaPolicy(Priority.P3, 60, 480);
            case P4 -> new SlaPolicy(Priority.P4, 240, 1440);
        };
    }
}
