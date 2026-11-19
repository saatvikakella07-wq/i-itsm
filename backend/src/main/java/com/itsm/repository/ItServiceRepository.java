package com.itsm.repository;

import com.itsm.model.ItService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data repository interface for IT Services catalog.
 */
@Repository
public interface ItServiceRepository extends JpaRepository<ItService, Long> {
    Optional<ItService> findByName(String name);
}
