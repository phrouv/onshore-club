# Requirement Specification — EP004 Club Management Frontend

| Field           | Value                             |
| --------------- | --------------------------------- |
| Epic            | EP004 – Club Management Frontend  |
| Document status | Draft                             |
| Type            | Frontend follow-up of EP003 Clubs |
| Owner           | _TBD_                             |
| Last updated    | 2026-11-01                        |

## 1. Introduction

### 1.1 Purpose

Define the admin-only frontend for managing **clubs**: the pages, routes, `services/clubs` API layer and Redux wiring that let an operator list, create, edit and open/close the sites of the platform.

### 1.2 Background

- EP003 introduced the `Club` entity and the admin-only `/api/clubs/**` endpoints in `partner-service`.
- The Onshore frontend already has an admin area (`AdminShell`) with CRUD pages (e.g. `AdminCategories`, `AdminPartners`), an API utility (`src/utils/api`), domain services (`src/services/*Service.js`) and a central `adminSlice`.
- No club screen exists yet; EP003 explicitly deferred the UI ("Backend API only; no dedicated screen yet").

### 1.3 Business goals

- Give operators a single place to review, create, edit and open/close every club.
- Reuse the existing admin shell, component library and Redux patterns to keep the change small and consistent.

## 2. Scope

**In scope**

- Admin-only club list, create, edit and open/close screens.
- `src/services/clubService.js` API layer and `adminSlice` wiring.
- Client-side validation aligned with EP003 rules (mandatory fields, coordinate ranges, email format).

**Out of scope**

- Member or partner club screens (club APIs are admin-only).
- Assigning activities to clubs (EP003 follow-up on the backend).
- Club deletion (not offered by the API).
- A public-facing map or distance search (pending Q-03 in EP003).

## 3. Dependencies

- Blocking: EP003 Clubs backend endpoints (`/api/clubs/**` routed by the api-gateway).
- Blocking: `ROLE_SYSTEM_ADMIN` route protection already provided by `ProtectedRoute` and the admin shell.
- Blocking: partner list for the `partnerId` selector (already available via `adminSlice.partners`).
- Non-blocking: Figma screens for the club list/form once available.

## 4. Pages & Routes

### 4.1 Route

Add one admin route in `src/App.jsx`, inside the existing `ROLE_SYSTEM_ADMIN` group:

```jsx
<Route element={<ProtectedRoute role="ROLE_SYSTEM_ADMIN" />}>
  <Route path="/admin" element={<AdminShell />}>
    {/* ...existing routes... */}
    <Route path="clubs" element={<AdminClubs />} />
  </Route>
</Route>
```

With the import `import AdminClubs from "./pages/admin/AdminClubs";`.

### 4.2 Navigation

Add a link in `src/components/layout/AdminShell.jsx`:

```jsx
import { MapPin } from "lucide-react";
// ...
{ to: "/admin/clubs", label: "Clubs", icon: MapPin },
```

### 4.3 Page summary

| Route          | Page         | Purpose                                                        |
| -------------- | ------------ | -------------------------------------------------------------- |
| `/admin/clubs` | `AdminClubs` | List clubs, filter by partner, create/edit (modal), open/close |

A dedicated detail page is not required in v1: the list + modal pattern (same as `AdminCategories`) covers the CRUD surface. A detail page can be added later if maps or per-club analytics are introduced.

## 5. Services layer — `src/services/clubService.js`

Follow the existing `partnerService` / `adminService` style using the `src/utils/api` helpers:

```js
import { get, patch, post, put } from "../utils/api";

export const clubService = {
  listClubs: (params) => get("/api/clubs", params),
  getClub: (id) => get(`/api/clubs/${id}`),
  createClub: (body) => post("/api/clubs", body),
  updateClub: (id, body) => put(`/api/clubs/${id}`, body),
  setClubOpen: (id, open) => patch(`/api/clubs/${id}/open`, { open }),
};
```

Payload contract (from EP003):

```json
{
  "partnerId": "<partner-id>",
  "name": "Zen Studio Marais",
  "street": "12 rue de la Paix",
  "city": "Paris",
  "zipCode": "75002",
  "latitude": 48.8698,
  "longitude": 2.3323,
  "description": "Main yoga studio",
  "contactFirstName": "Alex",
  "contactLastName": "Martin",
  "contactEmail": "contact@zen.example",
  "contactPhoneNumber": "+33100000000"
}
```

Responses add `id`, `partnerId`, `open` and `createdAt`.

## 6. Redux wiring — `src/Redux/admin/adminSlice.js`

Add thunks and state following the existing pattern:

```js
export const fetchClubs = createAsyncThunk(
  "admin/clubs",
  guarded(() => clubService.listClubs({})),
);
export const saveClub = createAsyncThunk(
  "admin/saveClub",
  guarded(({ id, body }) =>
    id ? clubService.updateClub(id, body) : clubService.createClub(body),
  ),
);
export const setClubOpen = createAsyncThunk(
  "admin/clubOpen",
  guarded(({ id, open }) => clubService.setClubOpen(id, open)),
);
```

