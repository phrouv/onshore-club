# Requirement Specification — EP005 Activities

| Field           | Value                          |
| --------------- | ------------------------------ |
| Epic            | EP005 – Activities             |
| Document status | Draft                          |
| Type            | Specification of a new entity  |
| Owner           | _TBD_                          |
| Last updated    | 2026-11-02                     |

## 1. Introduction

### 1.1 Purpose

Define the requirements for the **activity** entity: the class or session template a club offers, owned by `partner-service`, with its club reference, category assignment, instructor contact (`ContactPerson`), subscription date and lifecycle status (`ACTIVE`, `SUSPENDED`, `REJECTED`).

### 1.2 Background / current situation

- `partner-service` owns the `Activity` entity (table `activities`).
- Today an `Activity` references only `partnerId`, `name`, `category` (a free-text string), `description`, `durationMinutes`, `partnerPayout`, `defaultCredits` and an `active` boolean. It has no club, no instructor, no subscription date and no proper status.
- Known gaps:
  - The existing `activities` table is to be totally redesigned (this specification); its legacy pricing/credit columns are reviewed separately.
  - No club reference: an activity cannot tell members where it takes place. EP003 introduces the `Club` entity as the future site reference.
  - `category` is a free-text string; EP001 replaces it with a managed catalog referenced by id.
  - The `active` boolean is replaced by a lifecycle status.
  - No instructor contact and no subscription date.
- New model: an activity belongs to exactly one club (a club owns one or many activities), is linked to exactly one category from the EP001 catalog, carries an instructor (`ContactPerson`), a status and a subscription date.

### 1.3 Business goals

- Give every activity a concrete site (club), a category, an instructor and a subscription date.
- Replace the ad-hoc `active` flag with a lifecycle status.
- Keep other services (planning, booking, credit) able to resolve an activity and its status reliably.

### 1.4 Scope

**In scope**

- `Activity` entity owned by `partner-service`, referencing one club (`clubId`) and one category (`categoryId`), with `name`, `description`, instructor (`ContactPerson`), `status` and `subscriptionDate`.
- Activity CRUD, status workflow, listing and internal lookup.
- Enforcing the club and category references at creation and update.

**Out of scope**

- Legacy pricing/credit columns (`durationMinutes`, `partnerPayout`, `defaultCredits`) and the legacy `active` flag: superseded by the status and the EP001 credit grid; migration of their values is reviewed separately.
- Categories (EP001), clubs (EP003), partner status workflow (EP002).
- Activity deletion policy while classes or bookings still reference it.

### 1.5 Definitions

| Term              | Meaning                                                                  |
| ----------------- | ------------------------------------------------------------------------ |
| Activity          | A class/session template offered by a club (e.g. "Morning Yoga")         |
| Club              | The physical site that owns one or many activities (`clubId`)            |
| Category          | Classification of the activity from the EP001 catalog (`categoryId`)      |
| Instructor        | The contact person leading the activity (embedded `ContactPerson`)       |
| Activity status   | `ACTIVE`, `SUSPENDED`, `REJECTED`                                        |
| Subscription date | Date the activity was subscribed/registered on the platform              |

## 2. Stakeholders and users

| Role           | Interest                              | Permissions                                                                                          |
| -------------- | ------------------------------------- | ---------------------------------------------------------------------------------------------------- |
| Admin          | Reviews and moderates activities      | Full management of any activity (create, read, update, status); activity APIs are admin-only         |
| Partner owner  | Gets its activities registered        | No direct activity API access                                                                         |
| Member         | Discovers activities                  | No direct activity API access                                                                         |
| Other services | Resolve activity identity and status  | Internal read access (`/internal/activities/{id}`)                                                    |

## 3. Assumptions, dependencies, constraints

- Authentication and role headers (`X-User-Id`, `X-User-Roles`) are provided by the API gateway; admin checks use `UserRole.requireAdmin` / `UserRole.isAdmin`.
- `partner-service` owns activity data (PostgreSQL); other services access it through the API or `/internal`, never through shared tables.
- `/internal` endpoints are never routed by the api-gateway.
- The `ContactPerson` embeddable from `common-lib` is reused for the instructor.
- Categories come from the EP001 catalog (owned by `admin-service`), referenced by id.
- An activity belongs to exactly one club; its partner is resolved through that club.

## 4. Functional requirements

