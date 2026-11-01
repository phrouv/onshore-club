# Requirement Specification — EP003 Clubs

| Field           | Value                         |
| --------------- | ----------------------------- |
| Epic            | EP003 – Clubs                 |
| Document status | Draft                         |
| Type            | Specification of a new entity |
| Owner           | _TBD_                         |
| Last updated    | 2026-11-01                    |

## 1. Introduction

### 1.1 Purpose

Define the requirements for the **club** entity: the physical site or location, owned by a partner, where activities (classes) take place and where members check in.

### 1.2 Background / current situation

- `partner-service` owns the `Partner` entity (table `partners`) and the `Activity` entity (table `activities`).
- Today an `Activity` stores no location: it references only `partnerId`, `name`, `category`, `description`, `durationMinutes`, `partnerPayout`, `defaultCredits` and `active`.
- The booking flow already refers to "the club" of a booking (e.g. QR check-in validates `WRONG_PARTNER_CODE` against the booked club), but no `Club` entity or table exists yet.
- A partner can operate several sites; each site has its own address, geographical coordinates and contact.
- Known gaps:
  - No physical location model: activities cannot tell members where they take place.
  - No per-site open/closed state.

### 1.3 Business goals

- Give every activity a concrete, well-described location (the club).
- Let a partner manage several sites with their own address, coordinates and contact.
- Allow a club to be opened or closed independently, so that only open sites host activities and check-ins.
- Provide other services (planning, booking) a minimal, reliable club view.

### 1.4 Scope

**In scope**

- `Club` entity owned by `partner-service`, with `name`, `description`, address, geographical location (`latitude`/`longitude`), contact (`ContactPerson`) and an `open` flag.
- One partner owning one or many clubs.
- Choosing the partner at creation and moving a club between partners.
- Club CRUD, open/close workflow, listing and internal lookup.

**Out of scope**

- Club deletion while activities still reference it (guarded, but deletion policy itself is separate).
- Assigning activities to clubs (a follow-up; this epic only introduces the club as the future site reference).
- Categories (EP001), partner status workflow (EP002), payouts and credit pricing.

### 1.5 Definitions

| Term        | Meaning                                                                 |
| ----------- | ----------------------------------------------------------------------- |
| Club        | A physical site or location owned by a partner where activities happen  |
| Partner     | The venue or studio that owns one or more clubs (`partnerId`)           |
| Open        | Boolean flag: whether the club currently hosts activities and check-ins |
| GeoLocation | Embedded value holding `latitude` and `longitude`                       |
| ClubInfo    | Minimal internal view of a club used by other services                  |

## 2. Stakeholders and users

| Role           | Interest                               | Permissions                                                                                       |
| -------------- | -------------------------------------- | ------------------------------------------------------------------------------------------------- |
| Admin          | Reviews and moderates clubs            | Full read and management of any club (create, read, update, open/close); club APIs are admin-only |
| Partner owner  | Gets its sites registered              | No direct club API access                                                                         |
| Member         | Finds where an activity takes place    | No direct club API access                                                                         |
| Other services | Resolve the site of a class or booking | Internal read access (`/internal/clubs/{id}`)                                                     |

## 3. Assumptions, dependencies, constraints

- Authentication and role headers (`X-User-Id`, `X-User-Roles`) are provided by the API gateway; role checks use `UserRole.isAdmin` / `UserRole.has` / `UserRole.requireAdmin`.
- `partner-service` owns club data (PostgreSQL); other services access it through the API or `/internal`, never through shared tables.
- `/internal` endpoints are never routed by the api-gateway.
- The `Address` and `ContactPerson` embeddables from `common-lib` are reused; a new `GeoLocation` embeddable is introduced.
- Activities will later reference a club (`clubId`); until then the club is a standalone entity.

## 4. Functional requirements

