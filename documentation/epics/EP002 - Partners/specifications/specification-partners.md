# Requirement Specification — EP002 Partners

| Field           | Value                                                  |
| --------------- | ------------------------------------------------------ |
| Epic            | EP002 – Partners                                       |
| Document status | Draft                                                  |
| Type            | Specification of an existing entity, with enhancements |
| Owner           | _TBD_                                                  |
| Last updated    | 2026-10-31                                             |

## 1. Introduction

### 1.1 Purpose

Define the requirements for the **partner** entity (a venue or studio offering activities to club members): signup, approval workflow, profile management and browsing.

### 1.2 Background / current situation

- `partner-service` owns the `Partner` entity (table `partners`):
  - Required: `name`, `address`, `city`, `ownerUserId`.
  - Optional: `description`, `phone`, `professionalEmail`, `website`, `instagram`, `activityTypes`, `desiredSeats` (string), `managerFirstName`, `managerLastName`.
  - System-managed: `id` (UUID), `status` (default `PENDING`), `createdAt`.
- `PartnerStatus` (common-lib): `PENDING`, `ACTIVE`, `SUSPENDED`, `REJECTED`.
- Endpoints: `POST /api/partners`, `GET /api/partners` (`status`, `city`), `GET /api/partners/mine`, `GET /api/partners/{id}`, `PUT /api/partners/{id}`, `PATCH /api/partners/{id}/status`, and `GET /internal/partners/{id}` (returns `PartnerInfo`: id, name, ownerUserId, status).
- Known gaps:
  - Any status can be set from any status.
  - Listing filters in memory after `findAll()`.
  - Email is not validated; `desiredSeats` is a string.
  - The partner has no category: categories belong to activities (see EP001).
  - `GET /api/partners/{id}` is not restricted by status.

### 1.3 Business goals

- Controlled onboarding of partners through admin approval.
- A trustworthy public listing showing only approved partners.
- Reliable partner data for activities, bookings and payouts.

### 1.4 Scope

**In scope**

- Partner signup, profile update and status workflow.
- Partner listing, filtering and detail.
- Internal partner lookup for other services.

**Out of scope**

- Activities (separate specification).
- Category catalog (EP001), payouts and credit pricing.
- Several owners or staff accounts per partner.

### 1.5 Definitions

| Term           | Meaning                                                        |
| -------------- | -------------------------------------------------------------- |
| Partner        | A venue or studio that offers activities on the platform       |
| Owner          | The user who signed up the partner (`ownerUserId`)             |
| Partner status | `PENDING` (awaiting review), `ACTIVE`, `SUSPENDED`, `REJECTED` |
| PartnerInfo    | Minimal internal view of a partner used by other services      |

## 2. Stakeholders and users

| Role           | Interest                              | Permissions                                      |
| -------------- | ------------------------------------- | ------------------------------------------------ |
| Admin          | Reviews and moderates partners        | Read all, edit any partner, change status        |
| Partner owner  | Gets listed and keeps profile current | Sign up, read own partner, edit own profile      |
| Member         | Discovers partners                    | Read and filter `ACTIVE` partners                |
| Other services | Resolve partner identity and status   | Internal read access (`/internal/partners/{id}`) |

## 3. Assumptions, dependencies, constraints

- Authentication and role headers (`X-User-Id`, `X-User-Roles`) are provided by the API gateway; admin checks use `Roles.requireAdmin` / `Roles.isAdmin`.
- `partner-service` owns partner data (PostgreSQL); other services access it through the API or `/internal`, never through shared tables.
- `/internal` endpoints are never routed by the api-gateway.
- Categories are assigned to activities, not to partners (EP001).

## 4. Functional requirements

