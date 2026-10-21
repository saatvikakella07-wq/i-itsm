<!--
SYNC IMPACT REPORT (remove before committing)
Version change: N/A → 1.0.0 (initial)
Added sections: Core Principles, Tech Stack, Team Workflow, Governance
Deferred TODOs: none
-->

# I-ITSM Constitution

## Core Principles

### I. OOP-First Design (NON-NEGOTIABLE)
Every feature MUST be expressed through well-defined Java classes, interfaces, and design
patterns. Raw procedural code in service layers is not acceptable.
- Use interfaces to define contracts (e.g., `AssignmentStrategy`, `IncidentObserver`)
- Prefer composition over inheritance except where a true "is-a" relationship exists
- At least two GoF design patterns MUST be demonstrably implemented:
  Strategy (technician assignment) and Observer (notifications/audit)
- The State pattern MUST govern incident lifecycle transitions; invalid transitions MUST
  be rejected with a typed exception

### II. Beginner-Friendly Code
All code MUST be understandable by a team of four amateur programmers.
- Every class, method, and REST endpoint MUST have a short JavaDoc or inline comment
  explaining what it does and why
- Avoid clever one-liners; prefer clear, readable logic over brevity
- Each team member MUST be able to explain any part of the codebase they did not write
- No undocumented magic: Spring annotations and JPA relationships MUST have a brief comment

### III. Full-Stack Separation of Concerns
The frontend (React) and backend (Spring Boot) MUST communicate ONLY through a versioned
REST API. No business logic lives in React components.
- React is responsible for display, forms, and calling APIs only
- Spring Boot services own all business rules (priority calculation, SLA tracking, assignment)
- PostgreSQL is the single source of truth; no critical state lives only in memory

### IV. MVP Before Polish
The Minimum Viable Product (all mandatory modules) MUST be fully working before any
optional or advanced feature is started.
- Incident lifecycle (NEW → CLOSED) MUST work end-to-end before notifications are built
- Authentication and role-based access MUST be in place before any other feature
- SLA tracking MUST function correctly before duplicate detection or escalation is added

### V. Audit Everything
Every state change on an incident MUST produce an immutable audit log entry in the
database containing: timestamp, acting user, previous state, new state, and a description.
- Audit entries MUST NOT be deletable through any API endpoint
- This is a graded deliverable; missing audit logs will affect the final score

## Tech Stack & Architecture

- **Frontend**: React (Vite) — component-based, single-page application
- **Backend**: Java 21 + Spring Boot 3.x — REST API, business logic, security
- **Database**: PostgreSQL — relational, persistent storage via Spring Data JPA
- **Auth**: JWT (JSON Web Tokens) — stateless, role-aware (EMPLOYEE / TECHNICIAN / ADMIN)
- **Build**: Maven (backend), npm (frontend)
- **Version Control**: Git / GitHub — all four members commit regularly

Technology choice rationale (required in report):
- React + Spring Boot + PostgreSQL is a listed example combination in the project spec
- Spring Boot's annotation-driven OOP aligns directly with the OOP evaluation criteria
- PostgreSQL provides relational integrity for incident → SLA → audit relationships

## Team Workflow

- The GitHub repository is the single source of truth for all code
- Each feature MUST be developed on a separate branch and merged via a pull request
  reviewed by at least one other team member
- Every team member MUST commit code under their own GitHub account (required for
  individual contribution visibility in the repo history)
- The video demo MUST include all four members speaking and demonstrating their module
- The PDF report MUST be completed collaboratively, not written by one person alone
- Hard deadline: **22 November 2026** — no extensions will be given

## Governance

- This constitution supersedes all informal team agreements
- Amendments require discussion and agreement by all four members; changes are recorded
  here with an incremented version and updated `LAST_AMENDED_DATE`
- In case of a disagreement on implementation approach, the OOP-First principle (I) takes
  precedence: choose the approach that better demonstrates object-oriented design
- All four members are equally responsible for the final grade; non-contribution affects
  the whole team

**Version**: 1.0.0 | **Ratified**: 2026-10-21 | **Last Amended**: 2026-10-21