| ID    | Requirement                                                                                                                                                               | Priority |
| ----- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- |
| FR-01 | An operator with an admin role can create a club by first choosing the partner it belongs to (supplied via `partnerId`); it is created with `open = true`.                | Must     |
| FR-02 | A club cannot be created for a partner that is not `ACTIVE` (409 `PARTNER_NOT_ACTIVE`).                                                                                   | Must     |
| FR-03 | Only an operator with an admin role can create or update a club (403 `FORBIDDEN_OPERATION` otherwise).                                                                    | Must     |
| FR-04 | `name` and the address fields (`street`, `city`, `zipCode`) are mandatory; the geographical location (`latitude`, `longitude`), `description` and `contact` are optional. | Must     |
| FR-05 | `name` is unique within a partner (409 `CLUB_ALREADY_EXISTS`).                                                                                                            | Must     |
| FR-06 | When provided, `latitude` must be in `[-90, 90]` and `longitude` in `[-180, 180]` (400 `INVALID_PAYLOAD` otherwise).                                                      | Must     |
| FR-07 | An admin can change the `partnerId` of a club when updating it, moving the club to another partner.                                                                       | Must     |
| FR-08 | Only an admin can toggle a club `open`/closed without deleting it.                                                                                                        | Must     |
| FR-09 | Only admins can list clubs and filter them by `partnerId`; all clubs are returned, including closed ones.                                                                 | Must     |
| FR-10 | Only an admin can fetch a club by id (404 `CLUB_NOT_FOUND` if none).                                                                                                      | Must     |
| FR-11 | Other services can resolve a club by id through `/internal/clubs/{id}` (returns `ClubInfo`).                                                                              | Must     |
| FR-12 | A club that is not `open`, or whose partner is not `ACTIVE`, cannot host activities or check-ins (409 `CLUB_CLOSED`).                                                     | Should   |
| FR-13 | `contactEmail`, when provided, is well formed (400 `INVALID_PAYLOAD` otherwise).                                                                                          | Should   |
| FR-14 | Listing is filtered and paginated in the database instead of in memory.                                                                                                   | Could    |

## 5. Business rules

- BR-01: A club belongs to exactly one partner at a time; an admin can move it to another partner when updating it.
- BR-02: The club name is unique within its partner, but may be reused across different partners.
- BR-03: Club APIs are admin-only; there is no public club listing.
- BR-04: Closing a club keeps its data and history; existing activities, classes and bookings remain traceable to it.
- BR-05: A club cannot be deleted while activities reference it.

## 6. Non-functional requirements

| ID     | Category      | Requirement                                                                                   |
| ------ | ------------- | --------------------------------------------------------------------------------------------- |
| NFR-01 | Security      | Admin-only access is enforced server-side, not only in the UI.                                |
| NFR-02 | Performance   | Club list and detail respond in under 200 ms at p95.                                          |
| NFR-03 | Privacy       | Contact details are not written to logs.                                                      |
| NFR-04 | Auditability  | Creation, update and open/close changes record who and when.                                  |
| NFR-05 | Compatibility | Existing `/api/partners/**` and `/api/activities/**` endpoints stay backward compatible.      |
| NFR-06 | Quality       | Unit tests for admin access, uniqueness and open/closed rules; integration tests for the API. |

## 7. Interfaces

| Method | Endpoint                   | Access                | Description                     |
| ------ | -------------------------- | --------------------- | ------------------------------- |
| POST   | `/api/clubs`               | Admin                 | Create a club (created `open`)  |
| GET    | `/api/clubs`               | Admin                 | List clubs (`partnerId` filter) |
| GET    | `/api/clubs/{clubId}`      | Admin                 | Club detail                     |
| PUT    | `/api/clubs/{clubId}`      | Admin                 | Update profile                  |
| PATCH  | `/api/clubs/{clubId}/open` | Admin                 | Open/close the club (`?open=`)  |
| GET    | `/internal/clubs/{id}`     | Internal (not routed) | `ClubInfo` for other services   |

**Club payload**

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

**Errors:** `CLUB_NOT_FOUND` (404), `CLUB_ALREADY_EXISTS` (409), `CLUB_CLOSED` (409), `PARTNER_NOT_FOUND` (404), `PARTNER_NOT_ACTIVE` (409), `FORBIDDEN_OPERATION` (403), `INVALID_PAYLOAD` (400).

New endpoints require an api-gateway route for `/api/clubs/**`.

## 8. User stories and acceptance criteria

### US-01 Create a club (Admin)

As an admin, I want to register club records so that the partners' sites are registered on the platform.

