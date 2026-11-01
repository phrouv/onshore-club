# Requirement Specification — EP001 Categories

| Field           | Value                                              |
| --------------- | -------------------------------------------------- |
| Epic            | EP001 – Categories                                 |
| Document status | Draft                                              |
| Type            | Enhancement (introduce a managed category catalog) |
| Owner           | _TBD_                                              |
| Last updated    | 2026-10-31                                         |

## 1. Introduction

### 1.1 Purpose

Define the requirements for managing **categories** (e.g. yoga, climbing, spa) as a first-class, admin-managed concept shared by partners, activities, class planning and credit pricing.

### 1.2 Background / current situation

- `category` was a free-text string on `Partner` and `Activity` (partner-service), validated only with `@NotBlank`; the partner no longer has a category, only the activity does.
- Listing endpoints filter by category with a case-insensitive string match (`GET /api/activities`).
- credit-service stores the pricing grid keyed by that same string (`PUT /api/credit-settings/{category}`: `creditsLow`, `creditsNormal`, `creditsHigh`, `defaultPayout`), and planning-service resolves class prices through `CreditClient.price(category, demand)`.
- Consequences: typos and spelling variants create distinct categories, silently break price lookup, and there is no list of valid categories for the UI.

### 1.3 Business goals

- Consistent categorisation across the platform.
- Reliable credit pricing per category and demand level.
- A browsable, filterable catalog for club members.

### 1.4 Scope

**In scope**

- Category catalog (create, read, update, deactivate) managed by admins.
- Referencing categories from activities.
- Link between a category and its credit pricing.
- Category filtering in listings.

**Out of scope**

