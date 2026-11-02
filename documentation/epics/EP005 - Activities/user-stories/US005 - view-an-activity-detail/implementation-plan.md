# Implementation Plan Template - US005 View an Activity Detail

## Overview

Implementation plan for the admin fetch of a single activity by id, including its club, category, instructor contact and status. Only admins can call this endpoint.

- Story: US005 - View an Activity Detail
- Epic: EP005 - Activities
- Domain: Existing domain (partner-service owns the `Activity` entity)
- Primary Actor / Persona: Admin
- Business Value: The admin can review an activity's details
- Notes: Blocking dependencies — EP003 Clubs (detail includes the referenced club), EP001 Categories (detail includes the referenced category), api-gateway route `/api/activities/**` (already present)

## Steps

### 1. Setup & Configuration

- ⬜ Identify impacted modules: partner-service (owns `Activity`)
- ⬜ Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: not applicable (single detail fetch; no filtering/pagination)
- ⬜ Verify naming conventions (API & DB specs)
- ⬜ Check Liquibase master changelog inclusion needs (no new changelog — table recreated in US001)
- ⬜ Additional libraries required? No

### 2. Backend Implementation (Per Impacted Microservice)

Microservice modules (`microservices/services/partner-service`):

- ⬜ Create Entity(ies): `Activity` (already exists from US001 — no new fields)
- ⬜ Repository: `ActivityRepository extends JpaRepository<Activity, UUID>` (no new methods — reuse `findById`)
- ⬜ Liquibase: no new changelog required (table recreated in US001)
- ⬜ Alter the generated file to remove the 'drop existing foreign keys' and to add the added foreign keys because the liquibase plugin doesn't see any foreign keys. (not applicable — no new foreign keys in this story)
- ⬜ Service: `ActivityService` in `service/` and its implementation `ActivityServiceImpl` in `service/impl/` (methods: `get` — admin check 403, activity lookup 404)
- ⬜ DTO(s): `ActivityResponse` (reuse from US001 — includes club, category, instructor and status)
- ⬜ Mapper(s) (MapStruct): `ActivityMapper` (reuse from US001)
- ⬜ Controller(s): `ActivityController` (endpoints: `GET /api/activities/{id}`)
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
│               ├── service/ActivityService.java # 🟨 MODIFIED (add `get`)
│               ├── service/impl/ActivityServiceImpl.java # 🟨 MODIFIED (implement `get`)
│               └── controller/ActivityController.java # 🟨 MODIFIED (GET /api/activities/{id})
└── specifications
    └── epics
        └── EP005 - Activities
            └── user-stories
                └── US005 - view-an-activity-detail
                    └── implementation-plan.md # 🟨 UPDATED

```

### Notes

- Always follow existing code style & structure
- Follow the backend/frontend instruction rule files in `.github/instructions/` and the API/database specifications in `specifications/`
- Defer non-required fields / operations to future stories

## Status

- Status
  - Overall: ⬜ NOT STARTED
