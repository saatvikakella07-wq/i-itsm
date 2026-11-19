package com.itsm;

import com.itsm.model.Incident;
import com.itsm.model.Technician;
import com.itsm.model.User;
import com.itsm.model.enums.IncidentCategory;
import com.itsm.model.enums.Role;
import com.itsm.strategy.LoadBasedAssignment;
import com.itsm.strategy.SkillBasedAssignment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests demonstrating Strategy Pattern implementation and behavior.
 */
class AssignmentStrategyTest {

    private final SkillBasedAssignment skillStrategy = new SkillBasedAssignment();
    private final LoadBasedAssignment loadStrategy = new LoadBasedAssignment();

    @Test
    @DisplayName("SkillBasedAssignment: should select skilled technician with lowest workload")
    void testSkillBasedAssignmentMatchingSkills() {
        User user1 = new User("Alice Network", "alice@org.com", "hash", Role.TECHNICIAN);
        user1.setId(1L);
        Technician tech1 = new Technician(user1, "NETWORK,HARDWARE");
        tech1.setCurrentWorkload(3);

        User user2 = new User("Bob Network", "bob@org.com", "hash", Role.TECHNICIAN);
        user2.setId(2L);
        Technician tech2 = new Technician(user2, "NETWORK,SECURITY");
        tech2.setCurrentWorkload(1); // lower workload

        User user3 = new User("Charlie Cloud", "charlie@org.com", "hash", Role.TECHNICIAN);
        user3.setId(3L);
        Technician tech3 = new Technician(user3, "CLOUD,DATABASE");
        tech3.setCurrentWorkload(0); // lowest workload but doesn't have NETWORK skill

        Incident incident = new Incident();
        incident.setCategory(IncidentCategory.NETWORK);

        Optional<Technician> assigned = skillStrategy.assign(incident, List.of(tech1, tech2, tech3));

        assertTrue(assigned.isPresent());
        assertEquals("Bob Network", assigned.get().getUser().getName());
        assertEquals(1, assigned.get().getCurrentWorkload());
    }

    @Test
    @DisplayName("SkillBasedAssignment: fallback to lowest loaded technician when no skill matches")
    void testSkillBasedAssignmentFallback() {
        User user1 = new User("Alice Hardware", "alice@org.com", "hash", Role.TECHNICIAN);
        Technician tech1 = new Technician(user1, "HARDWARE");
        tech1.setCurrentWorkload(5);

        User user2 = new User("Bob Database", "bob@org.com", "hash", Role.TECHNICIAN);
        Technician tech2 = new Technician(user2, "DATABASE");
        tech2.setCurrentWorkload(2);

        Incident incident = new Incident();
        incident.setCategory(IncidentCategory.CLOUD); // Neither has CLOUD

        Optional<Technician> assigned = skillStrategy.assign(incident, List.of(tech1, tech2));

        assertTrue(assigned.isPresent());
        assertEquals("Bob Database", assigned.get().getUser().getName());
    }

    @Test
    @DisplayName("LoadBasedAssignment: selects technician with lowest currentWorkload regardless of skills")
    void testLoadBasedAssignment() {
        User user1 = new User("Alice", "alice@org.com", "hash", Role.TECHNICIAN);
        Technician tech1 = new Technician(user1, "APPLICATION");
        tech1.setCurrentWorkload(4);

        User user2 = new User("Bob", "bob@org.com", "hash", Role.TECHNICIAN);
        Technician tech2 = new Technician(user2, "SECURITY");
        tech2.setCurrentWorkload(0);

        Incident incident = new Incident();
        incident.setCategory(IncidentCategory.APPLICATION);

        Optional<Technician> assigned = loadStrategy.assign(incident, List.of(tech1, tech2));

        assertTrue(assigned.isPresent());
        assertEquals("Bob", assigned.get().getUser().getName());
    }
}
