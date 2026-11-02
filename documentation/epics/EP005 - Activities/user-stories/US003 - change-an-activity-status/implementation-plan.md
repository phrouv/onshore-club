# Implementation Plan Template - US003 Change an Activity Status

## Overview

Implementation plan for the admin status change of an activity through `PATCH /api/activities/{id}/status`. `ACTIVE` and `SUSPENDED` can be toggled back and forth; `REJECTED` is not terminal and can transition to `ACTIVE` or `SUSPENDED`. Status changes keep the activity's data and history so existing classes and bookings remain traceable.

- Story: US003 - Change an Activity Status
- Epic: EP005 - Activities
- Domain: Existing domain (partner-service owns the `Activity` entity)
- Primary Actor / Persona: Admin
- Business Value: The activity stops being offered or is reactivated as appropriate
- Notes: Blocking dependencies — EP003 Clubs, EP001 Categories and EP002 Partners (the activity must exist with valid references before a status change), api-gateway route `/api/activities/**` (already present)

## Steps

### 1. Setup & Configuration

- ⬜ Identify impacted modules: partner-service (owns `Activity`)
- ⬜ Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: not applicable (status change only; no filtering/pagination)
- ⬜ Verify naming conventions (API & DB specs)
- ⬜ Check Liquibase master changelog inclusion needs (no new changelog — `status` column recreated in US001)
- ⬜ Additional libraries required? No

### 2. Backend Implementation (Per Impacted Microservice)

Microservice modules (`microservices/services/partner-service`):

- ⬜ Create Entity(ies): `Activity` (already exists from US001 — `status` field present; no new fields)
- ⬜ Repository: `ActivityRepository extends JpaRepository<Activity, UUID>` (no new methods — reuse `findById`)
- ⬜ Liquibase: no new changelog required (`status` column recreated in US001)
- ⬜ Alter the generated file to remove the 'drop existing foreign keys' and to add the added foreign keys because the liquibase plugin doesn't see any foreign keys. (not applicable — no new foreign keys in this story)
- ⬜ Service: `ActivityService` in `service/` and its implementation `ActivityServiceImpl` in `service/impl/` (methods: `changeStatus` — admin check 403, activity lookup 404, target status valid 400, apply transition: `ACTIVE` ⇄ `SUSPENDED`, `REJECTED` → `ACTIVE` or `SUSPENDED`)
- ⬜ DTO(s): `ActivityResponse` (reuse from US001)
- ⬜ Mapper(s) (MapStruct): `ActivityMapper` (no change — reuse response mapping)
- ⬜ Controller(s): `ActivityController` (endpoints: `PATCH /api/activities/{id}/status`)
- ⬜ Gateway: confirm the route exists in `microservices/cloud/api-gateway` / `config-repo/api-gateway.yml` for the new endpoints (already present in `ClubRouteConfig`: `/api/activities/**` → partner-service)
- ⬜ Swagger annotations on DTOs & controller methods
- ⬜ Integration Tests:
  - ⬜ 200 with data
  - ⬜ 200 empty list
  - ⬜ Error path (repository/service exception)

### 3. Frontend Implementation (`onshore-frontend`)

- ⬜ Not applicable — backend API only in this epic; no dedicated screen yet.

### 4. Modified / New Files

```

/
├── microservices
│   └── services
│       └── partner-service
│           └── src/main/java/com/onshore/services
│               ├── service/ActivityService.java # 🟨 MODIFIED (add `changeStatus`)
│               ├── service/impl/ActivityServiceImpl.java # 🟨 MODIFIED (implement `changeStatus`)
│               └── controller/ActivityController.java # 🟨 MODIFIED (PATCH /api/activities/{id}/status)
└── specifications
    └── epics
        └── EP005 - Activities
            └── user-stories
                └── US003 - change-an-activity-status
                    └── implementation-plan.md # 🟨 UPDATED

```

### Notes

- Always follow existing code style & structure
- Follow the backend/frontend instruction rule files in `.github/instructions/` and the API/database specifications in `specifications/`
- Defer non-required fields / operations to future stories

## Status

- Status
  - Overall: ⬜ NOT STARTED
