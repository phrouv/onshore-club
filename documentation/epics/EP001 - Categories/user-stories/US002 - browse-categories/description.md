# User Story US002 - Browse Categories

## 1. Description

Provides operators with admin roles a catalog of category records and category details for catalog maintenance, including active and inactive categories. Category creation and status changes are covered separately.

## 2. User Story Statement

As an admin, I want to browse category records so that I can maintain the shared category catalog.

## 3. User Flow Diagram

```mermaid
graph TD
    A[Admin opens the categories list] --> B[Request categories]
    B --> C{Include inactive categories?}
    C -->|No| D[Show active categories]
    C -->|Yes| E[Show active and inactive categories]
    D --> F[Select a category]
    E --> F
    F --> G[Show category details]
```

## 4. UX / UI References

- No Figma or wireframe reference is included in EP001.
- Proposed read endpoints are documented in [specification-categories.md](../../specifications/specification-categories.md), section 7.
- The category list and detail presentation are design assumptions pending product design.

## 5. Dependencies

- Blocking: Admin role supplied through the API gateway.
- Non-blocking: US001 - Manage Category Records, for catalog data.
- No other epic or shared API specification is present in the workspace.

## 6. Personas & Primary Users

- Admin - reviews active and inactive catalog entries.

## 7. Acceptance Criteria

| ID  | Given                                                  | When                                                                   | Then                                                                 |
| --- | ------------------------------------------------------ | ---------------------------------------------------------------------- | -------------------------------------------------------------------- |
| AC1 | I am an admin and active categories exist              | I request the category list without asking to include inactive entries | The response contains active categories only                         |
| AC2 | I am an admin and active and inactive categories exist | I request the category list including inactive entries                 | The response includes both active and inactive categories            |
| AC3 | I am not an admin and categories exist                 | I request the category list or a category by code                      | The request is rejected with a 403 response                          |
| AC4 | I am an admin and a category exists                    | I request that category by code                                        | The response contains its code, name, description, and active status |

## 8. Notes / Additional Information

- The categories APIs are exclusively accessed by operators with admin roles. EP001 proposes `GET /api/categories` and `GET /api/categories/{code}`, both admin-only.
- The exact query parameter for an admin's inactive-category view is not settled by the proposed interface; the behavior, rather than a parameter name, is specified here.
