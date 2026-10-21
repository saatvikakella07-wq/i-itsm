# Feature Specification: Intelligent IT Service Management & Incident Resolution System (I-ITSM)

**Feature Branch**: `001-i-itsm-system`

**Created**: 2026-10-21

**Status**: Draft

**Input**: Build an Intelligent IT Service Management & Incident Resolution System (I-ITSM) using Java Spring Boot backend, PostgreSQL database, and React frontend, for a team of 4 amateur programmers, fulfilling the CS F213/MAC F212 OOP project requirements.

---

## User Scenarios & Testing *(mandatory)*

### User Story 1 — Employee Reports an Incident (Priority: P1)

An employee (e.g., a staff member) notices that the payment application is down. They log in, fill out a short form describing the problem, and submit it. The system automatically figures out how serious the problem is and assigns a technician.

**Why this priority**: This is the core action the entire system is built around. Without incident creation, nothing else works.

**Independent Test**: A logged-in employee can fill a form with a title, description, category, and affected service, submit it, and immediately see the incident in their "My Incidents" list with a status of NEW and a calculated priority.

**Acceptance Scenarios**:

1. **Given** an employee is logged in, **When** they submit an incident with title, description, category (e.g., Network), and affected service, **Then** the system creates the incident with status NEW, calculates a priority (P1–P4), and displays it in the employee's incident list.
2. **Given** an employee submits an incident, **When** the system processes it, **Then** the system automatically assigns it to an available technician with matching skills and sends a notification to that technician.
3. **Given** an employee submits a form with missing required fields (e.g., no description), **When** they click Submit, **Then** the system shows a clear error message and does not create the incident.

---

### User Story 2 — Technician Resolves an Incident (Priority: P1)

A technician logs in, sees their assigned incidents sorted by priority, picks the most critical one, investigates it, writes a resolution note, and marks it as resolved. The employee who reported it gets notified.

**Why this priority**: Without resolution, the incident lifecycle is incomplete — this is worth major marks.

**Independent Test**: A logged-in technician can view their assigned incidents, click one, update its status to IN_PROGRESS, add investigation notes, mark it RESOLVED with a resolution description, and the reporter receives an in-app notification.

**Acceptance Scenarios**:

1. **Given** a technician is logged in and has assigned incidents, **When** they open an incident, **Then** they see its full details: description, priority, SLA deadline, and status history.
2. **Given** a technician updates an incident's status to RESOLVED, **When** the update is saved, **Then** the system records an audit log entry with the timestamp, technician ID, old status, and new status.
3. **Given** an SLA deadline has passed for an unresolved incident, **When** the system checks SLA compliance, **Then** the incident is flagged as SLA_VIOLATED and the admin is notified.

---

### User Story 3 — Admin Monitors the System (Priority: P2)

An admin logs in and sees a dashboard showing total incidents, open/critical ones, SLA violations, and technician workloads. They can reassign incidents, manage users, and configure SLA policies.

**Why this priority**: Admin functionality enables oversight and configuration — required for full marks but secondary to core incident flow.

**Independent Test**: A logged-in admin can view a dashboard with at least 5 key metrics, search/filter incidents by status and priority, and reassign an incident to a different technician.

**Acceptance Scenarios**:

1. **Given** an admin is logged in, **When** they visit the dashboard, **Then** they see: total incidents, open incidents, critical incidents, resolved incidents, and SLA violations.
2. **Given** an admin searches incidents with filters (status=OPEN, priority=P1), **When** results load, **Then** only matching incidents are shown.
3. **Given** an admin reassigns an incident to a different technician, **When** the change is saved, **Then** an audit log entry is created, the old and new technicians are notified, and the incident record shows the new assignment.

---

### User Story 4 — Auto-Escalation of Unacknowledged Critical Incidents (Priority: P2)

If a P1 incident isn't acknowledged by the assigned technician within 5 minutes, the system automatically escalates it — notifying the admin and optionally reassigning.

**Why this priority**: This is one of the required "intelligent features" and demonstrates automation beyond basic CRUD.

**Independent Test**: A P1 incident assigned to a technician who does not acknowledge within 5 minutes triggers an escalation notification to the admin. This can be tested by creating a P1 incident and waiting (or simulating) the timeout.

**Acceptance Scenarios**:

1. **Given** a P1 incident is assigned and unacknowledged for 5+ minutes, **When** the system's background checker runs, **Then** an escalation notification is sent to the admin and the audit log records the escalation.
2. **Given** a P1 incident is acknowledged by the technician within 5 minutes, **When** the timer runs, **Then** no escalation occurs.

---

### User Story 5 — Employee Tracks and Closes Their Incident (Priority: P3)

After a technician resolves an incident, the employee views the resolution details, confirms the problem is fixed, and provides feedback. The incident is then closed.

**Why this priority**: Completes the full lifecycle and provides user feedback data — adds polish and UX score.

**Independent Test**: An employee whose incident is RESOLVED can view the resolution notes, mark it as CLOSED, and optionally leave a 1–5 star rating.

**Acceptance Scenarios**:

1. **Given** an employee's incident is RESOLVED, **When** they view it, **Then** they see the technician's resolution notes and a "Close Incident" button.
2. **Given** an employee clicks "Close Incident", **When** confirmed, **Then** the incident status changes to CLOSED, the audit log is updated, and the employee can optionally rate the resolution.

