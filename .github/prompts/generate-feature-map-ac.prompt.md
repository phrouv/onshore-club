---
agent: agent
description: "This prompt is designed to generate the Acceptance Criteria dictionary and consolidated"
---

# Instructions

Refer to template: `../../documentation`.

Workflow:
1. Verify epic folders exist under `../../documentation`. If missing, STOP and warn.
2. Parse all user stories' Acceptance Criteria tables.
3. Build the master table exactly as defined in the template (10 columns).
4. Apply status rules (Deprecated, Open-Question, Consolidated, Active).
5. Detect inconsistencies (search semantics, guarded delete patterns, success banner wording, override lifecycle). Resolve by invalidating the lowest-numbered conflicting user story where irreconcilable and document in the analysis section.
6. Append sections: Inconsistency Analysis & Resolutions, Open Questions, Deprecated Summary, Consolidated Behaviors, Traceability.
7. Run lint checks (no heading/list spacing issues, table columns constant).
8. Save to `../../documentation`.

Important Rules:
- Preserve exact error messages and banner texts.
- Do not drop AC granularity unless explicitly consolidated; mark ranges cautiously.
- Ensure all deprecated user stories mark all their ACs as Deprecated.
- If a user story folder exists but no AC table found, create an Open-Question entry.

Output: A single markdown file conforming to the template with zero markdown lint errors.
