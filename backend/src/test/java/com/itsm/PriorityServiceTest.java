package com.itsm;

import com.itsm.model.enums.Priority;
import com.itsm.service.PriorityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for PriorityService verifying mathematical formula:
 * Score = Impact (1-3) × Urgency (1-3) × Service Criticality (1-3)
 */
class PriorityServiceTest {

    private PriorityService priorityService;

    @BeforeEach
    void setUp() {
        priorityService = new PriorityService();
    }

    @Test
    @DisplayName("Should return P1 when score >= 9 (e.g. 3 x 3 x 3 = 27)")
    void testCalculatePriorityP1Maximum() {
        Priority priority = priorityService.calculatePriority(3, 3, 3);
        assertEquals(Priority.P1, priority);
    }

    @Test
    @DisplayName("Should return P1 when score is exactly 9 (e.g. 3 x 3 x 1 = 9)")
    void testCalculatePriorityP1Boundary() {
        Priority priority = priorityService.calculatePriority(3, 3, 1);
        assertEquals(Priority.P1, priority);
    }

    @Test
    @DisplayName("Should return P2 when score >= 5 and < 9 (e.g. 2 x 3 x 1 = 6)")
    void testCalculatePriorityP2() {
        Priority priority = priorityService.calculatePriority(2, 3, 1);
        assertEquals(Priority.P2, priority);
    }

    @Test
    @DisplayName("Should return P3 when score >= 2 and < 5 (e.g. 1 x 2 x 1 = 2)")
    void testCalculatePriorityP3() {
        Priority priority = priorityService.calculatePriority(1, 2, 1);
        assertEquals(Priority.P3, priority);
    }

    @Test
    @DisplayName("Should return P4 when score < 2 (e.g. 1 x 1 x 1 = 1)")
    void testCalculatePriorityP4() {
        Priority priority = priorityService.calculatePriority(1, 1, 1);
        assertEquals(Priority.P4, priority);
    }

    @Test
    @DisplayName("Should clamp out-of-range negative or high inputs to [1, 3]")
    void testCalculatePriorityClamped() {
        Priority maxClamped = priorityService.calculatePriority(10, 10, 10);
        assertEquals(Priority.P1, maxClamped);

        Priority minClamped = priorityService.calculatePriority(-5, 0, -2);
        assertEquals(Priority.P4, minClamped);
    }
}
