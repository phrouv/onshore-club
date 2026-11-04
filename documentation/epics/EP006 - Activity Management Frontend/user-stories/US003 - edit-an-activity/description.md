# User Story US003 - Edit an Activity

## 1. Description

Admin edits an activity from a prefilled modal on `/admin/activities`. Editable fields are the club (within the same partner), the category, the name, the description and the instructor contact. The `status` and `subscriptionDate` are read-only: they are not part of the edit form.

## 2. User Story Statement

As an admin, I want to edit an activity's details and reassign it to another club of the same partner so that the activity information stays accurate.

## 3. User Flow Diagram

```mermaid
graph TD
    A[Admin clicks Modifier on an activity] --> B[Open ActivityModal prefilled]
    B --> C[Change club/category/name/description/instructor]
    C --> D{Fields valid?}
    D -->|No| E[Show field errors]
    D -->|Yes| F[PUT /api/activities/{id}]
    F -->|200| G[Close modal, list updated]
    F -->|409 / 403| H[Show API error in the form]
```

## 4. UX / UI References

- Design system: Onshore design system (activity edit form pending).
- Components: `Modal`, `Field`, `Input`, `Select`, `Textarea`, `Button`, `Toaster`.
- TODO: Figma link for the "Edit activity" form once available.

## 5. Dependencies

- Blocking: EP005 Activities backend — `PUT /api/activities/{id}`.
- Blocking: `adminSlice.clubs` (open clubs) and `adminSlice.categories` (active categories) for the selectors.
- Blocking: US001 - Browse and Filter Activities (the page hosting the modal).
- Blocking: `src/services/activityService.js` `updateActivity` + `adminSlice.saveActivity`.

## 6. Personas & Primary Users

- Admin — keeps activity details current.

## 7. Acceptance Criteria

| ID  | Given                                                        | When                                                 | Then                                                                             |
| --- | ------------------------------------------------------------ | ---------------------------------------------------- | -------------------------------------------------------------------------------- |
| AC1 | I am an admin                                                | I click "Modifier" on an activity                    | The modal opens prefilled with the activity's data                               |
| AC2 | I change fields (category, name, description, instructor)    | I submit the form                                    | The updated values are reflected in the list                                     |
| AC3 | I choose a different club of the same partner                | I submit the form                                    | The activity appears under the new club                                          |
| AC4 | I clear a mandatory field (`clubId`, `categoryId` or `name`) | I submit the form                                    | Field errors are shown and no request is sent                                    |
| AC5 | I enter an invalid `instructorEmail`                         | I submit the form                                    | A validation error is shown and no request is sent                               |
| AC6 | The backend returns 409 `ACTIVITY_ALREADY_EXISTS`            | I save with a duplicate name in the same club        | The error "Une activité porte déjà ce nom dans ce club" is displayed in the form |
| AC7 | The backend returns 403 `FORBIDDEN_OPERATION`                | I move the activity to a club of a different partner | An error indicating the club does not belong to the same partner is shown        |

## 8. Notes / Additional Information

- `status` and `subscriptionDate` are read-only in the edit form; the backend ignores them on `PUT` (EP005 BR-05).
- An activity can be moved to another club only within the same partner (EP005 FR-13).
- API errors surface through `ApiError` (RFC 9457) and the global toast pattern.
