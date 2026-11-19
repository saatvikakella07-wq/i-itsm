package com.itsm;

/**
 * Standalone executable test runner that validates all core OOP design patterns,
 * state machine transitions, priority calculation, and assignment strategies.
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("       RUNNING I-ITSM CORE VALIDATION SUITE      ");
        System.out.println("=================================================");

        int passed = 0;
        int failed = 0;

        // 1. PriorityService Tests
        System.out.println("\n[1/3] Testing PriorityService (Intelligent Priority Formula)...");
        PriorityServiceTest priorityTest = new PriorityServiceTest();
        priorityTest.setUp();
        try {
            priorityTest.testCalculatePriorityP1Maximum();
            priorityTest.testCalculatePriorityP1Boundary();
            priorityTest.testCalculatePriorityP2();
            priorityTest.testCalculatePriorityP3();
            priorityTest.testCalculatePriorityP4();
            priorityTest.testCalculatePriorityClamped();
            System.out.println("  ✓ All 6 Priority calculation test scenarios PASSED");
            passed += 6;
        } catch (Throwable t) {
            System.err.println("  ✗ PriorityService failure: " + t.getMessage());
            failed++;
        }

        // 2. AssignmentStrategy Tests (Strategy Pattern)
        System.out.println("\n[2/3] Testing AssignmentStrategy (Strategy Pattern)...");
        AssignmentStrategyTest strategyTest = new AssignmentStrategyTest();
        try {
            strategyTest.testSkillBasedAssignmentMatchingSkills();
            strategyTest.testSkillBasedAssignmentFallback();
            strategyTest.testLoadBasedAssignment();
            System.out.println("  ✓ All 3 Assignment Strategy pattern tests PASSED");
            passed += 3;
        } catch (Throwable t) {
            System.err.println("  ✗ AssignmentStrategy failure: " + t.getMessage());
            failed++;
        }

        // 3. IncidentStateService Tests (State Pattern)
        System.out.println("\n[3/3] Testing IncidentStateService (State Pattern Lifecycle)...");
        IncidentStateServiceTest stateTest = new IncidentStateServiceTest();
        stateTest.setUp();
        try {
            stateTest.testValidLifecycleProgression();
            stateTest.testInvalidTransitionNewToClosed();
            stateTest.testInvalidTransitionResolvedToNew();
            stateTest.testCannotTransitionFromClosed();
            System.out.println("  ✓ All 4 State lifecycle transition tests PASSED");
            passed += 4;
        } catch (Throwable t) {
            System.err.println("  ✗ IncidentStateService failure: " + t.getMessage());
            failed++;
        }

        System.out.println("\n=================================================");
        System.out.println(String.format("TEST RUN SUMMARY: %d PASSED, %d FAILED", passed, failed));
        System.out.println("=================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
