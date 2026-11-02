# Implementation Plan Template - US001 Create an Activity

## Overview

Implementation plan for the admin creation of a new activity under an existing club and category. The activity is created with status `ACTIVE`; its partner is derived from the chosen club; `subscriptionDate` defaults to the creation date; the name must be unique within the club.

- Story: US001 - Create an Activity
- Epic: EP005 - Activities
- Domain: Existing domain (partner-service owns the `Activity` entity; redesigned)
- Primary Actor / Persona: Admin
- Business Value: The activity is offered on the platform
- Notes: Blocking dependencies — EP003 Clubs (`Club` entity + `open` flag), EP001 Categories (category catalog + `active` flag), EP002 Partners (`ACTIVE` partner status), `common-lib` `ContactPerson` embeddable (instructor), api-gateway route `/api/activities/**` (already present)

## Steps

### 1. Setup & Configuration

- 🟩 Identify impacted modules: partner-service (owns `Activity`), common-lib (new exception codes `CLUB_CLOSED`, `ACTIVITY_ALREADY_EXISTS`)
- 🟩 Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: not applicable (create only; no filtering/pagination)
- 🟩 Verify naming conventions (API & DB specs)
- 🟩 Check Liquibase master changelog inclusion needs
- 🟩 Additional libraries required? No

### 2. Backend Implementation (Per Impacted Microservice)

Microservice modules (`microservices/services/partner-service`):

- 🟩 Create Entity(ies): `Activity` (fields needed now: `clubId` (FK → clubs), `categoryId` (FK → categories), `name`, `description`, `instructor` (`ContactPerson` embeddable), `status` (default `ACTIVE`), `subscriptionDate`, `createdAt`; legacy dropped: `partnerId`, `category`, `durationMinutes`, `partnerPayout`, `defaultCredits`, `active`)
- 🟩 Repository: `ActivityRepository extends JpaRepository<Activity, UUID>` (add `existsByClubIdAndName(UUID clubId, String name)`)
- 🟩 Liquibase: add a changelog under `src/main/resources/db/changelog/` and include it in `db.changelog-master.yaml` (recreate `activities` with `id`, `club_id`, `category_id`, `name`, `description`, instructor columns, `status`, `subscription_date`, auditing columns; unique constraint `(club_id, name)`; drop legacy columns)
- 🟩 Alter the generated file to remove the 'drop existing foreign keys' and to add the added foreign keys because the liquibase plugin doesn't see any foreign keys. (FKs: `club_id` → `clubs`, `category_id` → `categories`)
- 🟩 Service: `ActivityService` in `service/` and its implementation `ActivityServiceImpl` in `service/impl/` (methods: `create` — admin check 403, club lookup 404, club `open` + partner `ACTIVE` 409, category exists and active 404, name unique within club 409, field validation 400, sets `status=ACTIVE` and `subscriptionDate`=creation date)
- 🟩 DTO(s): `ActivityRequest`, `ActivityResponse` (in `payload/`)
- 🟩 Mapper(s) (MapStruct): `ActivityMapper`
- 🟩 Controller(s): `ActivityController` (endpoints: `POST /api/activities` → 201)
- 🟩 Gateway: confirm the route exists in `microservices/cloud/api-gateway` / `config-repo/api-gateway.yml` for the new endpoints (already present in `ClubRouteConfig`: `/api/activities/**` → partner-service)
- 🟩 Swagger annotations on DTOs & controller methods
- 🟩 Integration Tests:
  - 🟩 200 with data
  - 🟩 200 empty list
  - 🟩 Error path (repository/service exception)

### 3. Frontend Implementation (`onshore-frontend`)

- 🟩 Not applicable — backend API only in this epic; no dedicated screen yet.

### 4. Modified / New Files

```

/
├── microservices
│   ├── common-lib
│   │   └── src/main/java/com/onshore/common_lib/exception
│   │       └── ClubExceptionType.java # 🟨 MODIFIED (add `CLUB_CLOSED`, `ACTIVITY_ALREADY_EXISTS`)
│   └── services
│       └── partner-service
│           ├── src/main/java/com/onshore/services
│           │   ├── model/Activity.java # 🟨 MODIFIED (redesign: clubId, categoryId, instructor, status, subscriptionDate)
│           │   ├── repository/ActivityRepository.java # 🟨 MODIFIED (add `existsByClubIdAndName`)
│           │   ├── service/ActivityService.java # 🟨 MODIFIED
│           │   ├── service/impl/ActivityServiceImpl.java # 🟨 MODIFIED
│           │   ├── mapper/ActivityMapper.java # 🟨 MODIFIED (MapStruct)
│           │   ├── payload/ActivityRequest.java # 🟨 MODIFIED
│           │   ├── payload/ActivityResponse.java # 🟨 MODIFIED
│           │   └── controller/ActivityController.java # 🟨 MODIFIED (POST /api/activities)
│           └── src/main/resources/db/changelog
│               ├── db.changelog-master.yaml # 🟨 MODIFIED (include `activities` changelog)
│               └── changelog-<generated>-activities-init.yaml # 🟩 NEW (Liquibase output)
└── specifications
    └── epics
        └── EP005 - Activities
            └── user-stories
                └── US001 - create-an-activity
                    └── implementation-plan.md # 🟨 UPDATED

```

### Notes

- Always follow existing code style & structure
- Follow the backend/frontend instruction rule files in `.github/instructions/` and the API/database specifications in `specifications/`
- Defer non-required fields / operations to future stories (legacy pricing/credit columns and their migration reviewed separately)

## Status

- Status
  - Overall: 🟩 COMPLETED
