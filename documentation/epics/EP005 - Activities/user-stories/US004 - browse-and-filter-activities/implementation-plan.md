# Implementation Plan Template - US004 Browse and Filter Activities

## Overview

Implementation plan for the admin listing of activities with optional filters on `clubId`, `categoryId` and `status`. When no status filter is given, all statuses — including `REJECTED` — are returned. Filtering and pagination are performed in the database rather than in memory.

- Story: US004 - Browse and Filter Activities
- Epic: EP005 - Activities
- Domain: Existing domain (partner-service owns the `Activity` entity)
- Primary Actor / Persona: Admin
- Business Value: The admin can review and manage the activity catalog
- Notes: Blocking dependencies — EP003 Clubs (`clubId` filter values), EP001 Categories (`categoryId` filter values), api-gateway route `/api/activities/**` (already present)

## Steps

### 1. Setup & Configuration

- ⬜ Identify impacted modules: partner-service (owns `Activity`)
- ⬜ Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: server-side (database filtering + pagination)
- ⬜ Verify naming conventions (API & DB specs)
- ⬜ Check Liquibase master changelog inclusion needs (no new changelog — filter columns recreated in US001)
- ⬜ Additional libraries required? No

### 2. Backend Implementation (Per Impacted Microservice)

Microservice modules (`microservices/services/partner-service`):

- ⬜ Create Entity(ies): `Activity` (already exists from US001 — `clubId`, `categoryId`, `status` fields present; no new fields)
- ⬜ Repository: `ActivityRepository extends JpaRepository<Activity, UUID>` (add filtered query — JPA Specification or derived methods with `Pageable` for `clubId`, `categoryId`, `status`)
- ⬜ Liquibase: no new changelog required (filter columns recreated in US001)
- ⬜ Alter the generated file to remove the 'drop existing foreign keys' and to add the added foreign keys because the liquibase plugin doesn't see any foreign keys. (not applicable — no new foreign keys in this story)
- ⬜ Service: `ActivityService` in `service/` and its implementation `ActivityServiceImpl` in `service/impl/` (methods: `list` — admin check 403, apply `clubId`/`categoryId`/`status` filters in the database, return paginated result)
- ⬜ DTO(s): `ActivityResponse` (reuse from US001)
- ⬜ Mapper(s) (MapStruct): `ActivityMapper` (reuse from US001)
- ⬜ Controller(s): `ActivityController` (endpoints: `GET /api/activities` with optional `clubId`, `categoryId`, `status` filters + pagination)
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
│               ├── repository/ActivityRepository.java # 🟨 MODIFIED (filtered/paginated query)
│               ├── service/ActivityService.java # 🟨 MODIFIED (add `list` with filters)
│               ├── service/impl/ActivityServiceImpl.java # 🟨 MODIFIED (implement `list` with filters)
│               └── controller/ActivityController.java # 🟨 MODIFIED (GET /api/activities with filters + pagination)
└── specifications
    └── epics
        └── EP005 - Activities
            └── user-stories
                └── US004 - browse-and-filter-activities
                    └── implementation-plan.md # 🟨 UPDATED

```

### Notes

- Always follow existing code style & structure
- Follow the backend/frontend instruction rule files in `.github/instructions/` and the API/database specifications in `specifications/`
- Defer non-required fields / operations to future stories

## Status

- Status
  - Overall: ⬜ NOT STARTED
