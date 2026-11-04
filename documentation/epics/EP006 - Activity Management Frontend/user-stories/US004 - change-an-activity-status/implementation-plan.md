# Implementation Plan Template - US004 Change an Activity Status

## Overview

Admin changes an activity's status directly from the list on `/admin/activities` using a color-coded `Badge` and contextual action buttons (`Suspendre`, `Réactiver`, `Rejeter`). The action calls the status endpoint and reports the outcome with a toast.

- Story: US004 - Change an Activity Status
- Epic: EP006 - Activity Management Frontend
- Domain: Existing domain (activity API in partner-service; frontend-only story)
- Primary Actor / Persona: Admin
- Business Value: control whether an activity is offered on the platform
- Notes: Blocking dependencies — EP005 Activities backend `PATCH /api/activities/{id}/status`, US001 (list hosting the actions), `activityService.setActivityStatus` + `adminSlice.setActivityStatus`

## Steps

### 1. Setup & Configuration

- ✅ Identify impacted modules: onshore-frontend (no microservice change — the `PATCH /api/activities/{id}/status` endpoint already exists from EP005)
- ✅ Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: not applicable (status action only)
- ✅ Verify naming conventions (API & DB specs)
- ✅ Check Liquibase master changelog inclusion needs (not applicable — no backend/DB change)
- ✅ Additional libraries required? No

### 2. Backend Implementation (Per Impacted Microservice)

No microservice impacted — the `PATCH /api/activities/{id}/status` endpoint (admin-only) already exists from EP005 US003.

### 3. Frontend Implementation (`onshore-frontend`)

- ✅ Page/component under `src/pages/admin`: `AdminActivities` — color-coded `Badge` (`ACTIVE` green, `SUSPENDED` amber, `REJECTED` red) + contextual action buttons
- ✅ API client under `src/services`: `activityService` (methods required: `setActivityStatus`), calling the API gateway
- ✅ Redux slice/state under `src/Redux`: extend `adminSlice` with `setActivityStatus` and `upsert` the updated activity
- ✅ Register the route (React Router): no new route (actions live on `/admin/activities` from US001)
- ✅ Create/Edit forms (only if in story scope): not in scope
- ✅ Client-side filtering (if specified): not applicable
- ✅ States:
  - Confirmation toast on success
  - Error toast on failure (status unchanged)
- ✅ Navigation: contextual status actions on each list row
- ✅ Validation (only if create/edit in scope): not in scope
- ✅ Follow design system & existing style patterns

### 4. Modified / New Files

```

/
├── onshore-frontend
│   └── src
│       ├── pages
│       │   └── admin
│       │       └── AdminActivities.jsx # 🟨 MODIFIED (status badge + contextual actions)
│       ├── services
│       │   └── activityService.js # 🟨 MODIFIED (add setActivityStatus)
│       └── Redux
│           └── admin
│               └── adminSlice.js # 🟨 MODIFIED (setActivityStatus thunk)
└── documentation
    └── epics
        └── EP006 - Activity Management Frontend
            └── user-stories
                └── US004 - change-an-activity-status
                    └── implementation-plan.md # 🟨 UPDATED

```

### Notes

- Always follow existing code style & structure
- Follow the backend/frontend instruction rule files in `.github/instructions/` and the API/database specifications in `specifications/`
- Defer non-required fields / operations to future stories

## Status

- Status
  - Overall: ✅ DONE
