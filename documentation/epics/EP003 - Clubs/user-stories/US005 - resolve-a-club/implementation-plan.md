# Implementation Plan Template - US005 Resolve a Club from Another Service

## Overview

Short summary of the user story purpose and expected outcome.

Include:

- Story: US005 - Resolve a Club from Another Service
- Epic: EP003 - Clubs
- Domain: Existing entity (`Club`) in `partner-service` + new `ClubInfo` record in `common-lib`
- Primary Actor / Persona: Booking service / Planning service
- Business Value: Show the site of a class and validate check-in
- Notes: Depends on US001 - Create a Club (clubs must exist); `common-lib` `ClubInfo` record

## Steps

### 1. Setup & Configuration

- 🟩 Identify impacted modules: `microservices/services/partner-service`, `microservices/common-lib`
- 🟩 Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: not applicable (lookup by id)
- 🟩 Verify naming conventions (API & DB specs)
- 🟩 Check Liquibase master changelog inclusion needs — no schema change (the `clubs` table already exists from US001)
- 🟩 Additional libraries required? No

### 2. Backend Implementation (Per Impacted Microservice)

Microservice modules (`microservices/services/partner-service`):

- 🟩 Create Entity(ies): `Club` already exists from US001 (fields needed now: `id`, `partnerId`, `name`, `city`, `latitude`, `longitude`, `open`; deferred: none)
- 🟩 Repository: `ClubRepository extends JpaRepository<Club, UUID>` (existing from US001)
- 🟩 Liquibase: no schema change (the `clubs` table already exists from US001)
- 🟩 Service: `ClubService` in `service/` and its implementation `ClubServiceImpl` in `service/impl/` (methods: `info(UUID id)`)
- 🟩 DTO(s): `ClubInfo` (new record in `common-lib`, `com.onshore.common_lib.club`)
- 🟩 Mapper(s): `ClubMapper` (existing; add `toInfo(Club)`)
- 🟩 Controller(s): `InternalController` (endpoints: `GET /internal/clubs/{id}`)
- 🟩 Gateway: no route — `/internal/**` endpoints are never routed by the api-gateway
- 🟩 Swagger annotations on DTOs & controller methods
- 🟩 Integration Tests:
  - 🟩 200 with data (returns `ClubInfo` with id, partnerId, name, city, coordinates and open)
  - 🟩 Error path: 404 `CLUB_NOT_FOUND`

### 3. Frontend Implementation (`onshore-frontend`)

- Not applicable — internal API, no user interface.

### 4. Modified / New Files

```
/
├── microservices
│   ├── common-lib
│   │   └── src/main/java/com/onshore/common_lib/club/ClubInfo.java # 🟩 NEW
│   └── services
│       └── partner-service
│           └── src/main/java/com/onshore/services
│               ├── service/ClubService.java # 🟨 MODIFIED (add info)
│               ├── service/impl/ClubServiceImpl.java # 🟨 MODIFIED (implement info)
│               ├── mapper/ClubMapper.java # 🟨 MODIFIED (add toInfo)
│               └── controller/InternalController.java # 🟨 MODIFIED (GET /internal/clubs/{id})
└── documentation
    └── epics
        └── EP003 - Clubs
            └── user-stories
                └── US005 - resolve-a-club
                    └── implementation-plan.md # 🟨 UPDATED
```

### Notes

- Always follow existing code style & structure
- Follow the backend/frontend instruction rule files in `.github/instructions/` and the API/database specifications in `specifications/`
- Defer non-required fields / operations to future stories

## Status

- Overall status: 🟩 COMPLETED

Status legend:

- 🟩 COMPLETED
- 🟨 IN PROGRESS
- ⬜ NOT STARTED
- 🟥 BLOCKED
