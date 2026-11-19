package com.itsm.repository;

import com.itsm.model.Incident;
import com.itsm.model.enums.IncidentCategory;
import com.itsm.model.enums.IncidentStatus;
import com.itsm.model.enums.Priority;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic JPA Specification builder for multi-criteria incident filtering.
 * Supports status, priority, category, service, technician, date range, and keyword search.
 */
public class IncidentSpecification {

    public static Specification<Incident> filter(
            IncidentStatus status,
            Priority priority,
            IncidentCategory category,
            Long serviceId,
            Long technicianId,
            Long reporterId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String query) {

        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (priority != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), priority));
            }
            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }
            if (serviceId != null) {
                predicates.add(criteriaBuilder.equal(root.get("service").get("id"), serviceId));
            }
            if (technicianId != null) {
                predicates.add(criteriaBuilder.equal(root.get("technician").get("id"), technicianId));
            }
            if (reporterId != null) {
                predicates.add(criteriaBuilder.equal(root.get("reporter").get("id"), reporterId));
            }
            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromDate));
            }
            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toDate));
            }
            if (query != null && !query.trim().isEmpty()) {
                String searchPattern = "%" + query.trim().toLowerCase() + "%";
                Predicate titleMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchPattern);
                Predicate descMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern);
                Predicate incNumMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("incidentNumber")), searchPattern);
                predicates.add(criteriaBuilder.or(titleMatch, descMatch, incNumMatch));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
