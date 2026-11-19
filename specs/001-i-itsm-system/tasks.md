# Tasks: I-ITSM — Intelligent IT Service Management System

**Feature**: 001-i-itsm-system
**Stack**: Java 21 + Spring Boot 3.x | React 18 + Vite | PostgreSQL 15
**Team**: 4 members | **Deadline**: 22 November 2026

> 📌 **Member Guide**: M1=Frontend, M2=Backend/APIs, M3=Domain Model/OOP, M4=Database/Testing
> Tasks marked [P] can be done in parallel by different team members.

---

## Phase 1: Setup (Project Initialization)

**Purpose**: Create the project skeleton, repo, and tooling so all members can start working.

- [x] T001 Create GitHub repository `i-itsm`, add all 4 members as collaborators, create `main` and `develop` branches
- [x] T002 Initialize Spring Boot project at `backend/` using start.spring.io — add dependencies: Spring Web, Spring Data JPA, PostgreSQL Driver, Spring Security, Lombok, Validation
- [x] T003 [P] Initialize React frontend at `frontend/` using `npm create vite@latest frontend -- --template react`, install `axios` and `react-router-dom`
- [x] T004 [P] Create `database/schema.sql` and `database/seed.sql` files (see data-model.md for table definitions)
- [x] T005 [P] Configure `backend/src/main/resources/application.properties` with PostgreSQL connection, JPA settings (`ddl-auto=validate`), and JWT secret placeholder
- [x] T006 [P] Create root `README.md` with: project description, team members, setup instructions (backend, frontend, database), and how to run
- [x] T007 Create `.gitignore` covering Java (target/, *.class), Node (node_modules/), env files, and IDE files

**Checkpoint**: All members can clone the repo, run the backend, and open the React dev server.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before any user story work begins.
**⚠️ CRITICAL**: No user story work can start until this phase is complete.

- [x] T008 [P] Create PostgreSQL database `itsm_db` and run `database/schema.sql` to create all tables (users, technicians, it_services, incidents, sla_policies, audit_logs, notifications)
- [x] T009 [P] Create all JPA Entity classes in `backend/src/main/java/com/itsm/model/`: `User.java`, `Technician.java`, `ItService.java`, `Incident.java`, `SlaPolicy.java`, `AuditLog.java`, `Notification.java` — include all fields from data-model.md with `@Entity`, `@Column`, `@ManyToOne` annotations and a short JavaDoc comment on each class
- [x] T010 [P] Create all Enum classes in `backend/src/main/java/com/itsm/model/enums/`: `Role.java` (EMPLOYEE/TECHNICIAN/ADMIN), `IncidentStatus.java` (NEW through CLOSED), `Priority.java` (P1–P4), `IncidentCategory.java` (9 categories), `NotificationType.java`
- [x] T011 Create Spring Data JPA Repository interfaces in `backend/src/main/java/com/itsm/repository/`: `UserRepository.java`, `IncidentRepository.java`, `TechnicianRepository.java`, `SlaRepository.java`, `AuditLogRepository.java`, `NotificationRepository.java` — each extends `JpaRepository`
- [x] T012 [P] Implement JWT utility class `backend/src/main/java/com/itsm/security/JwtUtil.java` — methods: `generateToken(User)`, `validateToken(String)`, `extractEmail(String)`
- [x] T013 [P] Implement `backend/src/main/java/com/itsm/security/SecurityConfig.java` — configure Spring Security to: permit `/api/auth/**` publicly, require JWT Bearer token for all other routes, define role-based access rules
- [x] T014 Create global exception handler `backend/src/main/java/com/itsm/exception/GlobalExceptionHandler.java` with `@RestControllerAdvice` — handle: `InvalidStateTransitionException`, `ResourceNotFoundException`, `UnauthorizedException`, validation errors; each returns a JSON `{ "error": "...", "status": 4xx }`
- [x] T015 [P] Create custom exception classes in `backend/src/main/java/com/itsm/exception/`: `InvalidStateTransitionException.java`, `ResourceNotFoundException.java`, `UnauthorizedException.java` — each with a descriptive constructor message
- [x] T016 Seed the database by running `database/seed.sql` — insert: 3 SLA policies (P1/P2/P3/P4), 5 IT services with criticality 1–3, 1 admin user, 2 technician users, 1 employee user (passwords BCrypt hashed)
- [x] T017 [P] Set up React Router in `frontend/src/App.jsx` — define routes: `/login`, `/register`, `/dashboard`, `/incidents`, `/incidents/:id`, `/admin`; add a protected route wrapper that checks for JWT token in localStorage

