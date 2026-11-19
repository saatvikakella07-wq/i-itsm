package com.itsm.model;

import jakarta.persistence.*;

/**
 * Entity representing an organizational IT Service (e.g., Payment Application, Email, VPN).
 * Defines business criticality (1=Low, 2=Medium, 3=High) used in priority scoring.
 */
@Entity
@Table(name = "it_services")
public class ItService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer criticality; // 1 (Low), 2 (Medium), 3 (High)

    public ItService() {}

    public ItService(String name, String description, Integer criticality) {
        this.name = name;
        this.description = description;
        this.criticality = criticality;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCriticality() {
        return criticality;
    }

    public void setCriticality(Integer criticality) {
        this.criticality = criticality;
    }
}
