# Requirement Specification — EP006 Activity Management Frontend

| Field           | Value                                  |
| --------------- | -------------------------------------- |
| Epic            | EP006 – Activity Management Frontend   |
| Document status | Draft                                  |
| Type            | Frontend follow-up of EP005 Activities |
| Owner           | _TBD_                                  |
| Last updated    | 2026-11-04                             |

## 1. Introduction

### 1.1 Purpose

Define the admin-only frontend for managing **activities**: the pages, routes, `services/activityService` API layer and Redux wiring that let an operator list, filter, create, edit and change the status of the activities offered by the platform.

### 1.2 Background

- EP005 introduced the `Activity` entity and the admin-only `/api/activities/**` endpoints in `partner-service` (owned by `partner-service`).
- The activity lifecycle is a 3-state workflow: `ACTIVE`, `SUSPENDED`, `REJECTED` (FR-11: `ACTIVE` ⇄ `SUSPENDED`; any status → `REJECTED`; `REJECTED` → `ACTIVE` or `SUSPENDED`).
- The Onshore frontend already has an admin area (`AdminShell`) with CRUD pages (e.g. `AdminCategories`, `AdminClubs`, `AdminPartners`), an API utility (`src/utils/api`), domain services (`src/services/*Service.js`) and a central `adminSlice`.
- No activity screen exists yet; EP005 explicitly deferred the UI ("Backend API only; no dedicated screen yet").

### 1.3 Business goals

- Give operators a single place to review, create, edit and suspend/reject/reactivate every activity.
- Reuse the existing admin shell, component library and Redux patterns to keep the change small and consistent.

## 2. Scope

**In scope**

- Admin-only activity list with filters (`clubId`, `categoryId`, `status`) and pagination.
- Admin-only create, edit and status-change actions.
- `src/services/activityService.js` API layer and `adminSlice` wiring.
- Client-side validation aligned with EP005 rules (mandatory `clubId`/`name`/`categoryId`, instructor email format).

**Out of scope**

- Member or partner activity screens (the partner area already has `PartnerActivities`).
- Assigning classes/bookings to activities (other epics).
- Activity deletion (not offered by the API).
- Legacy pricing/credit fields (`durationMinutes`, `partnerPayout`, `defaultCredits`) — superseded by EP005's status and the EP001 credit grid.

## 3. Dependencies

- Blocking: EP005 Activities backend endpoints (`/api/activities/**` routed by the api-gateway).
- Blocking: `ROLE_SYSTEM_ADMIN` route protection already provided by `ProtectedRoute` and the admin shell.
- Blocking: club list for the `clubId` selector (already available via `adminSlice.clubs`).
- Blocking: category list for the `categoryId` selector (already available via `adminSlice.categories`).
- Non-blocking: Figma screens for the activity list/form once available.

## 4. Pages & Routes

### 4.1 Route

Add one admin route in `src/App.jsx`, inside the existing `ROLE_SYSTEM_ADMIN` group:

```jsx
<Route element={<ProtectedRoute role="ROLE_SYSTEM_ADMIN" />}>
  <Route path="/admin" element={<AdminShell />}>
    {/* ...existing routes... */}
    <Route path="activities" element={<AdminActivities />} />
  </Route>
</Route>
```

With the import `import AdminActivities from "./pages/admin/AdminActivities";`.

### 4.2 Navigation

Add a link in `src/components/layout/AdminShell.jsx`:

```jsx
import { Dumbbell } from "lucide-react";
// ...
{ to: "/admin/activities", label: "Activités", icon: Dumbbell },
```

### 4.3 Page summary

| Route               | Page              | Purpose                                                                                       |
| ------------------- | ----------------- | --------------------------------------------------------------------------------------------- |
| `/admin/activities` | `AdminActivities` | List activities, filter by club/category/status, paginate, create/edit (modal), change status |

A dedicated detail page is not required in v1: the list + modal pattern (same as `AdminCategories`/`AdminClubs`) covers the CRUD surface. A detail page can be added later if per-activity analytics are introduced.

## 5. Services layer — `src/services/activityService.js`

Follow the existing `clubService` / `partnerService` style using the `src/utils/api` helpers:

```js
import { get, patch, post, put } from "../utils/api";

export const activityService = {
  listActivities: (params) => get("/api/activities", params),
  getActivity: (id) => get(`/api/activities/${id}`),
  createActivity: (body) => post("/api/activities", body),
  updateActivity: (id, body) => put(`/api/activities/${id}`, body),
  setActivityStatus: (id, status) =>
    patch(`/api/activities/${id}/status`, { status }),
};
```

