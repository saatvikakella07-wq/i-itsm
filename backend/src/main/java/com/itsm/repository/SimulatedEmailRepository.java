package com.itsm.repository;

import com.itsm.model.SimulatedEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository interface for simulated email logs.
 */
@Repository
public interface SimulatedEmailRepository extends JpaRepository<SimulatedEmail, Long> {
    List<SimulatedEmail> findByRecipientEmailOrderBySentAtDesc(String recipientEmail);
}
