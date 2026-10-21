# Quickstart & Validation Guide: I-ITSM

**Purpose**: How to set up and run the project, and how to validate each feature works.

---

## Prerequisites

| Tool | Version | Install |
|------|---------|---------|
| Java | 21+ | https://adoptium.net |
| Maven | 3.9+ | Bundled with Spring Boot or https://maven.apache.org |
| Node.js | 20+ | https://nodejs.org |
| PostgreSQL | 15+ | https://postgresql.org |
| Git | any | https://git-scm.com |

---

## Setup Steps

### 1. Clone the repo
```bash
git clone https://github.com/YOUR_TEAM/i-itsm.git
cd i-itsm
```

### 2. Set up the database
```bash
# Create the database
createdb itsm_db

# Run the seed script (tables + sample data)
psql itsm_db < database/schema.sql
psql itsm_db < database/seed.sql
```

### 3. Configure backend
Edit `backend/src/main/resources/application.properties`:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/itsm_db
spring.datasource.username=YOUR_POSTGRES_USER
spring.datasource.password=YOUR_POSTGRES_PASSWORD
jwt.secret=your_jwt_secret_key_here
```

### 4. Start the backend
```bash
cd backend
./mvnw spring-boot:run
# Backend runs on http://localhost:8080
```

### 5. Start the frontend
```bash
cd frontend
npm install
npm run dev
# Frontend runs on http://localhost:5173
```

---

## Validation Scenarios

### Scenario 1: Employee creates an incident (FR-004, FR-005)
1. Open http://localhost:5173
2. Register as EMPLOYEE (name, email, password)
3. Log in → redirected to Employee Dashboard
4. Click "Report Incident"
5. Fill: Title="Email not working", Category=APPLICATION, Service=Email, Impact=2, Urgency=3
6. Submit
7. ✅ **Expected**: Incident appears in "My Incidents" with status=CLASSIFIED, priority=P2 (2×3×service_criticality)

### Scenario 2: Technician resolves the incident (FR-008, FR-015)
1. Log in as TECHNICIAN (use seeded account: tech@org.com / password123)
2. Technician dashboard shows the assigned incident
3. Click "Acknowledge" → status changes to ACKNOWLEDGED
4. Click "Start Investigation" → status = IN_PROGRESS
5. Add notes: "Email server restarted"
6. Click "Resolve" with resolution details
7. ✅ **Expected**: Status = RESOLVED; Employee sees notification; Audit log has 4 entries

### Scenario 3: Invalid state transition is rejected (FR-007)
```bash
# Using curl or a REST client like Postman / Bruno
curl -X PATCH http://localhost:8080/api/incidents/1/status \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"newStatus": "CLOSED"}'
# ✅ Expected: 400 Bad Request with message "Invalid state transition: NEW → CLOSED"
```

### Scenario 4: Admin dashboard (FR-017)
1. Log in as ADMIN (admin@org.com / admin123)
2. Navigate to Dashboard
3. ✅ **Expected**: See summary cards: Total, Open, Critical, Resolved, SLA Violations

### Scenario 5: Escalation after 5 minutes (FR-018)
1. Create a P1 incident as employee
2. Do NOT acknowledge as technician
3. Wait 5+ minutes (or temporarily change the scheduler interval to 10 seconds for testing)
4. ✅ **Expected**: Admin receives a notification "ESCALATION: INC-00XX not acknowledged"

### Scenario 6: Duplicate detection (FR-019)
1. Create incident: Category=NETWORK, Service=Internet
2. Create another: Category=NETWORK, Service=Internet (within 24h)
3. ✅ **Expected**: Second incident is created with `potentialDuplicate=true` and linked to the first

---

## Test Accounts (from seed data)

| Role | Email | Password |
|------|-------|---------|
| ADMIN | admin@org.com | admin123 |
| TECHNICIAN | tech@org.com | tech123 |
| EMPLOYEE | emp@org.com | emp123 |
