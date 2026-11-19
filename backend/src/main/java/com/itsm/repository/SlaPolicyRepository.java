package com.itsm.repository;

import com.itsm.model.SlaPolicy;
import com.itsm.model.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data repository interface for SLA Policies.
 */
@Repository
public interface SlaPolicyRepository extends JpaRepository<SlaPolicy, Long> {
    Optional<SlaPolicy> findByPriority(Priority priority);
}