| ID    | Requirement                                                                                                            | Priority |
| ----- | ---------------------------------------------------------------------------------------------------------------------- | -------- |
| FR-01 | An authenticated user can sign up one partner; it is created as `PENDING` and the caller becomes its owner.            | Must     |
| FR-02 | A user who already owns a partner cannot sign up another (409 `PARTNER_ALREADY_EXISTS`).                               | Must     |
| FR-03 | `name`, `address` and `city` are mandatory; all other profile fields are optional.                                     | Must     |
| FR-04 | The owner or an admin can update the partner profile; anyone else is rejected (403 `FORBIDDEN_OPERATION`).             | Must     |
| FR-05 | Only an admin can change a partner's status.                                                                           | Must     |
| FR-06 | Non-admins see only `ACTIVE` partners in listings; admins can filter by any status.                                    | Must     |
| FR-07 | Listings filter by `city`, case-insensitively.                                                                         | Must     |
| FR-08 | An owner can fetch their own partner through `/mine` whatever its status (404 `PARTNER_NOT_FOUND` if none).            | Must     |
| FR-09 | A partner that is not `ACTIVE` cannot create activities or receive bookings (409 `PARTNER_NOT_ACTIVE`).                | Must     |
| FR-10 | Status transitions are restricted: `PENDING` → `ACTIVE` or `REJECTED`; `ACTIVE` ⇄ `SUSPENDED`; `REJECTED` is terminal. | Should   |
| FR-11 | `professionalEmail`, when provided, is well formed.                                                                    | Should   |
| FR-12 | The partner has no category; the category of an activity references an active code from the EP001 catalog.             | Must     |
| FR-13 | Listing is filtered and paginated in the database instead of in memory.                                                | Could    |
| FR-14 | The owner is notified when the partner's status changes.                                                               | Could    |

## 5. Business rules

- BR-01: A user owns at most one partner.
- BR-02: `ownerUserId` is immutable once the partner is created.
- BR-03: Only `ACTIVE` partners are publicly visible.
- BR-04: `status` and `ownerUserId` cannot be set through the profile update (`PUT`); status changes go through `PATCH /status`.
- BR-05: Suspending or rejecting a partner keeps its data and history.

## 6. Non-functional requirements

| ID     | Category      | Requirement                                                                             |
| ------ | ------------- | --------------------------------------------------------------------------------------- |
| NFR-01 | Security      | Ownership and admin checks are enforced server-side, not only in the UI.                |
| NFR-02 | Performance   | Partner list responds in under 200 ms at p95.                                           |
| NFR-03 | Privacy       | Contact and manager details are not written to logs.                                    |
| NFR-04 | Auditability  | Creation, update and status changes record who and when.                                |
| NFR-05 | Compatibility | Changes to existing endpoints stay backward compatible for current clients.             |
| NFR-06 | Quality       | Unit tests for ownership, status transitions and visibility; integration tests for API. |

## 7. Interfaces

| Method | Endpoint                    | Access                | Description                           |
| ------ | --------------------------- | --------------------- | ------------------------------------- |
| POST   | `/api/partners`             | Authenticated         | Sign up a partner (created `PENDING`) |
| GET    | `/api/partners`             | Authenticated         | List partners (`status`, `city`)      |
| GET    | `/api/partners/mine`        | Authenticated         | Partner owned by the caller           |
| GET    | `/api/partners/{id}`        | Authenticated         | Partner detail                        |
| PUT    | `/api/partners/{id}`        | Owner or admin        | Update profile                        |
| PATCH  | `/api/partners/{id}/status` | Admin                 | Change status (`?status=`)            |
| GET    | `/internal/partners/{id}`   | Internal (not routed) | `PartnerInfo` for other services      |

**Partner payload**

```json
{
  "name": "Zen Studio",
  "address": "12 rue de la Paix",
  "city": "Paris",
  "description": "Yoga and pilates studio",
  "phone": "+33100000000",
  "professionalEmail": "contact@zen.example",
  "website": "https://zen.example",
  "instagram": "@zenstudio",
  "activityTypes": "Yoga, Pilates",
  "desiredSeats": "20",
  "managerFirstName": "Alex",
  "managerLastName": "Martin"
}
```

Responses add `id`, `status`, `ownerUserId` and `createdAt`.

**Errors:** `PARTNER_NOT_FOUND` (404), `PARTNER_ALREADY_EXISTS` (409), `PARTNER_NOT_ACTIVE` (409), `FORBIDDEN_OPERATION` (403).

Existing endpoints affected: `/api/activities`, bookings and payouts (partner status checks).

## 8. User stories and acceptance criteria

