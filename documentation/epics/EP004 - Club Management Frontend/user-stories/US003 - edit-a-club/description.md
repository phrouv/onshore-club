# User Story US003 - Edit a Club

## 1. Description

Admin edits a club from a prefilled modal on `/admin/clubs`. Editable fields are `name`, `description`, address, geolocation, contact and the partner the club belongs to. The admin can move the club to another partner during the update.

## 2. User Story Statement

As an admin, I want to edit a club's details and reassign it to another partner so that the site information stays accurate.

## 3. User Flow Diagram

```mermaid
graph TD
    A[Admin clicks Modifier on a club] --> B[Open ClubModal prefilled]
    B --> C[Change fields and/or partner]
    C --> D{Fields valid?}
    D -->|No| E[Show field errors]
    D -->|Yes| F[PUT /api/clubs/{id}]
    F -->|200| G[Close modal, list updated]
    F -->|409| H[Show duplicate-name error in the form]
```

## 4. UX / UI References

- Design system: Onshore design system (club edit form pending).
- Components: `Modal`, `Field`, `Input`, `Textarea`, `Select`, `Button`, `Toaster`.
- TODO: Figma link for the "Edit club" form once available.

## 5. Dependencies

- Blocking: EP003 Clubs backend — `PUT /api/clubs/{clubId}`.
- Blocking: `adminSlice.partners` (partner list) for the partner selector.
- Blocking: US001 - Browse and Filter Clubs (the page hosting the modal).
- Blocking: `src/services/clubService.js` `updateClub` + `adminSlice.saveClub`.

## 6. Personas & Primary Users

- Admin — keeps club details current.

## 7. Acceptance Criteria

| ID  | Given                                         | When                         | Then                                               |
| --- | --------------------------------------------- | ---------------------------- | -------------------------------------------------- |
| AC1 | I am an admin                                 | I click "Modifier" on a club | The modal opens prefilled with the club's data     |
| AC2 | I change fields and save                      | I submit the form            | The updated values are reflected in the list       |
| AC3 | I choose a different partner and save         | I submit the form            | The club appears under the new partner             |
| AC4 | I clear a mandatory field                     | I submit the form            | Field errors are shown and no request is sent      |
| AC5 | I enter latitude/longitude out of range       | I submit the form            | A validation error is shown and no request is sent |
| AC6 | I enter a malformed contact email             | I submit the form            | A validation error is shown and no request is sent |
| AC7 | The backend returns 409 `CLUB_ALREADY_EXISTS` | I save with a duplicate name | The error is displayed in the form                 |

## 8. Notes / Additional Information

- `partnerId` is editable on update (an admin can move the club to another partner).
- Omitting geolocation on edit keeps the existing values (per EP003).
- API errors surface through `ApiError` (RFC 9457) and the global toast pattern.
