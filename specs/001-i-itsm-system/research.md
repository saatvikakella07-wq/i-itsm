# Research: I-ITSM Technical Decisions

**Feature**: 001-i-itsm-system
**Date**: 2026-10-21

---

## 1. Backend Framework

- **Decision**: Java 21 + Spring Boot 3.x
- **Rationale**: Spring Boot provides annotation-based OOP (services, repositories, controllers) that maps directly to OOP course concepts. Spring Security handles JWT out of the box. Spring Data JPA eliminates boilerplate database code.
- **Alternatives considered**: Node.js/Express (less OOP-natural), Django (Python, not Java)

## 2. Frontend Framework

- **Decision**: React 18 with Vite build tool
- **Rationale**: Component-based architecture is beginner-friendly. Vite is faster than Create React App. React Router handles navigation. Axios handles HTTP calls to Spring Boot.
- **Alternatives considered**: Angular (more complex for beginners), Vue (smaller community)

## 3. Database

- **Decision**: PostgreSQL 15 with Spring Data JPA (Hibernate)
- **Rationale**: Relational integrity is critical — incidents reference users, services, technicians, SLA policies. PostgreSQL is free, powerful, and the spec explicitly names it as a recommended option. JPA annotations make entity relationships readable.
- **Alternatives considered**: MySQL (similar, less feature-rich), MongoDB (no relations — bad fit)

## 4. Authentication

- **Decision**: JWT (JSON Web Tokens) via Spring Security + jjwt library
- **Rationale**: Stateless — no server-side session storage needed. Roles (EMPLOYEE/TECHNICIAN/ADMIN) are embedded in the token. Frontend stores token in localStorage and sends it in every request header.
- **Alternatives considered**: Session-based auth (requires sticky sessions), OAuth2 (too complex for scope)

## 5. Priority Calculation Algorithm

- **Decision**: Rule-based scoring: `Priority Score = Impact (1-3) × Urgency (1-3) × Service Criticality (1-3)`
  - Score 18–9 → P1 Critical
  - Score 8–5 → P2 High
  - Score 4–2 → P3 Medium
  - Score 1 → P4 Low
- **Rationale**: Simple, transparent, justifiable in the report. No ML required.
- **Alternatives considered**: ML-based (out of scope), weighted scoring (added complexity without gain)

## 6. Technician Assignment Strategies

- **Decision**: Strategy Pattern with two implementations:
  1. **SkillBasedAssignment** — Find technicians whose skill list includes the incident's category; pick the one with lowest workload among matches
  2. **LoadBasedAssignment** — Pick the available technician with the lowest current active incident count regardless of category
- **Rationale**: Strategy pattern is explicitly required by the OOP spec. Two strategies satisfy the "at least two" requirement.
- **Alternatives considered**: Round-robin (no skill matching), experience-based (requires history data)

## 7. Notification Mechanism

- **Decision**: Observer Pattern with two implementations:
  1. **InAppNotifier** — Saves a `Notification` record to the DB. Frontend polls or fetches on load.
  2. **AuditLogger** — Saves an `AuditLog` record for every incident event. Both run on every incident state change.
- **Rationale**: Observer pattern maps directly to the OOP course requirement. In-app notifications are simple to build. AuditLogger doubles as both notification and audit trail.
- **Alternatives considered**: WebSockets for real-time (complex, optional), real email (requires SMTP setup)

## 8. Escalation (Intelligent Feature)

- **Decision**: Spring `@Scheduled` task running every 60 seconds. Queries for P1 incidents that are ASSIGNED but not ACKNOWLEDGED and were created more than 5 minutes ago. Sends escalation notification to all ADMINs.
- **Rationale**: Spring Scheduler is built-in, no extra dependencies. Simple to understand and demonstrate.

## 9. Duplicate Detection (Intelligent Feature)

- **Decision**: On incident creation, query for open incidents with the same category and service created in the last 24 hours. If found, set a `potentialDuplicate=true` flag and link to the parent incident ID.
- **Rationale**: Pure database query — no ML needed. Satisfies "intelligent feature" requirement.

## 10. Constitution Compliance Check

| Principle | Status |
|-----------|--------|
| I. OOP-First (Strategy + Observer + State patterns) | ✅ Strategy for assignment, Observer for notifications, State for lifecycle |
| II. Beginner-friendly (comments on all classes) | ✅ Enforced via code review checklist in tasks |
| III. Full-stack separation (React ↔ REST ↔ Spring) | ✅ No business logic in React |
| IV. MVP before polish | ✅ Phase ordering enforces this |
| V. Audit everything | ✅ AuditLogger observer on every state change |