### US-01 Sign up a partner (Partner owner)

As a prospective partner, I want to submit my venue so that I can be listed on the platform.

- **Given** I am authenticated and own no partner, **when** I submit valid mandatory fields, **then** a partner is created with status `PENDING` and I am its owner.
- **Given** I already own a partner, **when** I submit another, **then** I get a 409.
- **Given** a mandatory field is missing, **when** I submit, **then** I get a 400 validation error.
- **Given** I provide an invalid email, **when** I submit, **then** I get a 400 validation error.

### US-02 Update my profile (Partner owner)

As a partner owner, I want to edit my profile so that my information stays current.

- **Given** I own the partner, **when** I update its profile, **then** the changes are saved and status and owner are unchanged.
- **Given** I do not own the partner and am not an admin, **when** I update it, **then** I get a 403.
- **Given** I send `status` or `ownerUserId` in the update, **then** they are ignored.

### US-03 Review a partner (Admin)

As an admin, I want to approve or reject pending partners so that only trusted venues are listed.

- **Given** a `PENDING` partner, **when** I set it to `ACTIVE`, **then** it appears in public listings.
- **Given** a `PENDING` partner, **when** I set it to `REJECTED`, **then** it never appears in public listings.
- **Given** a `REJECTED` partner, **when** I try to change its status, **then** the request is rejected.
- **Given** I am not an admin, **when** I change a status, **then** I get a 403.

### US-04 Suspend and reactivate a partner (Admin)

As an admin, I want to suspend a partner so that it stops receiving bookings.

- **Given** an `ACTIVE` partner, **when** I suspend it, **then** it disappears from public listings and cannot create activities or receive bookings.
- **Given** a `SUSPENDED` partner, **when** I reactivate it, **then** it is `ACTIVE` again with its data intact.

### US-05 Browse partners (Member)

As a member, I want to browse and filter partners so that I can find where to go.

- **Given** partners in several statuses, **when** I list partners, **then** only `ACTIVE` ones are returned.
- **Given** partners in several cities, **when** I filter by `city`, **then** only matching partners are returned, whatever the casing.

### US-06 View my partner (Partner owner)

As a partner owner, I want to see my partner and its status.

- **Given** I own a partner, **when** I call `/mine`, **then** I get it whatever its status.
- **Given** I own no partner, **when** I call `/mine`, **then** I get a 404.

### US-07 Be notified of status changes (Partner owner)

As a partner owner, I want to be told when my partner is approved, rejected or suspended.

- **Given** an admin changes my partner's status, **when** the change is saved, **then** I receive a notification with the new status.

## 9. Data migration

1. The columns `category`, `latitude`, `longitude` and `manager_role` are dropped from `partners` (Liquibase changeset 002); their values are lost.
2. No other schema or data change is required, unless `desiredSeats` is changed to a numeric type (see Q-03).

## 10. Risks and open questions

| #    | Item                                                                  | Mitigation / owner         |
| ---- | --------------------------------------------------------------------- | -------------------------- |
| R-01 | Enforcing transitions (FR-10) may block current admin practice        | Validate with admins first |
| R-02 | In-memory filtering degrades as the partner count grows               | FR-13                      |
| Q-01 | Can a `REJECTED` owner re-apply, given the one-partner-per-user rule? | Product owner              |
| Q-02 | Should public listings expose contact details and manager name?       | Product owner / DPO        |
| Q-03 | Should `desiredSeats` become numeric?                                 | Tech lead                  |
| Q-04 | Which channel is used for status notifications (email, in-app)?       | Product owner              |

## 11. Acceptance and sign-off

- All **Must** requirements implemented and tested.
- Product owner validates the user stories above.

| Role          | Name | Date | Signature |
| ------------- | ---- | ---- | --------- |
| Product owner |      |      |           |
| Tech lead     |      |      |           |

## 12. Revision history

| Version | Date       | Author | Changes                                                                       |
| ------- | ---------- | ------ | ----------------------------------------------------------------------------- |
| 0.1     | 2026-10-31 |        | Initial draft                                                                 |
| 0.2     | 2026-10-31 |        | Remove `category`, `latitude`, `longitude` and `managerRole` from the partner |
