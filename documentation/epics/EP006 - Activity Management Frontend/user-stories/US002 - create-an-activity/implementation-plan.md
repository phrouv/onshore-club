# Implementation Plan Template - US002 Create an Activity

## Overview

Admin creates an activity from a modal on `/admin/activities` by selecting an open club, an active category and a name, with optional description, instructor contact and `subscriptionDate`. Client-side validation runs before the request; on success the activity is created with status `ACTIVE` and appears in the list.

- Story: US002 - Create an Activity
- Epic: EP006 - Activity Management Frontend
- Domain: Existing domain (activity API in partner-service; frontend-only story)
- Primary Actor / Persona: Admin
- Business Value: register activities so they are offered on the platform
- Notes: Blocking dependencies — EP005 Activities backend `POST /api/activities`, `adminSlice.clubs` (open clubs) and `adminSlice.categories` (active categories), US001 (page hosting the modal), `activityService.createActivity` + `adminSlice.saveActivity`

## Steps

### 1. Setup & Configuration

- ✅ Identify impacted modules: onshore-frontend (no microservice change — the `POST /api/activities` endpoint already exists from EP005)
- ✅ Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: not applicable (create form only)
- ✅ Verify naming conventions (API & DB specs)
- ✅ Check Liquibase master changelog inclusion needs (not applicable — no backend/DB change)
- ✅ Additional libraries required? No

### 2. Backend Implementation (Per Impacted Microservice)

No microservice impacted — the `POST /api/activities` endpoint (admin-only) already exists from EP005 US001.

### 3. Frontend Implementation (`onshore-frontend`)

- ✅ Page/component under `src/pages/admin`: `ActivityModal` hosted in `AdminActivities` — create form with club, category, name, description, instructor contact and `subscriptionDate`
- ✅ API client under `src/services`: `activityService` (methods required: `createActivity`), calling the API gateway
- ✅ Redux slice/state under `src/Redux`: extend `adminSlice` with `saveActivity` (create branch) and `upsert` the created activity
- ✅ Register the route (React Router): no new route (the modal is hosted on `/admin/activities` from US001)
- ✅ Create/Edit forms (only if in story scope): create form in scope (`ActivityModal`)
- ✅ Client-side filtering (if specified): not applicable
- ✅ States:
  - Field errors (mandatory fields, invalid email)
  - API error in the form (`ACTIVITY_ALREADY_EXISTS`, `CLUB_CLOSED`, `PARTNER_NOT_ACTIVE`, `CATEGORY_NOT_FOUND`)
  - Busy submit disabled during the request
- ✅ Navigation: "+ Nouvelle activité" button opens the modal
- ✅ Validation (only if create/edit in scope): in scope — `clubId`, `categoryId` and `name` required; `instructorEmail` format-checked
- ✅ Follow design system & existing style patterns

### 4. Modified / New Files

```

/
├── onshore-frontend
│   └── src
│       ├── pages
│       │   └── admin
│       │       └── AdminActivities.jsx # 🟨 MODIFIED (ActivityModal + "+ Nouvelle activité" action)
│       ├── services
│       │   └── activityService.js # 🟨 MODIFIED (add createActivity)
│       └── Redux
│           └── admin
│               └── adminSlice.js # 🟨 MODIFIED (saveActivity thunk — create branch)
└── documentation
    └── epics
        └── EP006 - Activity Management Frontend
            └── user-stories
                └── US002 - create-an-activity
                    └── implementation-plan.md # 🟨 UPDATED

```

### Notes

- Always follow existing code style & structure
- Follow the backend/frontend instruction rule files in `.github/instructions/` and the API/database specifications in `specifications/`
- Defer non-required fields / operations to future stories

## Status

- Status
  - Overall: ✅ DONE
