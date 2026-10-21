# REST API Contracts: I-ITSM Backend

**Base URL**: `http://localhost:8080/api`
**Auth**: All endpoints except `/auth/**` require `Authorization: Bearer <JWT>` header

---

## Authentication

### POST /auth/register
Create a new user account.
```json
Request:  { "name": "Alice", "email": "alice@org.com", "password": "secret123", "role": "EMPLOYEE" }
Response: { "message": "User registered successfully" }
```

### POST /auth/login
Authenticate and receive a JWT token.
```json
Request:  { "email": "alice@org.com", "password": "secret123" }
Response: { "token": "eyJ...", "role": "EMPLOYEE", "userId": 1, "name": "Alice" }
```

---

## Incidents

### POST /incidents — Create incident (EMPLOYEE only)
```json
Request:  { "title": "Payment app down", "description": "...", "category": "APPLICATION", "serviceId": 3, "impact": 3, "urgency": 3 }
Response: { "id": 42, "incidentNumber": "INC-0042", "priority": "P1", "status": "NEW", "slaResolutionDeadline": "2026-10-21T21:30:00Z" }
```

### GET /incidents — List incidents (filtered)
Query params: `status`, `priority`, `category`, `serviceId`, `technicianId`, `from`, `to`
```json
Response: [ { "id": 42, "incidentNumber": "INC-0042", "title": "...", "priority": "P1", "status": "ASSIGNED", ... } ]
```

### GET /incidents/{id} — Get full incident details
```json
Response: { "id": 42, "title": "...", "description": "...", "priority": "P1", "status": "ASSIGNED", "technician": { "id": 5, "name": "Bob" }, "auditHistory": [...], ... }
```

### PATCH /incidents/{id}/status — Update incident status (role-based)
```json
Request:  { "newStatus": "IN_PROGRESS", "notes": "Started investigating network switch" }
Response: { "id": 42, "status": "IN_PROGRESS", "updatedAt": "..." }
Error:    { "error": "Invalid state transition: ASSIGNED → CLOSED" }
```

### PATCH /incidents/{id}/assign — Assign technician (ADMIN or auto-system)
```json
Request:  { "technicianId": 5, "strategy": "SKILL_BASED" }
Response: { "id": 42, "assignedTechnician": { "id": 5, "name": "Bob" } }
```

### PATCH /incidents/{id}/resolve — Mark resolved (TECHNICIAN)
```json
Request:  { "resolutionDetails": "Restarted payment gateway service. Root cause: memory leak." }
Response: { "id": 42, "status": "RESOLVED", "resolvedAt": "..." }
```

### PATCH /incidents/{id}/close — Close incident (EMPLOYEE)
```json
Request:  { "feedback": 5 }
Response: { "id": 42, "status": "CLOSED", "closedAt": "..." }
```

---

## Users & Technicians

### GET /users — List users (ADMIN only)
### PUT /users/{id} — Update user (ADMIN only)
### GET /technicians — List technicians with workload
### GET /technicians/me/incidents — Technician's own assigned incidents

---

## Services & SLA

### GET /services — List IT services
### POST /services — Create service (ADMIN only)
### GET /sla-policies — List SLA policies
### PUT /sla-policies/{id} — Update SLA policy (ADMIN only)

---

## Notifications

### GET /notifications/me — Get current user's notifications
```json
Response: [ { "id": 1, "message": "INC-0042 assigned to you", "isRead": false, "createdAt": "..." } ]
```
### PATCH /notifications/{id}/read — Mark notification as read

---

## Reports (ADMIN only)

### GET /reports/dashboard
```json
Response: { "totalIncidents": 250, "openIncidents": 42, "criticalIncidents": 8, "resolvedIncidents": 198, "slaViolations": 10 }
```
### GET /reports/technician-workload
### GET /reports/service-analysis
