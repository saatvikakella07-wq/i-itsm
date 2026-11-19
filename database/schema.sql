-- =============================================================================
-- I-ITSM Database Schema (PostgreSQL compatible)
-- Intelligent IT Service Management System
-- =============================================================================

DROP TABLE IF EXISTS simulated_emails CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS audit_logs CASCADE;
DROP TABLE IF EXISTS incidents CASCADE;
DROP TABLE IF EXISTS sla_policies CASCADE;
DROP TABLE IF EXISTS it_services CASCADE;
DROP TABLE IF EXISTS technicians CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- -----------------------------------------------------------------------------
-- 1. Users Table
-- -----------------------------------------------------------------------------
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('EMPLOYEE', 'TECHNICIAN', 'ADMIN')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 2. Technicians Table (Extends User via 1:1 user_id)
-- -----------------------------------------------------------------------------
CREATE TABLE technicians (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    skills VARCHAR(255) NOT NULL DEFAULT '', -- Comma-separated categories (e.g., 'NETWORK,APPLICATION,SECURITY')
    current_workload INT NOT NULL DEFAULT 0,
    is_available BOOLEAN NOT NULL DEFAULT TRUE
);

-- -----------------------------------------------------------------------------
-- 3. IT Services Table
-- -----------------------------------------------------------------------------
CREATE TABLE it_services (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    criticality INT NOT NULL CHECK (criticality BETWEEN 1 AND 3) -- 1: Low, 2: Medium, 3: High
);

-- -----------------------------------------------------------------------------
-- 4. SLA Policies Table
-- -----------------------------------------------------------------------------
CREATE TABLE sla_policies (
    id BIGSERIAL PRIMARY KEY,
    priority VARCHAR(10) NOT NULL UNIQUE CHECK (priority IN ('P1', 'P2', 'P3', 'P4')),
    response_minutes INT NOT NULL,
    resolution_minutes INT NOT NULL
);

-- -----------------------------------------------------------------------------
-- 5. Incidents Table (Central Entity)
-- -----------------------------------------------------------------------------
CREATE TABLE incidents (
    id BIGSERIAL PRIMARY KEY,
    incident_number VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    priority VARCHAR(10) NOT NULL DEFAULT 'P4',
    impact INT NOT NULL CHECK (impact BETWEEN 1 AND 3),
    urgency INT NOT NULL CHECK (urgency BETWEEN 1 AND 3),
    reporter_id BIGINT NOT NULL REFERENCES users(id),
    technician_id BIGINT REFERENCES users(id),
    service_id BIGINT NOT NULL REFERENCES it_services(id),
    sla_response_deadline TIMESTAMP,
    sla_resolution_deadline TIMESTAMP,
    sla_violated BOOLEAN NOT NULL DEFAULT FALSE,
    investigation_notes TEXT,
    resolution_details TEXT,
    potential_duplicate BOOLEAN NOT NULL DEFAULT FALSE,
    parent_incident_id BIGINT REFERENCES incidents(id) ON DELETE SET NULL,
    feedback INT CHECK (feedback BETWEEN 1 AND 5),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    closed_at TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 6. Audit Logs Table (Immutable Incident Event History)
-- -----------------------------------------------------------------------------
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    incident_id BIGINT NOT NULL REFERENCES incidents(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    previous_status VARCHAR(50),
    new_status VARCHAR(50),
    description TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 7. Notifications Table
-- -----------------------------------------------------------------------------
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    incident_id BIGINT REFERENCES incidents(id) ON DELETE SET NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------------------------
-- 8. Simulated Emails Table (Second Notification Channel)
-- -----------------------------------------------------------------------------
CREATE TABLE simulated_emails (
    id BIGSERIAL PRIMARY KEY,
    recipient_email VARCHAR(150) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    body TEXT NOT NULL,
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_incidents_status ON incidents(status);
CREATE INDEX idx_incidents_priority ON incidents(priority);
CREATE INDEX idx_incidents_reporter ON incidents(reporter_id);
CREATE INDEX idx_incidents_technician ON incidents(technician_id);
CREATE INDEX idx_audit_incident ON audit_logs(incident_id);
CREATE INDEX idx_notifications_user ON notifications(user_id, is_read);
