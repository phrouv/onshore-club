# User Story US004 - Change an Activity Status

## 1. Description

Admin changes an activity's status directly from the list on `/admin/activities` using a color-coded `Badge` and contextual action buttons (`Suspendre`, `Réactiver`, `Rejeter`). The action calls the status endpoint and reports the outcome with a toast.

## 2. User Story Statement

As an admin, I want to suspend, reactivate or reject an activity from the list so that I control whether it is offered on the platform.

## 3. User Flow Diagram

```mermaid
graph TD
    A[Admin sees an activity row] --> B[Click a status action]
    B --> C[PATCH /api/activities/{id}/status]
    C -->|200| D[List reflects the new status + confirmation toast]
    C -->|error| E[Error toast, status unchanged]
```

## 4. UX / UI References

- Design system: Onshore design system (status control pending).
- Components: `Badge` (color-coded: `ACTIVE` green, `SUSPENDED` amber, `REJECTED` red) + contextual action buttons, `Toaster`.
- TODO: Figma link for the status control once available.

## 5. Dependencies

- Blocking: EP005 Activities backend — `PATCH /api/activities/{id}/status`.
- Blocking: US001 - Browse and Filter Activities (the list hosting the actions).
- Blocking: `src/services/activityService.js` `setActivityStatus` + `adminSlice.setActivityStatus`.

## 6. Personas & Primary Users

- Admin — moderates the activity lifecycle.

## 7. Acceptance Criteria

| ID  | Given                           | When                               | Then                                                                                |
| --- | ------------------------------- | ---------------------------------- | ----------------------------------------------------------------------------------- |
| AC1 | An activity is `ACTIVE`         | I click "Suspendre"                | The list shows the activity as `SUSPENDED` and a toast "Activité suspendue" appears |
| AC2 | An activity is `SUSPENDED`      | I click "Réactiver"                | The list shows the activity as `ACTIVE` and a toast "Activité réactivée" appears    |
| AC3 | An activity has any status      | I click "Rejeter"                  | The list shows the activity as `REJECTED` and a toast "Activité rejetée" appears    |
| AC4 | An activity is `REJECTED`       | I click "Réactiver" or "Suspendre" | The list shows the activity with the selected status                                |
| AC5 | The status change request fails | I click a status action            | An error toast is shown and the activity's status is unchanged                      |

## 8. Notes / Additional Information

- The available actions depend on the current status: `ACTIVE` → `Suspendre`/`Rejeter`; `SUSPENDED` → `Réactiver`/`Rejeter`; `REJECTED` → `Réactiver`/`Suspendre` (EP005 FR-11).
- Suspending or rejecting keeps the activity's data and history; it never deletes the activity (EP005 BR-06).
- API errors surface through `ApiError` (RFC 9457) and the global toast pattern.