Slice state additions:

```js
clubs: [],
```

And `extraReducers` using `asyncCases` and the existing `upsert` helper:

```js
asyncCases(builder, fetchClubs, (state, { payload }) => {
  state.clubs = payload;
});
asyncCases(builder, saveClub, (state, { payload }) => {
  state.clubs = upsert(state.clubs, payload);
});
asyncCases(builder, setClubOpen, (state, { payload }) => {
  state.clubs = upsert(state.clubs, payload);
});
```

## 7. Components

### 7.1 `src/pages/admin/AdminClubs.jsx`

Structure mirroring `AdminCategories`:

- `PageHeader` with title "Clubs" and a "+ Nouveau club" action.
- Partner filter (`<select>` or `Select`) backed by `state.admin.partners`.
- Table columns: `Nom`, `Partenaire`, `Ville`, `Coordonnées`, `Ouvert`, actions (`Modifier`, open/close).
- `EmptyState` when no clubs, `ErrorBanner` for load errors.
- Inline `ClubModal` for create/edit (single modal component, same as `CategoryModal`).

### 7.2 `ClubModal` (form)

Fields and validation:

- `partnerId` — required select, chosen first when creating and editable when updating.
- `name`, `street`, `city`, `zipCode` — required text inputs.
- `latitude`, `longitude` — optional numeric inputs, validated in `[-90, 90]` / `[-180, 180]`.
- `description` — textarea.
- `contactFirstName`, `contactLastName`, `contactEmail`, `contactPhoneNumber` — optional; `contactEmail` format-checked.
- Submit disabled while busy (`useAction`), field errors shown inline, `409 CLUB_ALREADY_EXISTS` surfaced from `ApiError`.

### 7.3 Open/close toggle

Use the existing `SegmentedSwitch` (binary control) or a `Badge` + action button for the `open` state; call `setClubOpen({ id, open })` and toast "Club ouvert"/"Club fermé".

## 8. Functional requirements (UI)

| ID    | Requirement                                                                                 | Priority |
| ----- | ------------------------------------------------------------------------------------------- | -------- |
| FR-01 | The club page is only reachable by `ROLE_SYSTEM_ADMIN`.                                     | Must     |
| FR-02 | The page lists all clubs and filters them by partner.                                       | Must     |
| FR-03 | An admin can create a club by first selecting the partner, then filling mandatory fields.   | Must     |
| FR-04 | An admin can edit a club and change the partner it belongs to.                              | Must     |
| FR-05 | An admin can open/close a club from the list.                                               | Must     |
| FR-06 | Field-level errors and API errors (`CLUB_ALREADY_EXISTS`, `FORBIDDEN_OPERATION`) are shown. | Must     |

## 9. Acceptance criteria

| ID  | Given                                              | When                                  | Then                                                                 |
| --- | -------------------------------------------------- | ------------------------------------- | -------------------------------------------------------------------- |
| AC1 | I am an admin on `/admin/clubs`                    | The page loads                        | The list of clubs is displayed (or an empty state)                   |
| AC2 | I am not an admin                                  | I open `/admin/clubs`                 | I am redirected and cannot access the page                           |
| AC3 | Clubs of several partners exist                    | I filter by a partner                 | Only that partner's clubs are shown                                  |
| AC4 | I choose a partner and fill valid mandatory fields | I submit "Nouveau club"               | The club is created under the chosen partner and appears in the list |
| AC5 | I edit a club and choose a different partner       | I save                                | The club appears under the new partner                               |
| AC6 | A mandatory field is missing                       | I submit the form                     | Field errors are shown and no request is sent                        |
| AC7 | I enter latitude/longitude out of range            | I submit the form                     | A validation error is shown and no request is sent                   |
| AC8 | The backend returns 409 `CLUB_ALREADY_EXISTS`      | I create a club with a duplicate name | The error is displayed in the form                                   |
| AC9 | I toggle a club's `open` state                     | The action completes                  | The list reflects the new `open` value with a confirmation toast     |

## 10. Risks and open questions

| #    | Item                                                                 | Mitigation / owner        |
| ---- | -------------------------------------------------------------------- | ------------------------- |
| R-01 | Geolocation optional means the UI must allow empty coordinates       | Optional numeric inputs   |
| Q-01 | Should the list show a map preview for coordinates?                  | Product owner / tech lead |
| Q-02 | Should club management stay on one page or split into list + detail? | Product owner             |
| Q-03 | Are coordinates displayed raw or formatted (DMS vs decimal)?         | Product owner / tech lead |

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
| 0.1     | 2026-11-01 |        | Initial draft |
