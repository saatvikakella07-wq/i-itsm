package com.itsm.service;

import com.itsm.dto.TechnicianWorkloadResponse;
import com.itsm.dto.UserResponse;
import com.itsm.exception.ResourceNotFoundException;
import com.itsm.model.Technician;
import com.itsm.model.User;
import com.itsm.model.enums.Role;
import com.itsm.repository.TechnicianRepository;
import com.itsm.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service managing administrative user updates, role assignments, and technician queries.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final TechnicianRepository technicianRepository;

    public UserService(UserRepository userRepository, TechnicianRepository technicianRepository) {
        this.userRepository = userRepository;
        this.technicianRepository = technicianRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    @Transactional
    public UserResponse updateUser(Long id, Role role, Boolean isActive) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        if (role != null) {
            user.setRole(role);
            if (role == Role.TECHNICIAN && technicianRepository.findByUserId(id).isEmpty()) {
                technicianRepository.save(new Technician(user, "APPLICATION,NETWORK,DATABASE"));
            }
        }
        if (isActive != null) {
            user.setIsActive(isActive);
        }

        return UserResponse.fromEntity(userRepository.save(user));
    }

    public List<TechnicianWorkloadResponse> getAllTechnicians() {
        List<Technician> techs = technicianRepository.findAll();
        return techs.stream().map(t -> new TechnicianWorkloadResponse(
                t.getId(),
                t.getUser().getId(),
                t.getUser().getName(),
                t.getUser().getEmail(),
                t.getSkillList(),
                t.getCurrentWorkload(),
                t.getIsAvailable()
        )).toList();
    }
}
