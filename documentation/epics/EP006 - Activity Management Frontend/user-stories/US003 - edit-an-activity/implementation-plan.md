# Implementation Plan Template - US003 Edit an Activity

## Overview

Admin edits an activity from a prefilled modal on `/admin/activities`. Editable fields are the club (within the same partner), the category, the name, the description and the instructor contact; `status` and `subscriptionDate` are read-only. Client-side validation runs before the request; on success the list is updated.

- Story: US003 - Edit an Activity
- Epic: EP006 - Activity Management Frontend
- Domain: Existing domain (activity API in partner-service; frontend-only story)
- Primary Actor / Persona: Admin
- Business Value: keep the activity information accurate
- Notes: Blocking dependencies — EP005 Activities backend `PUT /api/activities/{id}`, `adminSlice.clubs` (open clubs) and `adminSlice.categories` (active categories), US001 (page hosting the modal), `activityService.updateActivity` + `adminSlice.saveActivity`

## Steps

### 1. Setup & Configuration

- ✅ Identify impacted modules: onshore-frontend (no microservice change — the `PUT /api/activities/{id}` endpoint already exists from EP005)
- ✅ Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: not applicable (edit form only)
- ✅ Verify naming conventions (API & DB specs)
- ✅ Check Liquibase master changelog inclusion needs (not applicable — no backend/DB change)
- ✅ Additional libraries required? No

### 2. Backend Implementation (Per Impacted Microservice)

No microservice impacted — the `PUT /api/activities/{id}` endpoint (admin-only) already exists from EP005 US002.

### 3. Frontend Implementation (`onshore-frontend`)

- ✅ Page/component under `src/pages/admin`: `ActivityModal` prefilled in `AdminActivities` — edit form with club (same partner), category, name, description and instructor contact
- ✅ API client under `src/services`: `activityService` (methods required: `updateActivity`), calling the API gateway
- ✅ Redux slice/state under `src/Redux`: extend `adminSlice` with `saveActivity` (update branch) and `upsert` the updated activity
- ✅ Register the route (React Router): no new route (the modal is hosted on `/admin/activities` from US001)
- ✅ Create/Edit forms (only if in story scope): edit form in scope (`ActivityModal`, prefilled)
- ✅ Client-side filtering (if specified): not applicable
- ✅ States:
  - Field errors (mandatory fields, invalid email)
  - API error in the form (`ACTIVITY_ALREADY_EXISTS`, `FORBIDDEN_OPERATION`)
  - Busy submit disabled during the request
- ✅ Navigation: "Modifier" action on a list row opens the prefilled modal
- ✅ Validation (only if create/edit in scope): in scope — `clubId`, `categoryId` and `name` required; `instructorEmail` format-checked
- ✅ Follow design system & existing style patterns

### 4. Modified / New Files

```

/
├── onshore-frontend
│   └── src
│       ├── pages
│       │   └── admin
│       │       └── AdminActivities.jsx # 🟨 MODIFIED (prefilled ActivityModal + "Modifier" action)
│       ├── services
│       │   └── activityService.js # 🟨 MODIFIED (add updateActivity)
│       └── Redux
│           └── admin
│               └── adminSlice.js # 🟨 MODIFIED (saveActivity thunk — update branch)
└── documentation
    └── epics
        └── EP006 - Activity Management Frontend
            └── user-stories
                └── US003 - edit-an-activity
                    └── implementation-plan.md # 🟨 UPDATED

```

### Notes

- Always follow existing code style & structure
- Follow the backend/frontend instruction rule files in `.github/instructions/` and the API/database specifications in `specifications/`
- Defer non-required fields / operations to future stories

## Status

- Status
  - Overall: ✅ DONE