| ID    | Requirement                                                                                                                                    | Priority |
| ----- | ---------------------------------------------------------------------------------------------------------------------------------------------- | -------- |
| FR-01 | An operator with an admin role can create an activity by choosing the club it belongs to (`clubId`) and its category (`categoryId`); it is created `ACTIVE`. | Must     |
| FR-02 | An activity can only be created for a club that is `open` and whose partner is `ACTIVE` (409 `CLUB_CLOSED` / `PARTNER_NOT_ACTIVE` otherwise).   | Must     |
| FR-03 | `clubId`, `name` and `categoryId` are mandatory; the instructor contact fields and `description` are optional; `subscriptionDate` defaults to the creation date. | Must     |
| FR-04 | `categoryId` must reference an existing, active category from the EP001 catalog (404 `CATEGORY_NOT_FOUND` otherwise). | Must     |
| FR-05 | `name` is unique within a club (409 `ACTIVITY_ALREADY_EXISTS`).                                                                                | Must     |
| FR-06 | Only an operator with an admin role can update an activity (403 `FORBIDDEN_OPERATION` otherwise).                                              | Must     |
| FR-07 | Only an admin can change an activity's status.                                                                                                | Must     |
| FR-08 | Only admins can list activities and filter by `clubId`, `categoryId` and `status`; all statuses are returned.                                  | Must     |
| FR-09 | An admin can fetch any activity by id (404 `ACTIVITY_NOT_FOUND` if none).                                                                      | Must     |
| FR-10 | Other services resolve an activity by id through `/internal/activities/{id}` (returns `ActivityInfo`).                                         | Must     |
| FR-11 | Status transitions: `ACTIVE` ⇄ `SUSPENDED`; `REJECTED` is not terminal and can transition to `ACTIVE` or `SUSPENDED`.                          | Should   |
| FR-12 | The instructor email, when provided, is well formed (400 `INVALID_PAYLOAD` otherwise).                                                         | Should   |
| FR-13 | An admin can move an activity to another club of the same partner when updating it.                                                             | Could    |
| FR-14 | Listing is filtered and paginated in the database instead of in memory.                                                                        | Could    |

## 5. Business rules

- BR-01: An activity belongs to exactly one club at a time; a club owns one or many activities.
- BR-02: An activity is linked to exactly one category from the EP001 catalog.
- BR-03: The partner of an activity is derived from its club; it is not stored on the activity itself.
- BR-04: Activity APIs are admin-only; there is no public activity listing.
- BR-05: `status` and `subscriptionDate` cannot be set through the profile update (`PUT`); status changes go through `PATCH /status`.
- BR-06: Suspending or rejecting an activity keeps its data and history; existing classes and bookings remain traceable to it.

## 6. Non-functional requirements

| ID     | Category      | Requirement                                                                                |
| ------ | ------------- | ------------------------------------------------------------------------------------------ |
| NFR-01 | Security      | Admin-only access is enforced server-side, not only in the UI.                             |
| NFR-02 | Performance   | Activity list and detail respond in under 200 ms at p95.                                   |
| NFR-03 | Privacy       | Instructor contact details are not written to logs.                                        |
| NFR-04 | Auditability  | Creation, update and status changes record who and when.                                   |
| NFR-05 | Compatibility | Existing clients of `/api/activities/**` and `/internal/activities/{id}` keep working during the transition. |
| NFR-06 | Quality       | Unit tests for admin access, category validation and status transitions; integration tests for the API. |

## 7. Interfaces

| Method | Endpoint                       | Access                | Description                               |
| ------ | ------------------------------ | --------------------- | ----------------------------------------- |
| POST   | `/api/activities`              | Admin                 | Create an activity (created `ACTIVE`)     |
| GET    | `/api/activities`              | Admin                 | List activities (`clubId`, `categoryId`, `status`) |
| GET    | `/api/activities/{id}`         | Admin                 | Activity detail                           |
| PUT    | `/api/activities/{id}`         | Admin                 | Update profile                            |
| PATCH  | `/api/activities/{id}/status`  | Admin                 | Change status (`?status=`)                |
| GET    | `/internal/activities/{id}`    | Internal (not routed) | `ActivityInfo` for other services         |

**Activity payload**

```json
{
  "clubId": "<club-id>",
  "name": "Morning Yoga",
  "categoryId": "<category-id>",
  "description": "Vinyasa flow for all levels",
  "instructorFirstName": "Alex",
  "instructorLastName": "Martin",
  "instructorEmail": "alex@zen.example",
  "instructorPhoneNumber": "+33100000000",
  "subscriptionDate": "2026-11-02"
}
```

Responses add `id`, `status` and `createdAt`.

**Errors:** `ACTIVITY_NOT_FOUND` (404), `CLUB_NOT_FOUND` (404), `CATEGORY_NOT_FOUND` (404), `ACTIVITY_ALREADY_EXISTS` (409), `CLUB_CLOSED` (409), `PARTNER_NOT_ACTIVE` (409), `FORBIDDEN_OPERATION` (403), `INVALID_PAYLOAD` (400).

Existing endpoints affected: `/api/activities`, `/internal/activities/{id}` (payload gains `clubId`, `categoryId`, `instructor*`, `status`, `subscriptionDate`; legacy `partnerId`, `category`, `durationMinutes`, `partnerPayout`, `defaultCredits`, `active` are dropped).

## 8. User stories and acceptance criteria

> The activity APIs (`POST`, `GET`, `PUT`, `PATCH` on `/api/activities/**`) are exclusively accessed by operators with admin roles. Other services use `/internal/activities/{id}`.

### US-01 Create an activity (Admin)

As an admin, I want to register an activity so that it is offered on the platform.