> The `patch` helper serializes `params` as a query string, so `setActivityStatus(id, status)` issues
> `PATCH /api/activities/{id}/status?status=<value>`, matching the EP005 contract.
>
> `partnerService` already exposes `listActivities` / `createActivity` / `updateActivity` for the partner area and catalog. EP006 introduces a dedicated `activityService.js`; existing call sites may be migrated to it or left as-is (see Q-01).

**List parameters** (`GET /api/activities`):

| Param           | Type                              | Notes                                  |
| --------------- | --------------------------------- | -------------------------------------- |
| `clubId`        | `UUID`                            | optional filter                        |
| `categoryId`    | `UUID`                            | optional filter                        |
| `status`        | `ACTIVE`\|`SUSPENDED`\|`REJECTED` | optional filter; absent = all statuses |
| `page`          | `int`                             | default `0`                            |
| `size`          | `int`                             | default `20`                           |
| `sortBy`        | `string`                          | default `createdDate`                  |
| `sortDirection` | `ASC`\|`DESC`                     | default `DESC`                         |

The list endpoint returns a Spring `Page` object (`{ content, number, size, totalElements, totalPages, ... }`) — **paginated**, unlike the clubs/categories lists which return plain arrays.

**Payload contract** (from EP005):

```json
{
  "clubId": "<club-id>",
  "categoryId": "<category-id>",
  "name": "Morning Yoga",
  "description": "Vinyasa flow for all levels",
  "instructorFirstName": "Alex",
  "instructorLastName": "Martin",
  "instructorEmail": "alex@zen.example",
  "instructorPhoneNumber": "+33100000000",
  "subscriptionDate": "2026-11-02"
}
```

Responses add `id`, `status` and `createdAt`. On update (`PUT`), `status` and `subscriptionDate` are ignored by the backend.

## 6. Redux wiring — `src/Redux/admin/adminSlice.js`

Add thunks and state following the existing pattern:

```js
export const fetchActivities = createAsyncThunk(
  "admin/fetchActivities",
  guarded((params) => activityService.listActivities(params)),
);
export const saveActivity = createAsyncThunk(
  "admin/saveActivity",
  guarded(({ id, body }) =>
    id
      ? activityService.updateActivity(id, body)
      : activityService.createActivity(body),
  ),
);
export const setActivityStatus = createAsyncThunk(
  "admin/setActivityStatus",
  guarded(({ id, status }) => activityService.setActivityStatus(id, status)),
);
```

Slice state additions (pagination kept alongside the list because the endpoint is paginated):

```js
activities: [],
activitiesPage: 0,
activitiesSize: 20,
activitiesTotal: 0,
activitiesPages: 0,
```

`extraReducers` using `asyncCases` and the existing `upsert` helper:

```js
asyncCases(builder, fetchActivities, (state, { payload }) => {
  state.activities = payload.content;
  state.activitiesPage = payload.number;
  state.activitiesSize = payload.size;
  state.activitiesTotal = payload.totalElements;
  state.activitiesPages = payload.totalPages;
});
asyncCases(builder, saveActivity, (state, { payload }) => {
  state.activities = upsert(state.activities, payload);
});
asyncCases(builder, setActivityStatus, (state, { payload }) => {
  state.activities = upsert(state.activities, payload);
});
```

> The existing `fetchAllActivities` (which stores the whole `Page` object) is superseded by `fetchActivities`; it can be removed once no other consumer uses it.

## 7. Components

### 7.1 `src/pages/admin/AdminActivities.jsx`

Structure mirroring `AdminClubs` / `AdminCategories`:

- `PageHeader` with title "Activités" and a "+ Nouvelle activité" action.
- Filters backed by `state.admin`: club (`Select`, open clubs only), category (`Select`, active categories only), status (`Select` over the three statuses).
- Pagination controls (previous/next + total count) driven by `activitiesPage` / `activitiesPages` / `activitiesTotal`.
- Table columns: `Nom`, `Club`, `Catégorie`, `Instructeur`, `Statut`, actions (`Modifier`, status actions).
- `EmptyState` when no activities, `ErrorBanner` for load errors.
- Inline `ActivityModal` for create/edit (single modal component, same as `ClubModal`).

### 7.2 `ActivityModal` (form)

Fields and validation:

- `clubId` — required select, populated from open clubs (`club.open === true`).
- `categoryId` — required select, populated from active categories (`category.active === true`).
- `name` — required text input, unique within the selected club.
- `description` — textarea.
- `instructorFirstName`, `instructorLastName`, `instructorEmail`, `instructorPhoneNumber` — optional; `instructorEmail` format-checked.
- `subscriptionDate` — optional date input on create; read-only on edit (the backend ignores it on `PUT`).
- Submit disabled while busy (`useAction`), field errors shown inline.
- API errors surfaced from `ApiError`:
  - `ACTIVITY_ALREADY_EXISTS` → "Une activité porte déjà ce nom dans ce club"
  - `CLUB_CLOSED` → "Le club est fermé"
  - `PARTNER_NOT_ACTIVE` → "Le partenaire n'est pas actif"
  - `CATEGORY_NOT_FOUND` → "Catégorie inconnue ou inactive"
  - `CLUB_NOT_FOUND` → "Club inconnu"

