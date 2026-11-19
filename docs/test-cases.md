# Comprehensive Test Cases: I-ITSM System

**Course**: CS F213 / MAC F212 — Object Oriented Programming (BITS Pilani)  
**Project**: Intelligent IT Service Management & Incident Resolution System  
**Feature**: 001-i-itsm-system  
**Total Test Cases**: 15  

---

## Summary Matrix

| Test ID | Category | Scope | Verification Target |
|:---|:---|:---|:---|
| **TC-001** | Functional | User Story 1 | Valid Employee Incident Creation & Auto-Prioritization |
| **TC-002** | Validation | Negative / Input | Missing Required Fields on Incident Submission |
| **TC-003** | Domain / OOP | PriorityService | Priority Score Boundary Conditions (P1 to P4) |
| **TC-004** | Domain / OOP | Strategy Pattern | Skill-Based Technician Assignment Matching |
| **TC-005** | Domain / OOP | Strategy Pattern | Load-Based Technician Assignment Workload Balancing |
| **TC-006** | Intelligent Feature | FR-019 | Duplicate Incident Detection within 24-Hour Window |
| **TC-007** | State Machine | State Pattern | Valid Complete Incident Lifecycle Progression |
| **TC-008** | State Machine | State Pattern (Negative) | Invalid Lifecycle Skip (e.g., NEW → CLOSED) |
| **TC-009** | Security / Auth | RBAC | Technician Unauthorized Operation on Other Technicians' Tickets |
| **TC-010** | Functional | User Story 2 | Technician Incident Resolution & Workload Decrement |
| **TC-011** | Functional | User Story 5 | Employee Incident Verification, Closure & Star Rating |
| **TC-012** | Automation | FR-018 | Auto-Escalation of Unacknowledged P1 Incidents after 5 Minutes |
| **TC-013** | Automation | FR-011 / FR-012 | Background SLA Breach Detection & Notification |
| **TC-014** | Reporting | Admin / FR-016 | Multi-Criteria Incident Search (5+ Filters) |
| **TC-015** | Security / Auth | FR-001 / JWT | Authentication with Invalid Password & Expired Token |

---

## Detailed Test Specifications

### TC-001: Valid Employee Incident Creation & Auto-Prioritization
- **Category**: Normal Operations / Functional
- **Preconditions**:
  - User `emp@org.com` is registered and logged in with role `EMPLOYEE`.
  - Service `Payment Application` (Criticality = 3) exists in the database.
- **Steps**:
  1. Employee navigates to "Report Incident" (`/incidents/new`).
  2. Enters Title: `"Payment gateway returns 500 error on checkout"`.
  3. Selects Category: `APPLICATION`.
  4. Selects Service: `Payment Application`.
  5. Sets Impact: `3` (Severe), Urgency: `3` (Immediate).
  6. Enters Description: `"All customer transactions are failing at bank auth step."`
  7. Submits the form.
- **Expected Result**:
  - HTTP 201 Created returned.
  - Incident status initialized to `NEW`, transitioned to `CLASSIFIED`, then `ASSIGNED`.
  - Calculated priority is `P1` ($3 \times 3 \times 3 = 27 \ge 9$).
  - Technician with `APPLICATION` skill is automatically assigned.
  - Incident response deadline set to 5 minutes, resolution deadline set to 30 minutes.

---

### TC-002: Missing Required Fields on Incident Submission
- **Category**: Input Validation / Negative Testing
- **Preconditions**:
  - User is logged in as `EMPLOYEE`.
- **Steps**:
  1. POST `/api/incidents` with payload missing `description` and `serviceId`:
     ```json
     { "title": "Crash", "category": "APPLICATION", "impact": 2, "urgency": 2 }
     ```
- **Expected Result**:
  - HTTP 400 Bad Request returned.
  - Error response body specifies field validation errors for `description` and `serviceId`.
  - No database record created.

---

