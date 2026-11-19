package com.itsm.service;

import com.itsm.model.enums.Priority;
import org.springframework.stereotype.Service;

/**
 * Service responsible for intelligent, automated incident priority determination.
 * Computes priority based on business impact, urgency, and IT service criticality.
 */
@Service
public class PriorityService {

    /**
     * Calculates the incident priority using the formula:
     * Priority Score = Impact (1-3) × Urgency (1-3) × Service Criticality (1-3)
     *
     * Classification scale:
     * - Score >= 9 : P1 (Critical)
     * - Score >= 5 : P2 (High)
     * - Score >= 2 : P3 (Medium)
     * - Score < 2  : P4 (Low)
     *
     * @param impact The extent of disruption (1: Low, 2: Medium, 3: High).
     * @param urgency The time sensitivity (1: Low, 2: Medium, 3: High).
     * @param serviceCriticality The organizational importance of the affected service (1: Low, 2: Medium, 3: High).
     * @return The computed Priority enum (P1, P2, P3, or P4).
     */
    public Priority calculatePriority(int impact, int urgency, int serviceCriticality) {
        int clampedImpact = Math.max(1, Math.min(3, impact));
        int clampedUrgency = Math.max(1, Math.min(3, urgency));
        int clampedCriticality = Math.max(1, Math.min(3, serviceCriticality));

        int score = clampedImpact * clampedUrgency * clampedCriticality;

        if (score >= 9) {
            return Priority.P1;
        } else if (score >= 5) {
            return Priority.P2;
        } else if (score >= 2) {
            return Priority.P3;
        } else {
            return Priority.P4;
        }
    }
}
