# Implementation Plan Template - US002 Update an Activity Profile

## Overview

Implementation plan for the admin update of an activity's profile: `name`, `description`, `categoryId`, the instructor contact fields and, optionally, the club it belongs to. The update never changes `status` or `subscriptionDate`; any `status` or `subscriptionDate` sent in the payload is ignored. Only admins can update, and the creation-time validation rules still apply.

- Story: US002 - Update an Activity Profile
- Epic: EP005 - Activities
- Domain: Existing domain (partner-service owns the `Activity` entity)
- Primary Actor / Persona: Admin
- Business Value: The activity's information stays current
- Notes: Blocking dependencies — EP003 Clubs (`Club` entity + club lookup for the optional club move), EP001 Categories (category catalog + `active` flag for `categoryId` validation), EP002 Partners (partner derived from the club), `common-lib` `ContactPerson` embeddable (instructor), api-gateway route `/api/activities/**` (already present)

## Steps

### 1. Setup & Configuration

- 🟩 Identify impacted modules: partner-service (owns `Activity`)
- 🟩 Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: not applicable (update only; no filtering/pagination)
- 🟩 Verify naming conventions (API & DB specs)
- 🟩 Check Liquibase master changelog inclusion needs (no new changelog — `activities` table recreated in US001)
- 🟩 Additional libraries required? No

### 2. Backend Implementation (Per Impacted Microservice)

Microservice modules (`microservices/services/partner-service`):

- 🟩 Create Entity(ies): `Activity` (already exists from US001 — `clubId`, `categoryId`, `name`, `description`, `instructor` (`ContactPerson` embeddable), `status`, `subscriptionDate`, `createdAt`; no new fields)
- 🟩 Repository: `ActivityRepository extends JpaRepository<Activity, UUID>` (add `existsByClubIdAndNameAndIdNot(UUID clubId, String name, UUID id)`)
- 🟩 Liquibase: no new changelog required (`activities` table recreated in US001)
- 🟩 Alter the generated file to remove the 'drop existing foreign keys' and to add the added foreign keys because the liquibase plugin doesn't see any foreign keys. (not applicable — no new foreign keys in this story)
- 🟩 Service: `ActivityService` in `service/` and its implementation `ActivityServiceImpl` in `service/impl/` (methods: `update` — admin check 403, activity lookup 404, field validation 400, category exists and active 404, name unique within club excluding self 409, club move allowed only within the same partner, ignores `status` and `subscriptionDate`)
- 🟩 DTO(s): `ActivityRequest`, `ActivityResponse` (reuse from US001)
- 🟩 Mapper(s) (MapStruct): `ActivityMapper` (update mapping — ignores `status` and `subscriptionDate`)
- 🟩 Controller(s): `ActivityController` (endpoints: `PUT /api/activities/{id}`)
- 🟩 Gateway: confirm the route exists in `microservices/cloud/api-gateway` / `config-repo/api-gateway.yml` for the new endpoints (already present in `ClubRouteConfig`: `/api/activities/**` → partner-service)
- 🟩 Swagger annotations on DTOs & controller methods
- 🟩 Integration Tests:
  - 🟩 200 with data
  - 🟩 not applicable (update only; no list endpoint)
  - 🟩 Error path (repository/service exception)

### 3. Frontend Implementation (`onshore-frontend`)

- 🟩 Not applicable — backend API only in this epic; no dedicated screen yet.

### 4. Modified / New Files

```

/
├── microservices
│   └── services
│       └── partner-service
│           └── src/main/java/com/onshore/services
│               ├── repository/ActivityRepository.java # 🟨 MODIFIED (add `existsByClubIdAndNameAndIdNot`)
│               ├── service/ActivityService.java # 🟨 MODIFIED (add `update`)
│               ├── service/impl/ActivityServiceImpl.java # 🟨 MODIFIED (implement `update`)
│               ├── mapper/ActivityMapper.java # 🟨 MODIFIED (update mapping; ignore `status` & `subscriptionDate`)
│               └── controller/ActivityController.java # 🟨 MODIFIED (PUT /api/activities/{id})
└── specifications
    └── epics
        └── EP005 - Activities
            └── user-stories
                └── US002 - update-an-activity-profile
                    └── implementation-plan.md # 🟨 UPDATED

```

### Notes

- Always follow existing code style & structure
- Follow the backend/frontend instruction rule files in `.github/instructions/` and the API/database specifications in `specifications/`
- Defer non-required fields / operations to future stories

## Status

- Status
  - Overall: 🟩 COMPLETED
