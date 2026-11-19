package com.itsm.controller;

import com.itsm.exception.ResourceNotFoundException;
import com.itsm.model.SlaPolicy;
import com.itsm.repository.SlaPolicyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for viewing and configuring Service Level Agreement policies.
 */
@RestController
@RequestMapping("/api/sla-policies")
public class SlaController {

    private final SlaPolicyRepository slaRepository;

    public SlaController(SlaPolicyRepository slaRepository) {
        this.slaRepository = slaRepository;
    }

    @GetMapping
    public ResponseEntity<List<SlaPolicy>> getAllPolicies() {
        return ResponseEntity.ok(slaRepository.findAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SlaPolicy> updatePolicy(@PathVariable Long id, @RequestBody SlaPolicy updated) {
        SlaPolicy policy = slaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SLA Policy not found with ID: " + id));

        if (updated.getResponseMinutes() != null) policy.setResponseMinutes(updated.getResponseMinutes());
        if (updated.getResolutionMinutes() != null) policy.setResolutionMinutes(updated.getResolutionMinutes());

        return ResponseEntity.ok(slaRepository.save(policy));
    }
}
