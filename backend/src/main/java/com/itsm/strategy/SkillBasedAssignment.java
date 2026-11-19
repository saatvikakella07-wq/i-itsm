package com.itsm.strategy;

import com.itsm.model.Incident;
import com.itsm.model.Technician;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Concrete Strategy: Matches technicians based on domain skill proficiency.
 * Filters technicians whose skill profile includes the incident's operational category,
 * and breaks ties by selecting the technician with the lowest active workload.
 * If no skilled technician is available, it gracefully falls back to the lowest loaded technician.
 */
@Component("skillBasedAssignment")
public class SkillBasedAssignment implements AssignmentStrategy {

    @Override
    public Optional<Technician> assign(Incident incident, List<Technician> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }

        String categoryName = incident.getCategory() != null ? incident.getCategory().name() : "";

        // 1. Primary candidate pool: technicians with matching skills
        List<Technician> skilled = candidates.stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsAvailable()))
                .filter(t -> t.hasSkill(categoryName))
                .sorted(Comparator.comparingInt(Technician::getCurrentWorkload))
                .toList();

        if (!skilled.isEmpty()) {
            return Optional.of(skilled.get(0));
        }

        // 2. Graceful fallback: least loaded available technician
        return candidates.stream()
                .filter(t -> Boolean.TRUE.equals(t.getIsAvailable()))
                .min(Comparator.comparingInt(Technician::getCurrentWorkload));
    }

    @Override
    public String getStrategyName() {
        return "SKILL_BASED";
    }
}
