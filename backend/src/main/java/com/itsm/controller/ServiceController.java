package com.itsm.controller;

import com.itsm.model.ItService;
import com.itsm.repository.ItServiceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller providing IT Services catalog endpoints.
 */
@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ItServiceRepository serviceRepository;

    public ServiceController(ItServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @GetMapping
    public ResponseEntity<List<ItService>> getAllServices() {
        return ResponseEntity.ok(serviceRepository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItService> createService(@RequestBody ItService itService) {
        ItService saved = serviceRepository.save(itService);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