- **Given** I am an admin, **when** I create a club, **then** I first choose the partner the club belongs to.
- **Given** I am an admin and the chosen partner is `ACTIVE`, **when** I submit valid mandatory fields, **then** a club is created with `open = true`.
- **Given** I am not an admin, **when** I create a club, **then** I get a 403.
- **Given** the partner is not `ACTIVE`, **when** I create a club, **then** I get a 409.
- **Given** the club name is already used by the same partner, **when** I create it, **then** I get a 409.
- **Given** a mandatory field is missing, **when** I create a club, **then** I get a 400 validation error.
- **Given** `latitude` or `longitude` is out of range, **when** I create a club, **then** I get a 400 validation error.

### US-02 Update a club profile (Admin)

As an admin, I want to edit club records so that the sites' information stays current.

- **Given** I am an admin, **when** I update a club, **then** the changes are saved, including the partner the club belongs to.
- **Given** I am an admin, **when** I choose a different partner during an update, **then** the club is moved to that partner.
- **Given** I am not an admin, **when** I update a club, **then** I get a 403.

### US-03 Open and close a club (Admin)

As an admin, I want to close a site temporarily so that it stops hosting activities and check-ins.

- **Given** an `open` club, **when** I close it, **then** it no longer hosts activities or check-ins.
- **Given** a closed club, **when** I reopen it, **then** it is `open` again with its data intact.
- **Given** I am not an admin, **when** I toggle a club, **then** I get a 403.

### US-04 Browse and view clubs (Admin)

As an admin, I want to browse and filter clubs so that I can review and manage the sites.

- **Given** clubs exist, **when** I list them, **then** all clubs are returned, including closed ones.
- **Given** clubs of several partners, **when** I filter by `partnerId`, **then** only matching clubs are returned.
- **Given** a club exists, **when** I request it by id, **then** I get its details, address and coordinates.
- **Given** an unknown id, **when** I request it, **then** I get a 404.
- **Given** I am not an admin, **when** I list or request clubs, **then** I get a 403.

### US-05 Resolve a club from another service

As the booking service, I want to resolve the site of a class so that I can show it and validate check-in.

- **Given** a club id, **when** the booking service calls `/internal/clubs/{id}`, **then** it receives `ClubInfo` (id, partnerId, name, city, coordinates, open).
- **Given** an unknown id, **when** the booking service calls `/internal/clubs/{id}`, **then** it receives a 404.

## 9. Data migration

1. Create the `clubs` table (Liquibase changeset) with `id`, `partner_id` (FK → `partners`), `name`, `description`, embedded address columns (`address_street`, `address_city`, `address_zip_code`), embedded geo columns (`geo_latitude`, `geo_longitude`), embedded contact columns (`contact_first_name`, `contact_last_name`, `contact_email`, `contact_phone_number`), `open` and auditing columns.
2. Add a unique constraint `(partner_id, name)`.
3. (Follow-up, not in this epic) add `club_id` to `activities` and backfill it from the partner's primary club where a single site exists.

## 10. Risks and open questions

| #    | Item                                                                                            | Mitigation / owner        |
| ---- | ----------------------------------------------------------------------------------------------- | ------------------------- |
| R-01 | ~~Flat vs nested endpoint style~~ Decided: flat `/api/clubs` endpoints.                         | API guild                 |
| R-02 | ~~Forcing geolocation~~ Decided: geolocation (`latitude`/`longitude`) is optional at creation.  | Product owner             |
| Q-01 | Must a club always belong to an `ACTIVE` partner, or can it be drafted before partner approval? | Product owner             |
| Q-02 | Should club names be unique globally or only within a partner?                                  | Product owner             |
| Q-03 | Are coordinates used for maps, distance search, or both?                                        | Product owner / tech lead |
| Q-04 | When an activity is created without a club, which club is assumed (the partner's default)?      | Product owner             |

## 11. Acceptance and sign-off

- All **Must** requirements implemented and tested.
- Product owner validates the user stories above.

| Role          | Name | Date | Signature |
| ------------- | ---- | ---- | --------- |
| Product owner |      |      |           |
| Tech lead     |      |      |           |

## 12. Revision history

| Version | Date       | Author | Changes       |
| ------- | ---------- | ------ | ------------- |
| 0.1     | 2026-11-01 |        | Initial draft |