---

### Edge Cases

- What happens when no technician with matching skills is available? → System assigns to the least-loaded technician and flags the incident as "unmatched assignment."
- What happens when a user tries to skip a state (e.g., NEW → CLOSED directly)? → The system rejects the transition with a clear error message: "Invalid state transition."
- What happens if two employees report the same incident simultaneously? → The system flags the second report as a potential duplicate and links it to the first.
- What happens if the database is unavailable? → The system shows a user-friendly error page and logs the failure.
- What happens when a technician tries to resolve an incident not assigned to them? → The system rejects the action with an "Unauthorized" error.

---

## Requirements *(mandatory)*

### Functional Requirements

**User Management**
- **FR-001**: The system MUST allow users to register with name, email, and password, and log in with JWT-based authentication.
- **FR-002**: The system MUST enforce role-based access: Employee, Technician, and Admin each see different pages and have different permissions.
- **FR-003**: Admins MUST be able to create, view, update, and deactivate user accounts.

**Incident Management**
- **FR-004**: Employees MUST be able to create an incident with: title, description, category, and affected IT service.
- **FR-005**: The system MUST automatically calculate incident priority (P1–P4) using: Impact × Urgency × Service Criticality.
- **FR-006**: The system MUST enforce a valid incident lifecycle: NEW → CLASSIFIED → ASSIGNED → ACKNOWLEDGED → IN_PROGRESS → RESOLVED → VERIFIED → CLOSED.
- **FR-007**: The system MUST reject invalid state transitions with a descriptive error.
- **FR-008**: Technicians MUST be able to view their assigned incidents, add investigation notes, and update status.
- **FR-009**: The system MUST support at least two technician assignment strategies: Skill-Based and Load-Based.

**SLA Management**
- **FR-010**: The system MUST calculate SLA deadlines (response and resolution) based on incident priority when an incident is created.
- **FR-011**: The system MUST detect and flag SLA violations when deadlines are exceeded.
- **FR-012**: The system MUST generate a warning notification when an SLA deadline is within 20% of its time limit.

**Notifications**
- **FR-013**: The system MUST send in-app notifications for: incident created, incident assigned, SLA warning, SLA violation, incident resolved, incident closed.
- **FR-014**: The system MUST simulate email notifications (logged or stored in DB) as a second notification mechanism.

**Audit Trail**
- **FR-015**: Every incident state change MUST produce an immutable audit log entry containing: timestamp, acting user, action, previous state, new state, and description.

**Search & Reporting**
- **FR-016**: Users MUST be able to filter incidents by: status, priority, category, service, and date range (5 criteria minimum).
- **FR-017**: The admin dashboard MUST display: total incidents, open incidents, critical incidents, resolved incidents, SLA violations, and technician workload summary.

**Intelligent Features**
- **FR-018**: The system MUST automatically escalate unacknowledged P1 incidents after 5 minutes by notifying the admin.
- **FR-019**: The system MUST detect potential duplicate incidents (same category + service within 24 hours) and flag them.

### Key Entities

- **User**: A person who can log in. Has a name, email, hashed password, and a role (EMPLOYEE / TECHNICIAN / ADMIN).
- **Technician**: A User with the TECHNICIAN role. Has skills (list of categories they can handle), current workload count, and availability status.
- **IT Service**: A system or application the organization depends on (e.g., Payment System, Email, Network). Has a criticality level (1–3).
- **Incident**: The central entity. Represents a reported IT problem. Has a title, description, category, priority, status, SLA deadline, reporter, assigned technician, affected service, and a full history of state changes.
- **SLA Policy**: Rules defining response and resolution time targets per priority level.
- **Audit Log Entry**: An immutable record of one action taken on an incident.
- **Notification**: A message sent to a user triggered by an incident event. Has read/unread status.

---

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: An employee can report an incident and receive an automatic priority and technician assignment within 3 seconds of submission.
- **SC-002**: A technician can complete the full resolution flow (acknowledge → investigate → resolve) in under 5 user interactions.
- **SC-003**: The admin dashboard loads with all 5+ metrics visible within 2 seconds.
- **SC-004**: All mandatory MVP features (user management, incident CRUD, SLA, notifications, audit, search) are fully functional.
- **SC-005**: Every incident state transition is logged — 100% of state changes appear in the audit trail.
- **SC-006**: At least two intelligent/automated features operate without manual user intervention (auto-priority and auto-escalation).
- **SC-007**: The system correctly rejects 100% of invalid state transitions (e.g., NEW → CLOSED).
- **SC-008**: At least 15 test cases are documented and pass, covering normal operations, edge cases, invalid inputs, and authorization.

---

## Assumptions

- The system is a web application; mobile support is out of scope.
- External email delivery is simulated (stored in the database or console-logged); no real email server is required.
- All four team members will work on a shared Git repository with individual branches.
- The initial database will be seeded with sample IT services, SLA policies, and test users.
- Incident categories are predefined: Hardware, Software, Network, Database, Cloud, Security, Authentication, Application, Storage — but new ones can be added by an admin.
- "Intelligent" features use rule-based logic; no machine learning is required.
- The system does not need to support more than ~100 concurrent users (academic project scale).
- The project deadline is 22 November 2026; features beyond the MVP are optional if time permits.
