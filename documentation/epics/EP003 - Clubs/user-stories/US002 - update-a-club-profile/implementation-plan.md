# Implementation Plan Template - US002 Update a Club Profile

## Overview

Short summary of the user story purpose and expected outcome.

Include:

- Story: US002 - Update a Club Profile
- Epic: EP003 - Clubs
- Domain: Existing entity (`Club`) in `partner-service`
- Primary Actor / Persona: Admin
- Business Value: The site information stays accurate
- Notes: Depends on US001 - Create a Club (a club must exist); EP002 Partners (`Partner` entity); api-gateway route `/api/clubs/**`

## Steps

### 1. Setup & Configuration

- 🟩 Identify impacted modules: `microservices/services/partner-service`
- 🟩 Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: not applicable (update only; no listing)
- 🟩 Verify naming conventions (API & DB specs)
- 🟩 Check Liquibase master changelog inclusion needs — no schema change (the `clubs` table already exists from US001)
- 🟩 Additional libraries required? No

### 2. Backend Implementation (Per Impacted Microservice)

Microservice modules (`microservices/services/partner-service`):

- 🟩 Create Entity(ies): `Club` already exists from US001 (fields needed now: `name`, `description`, `street`, `city`, `zipCode`, `latitude`, `longitude`, `contactFirstName`, `contactLastName`, `contactEmail`, `contactPhoneNumber`, `partnerId`; deferred: none)
- 🟩 Repository: `ClubRepository extends JpaRepository<Club, UUID>` (existing from US001)
- 🟩 Liquibase: no schema change (the `clubs` table already exists from US001)
- 🟩 Service: `ClubService` in `service/` and its implementation `ClubServiceImpl` in `service/impl/` (methods: `update(UUID id, ClubRequest request)`)
- 🟩 DTO(s): `ClubRequest`, `ClubResponse` (existing from US001)
- 🟩 Mapper(s): `ClubMapper` (existing; add `update(Club, ClubRequest)`)
- 🟩 Controller(s): `ClubController` (endpoints: `PUT /api/clubs/{clubId}`, admin-only)
- 🟩 Gateway: confirm the route exists in `microservices/cloud/api-gateway` / `config-repo/api-gateway.yml` for the new endpoints
- 🟩 Swagger annotations on DTOs & controller methods
- 🟩 Integration Tests:
  - 🟩 200 with data (update succeeds and returns the updated club)
  - 🟩 Error path: 403 non-admin
  - 🟩 Error path: 404 `CLUB_NOT_FOUND`
  - 🟩 Error path: 409 `CLUB_ALREADY_EXISTS`
  - 🟩 Error path: 400 `INVALID_PAYLOAD`

### 3. Frontend Implementation (`onshore-frontend`)

- Not in scope for this story — backend API only; no dedicated screen yet.

### 4. Modified / New Files

```
/
├── microservices
│   └── services
│       └── partner-service
│           └── src/main/java/com/onshore/services
│               ├── service/ClubService.java # 🟨 MODIFIED (add update)
│               ├── service/impl/ClubServiceImpl.java # 🟨 MODIFIED (implement update)
│               ├── mapper/ClubMapper.java # 🟨 MODIFIED (add update)
│               └── controller/ClubController.java # 🟨 MODIFIED (PUT /api/clubs/{clubId})
└── documentation
    └── epics
        └── EP003 - Clubs
            └── user-stories
                └── US002 - update-a-club-profile
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