**Checkpoint**: Backend starts without errors, JWT auth works via a test POST to `/api/auth/login`, React router navigates between stub pages.

---

## Phase 3: User Story 1 — Employee Reports an Incident (Priority: P1) 🎯 MVP

**Goal**: An employee can register, log in, create an incident, and see it in their list with auto-calculated priority and technician assignment.

**Independent Test**: See `quickstart.md` Scenario 1.

### Implementation

- [x] T018 [P] [US1] Create `AuthController.java` in `backend/.../controller/` with POST `/api/auth/register` and POST `/api/auth/login` — register saves BCrypt-hashed password; login returns JWT token, role, userId, name
- [x] T019 [P] [US1] Create `AuthService.java` in `backend/.../service/` — methods: `register(RegisterRequest)`, `login(LoginRequest)`; throw `UnauthorizedException` for bad credentials
- [x] T020 [P] [US1] Create DTO classes in `backend/.../dto/`: `RegisterRequest.java`, `LoginRequest.java`, `AuthResponse.java`, `IncidentCreateRequest.java`, `IncidentResponse.java` — annotate with `@NotNull`, `@Email`, `@Size` where appropriate
- [x] T021 [US1] Implement priority calculation logic in `backend/.../service/PriorityService.java` — method: `calculatePriority(int impact, int urgency, int serviceCriticality)` using formula `score = impact × urgency × criticality`; return P1 if score ≥ 9, P2 if ≥ 5, P3 if ≥ 2, else P4; add a JavaDoc comment explaining the formula
- [x] T022 [US1] Implement the Strategy Pattern in `backend/.../strategy/`: create interface `AssignmentStrategy.java` with method `assign(Incident, List<Technician>)`; implement `SkillBasedAssignment.java` (find technicians whose skills contain the incident's category, pick lowest workload) and `LoadBasedAssignment.java` (pick technician with lowest `currentWorkload` regardless of skills); add JavaDoc on each class explaining its purpose
- [x] T023 [US1] Implement `IncidentService.java` in `backend/.../service/` — method `createIncident(IncidentCreateRequest, Long reporterId)`: (1) fetch service, calculate priority, (2) calculate SLA deadlines from SlaPolicy, (3) set status=NEW then CLASSIFIED, (4) run assignment strategy, (5) set status=ASSIGNED, (6) increment technician workload, (7) save incident; add inline comments on each step
- [x] T024 [US1] Create `IncidentController.java` in `backend/.../controller/` — POST `/api/incidents` (EMPLOYEE only); GET `/api/incidents` with query params `status`, `priority`, `category`; GET `/api/incidents/{id}`; wire to `IncidentService`
- [x] T025 [P] [US1] Build Login page `frontend/src/pages/LoginPage.jsx` — form with email + password fields; on submit, call POST `/api/auth/login`; store token in localStorage; redirect to `/dashboard`
- [x] T026 [P] [US1] Build Register page `frontend/src/pages/RegisterPage.jsx` — form with name, email, password, role dropdown; on submit, call POST `/api/auth/register`; redirect to `/login`
- [x] T027 [US1] Build Employee Dashboard `frontend/src/pages/EmployeeDashboard.jsx` — shows list of employee's own incidents (fetch GET `/api/incidents`); each row shows incidentNumber, title, priority badge (color-coded), status, SLA deadline
- [x] T028 [US1] Build Create Incident form `frontend/src/pages/CreateIncidentPage.jsx` — fields: title, description, category dropdown, service dropdown (loaded from GET `/api/services`), impact (1-3), urgency (1-3); on submit call POST `/api/incidents`; show success and redirect
- [x] T029 [US1] Create `frontend/src/services/api.js` — Axios instance with baseURL `http://localhost:8080/api`; interceptor to attach `Authorization: Bearer <token>` header from localStorage to every request

**Checkpoint**: Register → Login → Create Incident → See incident in list with priority P1/P2/P3/P4 and assigned technician.

---

## Phase 4: User Story 2 — Technician Resolves an Incident (Priority: P1)

**Goal**: A technician can log in, see their assigned incidents, acknowledge, investigate, and resolve them. Audit log is populated at each step.

**Independent Test**: See `quickstart.md` Scenario 2.

### Implementation

- [x] T030 [US2] Implement the Observer Pattern in `backend/.../observer/`: create interface `IncidentObserver.java` with method `onIncidentEvent(Incident, String action, User actor)`; implement `InAppNotifier.java` (saves a Notification record to DB) and `AuditLogger.java` (saves an AuditLog record to DB); register both observers in `IncidentService`; add JavaDoc on each class
- [x] T031 [US2] Implement State Pattern enforcement in `backend/.../service/IncidentStateService.java` — method `transition(Incident, IncidentStatus newStatus, User actor)`: validates the transition using an allowed-transitions map (e.g., ASSIGNED can only → ACKNOWLEDGED); throws `InvalidStateTransitionException` with message "Invalid state transition: {old} → {new}" if invalid; saves the updated incident; fires all observers; add a comment block listing all valid transitions
- [x] T032 [US2] Add status update endpoint PATCH `/api/incidents/{id}/status` to `IncidentController.java` — body: `{ "newStatus": "IN_PROGRESS", "notes": "..." }`; role rules: only the assigned technician may move ASSIGNED→ACKNOWLEDGED→IN_PROGRESS→RESOLVED; only EMPLOYEE may move RESOLVED→VERIFIED→CLOSED
- [x] T033 [US2] Add resolution endpoint PATCH `/api/incidents/{id}/resolve` — TECHNICIAN only; body: `{ "resolutionDetails": "..." }`; sets status=RESOLVED, saves resolutionDetails, sets resolvedAt timestamp; fires observers
- [x] T034 [US2] Add GET `/api/incidents/{id}` to return full incident detail including audit log history and technician info (join queries via JPA)
- [x] T035 [P] [US2] Build Technician Dashboard `frontend/src/pages/TechnicianDashboard.jsx` — list of assigned incidents, sorted by priority (P1 first); each row shows priority badge, title, status, SLA deadline with color (green=OK, orange=warning, red=violated)
- [x] T036 [US2] Build Incident Detail page `frontend/src/pages/IncidentDetailPage.jsx` — shows all fields; for technicians: "Acknowledge", "Start Investigation", "Resolve" buttons that call PATCH endpoints; shows audit log timeline at the bottom
- [x] T037 [P] [US2] Build Notification Bell component `frontend/src/components/NotificationBell.jsx` — fetches GET `/api/notifications/me` on page load; shows unread count badge; clicking shows dropdown list of recent notifications; clicking a notification calls PATCH to mark it read

**Checkpoint**: Technician logs in → sees P1 incident → acknowledges → resolves → employee sees "RESOLVED" status and a notification.

---

## Phase 5: User Story 3 — Admin Monitors the System (Priority: P2)

**Goal**: Admin can view dashboard metrics, search/filter incidents, manage users, and reassign incidents.

**Independent Test**: See `quickstart.md` Scenario 4.

### Implementation

- [x] T038 [US3] Add reporting endpoints in `backend/.../controller/ReportController.java`: GET `/api/reports/dashboard` (total, open, critical, resolved, sla-violations counts); GET `/api/reports/technician-workload` (list of technicians + active incident count); GET `/api/reports/service-analysis` (incidents per service) — all ADMIN only
- [x] T039 [US3] Add user management endpoints in `backend/.../controller/UserController.java`: GET `/api/users` (ADMIN only); PUT `/api/users/{id}` to update role or deactivate; GET `/api/technicians` with workload counts
- [x] T040 [US3] Add reassignment endpoint PATCH `/api/incidents/{id}/assign` in `IncidentController.java` — ADMIN only; body: `{ "technicianId": 5 }`; updates assignment, decrements old technician workload, increments new, fires observers
- [x] T041 [P] [US3] Build Admin Dashboard page `frontend/src/pages/AdminDashboard.jsx` — 5 summary cards (Total / Open / Critical / Resolved / SLA Violations); technician workload table; service analysis table; data fetched from `/api/reports/*`
- [x] T042 [US3] Build Incident Management page `frontend/src/pages/AdminIncidentsPage.jsx` — table with all incidents; filter controls: status dropdown, priority dropdown, category dropdown, date range picker, search by title; calls GET `/api/incidents` with query params
- [x] T043 [P] [US3] Build User Management page `frontend/src/pages/AdminUsersPage.jsx` — table of users with role and status; admin can deactivate a user or change their role

**Checkpoint**: Admin logs in → dashboard shows all 5 metrics → can filter incidents → can reassign an incident.

---

## Phase 6: User Story 4 — Auto-Escalation of Critical Incidents (Priority: P2)

**Goal**: P1 incidents unacknowledged for 5+ minutes are automatically escalated — admins are notified.

**Independent Test**: See `quickstart.md` Scenario 5.

### Implementation

- [x] T044 [US4] Create `backend/.../scheduler/EscalationScheduler.java` — annotate class with `@Component`, method with `@Scheduled(fixedRate = 60000)` (runs every 60 seconds); query for incidents with status=ASSIGNED, priority=P1, createdAt < now-5min; for each: fire `InAppNotifier` for all ADMIN users with message "ESCALATION: {incidentNumber} not acknowledged"; add AuditLog entry with action="ESCALATED"; add JavaDoc explaining the escalation logic

**Checkpoint**: Create a P1 incident, wait 5+ minutes without acknowledging — admin notification appears.

---

## Phase 7: User Story 5 — Employee Closes Incident and Leaves Feedback (Priority: P3)

**Goal**: Employee verifies and closes a resolved incident, optionally leaving a rating.

**Independent Test**: See `quickstart.md` Scenario 6.

### Implementation

- [x] T045 [US5] Add PATCH `/api/incidents/{id}/close` endpoint — EMPLOYEE only (and only the reporter); body: `{ "feedback": 4 }`; validates status is RESOLVED or VERIFIED; sets status=CLOSED, closedAt=now; fires observers
- [x] T046 [US5] Add `feedback` field (Integer, nullable, 1–5) to `Incident` entity and schema — represents post-resolution rating from employee
- [x] T047 [US5] Update `IncidentDetailPage.jsx` — when incident status is RESOLVED, show "Close Incident" button and optional 1–5 star rating input for the reporter; hide button for other users

**Checkpoint**: Employee opens resolved incident → clicks Close → picks 3 stars → status becomes CLOSED.

---

## Phase 8: Intelligent Feature — Duplicate Detection (Bonus)

- [x] T048 [US-INT] Add duplicate check in `IncidentService.createIncident()` — after fetching the service, query `IncidentRepository` for open incidents with same `category` and `serviceId` created in the last 24 hours; if found, set `potentialDuplicate=true` and `parentIncidentId=<firstMatch.id>` on the new incident; add an AuditLog entry "POTENTIAL_DUPLICATE detected"
- [x] T049 [P] [US-INT] Show duplicate warning in `IncidentDetailPage.jsx` — if `potentialDuplicate=true`, show a yellow banner: "⚠️ This may be related to [INC-XXXX]" with a link to the parent incident

---

## Phase 9: Polish & Cross-Cutting Concerns

- [x] T050 [P] Add `@NotNull`, `@Email`, `@Size`, `@Min`/`@Max` validation annotations to all DTO classes and test that invalid requests return 400 with descriptive messages
- [x] T051 [P] Add `@PreAuthorize` role checks on all service methods as a second layer of security beyond the controller filters (defense in depth)
- [x] T052 Add search endpoint GET `/api/incidents` to support 5+ filter criteria simultaneously using Spring Data JPA `Specification` — filters: status, priority, category, serviceId, technicianId, from date, to date, title keyword
- [x] T053 [P] Add SLA violation background job in `backend/.../scheduler/SlaMonitorScheduler.java` — runs every 5 minutes; finds incidents where `slaResolutionDeadline < now` and `status != CLOSED/RESOLVED`; sets `slaViolated=true`; fires `InAppNotifier` to reporter and admin
- [x] T054 [P] Add email simulation: `EmailNotifier.java` implementing `IncidentObserver` — instead of real SMTP, save an entry to a `simulated_emails` table or log the email body to console with format `[EMAIL] To: X Subject: Y Body: Z`; register in `IncidentService` alongside `InAppNotifier`
- [x] T055 [P] Write and document 15 test cases in `docs/test-cases.md` covering: normal incident creation, invalid state transition, SLA violation detection, duplicate detection, unauthorized access, missing fields, role-based access, escalation trigger — each with: ID, description, preconditions, steps, expected result
- [x] T056 Run all 6 validation scenarios from `quickstart.md` and fix any failures
- [ ] T057 Write the project report PDF — sections: Problem Analysis, Requirements, Architecture Diagram, UML Class Diagram, UML Use Case Diagram, ER Diagram, Technology Justification, Test Cases, OOP concepts demonstrated

---

## Dependencies & Execution Order

### Phase Dependencies
- **Phase 1 (Setup)**: Start immediately — no dependencies
- **Phase 2 (Foundation)**: Needs Phase 1 — BLOCKS everything else
- **Phases 3–8 (Stories)**: All require Phase 2; can proceed in priority order
- **Phase 9 (Polish)**: After all required stories complete

### Parallel Opportunities
- T002 (backend init) and T003 (frontend init) can run simultaneously
- T009 (entities) and T012 (JWT) can run simultaneously
- T025/T026 (React pages) and T021/T022 (backend services) can run simultaneously within a story
- M1 (frontend tasks) and M2+M3 (backend tasks) can always run in parallel

### Suggested Member Split
| Member | Primary Tasks |
|--------|---------------|
| M1 (Frontend) | T003, T017, T025–T029, T035–T037, T041–T043, T047, T049 |
| M2 (Backend/APIs) | T002, T005, T013, T018–T019, T024, T032–T034, T038–T040, T044–T045 |
| M3 (Domain/OOP) | T010, T020–T023, T030–T031, T048, T051 |
| M4 (Database/Testing) | T004, T008, T011, T015–T016, T050, T052–T056 |

---

## Implementation Strategy

### MVP First (Phases 1–3)
1. Complete Phase 1: Setup → everyone can run the project
2. Complete Phase 2: Foundation → auth + database + entities ready
3. Complete Phase 3: US1 → employee can create an incident
4. **STOP & VALIDATE**: Demo Scenario 1 from quickstart.md
5. Continue with Phase 4 (US2: Technician resolves)

### Full MVP (Phases 1–5 = all P1 stories)
After Phase 5 completes: all three roles work, SLA visible, audit logged.
This is enough to pass with a good score.

### Target for High Score (Phases 1–9)
Add escalation, duplicate detection, email simulation, full search, 15 test cases, report PDF.
