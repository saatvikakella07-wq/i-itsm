package com.itsm.strategy;

import com.itsm.model.Incident;
import com.itsm.model.Technician;

import java.util.List;
import java.util.Optional;

/**
 * Strategy Pattern Interface: Defines the contract for dynamic technician assignment algorithms.
 * Allows interchangeability between skill-based matching and workload-balanced routing.
 */
public interface AssignmentStrategy {

    /**
     * Recommends or assigns an appropriate technician for the given incident.
     *
     * @param incident The incident requiring assignment.
     * @param candidates The list of active, available technicians.
     * @return An Optional containing the selected technician, or empty if no candidate matches.
     */
    Optional<Technician> assign(Incident incident, List<Technician> candidates);

    /**
     * Unique identifier for this assignment strategy.
     */
    String getStrategyName();
}
