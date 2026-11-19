# 🛠️ I-ITSM: Intelligent IT Service Management System

> **Course**: CS F213 / MAC F212 — Object Oriented Programming (BITS Pilani)  
> **Team Size**: 4 Members  
> **Deadline**: 22 November 2026  
> **Tech Stack**: Java 21 + Spring Boot 3 | React 18 + Vite | PostgreSQL 15  

---

## 👥 Team Roles & Responsibilities

We have divided the project cleanly into 4 roles so everyone has ownership:

| Member | Focus Area | What You Own | Key Deliverables |
| :--- | :--- | :--- | :--- |
| **Member 1 (M1)** | **Frontend (React)** | UI screens, forms, client routing, API calls with Axios | Login/Register page, Incident list & detail view, Admin dashboard |
| **Member 2 (M2)** | **Backend & APIs (Spring Boot)** | REST controllers, Spring Security (JWT), DTOs, Exception handling | Auth endpoints, Incident lifecycle APIs, SLA escalation endpoints |
| **Member 3 (M3)** | **Domain Model & OOP Patterns** | Core OOP design, Design Patterns, Business Logic | Strategy Pattern (assignment), Observer Pattern (notifications), State machine |
| **Member 4 (M4)** | **Database & QA / Testing** | PostgreSQL schema, seed data, JUnit tests, End-to-end testing | `schema.sql`, `seed.sql`, SLA background check, JUnit test suite |

---

## 🗺️ How to Read This Project (Start Here!)

All planning and design is ready under the [`specs/001-i-itsm-system/`](specs/001-i-itsm-system/) folder:

1. **[`constitution.md`](.specify/memory/constitution.md)** *(5 min read)*: Our team ground rules (OOP first, clean comments, beginner-friendly code).
2. **[`specs/001-i-itsm-system/spec.md`](specs/001-i-itsm-system/spec.md)** *(10 min read)*: What the system does — user stories, roles (Employee, Technician, Admin), and functional requirements.
3. **[`specs/001-i-itsm-system/tasks.md`](specs/001-i-itsm-system/tasks.md)**: **The most important file!** Contains all 57 actionable tasks broken down phase-by-phase with `[M1]`, `[M2]`, `[M3]`, `[M4]` labels.
4. **[`specs/001-i-itsm-system/data-model.md`](specs/001-i-itsm-system/data-model.md)**: The database tables, fields, and state machine transitions.
5. **[`specs/001-i-itsm-system/contracts/api.md`](specs/001-i-itsm-system/contracts/api.md)**: The exact REST endpoints backend and frontend will use to talk to each other.
6. **[`specs/001-i-itsm-system/quickstart.md`](specs/001-i-itsm-system/quickstart.md)**: How to set up your local environment (Java, Node, Postgres) and verify it works.

---

## 🧩 Required OOP Concepts (For the Evaluation)

To get maximum marks in the viva and evaluation, we are implementing:
- **Strategy Pattern**: Dynamic technician assignment (`SkillBasedAssignment` vs `LoadBasedAssignment`).
- **Observer Pattern**: Event notifications (`EmailNotifier`, `InAppNotifier`, `AuditLogger`) when incident status changes.
- **State Machine**: Enforcing valid incident lifecycle (`NEW` → `ASSIGNED` → `IN_PROGRESS` → `RESOLVED` → `CLOSED`).
- **Polymorphism & Abstraction**: Base services, interfaces, and DTO contracts.
- **Exception Hierarchy**: Custom exceptions (`InvalidStateTransitionException`, `ResourceNotFoundException`).

---

## 🚀 Quick Setup Guide

### Prerequisites
- **Java 21** or **Java 17** (`java -version`)
- **Node.js 18+** (`node -v`)
- **PostgreSQL 15+** running locally on port `5432`

### 1. Database Setup
```bash
# In psql or pgAdmin:
CREATE DATABASE itsm_db;
\c itsm_db;
\i database/schema.sql
\i database/seed.sql
```

### 2. Backend (Spring Boot)
```bash
cd backend
# Run with PostgreSQL (default)
mvn spring-boot:run

# Or run with embedded in-memory H2 (zero DB setup needed):
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# Server runs at http://localhost:8080
```

### 3. Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
# App runs at http://localhost:5173
```

---

## 🔑 Pre-Seeded Test Accounts

| Role | Email | Password | Pre-seeded Features |
|:---|:---|:---|:---|
| **ADMIN** | `admin@org.com` | `admin123` | KPI summary cards, SLA violation monitors, reassignment, user account management |
| **TECHNICIAN** | `tech@org.com` | `tech123` | Network/Hardware/Security skill profile, assigned ticket queue, investigate, resolve |
| **TECHNICIAN** | `tech2@org.com` | `tech123` | Application/Cloud/DB skill profile, workload queue |
| **EMPLOYEE** | `emp@org.com` | `emp123` | Report incidents, live calculated priority preview, ticket tracking, closure & rating |

---

## 🧪 Running the Core Validation Suite
```bash
cd backend
mvn test-compile
java -cp "target/classes:target/test-classes:..." com.itsm.TestRunner
```
See [`docs/test-cases.md`](docs/test-cases.md) for 15 documented test cases covering lifecycle, patterns, boundary conditions, and security.

---

## 📋 Git Workflow for the Team
- Always pull the latest changes before starting work: `git pull origin main`
- Create a branch for your task: `git checkout -b feature/m1-login-screen`
- Make clear commits: `git commit -m "feat(frontend): add login form component"`
- Never push directly to `main` without testing!
