-- =============================================================================
-- I-ITSM Seed Data
-- =============================================================================

-- 1. SLA Policies
INSERT INTO sla_policies (priority, response_minutes, resolution_minutes) VALUES
('P1', 5, 30),
('P2', 15, 120),
('P3', 60, 480),
('P4', 240, 1440)
ON CONFLICT (priority) DO UPDATE 
SET response_minutes = EXCLUDED.response_minutes, resolution_minutes = EXCLUDED.resolution_minutes;

-- 2. IT Services (Criticality: 1=Low, 2=Medium, 3=High)
INSERT INTO it_services (id, name, description, criticality) VALUES
(1, 'Payment Application', 'Core customer transaction and payment gateway platform', 3),
(2, 'Corporate Email & Communication', 'Enterprise mail server and instant messaging services', 2),
(3, 'Corporate Network & VPN', 'Primary LAN/WAN routing, firewall, and remote VPN access', 3),
(4, 'Employee Portal / Intranet', 'Internal HR, leave tracking, and document intranet system', 1),
(5, 'Cloud Database Cluster', 'Production PostgreSQL & Redis database cluster infrastructure', 3)
ON CONFLICT (id) DO NOTHING;

-- Reset sequence for it_services
SELECT setval('it_services_id_seq', (SELECT MAX(id) FROM it_services));

-- 3. Users (Initial accounts: admin@org.com, tech@org.com, tech2@org.com, emp@org.com)
-- BCrypt hashes will also be verified / auto-seeded by backend Spring DataInitializer for security compatibility
INSERT INTO users (id, name, email, password_hash, role, is_active) VALUES
(1, 'System Administrator', 'admin@org.com', '$2a$10$w4r6xUfZiO5E7K0gI7qEaeX9L6/pYd.5o4lQW8BqR6u0H8c6OqGOm', 'ADMIN', TRUE),
(2, 'Bob NetworkTech', 'tech@org.com', '$2a$10$w4r6xUfZiO5E7K0gI7qEaeX9L6/pYd.5o4lQW8BqR6u0H8c6OqGOm', 'TECHNICIAN', TRUE),
(3, 'Sarah CloudTech', 'tech2@org.com', '$2a$10$w4r6xUfZiO5E7K0gI7qEaeX9L6/pYd.5o4lQW8BqR6u0H8c6OqGOm', 'TECHNICIAN', TRUE),
(4, 'Alice Employee', 'emp@org.com', '$2a$10$w4r6xUfZiO5E7K0gI7qEaeX9L6/pYd.5o4lQW8BqR6u0H8c6OqGOm', 'EMPLOYEE', TRUE)
ON CONFLICT (email) DO NOTHING;

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- 4. Technicians
INSERT INTO technicians (id, user_id, skills, current_workload, is_available) VALUES
(1, 2, 'NETWORK,HARDWARE,SECURITY', 0, TRUE),
(2, 3, 'APPLICATION,DATABASE,CLOUD,SOFTWARE,AUTH,STORAGE', 0, TRUE)
ON CONFLICT (user_id) DO NOTHING;

SELECT setval('technicians_id_seq', (SELECT MAX(id) FROM technicians));
