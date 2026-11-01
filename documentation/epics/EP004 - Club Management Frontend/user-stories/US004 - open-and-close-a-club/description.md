# User Story US004 - Open and Close a Club

## 1. Description

Admin toggles a club's `open` state directly from the list on `/admin/clubs` using a binary control (`SegmentedSwitch` or a badge + action button). The action calls the toggle endpoint and reports the outcome with a toast.

## 2. User Story Statement

As an admin, I want to open and close a club from the list so that I can control which sites host activities and check-ins.

## 3. User Flow Diagram

```mermaid
graph TD
    A[Admin sees a club row] --> B[Toggle open/closed]
    B --> C[PATCH /api/clubs/{id}/open]
    C -->|200| D[List reflects the new open value + confirmation toast]
    C -->|error| E[Error toast, state unchanged]
```

## 4. UX / UI References

- Design system: Onshore design system (open/close toggle pending).
- Components: `SegmentedSwitch` (binary control) or `Badge` + action button, `Toaster`.
- TODO: Figma link for the open/close control once available.

## 5. Dependencies

- Blocking: EP003 Clubs backend — `PATCH /api/clubs/{clubId}/open`.
- Blocking: US001 - Browse and Filter Clubs (the list hosting the toggle).
- Blocking: `src/services/clubService.js` `setClubOpen` + `adminSlice.setClubOpen`.

## 6. Personas & Primary Users

- Admin — manages the availability of any club.

## 7. Acceptance Criteria

| ID  | Given                    | When            | Then                                                               |
| --- | ------------------------ | --------------- | ------------------------------------------------------------------ |
| AC1 | A club is `open`         | I close it      | The list shows the club as closed and a confirmation toast appears |
| AC2 | A club is closed         | I reopen it     | The list shows the club as `open` and a confirmation toast appears |
| AC3 | The toggle request fails | I toggle a club | An error toast is shown and the club's state is unchanged          |

## 8. Notes / Additional Information

- Closing keeps the club's data and history; it never deletes the club.
- API errors surface through `ApiError` (RFC 9457) and the global toast pattern.
