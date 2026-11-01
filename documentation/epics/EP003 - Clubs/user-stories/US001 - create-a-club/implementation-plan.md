# Implementation Plan Template - US001 Create a Club

## Overview

Short summary of the user story purpose and expected outcome.

Include:

- Story: US001 - Create a Club
- Epic: EP003 - Clubs
- Domain: New entity (`Club`) in the existing `partner-service` domain
- Primary Actor / Persona: Admin
- Business Value: The partner's activities have a defined site where they take place
- Notes: Depends on EP002 Partners (`Partner` entity and `ACTIVE` status); reuses `common-lib` `Address` and `ContactPerson`; introduces a new `GeoLocation` embeddable; requires an api-gateway route for `/api/clubs/**`

## Steps

### 1. Setup & Configuration

- 🟩 Identify impacted modules: `microservices/services/partner-service`, `microservices/common-lib`, `microservices/cloud/api-gateway`
- 🟩 Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: not applicable (creation only; no listing)
- 🟩 Verify naming conventions (API & DB specs)
- 🟩 Check Liquibase master changelog inclusion needs
- 🟩 Additional libraries required? No

### 2. Backend Implementation (Per Impacted Microservice)

Microservice modules (`microservices/services/partner-service`):

- 🟩 Create Entity(ies): `Club` (fields needed now: `id`, `partnerId`, `name`, `description`, `street`, `city`, `zipCode`, `latitude`, `longitude`, `contactFirstName`, `contactLastName`, `contactEmail`, `contactPhoneNumber`, `open`, `createdAt`; deferred: none)
- 🟩 Repository: `ClubRepository extends JpaRepository<Club, UUID>`
- 🟩 Liquibase: add a changelog under `src/main/resources/db/changelog/` and include it in `db.changelog-master.yaml` (run `mvn clean compile && mvn -Pliquibase liquibase:diff` from the service folder if the Liquibase profile is configured)
- 🟩 Alter the generated file to remove the 'drop existing foreign keys' and to add the added foreign keys because the liquibase plugin doesn't see any foreign keys.
- 🟩 Service: `ClubService` in `service/` and its implementation `ClubServiceImpl` in `service/impl/` (methods: `create(ClubRequest request)`)
- 🟩 DTO(s): `ClubRequest`, `ClubResponse` (in `payload/`, following the partner-service convention)
- 🟩 Mapper(s): `ClubMapper` (static mapper, same pattern as `PartnerMapper`)
- 🟩 Controller(s): `ClubController` (endpoints: `POST /api/clubs`, admin-only)
- 🟩 Gateway: confirm the route exists in `microservices/cloud/api-gateway` / `config-repo/api-gateway.yml` for the new endpoints
- 🟩 Swagger annotations on DTOs & controller methods
- 🟩 Integration Tests:
  - 🟩 201 created with valid data (club created with `open = true`)
  - 🟩 Error path: 403 non-admin
  - 🟩 Error path: 409 `PARTNER_NOT_ACTIVE`
  - 🟩 Error path: 409 `CLUB_ALREADY_EXISTS`
  - 🟩 Error path: 400 `INVALID_PAYLOAD`

### 3. Frontend Implementation (`onshore-frontend`)

- Not in scope for this story — backend API only; no dedicated screen yet.

### 4. Modified / New Files

```
/
├── microservices
│   ├── common-lib
│   │   └── src/main/java/com/onshore/common_lib/model/GeoLocation.java # 🟩 NEW
│   ├── cloud
│   │   └── api-gateway
│   │       └── src/main/java/com/onshore/cloud/config/ClubRouteConfig.java # 🟨 MODIFIED (add /api/clubs/** route)
│   └── services
│       └── partner-service
│           ├── src/main/java/com/onshore/services
│           │   ├── model/Club.java # 🟩 NEW
│           │   ├── repository/ClubRepository.java # 🟩 NEW
│           │   ├── service/ClubService.java # 🟩 NEW
│           │   ├── service/impl/ClubServiceImpl.java # 🟩 NEW
│           │   ├── mapper/ClubMapper.java # 🟩 NEW
│           │   ├── payload/ClubRequest.java # 🟩 NEW
│           │   ├── payload/ClubResponse.java # 🟩 NEW
│           │   └── controller/ClubController.java # 🟩 NEW (POST /api/clubs)
│           └── src/main/resources/db/changelog
│               ├── db.changelog-master.yaml # 🟨 MODIFIED (include clubs changelog)
│               └── changelog-<generated>-clubs-init.yaml # 🟩 NEW (Liquibase output)
└── documentation
    └── epics
        └── EP003 - Clubs
            └── user-stories
                └── US001 - create-a-club
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
