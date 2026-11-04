# Implementation Plan Template - US001 Browse and Filter Activities

## Overview

Admin-only page at `/admin/activities` that lists every activity with filters on club, category and status, plus pagination. It reuses the existing admin shell and table pattern, with empty, no-match and error states. Non-admins are redirected.

- Story: US001 - Browse and Filter Activities
- Epic: EP006 - Activity Management Frontend
- Domain: Existing domain (activity API in partner-service; frontend-only story)
- Primary Actor / Persona: Admin
- Business Value: The admin can review and manage the platform's activity catalog
- Notes: Blocking dependencies — EP005 Activities backend `GET /api/activities` (admin-only, paginated), `ROLE_SYSTEM_ADMIN` route protection via `ProtectedRoute`/`AdminShell`, `adminSlice.clubs` and `adminSlice.categories` for the filter options, `activityService.js`

## Steps

### 1. Setup & Configuration

- ✅ Identify impacted modules: onshore-frontend (no microservice change — the `GET /api/activities` endpoint already exists from EP005)
- ✅ Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: server-side (filters and pagination sent to the API)
- ✅ Verify naming conventions (API & DB specs)
- ✅ Check Liquibase master changelog inclusion needs (not applicable — no backend/DB change)
- ✅ Additional libraries required? No

### 2. Backend Implementation (Per Impacted Microservice)

No microservice impacted — the `GET /api/activities` endpoint (admin-only, paginated) already exists from EP005 US004.

### 3. Frontend Implementation (`onshore-frontend`)

- ✅ Page/component under `src/pages/admin`: `AdminActivities` — display Nom, Club, Catégorie, Instructeur, Statut; handle filter selection and pagination
- ✅ API client under `src/services`: `activityService` (methods required: `listActivities`), calling the API gateway
- ✅ Redux slice/state under `src/Redux`: extend `adminSlice` with `fetchActivities` and paginated state (`activities`, `activitiesPage`, `activitiesSize`, `activitiesTotal`, `activitiesPages`)
- ✅ Register the route (React Router): `/admin/activities` inside the `ROLE_SYSTEM_ADMIN` group
- ✅ Create/Edit forms (only if in story scope): not in scope (deferred to US002/US003)
- ✅ Client-side filtering (if specified): not applicable — server-side filters (`clubId`, `categoryId`, `status`) + pagination
- ✅ States:
  - Empty list
  - No filter match
  - Error message (reuse global pattern — `ErrorBanner`)
- ✅ Navigation: `AdminShell` link "Activités" with the `Dumbbell` icon
- ✅ Validation (only if create/edit in scope): not in scope
- ✅ Follow design system & existing style patterns

### 4. Modified / New Files

```

/
├── onshore-frontend
│   └── src
│       ├── pages
│       │   └── admin
│       │       └── AdminActivities.jsx # 🟩 NEW (list UI)
│       ├── services
│       │   └── activityService.js # 🟩 NEW (API client)
│       ├── Redux
│       │   └── admin
│       │       └── adminSlice.js # 🟨 MODIFIED (fetchActivities + paginated state)
│       ├── components
│       │   └── layout
│       │       └── AdminShell.jsx # 🟨 MODIFIED (add "Activités" nav link)
│       └── App.jsx # 🟨 MODIFIED (register /admin/activities route)
└── documentation
    └── epics
        └── EP006 - Activity Management Frontend
            └── user-stories
                └── US001 - browse-and-filter-activities
                    └── implementation-plan.md # 🟨 UPDATED

```

### Notes

- Always follow existing code style & structure
- Follow the backend/frontend instruction rule files in `.github/instructions/` and the API/database specifications in `specifications/`
- Defer non-required fields / operations to future stories

## Status

- Status
  - Overall: ✅ DONE
