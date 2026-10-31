# Implementation Plan Template - US001 Manage Category Records

## Overview

Implement category creation and editing for admins so category records have a unique, immutable code, editable name and optional description, and an `image` value containing the URL of the category image, uploaded as a JPEG or PNG and stored in a MinIO bucket. New categories are active and available in the catalog.

- Story: US001 - Manage Category Records
- Epic: EP001 - Categories
- Domain: Shared category catalog
- Primary Actor / Persona: Admin
- Business Value: Partners classify activities consistently using shared category definitions.
- Notes: The category catalog is owned by `admin-service`. Requires the authenticated admin role supplied through the API gateway. US002 - Browse Categories is a non-blocking dependency for viewing the resulting catalog entry. Exact endpoint paths are not specified in the story.

## Steps

### 1. Setup & Configuration

- ✅ Identify impacted modules: `admin-service` category catalog; `onshore-frontend` admin category management; API gateway supplies the admin role. Confirm whether any gateway route change is needed after endpoint paths are established.
- ✅ Confirm client-side vs server-side filtering/pagination strategy (if relevant) — current story: not applicable; filtering and pagination are not in scope.
- ✅ Verify naming conventions (API & DB specs); the story does not specify endpoint paths, HTTP methods, or database naming.
- ✅ Check whether the owning service needs a Liquibase changelog and master changelog inclusion for the category fields: code, name, optional description, active status, and `image`.
- ✅ Additional libraries required? Not specified by the story.

### 2. Backend Implementation (Per Impacted Microservice)

`admin-service` (category catalog owner):

- ✅ Add a category database column named `image`; persist the MinIO image URL in it with category code, name, optional description, and active status. New categories are active.
- ✅ Include `image` in category create and update data; its value is the URL of the uploaded image (JPEG or PNG, 2 MB maximum) stored in a MinIO bucket.
- ✅ `PUT /api/categories/{code}` accepts `image` in the update DTO and persists the new image's URL (replace only, clearing is not allowed; the old file is deleted). It is admin-only and does not change the code.
- ✅ Enforce code uniqueness after trimming and case-insensitive comparison.
- ✅ Keep the category code immutable when updating name or description; reject attempts to change it.
- ✅ Allow admins to create and update category records; reject non-admin create and update requests with 403.
- ✅ Return a 409 conflict when a create request uses an existing code after trimming and case-insensitive comparison.
- ✅ Define the create/update endpoint paths and HTTP methods using the owning service's conventions; these are not specified in the story.
- ✅ Gateway: confirm the API gateway route for the selected endpoints and preserve the admin role requirement.
- ✅ Swagger annotations on DTOs and controller methods, following the owning service's existing convention. No annotations added: no controller or DTO in the codebase uses them; springdoc generates the docs.
- ✅ Integration tests for the story's acceptance criteria:
  - ✅ Admin creates a category with an unused code; it is created and appears in the active category list.
  - ✅ Creating a category with an image uploads it to MinIO and retains its URL on the category.
  - ✅ Duplicate code after trimming and case-insensitive comparison is rejected with 409.
  - ✅ Admin updates name or description while the code remains unchanged.
  - ✅ Admin updates the `image`; the new URL is retained and the category code remains unchanged.
  - ✅ PUT with a new image replaces the stored one and deletes the old file; the current URL keeps it.
  - ✅ Non-admin PUT with `image` is rejected with 403 and the stored `image` is unchanged.
  - ✅ Attempted code change is rejected and the established code remains unchanged.
  - ✅ Non-admin create or update request is rejected with 403.

### 3. Frontend Implementation (`onshore-frontend`)

- ✅ Add the admin category management view under `src/pages`; the specific area and component filename are not specified in the story.
- ✅ Add an API client under `src/services` for category creation and update; use the endpoint paths and methods established for the owning service.
- ✅ Add Redux slice/state under `src/Redux` only if shared state is needed.
- ✅ Register the category management route using the existing React Router conventions.
- ✅ Provide create and edit fields for code, name, optional description, and `image`. Keep the code immutable during editing. The edit form allows replacing `image` with a JPEG or PNG file (2 MB maximum), sent as a data URL in the `PUT` call, with a preview and a thumbnail in the list.
- ✅ Show the created category in the active category list and show updated name or description after editing.
- ✅ Handle 409 duplicate-code and 403 authorization responses using the existing global error pattern; exact error text is not specified in the story.
- ✅ Follow the existing design system and style patterns.

### 4. Modified / New Files

```text
/
├── microservices
│   └── services
│       └── admin-service
│           └── src/main/java/com/onshore/services
│               └── [category persistence, service, DTO, mapper, and controller files TBD]
│           └── src/main/resources/db/changelog
│               └── [category changelog and master inclusion TBD after schema review]
├── onshore-frontend
│   └── src
│       ├── pages
│       │   └── [admin category management area and component TBD]
│       └── services
│           └── [category API client TBD]
└── documentation
    └── epics
        └── EP001 - Categories
            └── user-stories
                └── US001 - manage-category-records
                    └── implementation-plan.md # 🟩 NEW
```

### Notes

- Always follow existing code style and structure.
- Follow the backend/frontend instruction rule files in `ai/rules/` and the API/database specifications in `specifications/` when implementation begins.
- Defer category activation, pricing, and activity assignment to their separate stories.
- Do not invent exact validation error text; the story does not specify it.
- Confirm endpoint paths before implementation; they are not specified in this story.
- Category images are served from a public-read MinIO bucket; confirm this versus signed URLs. `PUT` can only replace `image`, not clear it.

## Status

- Status
  - ✅ DONE
