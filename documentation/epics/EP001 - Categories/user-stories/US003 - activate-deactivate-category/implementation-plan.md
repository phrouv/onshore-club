# Implementation Plan Template - US003 Activate or Deactivate a Category

## Overview

Implement the activation and deactivation of categories by admins from the categories list. Deactivation removes a category from the active catalog without rewriting existing activity references.

- Story: US003 - Activate or Deactivate a Category
- Epic: EP001 - Categories
- Domain: Shared category catalog
- Primary Actor / Persona: Admin
- Business Value: Admins control which classifications can be newly selected.
- Notes: The category catalog is owned by `admin-service`. Requires the authenticated admin role supplied through the API gateway. Rejecting inactive categories on new activity assignments is covered by US004.

## Steps

### 1. Setup & Configuration

- ✅ Identify impacted modules: `admin-service` category catalog; `onshore-frontend` admin categories list. The gateway route `/api/categories/**` created for US001 already covers the status endpoint.
- ✅ Confirm client-side vs server-side filtering/pagination strategy (if relevant) — not applicable.
- ✅ Verify naming conventions: follows the partner status endpoint `PATCH /{id}/status`, here `PATCH /api/categories/{code}/status?active=true|false`.
- ✅ Check whether a Liquibase changelog is needed: no, the `active` column was added in US001.
- ✅ Additional libraries required? No.

### 2. Backend Implementation (Per Impacted Microservice)

`admin-service` (category catalog owner):

- ✅ `PATCH /api/categories/{code}/status` sets the `active` flag; admin-only, non-admin requests are rejected with 403.
- ✅ Return 404 for an unknown category code.
- ✅ Leave existing activity references untouched; only the status changes.
- ✅ The categories list is admin-only; non-admin requests are rejected with 403.
- ✅ Swagger annotations: none added, following the existing convention.
- ✅ Integration tests for the story's acceptance criteria:
  - ✅ Admin deactivates a category; it is excluded from the active category list.
  - ✅ Admin activates an inactive category; it is included in the active category list.
  - ✅ Non-admin status change is rejected with 403 and the status is unchanged.
  - ✅ Status change on an unknown category is rejected with 404.

### 3. Frontend Implementation (`onshore-frontend`)

- ✅ Add `setCategoryStatus` to the API client in `src/services/adminService.js`.
- ✅ Add the `setCategoryStatus` thunk and its reducer in `src/Redux/admin/adminSlice.js`; the returned category replaces the row by code.
- ✅ In the categories list, show an "Activer" button for inactive categories and a "Désactiver" power icon button for active ones, as in the partners list.
- ✅ Confirm with the toasts "Catégorie activée" and "Catégorie désactivée"; disable the buttons while a request is running.
- ✅ Handle 403 and 404 responses using the existing global error pattern.
- ✅ Follow the existing design system and style patterns.

### 4. Modified / New Files

```text
/
├── microservices
│   └── services
│       └── admin-service
│           └── src
│               ├── main/java/com/onshore/services
│               │   ├── controller/CategoryController.java
│               │   └── service/{CategoryService,impl/CategoryServiceImpl}.java
│               └── test/java/com/onshore/services/CategoryIntegrationTest.java
├── onshore-frontend
│   └── src
│       ├── pages/admin/AdminCategories.jsx
│       ├── Redux/admin/adminSlice.js
│       └── services/adminService.js
└── documentation
    └── epics
        └── EP001 - Categories
            └── user-stories
                └── US003 - activate-deactivate-category
                    └── implementation-plan.md # 🟩 NEW
```

### Notes

- Always follow existing code style and structure.
- Rejecting inactive categories on new activity assignments (FR-05) is not part of this story; see US004.

## Status

- Status
  - ✅ DONE