### TC-003: Priority Calculation Mathematical Formula Boundaries
- **Category**: Domain Model Unit Test
- **Preconditions**: `PriorityService` instantiated.
- **Steps & Matrix**:
  - Input `(3, 3, 3)`: Score = 27 $\to$ Expected: `P1` (Score $\ge 9$)
  - Input `(3, 3, 1)`: Score = 9 $\to$ Expected: `P1` (Exact boundary)
  - Input `(2, 3, 1)`: Score = 6 $\to$ Expected: `P2` ($5 \le \text{Score} < 9$)
  - Input `(1, 2, 1)`: Score = 2 $\to$ Expected: `P3` ($2 \le \text{Score} < 5$)
  - Input `(1, 1, 1)`: Score = 1 $\to$ Expected: `P4` (Score $< 2$)
  - Input `(-5, 0, 10)`: Out of range inputs clamped to $[1, 3]$.
- **Expected Result**: All calculations strictly match the formula and grading rubric.

---

### TC-004: Skill-Based Technician Assignment Matching (Strategy Pattern)
- **Category**: OOP Design Pattern / Strategy
- **Preconditions**:
  - Technician A has skills `["NETWORK"]`, workload = 3.
  - Technician B has skills `["NETWORK", "SECURITY"]`, workload = 1.
  - Technician C has skills `["DATABASE"]`, workload = 0.
- **Steps**:
  1. Trigger assignment strategy with Category = `NETWORK`.
- **Expected Result**:
  - `SkillBasedAssignment` filters to Technicians A and B (matching skill).
  - Selects Technician B because their active workload (1) is lower than Technician A (3).
  - Technician C is excluded despite lower workload because skills do not match.

---

### TC-005: Load-Based Assignment Workload Balancing (Strategy Pattern)
- **Category**: OOP Design Pattern / Strategy
- **Preconditions**:
  - Technician A workload = 4.
  - Technician B workload = 0.
- **Steps**:
  1. Trigger `LoadBasedAssignment` with an incident.
- **Expected Result**:
  - Selects Technician B immediately based purely on lowest workload ($0 < 4$).

---

### TC-006: Duplicate Incident Detection within 24-Hour Window
- **Category**: Intelligent Feature / Correlation (FR-019)
- **Preconditions**:
  - Incident INC-0001 created at $T_0$ with Service = `Corporate Network` and Category = `NETWORK`.
- **Steps**:
  1. A second employee submits an incident with Service = `Corporate Network` and Category = `NETWORK` at $T_0 + 2\text{ hours}$.
- **Expected Result**:
  - Second incident created with `potentialDuplicate = true`.
  - `parentIncidentId` set to INC-0001's ID.
  - Audit log records `"POTENTIAL_DUPLICATE detected"`.
  - Frontend detail page renders yellow warning banner with link to parent ticket.

---

### TC-007: Valid Incident Lifecycle State Progression (State Pattern)
- **Category**: State Pattern / Normal Operation
- **Preconditions**:
  - Incident exists in `NEW` state.
- **Steps**:
  1. System transitions `NEW` $\to$ `CLASSIFIED`
  2. System transitions `CLASSIFIED` $\to$ `ASSIGNED`
  3. Technician transitions `ASSIGNED` $\to$ `ACKNOWLEDGED`
  4. Technician transitions `ACKNOWLEDGED` $\to$ `IN_PROGRESS`
  5. Technician transitions `IN_PROGRESS` $\to$ `RESOLVED`
  6. Reporter transitions `RESOLVED` $\to$ `CLOSED`
- **Expected Result**:
  - Each transition succeeds (HTTP 200).
  - 100% of transitions generate an immutable `AuditLog` entry.
  - Terminal state `CLOSED` records `closedAt` timestamp.

---

### TC-008: Invalid Lifecycle State Skip Rejection
- **Category**: State Pattern / Exception Handling (FR-007)
- **Preconditions**:
  - Incident is currently in state `NEW` or `ASSIGNED`.
- **Steps**:
  1. Issue PATCH `/api/incidents/{id}/status` with `newStatus: "CLOSED"`.
- **Expected Result**:
  - Request rejected with HTTP 400 Bad Request.
  - `InvalidStateTransitionException` thrown with message `"Invalid state transition: NEW → CLOSED"`.
  - Status remains unchanged in database.

---

### TC-009: Unauthorized State Transition by Non-Assigned Technician
- **Category**: Security / Role-Based Access Control
- **Preconditions**:
  - Incident INC-0002 assigned to Technician `Bob` (User ID 2).
  - Authenticated as Technician `Sarah` (User ID 3).
