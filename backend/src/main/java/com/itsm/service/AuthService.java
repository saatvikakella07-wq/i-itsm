package com.itsm.service;

import com.itsm.dto.AuthResponse;
import com.itsm.dto.LoginRequest;
import com.itsm.dto.RegisterRequest;
import com.itsm.exception.UnauthorizedException;
import com.itsm.model.Technician;
import com.itsm.model.User;
import com.itsm.model.enums.Role;
import com.itsm.repository.TechnicianRepository;
import com.itsm.repository.UserRepository;
import com.itsm.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service managing user registration and JWT authentication credentials.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TechnicianRepository technicianRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       TechnicianRepository technicianRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.technicianRepository = technicianRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registers a new user with BCrypt hashed credentials.
     * If registering as a TECHNICIAN, an associated Technician profile is automatically provisioned.
     */
    @Transactional
    public User register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("An account with this email address already exists.");
        }

        User user = new User(
                req.getName(),
                req.getEmail().toLowerCase().trim(),
                passwordEncoder.encode(req.getPassword()),
                req.getRole()
        );

        User savedUser = userRepository.save(user);

        // Auto-provision technician record if role is TECHNICIAN
        if (savedUser.getRole() == Role.TECHNICIAN) {
            Technician tech = new Technician(savedUser, "APPLICATION,SOFTWARE,NETWORK");
            technicianRepository.save(tech);
        }

        return savedUser;
    }

    /**
     * Authenticates user credentials and issues a signed JWT token.
     */
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password.");
        }

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new UnauthorizedException("Account is deactivated. Please contact your system administrator.");
        }

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
