# 🛠️ I-ITSM: Intelligent IT Service Management System

> **Course**: CS F213 / MAC F212 — Object Oriented Programming (BITS Pilani)  
> **Academic Term**: Semester-I, 2026–2027  
> **Team Members**: Saatvik, Kshitij, Aryan, Abhinanda  
> **Repository**: [https://github.com/saatvikakella07-wq/i-itsm](https://github.com/saatvikakella07-wq/i-itsm)  
> **Tech Stack**: Java 21 + Spring Boot 3.3.5 | React 18 + Vite | PostgreSQL 15 / Embedded H2  

---

## 📋 Evaluator Quick Links & Rubric Compliance Matrix

This project was built strictly in accordance with the **CS F213/MAC F212 Project Guidelines & Evaluation Rubric (120 Marks Total)**:

| Evaluation Criterion | Marks | System Implementation | Key Source Files |
|:---|:---:|:---|:---|
| **1. System Design & Architecture** | 10 | Multi-tier layered architecture (React UI $\to$ Spring Boot REST API $\to$ Business Services $\to$ JPA Domain Models $\to$ Relational Persistence). Full separation of concerns. | [`README.md`](README.md), [`specs/001-i-itsm-system/spec.md`](specs/001-i-itsm-system/spec.md) |
| **2. Functionality Implementation (MVP)** | 60 | Complete incident lifecycle (`NEW` $\to$ `CLASSIFIED` $\to$ `ASSIGNED` $\to$ `ACKNOWLEDGED` $\to$ `IN_PROGRESS` $\to$ `RESOLVED` $\to$ `VERIFIED` $\to$ `CLOSED`). User management with 3 distinct roles (Employee, Technician, Admin). | [`IncidentService.java`](backend/src/main/java/com/itsm/service/IncidentService.java), [`IncidentStateService.java`](backend/src/main/java/com/itsm/service/IncidentStateService.java) |
| **3. User Experience & Operations** | 10 | Interactive dashboards for all 3 roles, SLA countdown deadlines with color badges, in-app notification bell with unread counters, star rating feedback modal. | [`App.jsx`](frontend/src/App.jsx), [`NotificationBell.jsx`](frontend/src/components/NotificationBell.jsx), [`IncidentDetailPage.jsx`](frontend/src/pages/IncidentDetailPage.jsx) |
| **4. Technical Depth & OOP Patterns** | 15 | **Strategy Pattern** (Technician Assignment), **Observer Pattern** (Audit & Notifications), **State Pattern** (Lifecycle), stateless JWT security, and typed exception hierarchies. | [`strategy/`](backend/src/main/java/com/itsm/strategy/), [`observer/`](backend/src/main/java/com/itsm/observer/), [`SecurityConfig.java`](backend/src/main/java/com/itsm/security/SecurityConfig.java) |
| **5. Testing & Validation** | 5 | 13 automated unit tests passing 100% on core patterns and algorithms; 15 documented test specifications covering edge cases and authorization. | [`TestRunner.java`](backend/src/test/java/com/itsm/TestRunner.java), [`docs/test-cases.md`](docs/test-cases.md) |
| **6. Innovation Challenge** | 10 | Real-time Priority Determination algorithm, Automated Background Escalation scheduler, and 24-hour Duplicate Incident Detection. | [`PriorityService.java`](backend/src/main/java/com/itsm/service/PriorityService.java), [`EscalationScheduler.java`](backend/src/main/java/com/itsm/scheduler/EscalationScheduler.java) |
| **7. Documentation & Presentation** | 10 | Comprehensive README, data models, API contracts, task tracking, and reproduction scripts. | [`docs/test-cases.md`](docs/test-cases.md), [`specs/001-i-itsm-system/`](specs/001-i-itsm-system/) |

---

## 👥 Team Members & Identifiable Responsibilities

As required by the course guidelines, each member owned a distinct, clearly identifiable module of the system:

| Member | Focus Area | What Was Owned & Implemented | Key Deliverables |
|:---|:---|:---|:---|
| **Saatvik** | **Full-Stack Integration & Architecture** | Spring Boot framework setup, main application bootstrap, lifecycle controllers, and end-to-end integration | `ItsmApplication`, `IncidentController`, `IncidentStateService`, and application deployment config |
| **Kshitij** | **Domain Model & OOP Design Patterns** | OOP design abstractions, Strategy Pattern routing, Observer Pattern event bus, and Priority calculation engine | `AssignmentStrategy`, `SkillBasedAssignment`, `LoadBasedAssignment`, `IncidentObserver`, and `PriorityService` |
| **Aryan** | **Frontend (React 18 + Vite)** | Responsive client-side application, role-scoped routing, form validations, and real-time state components | `EmployeeDashboard`, `CreateIncidentPage`, `TechnicianDashboard`, `AdminDashboard`, and `NotificationBell` |
| **Abhinanda** | **Database, Security & QA / Testing** | PostgreSQL schema, seed data, JWT authentication filter, background schedulers, and 15-scenario test suite | `schema.sql`, `seed.sql`, `SecurityConfig`, `JwtUtil`, `EscalationScheduler`, `SlaMonitorScheduler`, and `docs/test-cases.md` |

---

## 🧩 Object-Oriented Design Concepts Demonstrated

### 1. Strategy Pattern (Technician Assignment)
- **Interface**: `com.itsm.strategy.AssignmentStrategy`
- **Concrete Strategies**:
  - `SkillBasedAssignment`: Inspects technician skills against incident operational category (e.g., `NETWORK`, `APPLICATION`). Breaks ties by selecting the lowest active workload. Falls back gracefully to least-loaded technician if no direct skill match exists.
  - `LoadBasedAssignment`: Selects the technician with the minimal active ticket count across the organization regardless of category, balancing workload uniformly.
- **OOP Principle**: *Open/Closed Principle & Polymorphism* — new assignment strategies (e.g., Round-Robin, Experience-Based) can be added without altering the client code in `IncidentService`.

### 2. Observer Pattern (Decoupled Notification & Audit Event Bus)
- **Interface**: `com.itsm.observer.IncidentObserver`
- **Concrete Observers**:
  - `InAppNotifier`: Dispatches persistent user notifications for ticket assignment, SLA warnings, resolution, and closure.
  - `AuditLogger`: Persists immutable, sequential audit logs for 100% of state transitions, identifying the acting user, timestamp, and transition details.
  - `SimulatedEmailNotifier`: Simulates transactional email dispatches (`[EMAIL] To: X Subject: Y Body: Z`) and records them in `simulated_emails`.
- **OOP Principle**: *Loose Coupling* — core incident logic does not depend on notification channels; subscribers react independently to events.

### 3. State Pattern (Incident Lifecycle Management)
- **Enforcement Service**: `com.itsm.service.IncidentStateService`
- **Valid Progression**:
  $$\text{NEW} \longrightarrow \text{CLASSIFIED} \longrightarrow \text{ASSIGNED} \longrightarrow \text{ACKNOWLEDGED} \longrightarrow \text{IN\_PROGRESS} \longrightarrow \text{RESOLVED} \longrightarrow \text{CLOSED}$$
- **Constraint Enforcement**: Direct skips (e.g., $\text{NEW} \to \text{CLOSED}$) or backwards transitions (e.g., $\text{RESOLVED} \to \text{NEW}$) are strictly blocked by throwing a typed `InvalidStateTransitionException` returning HTTP 400 Bad Request.

### 4. Encapsulation & Domain Modeling
- Protected entity fields with accessor/mutator methods and lifecycle hooks (`@PrePersist`).
- Rich associations: `Incident` has relationships with `User` (Reporter), `User` (Technician), `ItService`, `AuditLog`, and self-referencing `parentIncident` (for duplicate detection).
- Data integrity maintained through JPA repository interfaces with typed query specifications.

### 5. Exception Handling Hierarchy
- Custom typed exceptions derived from `RuntimeException`:
  - `InvalidStateTransitionException` $\to$ HTTP 400
  - `ResourceNotFoundException` $\to$ HTTP 404
  - `UnauthorizedException` $\to$ HTTP 403
- Handled uniformly via `@RestControllerAdvice` in `GlobalExceptionHandler`.

---

## 🤖 Intelligent Features & Automations

### 1. Intelligent Priority Determination
Priorities are computed mathematically on ticket creation based on three objective factors:
$$\text{Priority Score} = \text{Impact } (1\text{–}3) \times \text{Urgency } (1\text{–}3) \times \text{Service Criticality } (1\text{–}3)$$
- $\text{Score} \ge 9 \implies \mathbf{P1\text{ (Critical)}}$ (SLA Resolution Target: 30 minutes)
- $\text{Score} \ge 5 \implies \mathbf{P2\text{ (High)}}$ (SLA Resolution Target: 2 hours)
- $\text{Score} \ge 2 \implies \mathbf{P3\text{ (Medium)}}$ (SLA Resolution Target: 8 hours)
- $\text{Score} < 2 \implies \mathbf{P4\text{ (Low)}}$ (SLA Resolution Target: 24 hours)

### 2. Automated Critical Incident Escalation (`EscalationScheduler`)
- A background scheduler runs every 60 seconds.
- Identifies critical ($\mathbf{P1}$) tickets in `ASSIGNED` status that have remained unacknowledged for $> 5$ minutes.
- Automatically escalates the incident to all System Administrators via persistent alerts and logs an audit record.

### 3. Duplicate Incident Detection (`IncidentService`)
- When a ticket is submitted, the system checks for existing active tickets matching the same IT service and category logged in the past 24 hours.
- If detected, sets `potentialDuplicate = true`, links the parent ticket, and displays a prominent warning banner on the ticket detail view.

### 4. Continuous SLA Compliance Monitoring (`SlaMonitorScheduler`)
- Monitors active tickets against their calculated SLA resolution deadlines.
- Automatically flags breached tickets (`slaViolated = true`) and alerts stakeholders.

---

## ⚡ Quickstart Guide for Evaluators

### Prerequisites
- **Java 17 or 21+** (`java -version`)
- **Maven 3.8+** (`mvn -v`)
- **Node.js 18+** (`node -v`)

---

### Step 1: Start Backend (Zero Configuration Mode)
The application includes an embedded in-memory H2 database profile with automatic data initialization. **No PostgreSQL installation is required to evaluate the project.**

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
*Backend server starts at **`http://localhost:8081`**.*

---

### Step 2: Start Frontend (React + Vite)
In a second terminal window:
```bash
cd frontend
npm install
npm run dev
```
*Frontend interface starts at **`http://localhost:5173`**.*

---

### Step 3: Open and Evaluate
Visit **`http://localhost:5173`** in your browser.

The login screen features **1-click demo account buttons**:

| Role | Email | Password | What the Evaluator Can Test |
|:---|:---|:---|:---|
| **Employee** | `emp@org.com` | `emp123` | Report an incident, view real-time priority preview, track ticket history, verify and close resolved tickets with star ratings. |
| **Technician** | `tech@org.com` | `tech123` | Prioritized resolution queue (P1 first), acknowledge tickets, start investigations, resolve tickets with root-cause notes. |
| **Admin** | `admin@org.com` | `admin123` | Executive KPI cards (Total, Open, Critical, Resolved, SLA Breaches), filter all tickets, reassign technicians, manage user accounts. |

---

## 🧪 Testing & Validation Results

### Automated Unit Test Suite
To execute the automated unit test suite:
```bash
cd backend
mvn test-compile
java -cp "target/classes:target/test-classes:$(find ~/.m2/repository -name '*.jar' | tr '\n' ':')" com.itsm.TestRunner
```

**Output**:
```
=================================================
       RUNNING I-ITSM CORE VALIDATION SUITE      
=================================================
[1/3] Testing PriorityService (Intelligent Priority Formula)...
  ✓ All 6 Priority calculation test scenarios PASSED
[2/3] Testing AssignmentStrategy (Strategy Pattern)...
  ✓ All 3 Assignment Strategy pattern tests PASSED
[3/3] Testing IncidentStateService (State Pattern Lifecycle)...
  ✓ All 4 State lifecycle transition tests PASSED
=================================================
TEST RUN SUMMARY: 13 PASSED, 0 FAILED
=================================================
```

### Documented Test Cases
See [`docs/test-cases.md`](docs/test-cases.md) for 15 end-to-end test specifications covering:
- **TC-001**: Valid incident submission & auto-prioritization
- **TC-002**: Missing required input fields rejection (HTTP 400)
- **TC-003**: Priority calculation mathematical boundary testing
- **TC-004**: Skill-based assignment matching and workload tie-breaking
- **TC-005**: Load-based workload balancing
- **TC-006**: Duplicate incident detection within 24-hour window
- **TC-007**: Complete valid incident state progression
- **TC-008**: Illegal state skip rejection (e.g., `NEW` $\to$ `CLOSED`)
- **TC-009**: Unauthorized status transition by non-assigned technician
- **TC-010**: Technician resolution & active workload decrement
- **TC-011**: Employee closure & 1–5 star rating
- **TC-012**: 5-minute unacknowledged critical incident auto-escalation
- **TC-013**: Continuous SLA violation detection
- **TC-014**: Multi-criteria search filter (5+ criteria simultaneously)
- **TC-015**: JWT authentication failure & expired token protection

---

## 🎥 Demonstration Workflows (For Video Presentation)

As outlined in the course evaluation criteria (Page 15 of project brief), the application supports three complete demonstration workflows:

1. **Workflow 1: Incident Creation & Intelligent Routing**  
   *Employee reports an incident $\to$ system calculates priority score $\to$ strategy matches technician $\to$ notification and audit log generated.*
2. **Workflow 2: Technician Investigation & Resolution**  
   *Technician opens prioritized queue $\to$ acknowledges incident $\to$ investigates $\to$ resolves with technical notes $\to$ employee notified.*
3. **Workflow 3: Duplication, Escalation & Admin Oversight**  
   *Multiple reports trigger duplicate detection $\to$ unacknowledged critical tickets auto-escalate to admin $\to$ admin monitors metrics on executive dashboard and reassigns workloads.*
