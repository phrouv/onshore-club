# Traceability Matrix Template – {{EPIC_CODE}} {{EPIC_NAME}}

This template standardizes how to produce a bidirectional traceability matrix that maps Functional Requirements defined in the specification file (e.g. `{{SPEC_FILENAME}}`) to User Story Acceptance Criteria (US00x ACy). It highlights coverage, gaps, and conflicts, and provides actionable recommendations.

> Replace all double–brace placeholders (e.g. `{{EPIC_CODE}}`) and remove instructional blockquotes (`>`). Keep section order. Delete any optional sections not applicable (but prefer resolving gaps instead of deleting them).

---

## 1. Legend

- **Req ID**: Requirement identifier from specification (pattern: `FRE-{{DOMAIN}}-0x`).
- **Requirement Summary**: Concise (non-normative) phrasing distilled from the specification.
- **Covered By**: List of Acceptance Criteria referencing the behavior (`US00x ACy`).
- **Coverage Notes**: Clarifications, gaps, deltas vs specification wording, or extensions introduced only by ACs.

Optional (add if helpful):

- **Status Tags**: Covered | Partially Covered | Not Covered | Conflict

---

## 2. Summary Overview

High-level coverage snapshot for quick stakeholder scan. Each requirement line should have a status (“Covered”, “Partially Covered”, “Not Covered”, or “Conflict”).

| Requirement | Primary User Stories | Coverage Status |
|-------------|----------------------|-----------------|
| FRE-{{DOMAIN}}-01 Page layout & default states | US001 (list), US002 (entry point) | Partially Covered |
| FRE-{{DOMAIN}}-02 Search behavior (starts-with, deselect) | US001 | Partially Covered |
| FRE-{{DOMAIN}}-03 Create form (fields, confirmation, banners) | US002 | Partially Covered |
| FRE-{{DOMAIN}}-04 Edit form + delete workflow | US003, US004 | Partially Covered |

> Adjust example rows to the real requirement set. Add/remove rows as needed.

---

## 3. Detailed Requirement Sections

For each requirement create a subsection mirroring the specification structure: title, specification bullets, mapping table, and explicit gap list.

### FRE-{{DOMAIN}}-01 – Page layout & default display

Specification Points:

- Bullet each atomic behavior from `{{SPEC_FILENAME}}` (verbatim or slightly condensed)
- ...

Acceptance Criteria Mapping:

| Requirement Aspect | Acceptance Criteria | Notes |
|--------------------|---------------------|-------|
| Left list renders rows `ID - Label` | US001 AC1 | Alphabetical ordering not asserted (gap). |
| Placeholder shown when none selected | US001 AC1, AC5 | Exact message asserted only in AC5. |
| No selection by default | US001 AC1 | Implicit, not hard-asserted. |
| Selection opens edit view | US001 AC3; US003 AC1 | Highlighting style untested. |
| Create button opens create form | US001 AC4; US002 AC1 | Covered. |
| Return to default after delete | US004 AC4 | Placeholder redisplay not asserted. |
| Resizable / togglable left panel | (None) | Gap. |

Gaps Identified:

- Alphabetical ordering not asserted
- Resizing/toggling left panel not covered
- Placeholder re-display after actions not asserted
- Selection highlight visual not covered

> Repeat analogous subsections for FRE-{{DOMAIN}}-02, 03, 04, etc.

### FRE-{{DOMAIN}}-02 – Search behavior

Specification Points:

- Starts-with filtering on ID and/or label
- Client-side only (no API call)
- Clearing current selection upon typing
- ...

Acceptance Criteria Mapping:

| Requirement Aspect | Acceptance Criteria | Notes |
|--------------------|---------------------|-------|
| Search input filters list | US001 AC2 | Logic TBD (gap: starts-with). |
| No results when no match | US001 AC7 | Deselection not asserted. |
| Deselects current selection on search start | (None) | Gap. |
| Client-side only filtering | (None) | Gap (non-functional). |

