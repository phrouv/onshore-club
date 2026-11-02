# User Story US006 - Resolve an Activity from Another Service

## 1. Description

Allows another service (planning or booking) to resolve an activity by id through the internal endpoint `/internal/activities/{id}`. It returns `ActivityInfo` (id, clubId, partnerId, name, categoryId, status). This endpoint is internal only and is never routed by the api-gateway.

## 2. User Story Statement

As the planning or booking service, I want to resolve an activity so that I can show and schedule it.

## 3. User Flow Diagram

```mermaid
graph TD
    A[Other service calls /internal/activities/{id}] --> B{Activity exists?}
    B -->|No| C[404 ACTIVITY_NOT_FOUND]
    B -->|Yes| D[Return ActivityInfo: id, clubId, partnerId, name, categoryId, status]
```

## 4. UX / UI References

- Service-to-service only; no UI.
- Design system: N/A.

## 5. Dependencies

- Blocking: EP003 Clubs — `clubId` in `ActivityInfo`.
- Blocking: EP001 Categories — `categoryId` in `ActivityInfo`.
- Non-blocking: EP002 Partners — `partnerId` is derived from the club.

## 6. Personas & Primary Users

- Planning service — schedules classes for an activity.
- Booking service — shows and validates the activity of a booking.

## 7. Acceptance Criteria

| ID  | Given              | When                                              | Then                                                                                     |
| --- | ------------------ | ------------------------------------------------- | ---------------------------------------------------------------------------------------- |
| AC1 | An activity exists | Another service calls `/internal/activities/{id}` | It receives `ActivityInfo` containing id, clubId, partnerId, name, categoryId and status |
| AC2 | The id is unknown  | Another service calls `/internal/activities/{id}` | It receives a 404 `ACTIVITY_NOT_FOUND`                                                   |

## 8. Notes / Additional Information

- `/internal` endpoints are never routed by the api-gateway (assumption/constraint).
- `ActivityInfo` is a minimal view; it does not include instructor contact or full profile fields.