- **Given** I am an admin, **when** I create an activity with a valid club and category, **then** the activity is created with status `ACTIVE`.
- **Given** the club is closed or its partner is not `ACTIVE`, **when** I create an activity, **then** I get a 409.
- **Given** the category id is unknown or its category is inactive, **when** I create an activity, **then** I get a 404.
- **Given** the activity name is already used by the same club, **when** I create it, **then** I get a 409.
- **Given** a mandatory field (`clubId`, `name` or `categoryId`) is missing, **when** I create an activity, **then** I get a 400 validation error.
- **Given** I am not an admin, **when** I create an activity, **then** I get a 403.

### US-02 Update an activity profile (Admin)

As an admin, I want to edit an activity so that its information stays current.

- **Given** I am an admin, **when** I update an activity, **then** the changes are saved and status and subscription date are unchanged.
- **Given** I send `status` or `subscriptionDate` in the update, **then** they are ignored.
- **Given** I am not an admin, **when** I update an activity, **then** I get a 403.

### US-03 Change an activity status (Admin)

As an admin, I want to suspend or reject an activity so that it stops being offered.

- **Given** an `ACTIVE` activity, **when** I suspend it, **then** it is `SUSPENDED`.
- **Given** a `SUSPENDED` activity, **when** I reactivate it, **then** it is `ACTIVE` again with its data intact.
- **Given** an activity, **when** I set it to `REJECTED`, **then** it is `REJECTED`.
- **Given** a `REJECTED` activity, **when** I change its status to `ACTIVE` or `SUSPENDED`, **then** it moves to that status.
- **Given** I am not an admin, **when** I change a status, **then** I get a 403.

### US-04 Browse and filter activities (Admin)

As an admin, I want to browse and filter activities so that I can review and manage them.

- **Given** activities in several statuses, **when** I list activities with a `status` filter, **then** only activities with that status are returned.
- **Given** activities of several clubs, **when** I filter by `clubId`, **then** only activities of that club are returned.
- **Given** activities in several categories, **when** I filter by `categoryId`, **then** only activities of that category are returned.
- **Given** I am not an admin, **when** I list activities, **then** I get a 403.

### US-05 View an activity detail (Admin)

As an admin, I want to see an activity's details, instructor and club.

- **Given** I am an admin and an activity exists, **when** I request it by id, **then** I get its details, instructor, club and status.
- **Given** I am an admin and the id is unknown, **when** I request it, **then** I get a 404.
- **Given** I am not an admin, **when** I request an activity by id, **then** I get a 403.

### US-06 Resolve an activity from another service

As the planning or booking service, I want to resolve an activity so that I can show and schedule it.

- **Given** an activity id, **when** another service calls `/internal/activities/{id}`, **then** it receives `ActivityInfo` (id, clubId, partnerId, name, categoryId, status).
- **Given** an unknown id, **when** another service calls `/internal/activities/{id}`, **then** it receives a 404.

## 9. Data migration

1. Recreate the `activities` table (Liquibase changeset) with `id`, `club_id` (FK → `clubs`), `category_id` (FK → `categories`), `name`, `description`, embedded instructor columns (`instructor_first_name`, `instructor_last_name`, `instructor_email`, `instructor_phone_number`), `status` (default `ACTIVE`), `subscription_date` and auditing columns.
2. Add a unique constraint `(club_id, name)` and indexes on `club_id`, `category_id` and `status`.
3. Drop the legacy columns (`partner_id`, `category`, `duration_minutes`, `partner_payout`, `default_credits`, `active`); `category_id` is backfilled from the EP001 catalog and `club_id` from the partner's primary club. Migration of the legacy pricing/credit values is reviewed separately.

## 10. Risks and open questions

| #    | Item                                                                                                | Mitigation / owner                 |
| ---- | --------------------------------------------------------------------------------------------------- | ---------------------------------- |
| R-01 | Dropping `durationMinutes`, `partnerPayout` and `defaultCredits` impacts planning, booking and credit services | Coordinate migration with EP001 |
| R-02 | No `PENDING` status: the initial status and the path into `REJECTED` must be confirmed              | Product owner                      |
| Q-01 | What is the default status on creation (`ACTIVE`?), and is there an approval flow?                  | Product owner                      |
| Q-02 | What exactly does `subscriptionDate` represent, and is it system-set or user-provided?              | Product owner                      |
| Q-03 | Can an activity be moved to another club, and is it limited to the same partner?                    | Product owner                      |
| Q-04 | Should the instructor be mandatory or optional?                                                     | Product owner                      |
| Q-05 | Is the activity name unique per club, per partner, or globally?                                     | Product owner                      |
| Q-06 | Where do `durationMinutes`, `partnerPayout` and `defaultCredits` live after this redesign?          | Product owner / tech lead          |

## 11. Acceptance and sign-off

- All **Must** requirements implemented and tested.
- Product owner validates the user stories above.

| Role          | Name | Date | Signature |
| ------------- | ---- | ---- | --------- |
| Product owner |      |      |           |
| Tech lead     |      |      |           |

## 12. Revision history

| Version | Date       | Author | Changes        |
| ------- | ---------- | ------ | -------------- |
| 0.1     | 2026-11-02 |        | Initial draft  |
