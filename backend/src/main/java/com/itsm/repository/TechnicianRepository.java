package com.itsm.repository;

import com.itsm.model.Technician;
import com.itsm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository interface for Technician entity data access.
 */
@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    Optional<Technician> findByUser(User user);
    Optional<Technician> findByUserId(Long userId);
    List<Technician> findByIsAvailableTrue();
}
