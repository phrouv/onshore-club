# Implementation Plan Template - US002 Create a Club

## Overview

Short summary of the user story purpose and expected outcome.

Include:

- Story: US002 - Create a Club
- Epic: EP004 - Club Management Frontend
- Domain: Existing frontend (`onshore-frontend`, admin area)
- Primary Actor / Persona: Admin
- Business Value: The platform's sites are registered
- Notes: Depends on EP003 Clubs backend (`POST /api/clubs`); `adminSlice.partners` for the partner selector; US001 - Browse and Filter Clubs (the page hosting the modal)

## Steps

### 1. Setup & Configuration

- 🟩 Identify impacted modules: `onshore-frontend` (no backend — EP003 already delivered `/api/clubs`)
- 🟩 Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: not applicable (creation only)
- 🟩 Verify naming conventions (API & DB specs)
- 🟩 Check Liquibase master changelog inclusion needs — not applicable (no DB change)
- 🟩 Additional libraries required? No

### 2. Backend Implementation (Per Impacted Microservice)

- Not applicable — no backend microservice is impacted by this story (EP003 already delivered `/api/clubs`).

### 3. Frontend Implementation (`onshore-frontend`)

- 🟩 Page/component under `src/pages/admin`: `ClubModal` (create form) inside `AdminClubs.jsx` — fields: `partnerId` (select, first), `name`, `street`, `city`, `zipCode`, `latitude`, `longitude`, `description`, `contactFirstName`, `contactLastName`, `contactEmail`, `contactPhoneNumber`
- 🟩 API client under `src/services`: `clubService.createClub` (calls the API gateway `POST /api/clubs`)
- 🟩 Redux slice/state under `src/Redux`: `adminSlice` — add `saveClub` (create path) and upsert the created club into `clubs`
- 🟩 Register the route (React Router): not needed — route registered in US001
- 🟩 Create/Edit forms: `ClubModal` create form (in scope)
- 🟩 Client-side filtering: not in this story
- 🟩 States:
  - Field errors (inline)
  - API error `409 CLUB_ALREADY_EXISTS` (displayed in the form)
  - Busy state (submit disabled)
- 🟩 Navigation: "+ Nouveau club" button opens the modal
- 🟩 Validation: `partnerId` required; `name`/`street`/`city`/`zipCode` required; `latitude` in `[-90, 90]`, `longitude` in `[-180, 180]`; `contactEmail` well formed
- 🟩 Follow design system & existing style patterns

### 4. Modified / New Files

```
/
├── onshore-frontend
│   └── src
│       ├── pages
│       │   └── admin
│       │       └── AdminClubs.jsx # 🟨 MODIFIED (add ClubModal + "+ Nouveau club" button)
│       ├── services
│       │   └── clubService.js # 🟨 MODIFIED (add createClub)
│       └── Redux
│           └── admin
│               └── adminSlice.js # 🟨 MODIFIED (add saveClub)
└── documentation
    └── epics
        └── EP004 - Club Management Frontend
            └── user-stories
                └── US002 - create-a-club
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
