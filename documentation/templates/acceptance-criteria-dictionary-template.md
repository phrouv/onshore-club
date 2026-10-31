---
# Acceptance Criteria Dictionary Template

## Purpose
Provide a standardized structure for generating `acceptance-criteria-dictionary.md` consolidating ACs across multiple epics.

## Generation Preconditions
1. Epic folders must be present under `documentation/epics/`.
2. Each epic contains user stories in `user-stories/` or equivalent naming.
3. User stories include Acceptance Criteria tables (| ID | Given | When | Then |) or clearly enumerated AC lines.

If any epic or user story lacks ACs, mark related entries as `Open-Question` with a comment.

## Table Columns (Canonical)
| Epic | Epic Name | User Story | Story Name | AC ID | Given | When | Then | Status | Comments |

Column meanings:
- Epic: Code like EP007.
- Epic Name: Human-readable epic name.
- User Story: Code like US005.
- Story Name: Kebab-case or short descriptor folder name.
- AC ID: Row identifier (AC1, AC2...). Use ranges (AC1..AC10) only when collapsing many homogeneous ACs; avoid losing atomic detail unless explicitly instructed.
- Given / When / Then: Direct mapping from the user story AC table. Preserve wording. Normalize tense to present.
- Status: Active | Deprecated | Open-Question | Consolidated.
- Comments: Rationale, dependency notes, inconsistency resolution, deprecation reason.

## Status Rules
- Deprecated: User story flagged deprecated (e.g., replaced by new model). All its ACs are Deprecated.
- Open-Question: Missing ACs, ambiguous wording, or pending business decision.
- Consolidated: Multiple historical ACs unified under a canonical one; list source references.
- Active: Default when valid and not superseded.

## Inconsistency Analysis Section
After the master table add a section:
```
## Inconsistency Analysis & Resolutions

| Conflict Area | Observed In | Description | Resolution | Notes |
|---------------|-------------|-------------|------------|-------|
```
Populate rows for discrepancies (search semantics, banner wording, guarded delete patterns, etc.). Resolution must specify authoritative source user story.

## Open Questions Section
List unresolved items as bullet list under `## Open Questions`.

## Deprecated Summary
Bullet list summarizing deprecated user stories and why.

## Consolidated Behaviors
Bullet list for cross-cutting behaviors standardized (search clearing selection, success banners, guarded delete pattern...).

## Traceability
Short paragraph explaining purpose and downstream usage (BDD generation, coverage tracking).

## Generation Steps (Agent Workflow)
1. Validate epic folders exist. If absent: abort and warn.
2. Enumerate user stories; parse AC tables.
3. Build a data model of AC entries; apply status rules.
4. Detect inconsistencies:
   - Search semantics (starts-with vs contains)
   - Guarded delete patterns
   - Banner message wording and test IDs
   - Override lifecycle (manual vs automatic)
5. Compose master table.
6. Append analysis sections.
7. Save file to `documentation/feature-map/acceptance-criteria-dictionary.md`.

## Wording & Formatting Rules
- Maintain exact error messages and banner texts.
- Keep table uniform (10 columns).
- Use present tense, active voice.
- Avoid trailing spaces.
- Wrap ranges (AC1..ACN) only when granular detail adds no differentiating value.

## Example Row
`| EP007 | WSA Management | US035 | success-banner-messaging | AC1 | Create template operation succeeds | Persistence completes | Success banner "Template created" displayed | Active | data-testid banner-template-created |`

## Validation Checklist
- [ ] All epics processed
- [ ] No missing ACs unless marked Open-Question
- [ ] Deprecated stories fully marked
- [ ] Inconsistency table present
- [ ] Open Questions present if any
- [ ] No markdown lint errors (headings spacing, table columns)
- [ ] File path correct

---
