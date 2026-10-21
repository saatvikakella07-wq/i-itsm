# Data Model: I-ITSM System

**Feature**: 001-i-itsm-system
**Date**: 2026-10-21

---

## Entities & Relationships

```
User (1) ──────────────────────────── (many) Incident [as reporter]
Technician (1) ─────────────────────── (many) Incident [as assignee]
ItService (1) ──────────────────────── (many) Incident
Incident (1) ───────────────────────── (many) AuditLog
Incident (1) ───────────────────────── (many) Notification
SlaPolicy (1) ──────────────────────── (many) Incident
```

---

## Entity: User

| Field | Type | Notes |
|-------|------|-------|
| id | Long (PK) | Auto-generated |
| name | String | Not null |
| email | String | Unique, not null |
| passwordHash | String | BCrypt hashed |
| role | Enum | EMPLOYEE / TECHNICIAN / ADMIN |
| isActive | Boolean | Soft-delete support |
| createdAt | Timestamp | Auto-set |

---

## Entity: Technician

Extends User (via a separate profile table linked by user_id)

| Field | Type | Notes |
|-------|------|-------|
| id | Long (PK) | |
| userId | Long (FK → User) | One-to-one |
| skills | String[] / List\<String\> | Categories they handle (e.g., ["Network","Cloud"]) |
| currentWorkload | Integer | Count of active (non-closed) incidents |
| isAvailable | Boolean | Manually set availability |

---

## Entity: ItService

| Field | Type | Notes |
|-------|------|-------|
| id | Long (PK) | |
| name | String | e.g., "Payment Application" |
| description | String | |
| criticality | Integer | 1 (Low), 2 (Medium), 3 (High) |

---

## Entity: SlaPolicy

| Field | Type | Notes |
|-------|------|-------|
| id | Long (PK) | |
| priority | Enum | P1 / P2 / P3 / P4 |
| responseMinutes | Integer | Minutes to first response |
| resolutionMinutes | Integer | Minutes to full resolution |

Default policies:
- P1: response=5, resolution=30
- P2: response=15, resolution=120
- P3: response=60, resolution=480
- P4: response=240, resolution=1440

---

## Entity: Incident (central entity)

| Field | Type | Notes |
|-------|------|-------|
| id | Long (PK) | |
| incidentNumber | String | Human-readable, e.g. "INC-0042" |
| title | String | Short summary |
| description | String | Full problem description |
| category | Enum | HARDWARE/SOFTWARE/NETWORK/DATABASE/CLOUD/SECURITY/AUTH/APPLICATION/STORAGE |
| status | Enum | NEW/CLASSIFIED/ASSIGNED/ACKNOWLEDGED/IN_PROGRESS/RESOLVED/VERIFIED/CLOSED |
| priority | Enum | P1/P2/P3/P4 — auto-calculated |
| impact | Integer | 1–3 (reporter-supplied) |
| urgency | Integer | 1–3 (reporter-supplied) |
| reporterId | Long (FK → User) | Who reported it |
| technicianId | Long (FK → User, nullable) | Assigned technician |
| serviceId | Long (FK → ItService) | Affected service |
| slaResponseDeadline | Timestamp | Calculated on creation |
| slaResolutionDeadline | Timestamp | Calculated on creation |
| slaViolated | Boolean | Set by background job |
| investigationNotes | String | Technician notes |
| resolutionDetails | String | How it was fixed |
| potentialDuplicate | Boolean | Flag from duplicate detection |
| parentIncidentId | Long (nullable) | Links to original if duplicate |
| createdAt | Timestamp | |
| resolvedAt | Timestamp (nullable) | |
| closedAt | Timestamp (nullable) | |

---

## Entity: AuditLog

| Field | Type | Notes |
|-------|------|-------|
| id | Long (PK) | |
| incidentId | Long (FK → Incident) | |
| userId | Long (FK → User) | Who performed the action |
| action | String | e.g., "STATUS_CHANGED", "ASSIGNED", "ESCALATED" |
| previousStatus | Enum (nullable) | |
| newStatus | Enum (nullable) | |
| description | String | Human-readable description |
| timestamp | Timestamp | Auto-set, immutable |

---

## Entity: Notification

| Field | Type | Notes |
|-------|------|-------|
| id | Long (PK) | |
| userId | Long (FK → User) | Recipient |
| incidentId | Long (FK → Incident) | Related incident |
| message | String | Notification text |
| type | Enum | ASSIGNED/SLA_WARNING/SLA_VIOLATED/RESOLVED/ESCALATED/CLOSED |
| isRead | Boolean | Default false |
| createdAt | Timestamp | |

---

## State Transitions: Incident Lifecycle

```
NEW
 └─→ CLASSIFIED      (system, after priority calculated)
      └─→ ASSIGNED       (system, after technician assigned)
           └─→ ACKNOWLEDGED  (technician action)
                └─→ IN_PROGRESS  (technician action)
                     └─→ RESOLVED    (technician action)
                          └─→ VERIFIED   (employee action)
                               └─→ CLOSED     (employee action)
```

**Blocked transitions** (must throw `InvalidStateTransitionException`):
- NEW → anything except CLASSIFIED
- CLASSIFIED → anything except ASSIGNED
- ASSIGNED → anything except ACKNOWLEDGED
- ACKNOWLEDGED → anything except IN_PROGRESS
- IN_PROGRESS → anything except RESOLVED
- RESOLVED → anything except VERIFIED or CLOSED
- CLOSED → nothing (terminal state)
