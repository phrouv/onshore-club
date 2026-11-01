# Implementation Plan Template - US001 Browse and Filter Clubs

## Overview

Short summary of the user story purpose and expected outcome.

Include:

- Story: US001 - Browse and Filter Clubs
- Epic: EP004 - Club Management Frontend
- Domain: Existing frontend (`onshore-frontend`, admin area)
- Primary Actor / Persona: Admin
- Business Value: Review and manage all the sites of the platform
- Notes: Depends on EP003 Clubs backend (`GET /api/clubs`); `adminSlice.partners` for the filter; `ProtectedRoute` / `AdminShell` for the admin route

## Steps

### 1. Setup & Configuration

- 🟩 Identify impacted modules: `onshore-frontend` (no backend — EP003 already delivered `/api/clubs`)
- 🟩 Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: client-side filter by `partnerId` (load all clubs, filter locally)
- 🟩 Verify naming conventions (API & DB specs)
- 🟩 Check Liquibase master changelog inclusion needs — not applicable (no DB change)
- 🟩 Additional libraries required? No

### 2. Backend Implementation (Per Impacted Microservice)

- Not applicable — no backend microservice is impacted by this story (EP003 already delivered `/api/clubs`).

### 3. Frontend Implementation (`onshore-frontend`)

- 🟩 Page/component under `src/pages/admin`: `AdminClubs.jsx` — display `name`, partner, `city`, coordinates and `open`; handle partner filter and navigation
- 🟩 API client under `src/services`: `clubService.listClubs` (calls the API gateway `GET /api/clubs`)
- 🟩 Redux slice/state under `src/Redux`: `adminSlice` — add `clubs` state + `fetchClubs` thunk
- 🟩 Register the route (React Router): `/admin/clubs` → `AdminClubs` inside the `ROLE_SYSTEM_ADMIN` group
- 🟩 Create/Edit forms: not in this story (deferred to US002 / US003)
- 🟩 Client-side filtering — rule: by `partnerId` (filter the loaded list locally)
- 🟩 States:
  - Empty list
  - No filter match
  - Error message (reuse global `ErrorBanner`)
- 🟩 Navigation: `AdminShell` "Clubs" link (`MapPin`); row edit and create button deferred to US002 / US003
- 🟩 Validation: not in this story (no forms)
- 🟩 Follow design system & existing style patterns

### 4. Modified / New Files

```
/
├── onshore-frontend
│   └── src
│       ├── App.jsx # 🟨 MODIFIED (add /admin/clubs route)
│       ├── pages
│       │   └── admin
│       │       └── AdminClubs.jsx # 🟩 NEW (list UI)
│       ├── services
│       │   └── clubService.js # 🟩 NEW (API client)
│       ├── Redux
│       │   └── admin
│       │       └── adminSlice.js # 🟨 MODIFIED (add clubs state + fetchClubs)
│       └── components
│           └── layout
│               └── AdminShell.jsx # 🟨 MODIFIED (add Clubs nav link)
└── documentation
    └── epics
        └── EP004 - Club Management Frontend
            └── user-stories
                └── US001 - browse-and-filter-clubs
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
