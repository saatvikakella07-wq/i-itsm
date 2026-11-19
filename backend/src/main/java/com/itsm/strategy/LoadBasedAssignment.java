package com.itsm.strategy;

import com.itsm.model.Incident;
import com.itsm.model.Technician;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Concrete Strategy: Assigns incidents based strictly on technician workload balancing.
 * Selects the available technician with the lowest currentWorkload, ensuring uniform work distribution.
 */
@Component("loadBasedAssignment")
public class LoadBasedAssignment implements AssignmentStrategy {

    @Override
    public Optional<Technician> assign(Incident incident, List<Technician> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }

        return candidates.stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsAvailable()))
                .min(Comparator.comparingInt(Technician::getCurrentWorkload));
    }

    @Override
    public String getStrategyName() {
        return "LOAD_BASED";
    }
}
