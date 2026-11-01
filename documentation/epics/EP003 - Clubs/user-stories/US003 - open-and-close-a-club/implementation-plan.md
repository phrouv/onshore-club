# Implementation Plan Template - US003 Open and Close a Club

## Overview

Short summary of the user story purpose and expected outcome.

Include:

- Story: US003 - Open and Close a Club
- Epic: EP003 - Clubs
- Domain: Existing entity (`Club`) in `partner-service`
- Primary Actor / Persona: Admin
- Business Value: Temporarily unavailable sites stop hosting activities and check-ins
- Notes: Depends on US001 - Create a Club (a club must exist); EP002 Partners (`Partner` entity); api-gateway route `/api/clubs/**`

## Steps

### 1. Setup & Configuration

- 🟩 Identify impacted modules: `microservices/services/partner-service`
- 🟩 Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: not applicable (toggle only; no listing)
- 🟩 Verify naming conventions (API & DB specs)
- 🟩 Check Liquibase master changelog inclusion needs — no schema change (the `clubs` table already exists from US001)
- 🟩 Additional libraries required? No

### 2. Backend Implementation (Per Impacted Microservice)

Microservice modules (`microservices/services/partner-service`):

- 🟩 Create Entity(ies): `Club` already exists from US001 (fields needed now: `open`; deferred: none)
- 🟩 Repository: `ClubRepository extends JpaRepository<Club, UUID>` (existing from US001)
- 🟩 Liquibase: no schema change (the `clubs` table already exists from US001)
- 🟩 Service: `ClubService` in `service/` and its implementation `ClubServiceImpl` in `service/impl/` (methods: `changeOpen(UUID id, boolean open)`)
- 🟩 DTO(s): `ClubResponse` (existing from US001)
- 🟩 Mapper(s): `ClubMapper` (existing; no change)
- 🟩 Controller(s): `ClubController` (endpoints: `PATCH /api/clubs/{clubId}/open`, admin-only, `?open=`)
- 🟩 Gateway: confirm the route exists in `microservices/cloud/api-gateway` / `config-repo/api-gateway.yml` for the new endpoints
- 🟩 Swagger annotations on DTOs & controller methods
- 🟩 Integration Tests:
  - 🟩 200 with data (toggle succeeds and returns the club with the new `open` value)
  - 🟩 Error path: 403 non-admin
  - 🟩 Error path: 404 `CLUB_NOT_FOUND`
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
│               ├── service/ClubService.java # 🟨 MODIFIED (add changeOpen)
│               ├── service/impl/ClubServiceImpl.java # 🟨 MODIFIED (implement changeOpen)
│               └── controller/ClubController.java # 🟨 MODIFIED (PATCH /api/clubs/{clubId}/open)
└── documentation
    └── epics
        └── EP003 - Clubs
            └── user-stories
                └── US003 - open-and-close-a-club
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
