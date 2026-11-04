# Implementation Plan Template - US006 Resolve an Activity from Another Service

## Overview

Implementation plan for the internal service-to-service resolution of an activity by id through `/internal/activities/{id}`. It returns `ActivityInfo` (id, clubId, partnerId, name, categoryId, status). This endpoint is internal only and is never routed by the api-gateway.

- Story: US006 - Resolve an Activity from Another Service
- Epic: EP005 - Activities
- Domain: Existing domain (partner-service owns the `Activity` entity)
- Primary Actor / Persona: Planning service / Booking service
- Business Value: Other services can show and schedule the activity
- Notes: Blocking dependencies — EP003 Clubs (`clubId` in `ActivityInfo`), EP001 Categories (`categoryId` in `ActivityInfo`), non-blocking EP002 Partners (`partnerId` derived from the club)

## Steps

### 1. Setup & Configuration

- ✅ Identify impacted modules: partner-service (owns `Activity`, `InternalController`, `ActivityMapper`), common-lib (`ActivityInfo` record)
- ✅ Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: not applicable (single resolve fetch; no filtering/pagination)
- ✅ Verify naming conventions (API & DB specs)
- ✅ Check Liquibase master changelog inclusion needs (no new changelog — table recreated in US001)
- ✅ Additional libraries required? No

### 2. Backend Implementation (Per Impacted Microservice)

Microservice modules (`microservices/services/partner-service`):

- ✅ Create Entity(ies): `Activity` (already exists from US001 — no new fields)
- ✅ Repository: `ActivityRepository extends JpaRepository<Activity, UUID>` (no new methods — reuse `findById`; resolve `partnerId` via the club)
- ✅ Liquibase: no new changelog required (table recreated in US001)
- ✅ Alter the generated file to remove the 'drop existing foreign keys' and to add the added foreign keys because the liquibase plugin doesn't see any foreign keys. (not applicable — no new foreign keys in this story)
- ✅ Service: `ActivityService` in `service/` and its implementation `ActivityServiceImpl` in `service/impl/` (methods: `info` — activity lookup 404, derive `partnerId` from the club)
- ✅ DTO(s): `ActivityInfo` (common-lib record — updated to id, clubId, partnerId, name, categoryId, status)
- ✅ Mapper(s) (MapStruct): `ActivityMapper` (update `toInfo` — map `clubId`, `categoryId`, `status`, `partnerId` from the club)
- ✅ Controller(s): `InternalController` (endpoints: `GET /internal/activities/{id}` — added)
- ✅ Gateway: not applicable — `/internal/**` endpoints are never routed by the api-gateway
- ✅ Swagger annotations on DTOs & controller methods (not applicable — internal endpoint)
- ✅ Integration Tests:
  - ✅ 200 with data (AC1 resolve)
  - ✅ Error path (AC2 404 ACTIVITY_NOT_FOUND)

### 3. Frontend Implementation (`onshore-frontend`)

- ✅ Not applicable — service-to-service only; no UI.

### 4. Modified / New Files

```

/
├── microservices
│   ├── common-lib
│   │   └── src/main/java/com/onshore/common_lib/club
│   │       └── ActivityInfo.java # 🟨 MODIFIED (record: id, clubId, partnerId, name, categoryId, status)
│   └── services
│       └── partner-service
│           └── src/main/java/com/onshore/services
│               ├── mapper/ActivityMapper.java # 🟨 MODIFIED (update `toInfo`)
│               ├── service/ActivityService.java # 🟨 MODIFIED (update `info`)
│               ├── service/impl/ActivityServiceImpl.java # 🟨 MODIFIED (implement `info` with club-derived partnerId)
│               └── controller/InternalController.java # 🟨 MODIFIED (GET /internal/activities/{id})
└── specifications
    └── epics
        └── EP005 - Activities
            └── user-stories
                └── US006 - resolve-an-activity
                    └── implementation-plan.md # 🟨 UPDATED

```

### Notes

- Always follow existing code style & structure
- Follow the backend/frontend instruction rule files in `.github/instructions/` and the API/database specifications in `specifications/`
- Defer non-required fields / operations to future stories

## Status

- Status
  - Overall: ✅ DONE