- Hierarchical categories (sub-categories) — candidate for a later epic.
- Per-partner custom pricing (already handled by the activity's own `defaultCredits`).
- Localisation of category labels.

### 1.5 Definitions

| Term           | Meaning                                                                                                                                      |
| -------------- | -------------------------------------------------------------------------------------------------------------------------------------------- |
| Category       | Classification of an activity (e.g. "Yoga")                                                                                                  |
| Demand level   | `LOW`, `NORMAL`, `HIGH`; selects the price in the credit grid                                                                                |
| Credit grid    | Admin-managed prices per category and demand level                                                                                           |
| Payout         | Amount paid to the partner per booking, by default per category                                                                              |
| Category image | Database/API field named `image` containing the URL of the category image (a JPEG or PNG uploaded by the admin and stored in a MinIO bucket) |

## 2. Stakeholders and users

| Role                              | Interest                      | Permissions                                                                              |
| --------------------------------- | ----------------------------- | ---------------------------------------------------------------------------------------- |
| Admin                             | Maintains catalog and pricing | Full read and CRUD on categories and credit grid (categories APIs admin-only)            |
| Partner                           | Classifies own activities     | No direct category API access; selects a category code when creating/updating activities |
| Member                            | Discovers activities          | No direct category API access; filters activity listings by category code                |
| Other services (planning, credit) | Resolve prices                | Internal (service-to-service) read access                                                |

## 3. Assumptions, dependencies, constraints

- Authentication and role headers (`X-User-Id`, `X-User-Roles`) are provided by the API gateway; admin checks use `Roles.requireAdmin`.
- Services own their data (PostgreSQL per service); `admin-service` owns the category catalog, which is exposed to other services through an API, not shared tables. `partner-service` continues to own partner and activity records, and `credit-service` retains pricing keyed by category code.
- Existing free-text values must be migrated without breaking existing activities, classes or prices.

## 4. Functional requirements

| ID    | Requirement                                                                                                                                                                                     | Priority |
| ----- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- |
| FR-01 | An admin can create a category with a unique code, a display name and an optional description.                                                                                                  | Must     |
| FR-02 | An admin can rename or edit a category without changing its code.                                                                                                                               | Must     |
| FR-03 | An admin can deactivate or reactivate a category from the categories list; inactive categories cannot be selected for new activities but remain on existing ones.                               | Must     |
| FR-04 | Only operators with admin roles can list categories, including inactive ones.                                                                                                                   | Must     |
| FR-05 | Creating or updating an activity requires an existing, active category code; unknown codes are rejected with a validation error.                                                                | Must     |
| FR-06 | Category code matching is case-insensitive and unique (no duplicates differing only by case or spaces).                                                                                         | Must     |
| FR-07 | `GET /api/activities` filters by category code.                                                                                                                                                 | Must     |
| FR-08 | Creating a category should offer creating its credit settings (`creditsLow`, `creditsNormal`, `creditsHigh`, `defaultPayout`).                                                                  | Should   |
| FR-09 | A category cannot be deleted while activities reference it; deactivation is used instead.                                                                                                       | Must     |
| FR-10 | Existing free-text categories are migrated to catalog entries, normalised by trimming and case folding.                                                                                         | Must     |
| FR-11 | The admin UI shows category usage counts (activities).                                                                                                                                          | Could    |
| FR-12 | An admin can upload a JPEG or PNG (2 MB maximum) when creating or updating a category; it is stored in a MinIO bucket and its URL is saved in `image`.                                          | Must     |
| FR-13 | `PUT /api/categories/{code}` accepts `name`, `description` and `image`; `image` is the current URL (unchanged) or a new upload that replaces it and deletes the old file; it cannot be cleared. | Must     |

## 5. Business rules

- BR-01: A category code is immutable once created.
- BR-02: Price resolution order is unchanged: the activity's own `defaultCredits` wins, otherwise the credit grid by category and demand level.
- BR-03: A category without credit settings cannot be used to price a class; the user gets an explicit error instead of a silent default.
- BR-04: Credit grid and payout values are visible to admins only; payouts are also visible to partners for their own activities.

## 6. Non-functional requirements

| ID     | Category       | Requirement                                                                             |
| ------ | -------------- | --------------------------------------------------------------------------------------- |
| NFR-01 | Security       | All category API operations are admin-only; enforced server-side, not only in the UI.   |
| NFR-02 | Performance    | Category list responds in under 200 ms at p95 and can be cached by consumers.           |
| NFR-03 | Data integrity | Migration is idempotent and reversible; no loss of existing activity/class data.        |
| NFR-04 | Compatibility  | Existing API clients sending a category string keep working during a transition period. |
| NFR-05 | Auditability   | Creation, update and deactivation record who and when.                                  |
| NFR-06 | Quality        | Unit tests for validation and migration; integration tests for the API.                 |

## 7. Interfaces (proposed)

| Method | Endpoint                        | Access | Description                                              |
| ------ | ------------------------------- | ------ | -------------------------------------------------------- |
| GET    | `/api/categories`               | Admin  | List categories (`?active=true`)                         |
| GET    | `/api/categories/{code}`        | Admin  | Category detail                                          |
| POST   | `/api/categories`               | Admin  | Create category                                          |
| PUT    | `/api/categories/{code}`        | Admin  | Update name, description and/or `image` (code immutable) |
| PATCH  | `/api/categories/{code}/status` | Admin  | Activate or deactivate (`?active=true\|false`)           |

**Category payload**

```json
{
  "code": "YOGA",
  "name": "Yoga",
  "description": "Yoga and stretching classes",
  "image": "<image-url, or Base64 / data URL of a new JPEG or PNG on create and update>",
  "active": true
}
```

Existing endpoints affected: `/api/activities`, `/api/credit-settings/{category}`.

## 8. User stories and acceptance criteria

> The categories APIs (`GET`, `POST`, `PUT`, `PATCH`) are exclusively accessed by operators with admin roles. Partner and member scenarios below operate through activity/listing endpoints, not the categories API.

### US-01 Manage categories (Admin)

As an admin, I want to create and edit categories so that partners classify activities consistently.

- **Given** I am an admin, **when** I create a category with a new code, **then** it appears in the active list.
- **Given** a category with the same code (any casing) exists, **when** I create it again, **then** I get a 409 conflict.
- **Given** I am not an admin, **when** I try to create a category, **then** I get a 403.
- **Given** I am an admin and provide a JPEG or PNG image, **when** I create a category, **then** the image is stored in MinIO and the category keeps its URL in `image`.
- **Given** I am an admin and a category exists, **when** I call `PUT /api/categories/{code}` with a new JPEG or PNG, **then** the category stores the new image's URL, the old file is deleted and its code is unchanged.
- **Given** I am an admin, **when** I send an image that is not a JPEG or PNG, or larger than 2 MB, **then** I get a 400.

### US-02 Deactivate a category (Admin)

As an admin, I want to deactivate a category so that it is no longer offered.

- **Given** an active category, **when** I deactivate it, **then** it disappears from the selectable list and existing activities keep it.
- **Given** a category is deactivated, **when** a partner creates an activity with it, **then** the request is rejected.
- **Given** I am an admin and a category is inactive, **when** I activate it from the categories list, **then** it returns to the active list.
- **Given** I am not an admin, **when** I change a category's status, **then** I get a 403.
- **Given** an unknown category code, **when** I change its status, **then** I get a 404.

### US-03 Pick a category (Partner)

As a partner, I want to choose a category from a list when creating an activity.

- **Given** I create an activity with an unknown category code, **then** I get a 400 validation error.

### US-04 Filter by category (Member)

As a member, I want to filter activities by category.

- **Given** activities in several categories, **when** I filter by `YOGA`, **then** only yoga activities are returned.

### US-05 Price a class (System)

As the platform, I want class prices resolved from the category grid.

- **Given** an activity with no `defaultCredits` and a category with settings, **when** a class is scheduled at `HIGH` demand, **then** the class costs `creditsHigh`.
- **Given** the category has no settings, **then** scheduling fails with an explicit error.

## 9. Data migration

1. Collect distinct `category` values from `Activity` (trimmed, case-folded).
2. Create a catalog entry per distinct value; flag near-duplicates for admin review.
3. Align credit settings keys with the new category codes.
4. Switch validation to the catalog; keep accepting legacy strings during the transition (NFR-04).

## 10. Risks and open questions

| #    | Item                                                                          | Mitigation / owner                     |
| ---- | ----------------------------------------------------------------------------- | -------------------------------------- |
| R-01 | Free-text duplicates ("yoga" vs "Yoga ") make migration ambiguous             | Admin review step before cut-over      |
| R-02 | Price lookup failures on unmapped categories                                  | BR-03 explicit error + migration check |
| Q-01 | Do categories need an icon, colour or ordering for the UI?                    | Product owner                          |
| Q-02 | Is a single category per activity enough, or multiple?                        | Product owner                          |
| Q-03 | Should category images be served from a public bucket or through signed URLs? | Product owner / tech lead              |

## 11. Acceptance and sign-off

- All **Must** requirements implemented and tested.
- Migration run on a copy of production data with no unmapped categories.
- Product owner validates the user stories above.

| Role          | Name | Date | Signature |
| ------------- | ---- | ---- | --------- |
| Product owner |      |      |           |
| Tech lead     |      |      |           |

## 12. Revision history

| 0.3 | 2026-10-31 | | Name category image field `image` |

| Version | Date       | Author | Changes                                                                       |
| ------- | ---------- | ------ | ----------------------------------------------------------------------------- |
| 0.1     | 2026-10-31 |        | Initial draft                                                                 |
| 0.2     | 2026-10-31 |        | Add category image object key                                                 |
| 0.4     | 2026-10-31 |        | PUT allows replacing the category image                                       |
| 0.5     | 2026-10-31 |        | Replace S3Proxy with MinIO as image object storage                            |
| 0.6     | 2026-10-31 |        | Category image is an uploaded JPEG/PNG stored in MinIO; `image` holds its URL |
| 0.7     | 2026-10-31 |        | Add activate/deactivate from the categories list                              |
| 0.8     | 2026-10-31 |        | Partners no longer have a category; only activities do                        |