Gaps Identified:

- Starts-with semantics absent
- Deselection behavior absent
- Client-side only constraint untested

---

## 4. Consolidated Gap & Conflict List

Aggregate all gaps & conflicts into a single actionable table with unique IDs. Prefix Gaps with `GAP-XX` and Conflicts with `CONFLICT-XX` (incremental numbers across the whole epic).

| Gap / Conflict ID | Description | Affected Requirement(s) | Suggested Follow-up |
|-------------------|-------------|--------------------------|---------------------|
| GAP-01 | Alphabetical ordering not asserted | FRE-{{DOMAIN}}-01 | Add AC for sorted order. |
| GAP-02 | Panel resize/toggle untested | FRE-{{DOMAIN}}-01 | Decide scope or add ACs. |
| GAP-03 | Search deselection missing | FRE-{{DOMAIN}}-02 | Add AC clearing selection. |
| CONFLICT-01 | Save success: spec says stay vs AC returns list | FRE-{{DOMAIN}}-03/04 | Decide canonical behavior, update spec or ACs. |

> Maintain stable IDs. When a gap is resolved, keep the row and mark as RESOLVED (date) or move to a historical appendix.

---

## 5. Traceability Matrix (Flat View)

Provide a compact table for quick auditing and tool-friendly parsing.

| Req ID | Requirement Summary | Covered By Acceptance Criteria | Coverage Notes |
|--------|---------------------|-------------------------------|----------------|
| FRE-{{DOMAIN}}-01 | Two-panel layout, list, default placeholder, selection | US001 AC1, AC3, AC4, AC5; US003 AC1 | Missing alphabetical order; panel resize gap |
| FRE-{{DOMAIN}}-02 | Client-side starts-with search clearing selection | US001 AC2, AC7 | Starts-with & deselection untested |
| FRE-{{DOMAIN}}-03 | Create form fields, confirmation, banners | US002 AC1-AC7 | Confirmation & success banner gaps; navigation conflict |
| FRE-{{DOMAIN}}-04 | Edit form, delete workflow, banners | US003 AC1-AC9; US004 AC1-AC7 | Same confirmation gap; success banner missing |

> Ensure AC references remain accurate if AC numbering changes; update both mapping and flat matrix.

---

## 6. Recommendations

Prioritize recommendations that unblock development & testing clarity.

1. Align on save success navigation and update either specification or ACs (resolves CONFLICT-01).
2. Add ACs for confirmation popins or formally remove feature from spec (reduces complexity).
3. Introduce AC verifying alphabetical ordering & post-action default state consistency.
4. Clarify search semantics (starts-with vs contains) and add deselection AC.
5. Decide on panel resize/toggle scope (implement or descoped removal from spec).
6. Add success banner AC with exact message & selector for automation (data-testid).
7. Add client-side only non-functional assertion (no network calls on search).

> Keep list concise (<= 10). Reference Gap / Conflict IDs for traceability.

---

## 7. Maintenance

- Review this matrix whenever `{{SPEC_FILENAME}}` or any User Story AC list changes.
- Update mapping tables; never silently drop a resolved gap/conflict—mark resolution.
- Keep one row per requirement; extend with new requirements as scope evolves.
- Ensure new ACs immediately update Coverage Status (avoid stale "Partially Covered").

Optional Appendix Sections (delete if unused):

- Historical Gap Resolution Log
- Non-Functional Requirement Traceability
- Test Selector Inventory (list of data-testid values referenced by ACs/tests)

---

## 8. Mini Example (Remove in Final Document)

Below is a minimal illustrative example BEFORE you fill real data. Remove this section once actual content is authored.

Example Gap Entry:

```markdown
| GAP-04 | Success banners not asserted | FRE-{{DOMAIN}}-03/04 | Add AC specifying banner text and selector |
```

Example Requirement Aspect Mapping:

```markdown
| Confirmation popin before save | (None) | Gap – spec requires confirmation, AC absent |
```

End of template.