- **Steps**:
  1. Sarah calls PATCH `/api/incidents/2/resolve` with resolution notes.
- **Expected Result**:
  - Request rejected with HTTP 403 Forbidden.
  - Error message: `"You are not assigned to resolve this incident."`

---

### TC-010: Technician Incident Resolution & Workload Decrement
- **Category**: Functional / Lifecycle Completion
- **Preconditions**:
  - Incident is in `IN_PROGRESS` state.
  - Assigned technician currentWorkload = 2.
- **Steps**:
  1. Assigned technician calls PATCH `/api/incidents/{id}/resolve` with:
     ```json
     { "resolutionDetails": "Rebooted core switch; replaced faulty fiber patch cable." }
     ```
- **Expected Result**:
  - Status updated to `RESOLVED`.
  - `resolvedAt` timestamp saved.
  - Technician `currentWorkload` decremented to 1.
  - In-app notification sent to the reporter.
  - Simulated email logged to console and `simulated_emails` table.

---

### TC-011: Employee Verification, Ticket Closure & Feedback Rating
- **Category**: User Story 5 / Customer Satisfaction
- **Preconditions**:
  - Incident is in `RESOLVED` state.
  - Logged in as the reporting employee.
- **Steps**:
  1. Reporter calls PATCH `/api/incidents/{id}/close` with:
     ```json
     { "feedback": 5 }
     ```
- **Expected Result**:
  - Status updated to `CLOSED`.
  - Feedback score recorded as 5.
  - Audit log records closure and rating.

---

### TC-012: Auto-Escalation of Unacknowledged P1 Incidents
- **Category**: Intelligent Automation (FR-018)
- **Preconditions**:
  - Incident INC-0005 created with priority `P1`, status `ASSIGNED`.
  - Created timestamp is older than 5 minutes ($now - 6\text{ minutes}$).
  - Technician has not acknowledged.
- **Steps**:
  1. `EscalationScheduler` background task executes (runs every 60s).
- **Expected Result**:
  - System identifies unacknowledged critical ticket.
  - Dispatches `ESCALATED` in-app notification to all active Administrators.
  - Logs `[EMAIL] To: admin@org.com | Subject: [I-ITSM ESCALATION]...`
  - AuditLog records action `ESCALATED`.

---

### TC-013: SLA Breach Monitoring & Automatic Flagging
- **Category**: SLA Management (FR-011)
- **Preconditions**:
  - Incident SLA resolution deadline is past ($deadline < now$).
  - Incident status is `IN_PROGRESS`.
  - `slaViolated = false`.
- **Steps**:
  1. `SlaMonitorScheduler` background task executes.
- **Expected Result**:
  - `slaViolated` flag set to `true` in database.
  - `SLA_VIOLATED` notification generated for reporter and admins.
  - AuditLog entry created for SLA breach.
  - Admin dashboard metric `slaViolations` incremented.

---

### TC-014: Multi-Criteria Incident Search (5+ Filters)
- **Category**: Search & Filtering (FR-016)
- **Preconditions**:
  - 10 diverse incidents exist in database across categories, statuses, and services.
- **Steps**:
  1. Execute GET `/api/incidents` with simultaneous query parameters:
     `?status=IN_PROGRESS&priority=P1&category=NETWORK&serviceId=3&q=switch`
- **Expected Result**:
  - Returns only incidents satisfying all 5 criteria simultaneously.
  - SQL executed uses JPA Specification dynamically conjoining predicates with `AND`.

---

### TC-015: Authentication with Invalid Password & Expired Token
- **Category**: Security & JWT Verification
- **Preconditions**:
  - User `admin@org.com` registered.
- **Steps**:
  1. Attempt POST `/api/auth/login` with password `"wrongpassword"`.
  2. Attempt GET `/api/incidents` with an expired/tampered JWT header:
     `Authorization: Bearer eyJhbGciOi...invalid...`
- **Expected Result**:
  - Step 1: HTTP 401 Unauthorized (`"Invalid email or password"`).
  - Step 2: HTTP 403 / 401 Unauthorized (`"Access denied"` / filter rejects context).
  - No access granted to protected resources.
