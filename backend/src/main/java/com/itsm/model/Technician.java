package com.itsm.model;

import jakarta.persistence.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Entity representing technical support personnel.
 * Extends the User domain model via a 1:1 relationship and stores technician skills,
 * current active workload, and operational availability.
 */
@Entity
@Table(name = "technicians")
public class Technician {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 255)
    private String skills = ""; // Comma-separated list of categories, e.g. "NETWORK,DATABASE"

    @Column(name = "current_workload", nullable = false)
    private Integer currentWorkload = 0;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    public Technician() {}

    public Technician(User user, String skills) {
        this.user = user;
        this.skills = skills != null ? skills : "";
        this.currentWorkload = 0;
        this.isAvailable = true;
    }

    public List<String> getSkillList() {
        if (skills == null || skills.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(skills.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public boolean hasSkill(String category) {
        if (category == null) return false;
        return getSkillList().contains(category.trim().toUpperCase());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public Integer getCurrentWorkload() {
        return currentWorkload;
    }

    public void setCurrentWorkload(Integer currentWorkload) {
        this.currentWorkload = currentWorkload;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean available) {
        isAvailable = available;
    }
}