### 7.3 Status change

Display the status as a color-coded `Badge` (`ACTIVE` = green, `SUSPENDED` = amber, `REJECTED` = red) with contextual action buttons, calling `setActivityStatus({ id, status })` and toasting on success:

| Current status | Available actions        | Target status           |
| -------------- | ------------------------ | ----------------------- |
| `ACTIVE`       | `Suspendre`, `Rejeter`   | `SUSPENDED`, `REJECTED` |
| `SUSPENDED`    | `Réactiver`, `Rejeter`   | `ACTIVE`, `REJECTED`    |
| `REJECTED`     | `Réactiver`, `Suspendre` | `ACTIVE`, `SUSPENDED`   |

Toasts: "Activité suspendue", "Activité réactivée", "Activité rejetée".

## 8. Functional requirements (UI)

| ID    | Requirement                                                                                                                                                                  | Priority |
| ----- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- |
| FR-01 | The activity page is only reachable by `ROLE_SYSTEM_ADMIN`.                                                                                                                  | Must     |
| FR-02 | The page lists activities, filters them by club, category and status, and paginates.                                                                                         | Must     |
| FR-03 | An admin can create an activity by selecting an open club, an active category and a name.                                                                                    | Must     |
| FR-04 | An admin can edit an activity and change its club (same partner), category, name, description and instructor.                                                                | Must     |
| FR-05 | An admin can suspend, reactivate or reject an activity from the list.                                                                                                        | Must     |
| FR-06 | Field-level errors and API errors (`ACTIVITY_ALREADY_EXISTS`, `CLUB_CLOSED`, `PARTNER_NOT_ACTIVE`, `CATEGORY_NOT_FOUND`, `CLUB_NOT_FOUND`, `FORBIDDEN_OPERATION`) are shown. | Must     |

## 9. Acceptance criteria

| ID   | Given                                                           | When                                       | Then                                                         |
| ---- | --------------------------------------------------------------- | ------------------------------------------ | ------------------------------------------------------------ |
| AC1  | I am an admin on `/admin/activities`                            | The page loads                             | The list of activities is displayed (or an empty state)      |
| AC2  | I am not an admin                                               | I open `/admin/activities`                 | I am redirected and cannot access the page                   |
| AC3  | Activities exist across clubs/categories/statuses               | I filter by a club, category or status     | Only matching activities are shown                           |
| AC4  | More activities exist than one page                             | I change the page                          | The next/previous page of activities is shown                |
| AC5  | I select an open club, an active category and a valid name      | I submit "Nouvelle activité"               | The activity is created `ACTIVE` and appears in the list     |
| AC6  | I edit an activity and change its club/category/name/instructor | I save                                     | The activity is updated in the list                          |
| AC7  | A mandatory field (`clubId`, `categoryId` or `name`) is missing | I submit the form                          | Field errors are shown and no request is sent                |
| AC8  | I enter an invalid `instructorEmail`                            | I submit the form                          | A validation error is shown and no request is sent           |
| AC9  | The backend returns 409 `ACTIVITY_ALREADY_EXISTS`               | I create an activity with a duplicate name | The error is displayed in the form                           |
| AC10 | I suspend / reactivate / reject an activity                     | The action completes                       | The list reflects the new `status` with a confirmation toast |

## 10. Risks and open questions

| #    | Item                                                                                                 | Mitigation / owner                           |
| ---- | ---------------------------------------------------------------------------------------------------- | -------------------------------------------- |
| R-01 | The activity list is paginated (Spring `Page`), unlike clubs/categories                              | Slice keeps `content` + pagination fields    |
| Q-01 | Should activities live in a dedicated `activityService.js`, or extend the existing `partnerService`? | Tech lead                                    |
| Q-02 | Should the status change be contextual buttons or a dropdown?                                        | Product owner (proposal: contextual buttons) |
| Q-03 | Should `subscriptionDate` be user-editable on create, or always system-set?                          | Product owner (EP005 Q-02)                   |
| Q-04 | Should the list show the instructor contact, or only the name?                                       | Product owner / tech lead                    |

## 11. Acceptance and sign-off

- All **Must** requirements implemented and tested (`npm run lint`, `npm run build`).
- Product owner validates the flows above.

| Role          | Name | Date | Signature |
| ------------- | ---- | ---- | --------- |
| Product owner |      |      |           |
| Tech lead     |      |      |           |

## 12. Revision history

| Version | Date       | Author | Changes       |
| ------- | ---------- | ------ | ------------- |
| 0.1     | 2026-11-04 |        | Initial draft |
