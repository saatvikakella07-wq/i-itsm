package com.itsm.controller;

import com.itsm.dto.IncidentResponse;
import com.itsm.dto.TechnicianWorkloadResponse;
import com.itsm.dto.UserResponse;
import com.itsm.exception.ResourceNotFoundException;
import com.itsm.exception.UnauthorizedException;
import com.itsm.model.Incident;
import com.itsm.model.User;
import com.itsm.model.enums.Role;
import com.itsm.repository.IncidentRepository;
import com.itsm.repository.UserRepository;
import com.itsm.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller exposing user administration and technician workload endpoints.
 */
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final IncidentRepository incidentRepository;

    public UserController(UserService userService,
                          UserRepository userRepository,
                          IncidentRepository incidentRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.incidentRepository = incidentRepository;
    }

    private User getCurrentUser(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new UnauthorizedException("User is not authenticated.");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));
    }

    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getMe(Authentication auth) {
        User user = getCurrentUser(auth);
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @RequestBody Map<String, Object> body) {
        Role role = body.containsKey("role") ? Role.valueOf((String) body.get("role")) : null;
        Boolean isActive = body.containsKey("isActive") ? (Boolean) body.get("isActive") : null;
        return ResponseEntity.ok(userService.updateUser(id, role, isActive));
    }

    @GetMapping("/technicians")
    public ResponseEntity<List<TechnicianWorkloadResponse>> getTechnicians() {
        return ResponseEntity.ok(userService.getAllTechnicians());
    }

    @GetMapping("/technicians/me/incidents")
    public ResponseEntity<List<IncidentResponse>> getMyAssignedIncidents(Authentication auth) {
        User user = getCurrentUser(auth);
        List<Incident> list = incidentRepository.findByTechnician(user);
        return ResponseEntity.ok(list.stream().map(IncidentResponse::fromEntity).toList());
    }
}
