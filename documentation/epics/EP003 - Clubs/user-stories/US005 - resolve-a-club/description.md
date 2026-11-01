# User Story US005 - Resolve a Club from Another Service

## 1. Description

Exposes an internal service-to-service endpoint `GET /internal/clubs/{id}` returning a minimal `ClubInfo` (id, partnerId, name, city, coordinates, open). Used by booking and planning to display the site of a class and to validate check-in.

## 2. User Story Statement

As the booking service, I want to resolve the site of a class by club id so that I can show it and validate check-in.

## 3. User Flow Diagram

```mermaid
graph TD
    A[Booking service calls /internal/clubs/{id}] --> B{Club exists?}
    B -->|No| C[404 CLUB_NOT_FOUND]
    B -->|Yes| D[200 ClubInfo]
```

## 4. UX / UI References

- None — internal API, no user interface.

## 5. Dependencies

- Blocking: US001 - Create a Club (clubs must exist).
- Blocking: `common-lib` `ClubInfo` record.

## 6. Personas & Primary Users

- Booking service — shows the site of a class and validates check-in.
- Planning service — displays the location of a class.

## 7. Acceptance Criteria

| ID  | Given                  | When                                             | Then                                                                          |
| --- | ---------------------- | ------------------------------------------------ | ----------------------------------------------------------------------------- |
| AC1 | A club id exists       | The booking service calls `/internal/clubs/{id}` | It receives a `ClubInfo` with id, partnerId, name, city, coordinates and open |
| AC2 | The club id is unknown | The booking service calls `/internal/clubs/{id}` | It receives 404 `CLUB_NOT_FOUND`                                              |

## 8. Notes / Additional Information

- `/internal/**` endpoints are never routed by the api-gateway; they are reachable service-to-service only.
- Error responses follow the RFC 9457 envelope shared by all services.
