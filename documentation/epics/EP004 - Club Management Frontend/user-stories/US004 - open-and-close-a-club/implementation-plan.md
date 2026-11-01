# Implementation Plan Template - US004 Open and Close a Club

## Overview

Short summary of the user story purpose and expected outcome.

Include:

- Story: US004 - Open and Close a Club
- Epic: EP004 - Club Management Frontend
- Domain: Existing frontend (`onshore-frontend`, admin area)
- Primary Actor / Persona: Admin
- Business Value: Control which sites host activities and check-ins
- Notes: Depends on EP003 Clubs backend (`PATCH /api/clubs/{clubId}/open`); US001 - Browse and Filter Clubs (the list hosting the toggle)

## Steps

### 1. Setup & Configuration

- 🟩 Identify impacted modules: `onshore-frontend` (no backend — EP003 already delivered `/api/clubs`)
- 🟩 Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: not applicable (toggle only)
- 🟩 Verify naming conventions (API & DB specs)
- 🟩 Check Liquibase master changelog inclusion needs — not applicable (no DB change)
- 🟩 Additional libraries required? No

### 2. Backend Implementation (Per Impacted Microservice)

- Not applicable — no backend microservice is impacted by this story (EP003 already delivered `/api/clubs`).

### 3. Frontend Implementation (`onshore-frontend`)

- 🟩 Page/component under `src/pages/admin`: add an open/close control to each club row in `AdminClubs.jsx` (`SegmentedSwitch` or `Badge` + action button)
- 🟩 API client under `src/services`: `clubService.setClubOpen` (calls the API gateway `PATCH /api/clubs/{clubId}/open`)
- 🟩 Redux slice/state under `src/Redux`: `adminSlice` — add `setClubOpen` and upsert the updated club into `clubs`
- 🟩 Register the route (React Router): not needed — route registered in US001
- 🟩 Create/Edit forms: not in this story
- 🟩 Client-side filtering: not in this story
- 🟩 States:
  - Confirmation toast ("Club ouvert" / "Club fermé")
  - Error toast on failure (state unchanged)
- 🟩 Navigation: the toggle is an inline row action
- 🟩 Validation: not in this story
- 🟩 Follow design system & existing style patterns

### 4. Modified / New Files

```
/
├── onshore-frontend
│   └── src
│       ├── pages
│       │   └── admin
│       │       └── AdminClubs.jsx # 🟨 MODIFIED (add open/close control)
│       ├── services
│       │   └── clubService.js # 🟨 MODIFIED (add setClubOpen)
│       └── Redux
│           └── admin
│               └── adminSlice.js # 🟨 MODIFIED (add setClubOpen)
└── documentation
    └── epics
        └── EP004 - Club Management Frontend
            └── user-stories
                └── US004 - open-and-close-a-club
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
