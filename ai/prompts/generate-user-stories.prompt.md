---
agent: agent
description: "This prompt is designed to generate a user stories for a given epic."
---

## Objective:

You are given:

- A specific **Epic Markdown file**: `../../documentation/epics`
- Access to all Epic files in: `../../documentation/epics`

Your task:

1. Parse the **target Epic** in full.
2. Analyze **all other Epics** in `../../documentation/epics` for:
   - **Cross-epic dependencies**
   - Shared **design/API links**
   - Consistency in **technical and business context**
3. Generate **highly detailed user stories** based on the Epic, an epic can have multiple user stories, don't combine user stories.
4. Follow the instructions given in the file `ai/rules/conception/conception-phase.instructions.md` about the User Story structure and content.

## Acceptance Criteria Guidelines (Functional Only)
When producing acceptance criteria inside each user story (as per the conception rules):
- Focus ONLY on functional, testable behavior (inputs, business rules, state changes, persistence, role-based visibility, messages).
- EXCLUDE any accessibility (a11y) specifics, telemetry / analytics events, and arbitrary performance timing metrics (e.g. "under 100ms").
- Do not add infrastructure, scalability, or UX quality adjectives ("fast", "user friendly").
- Keep each scenario to a single GIVEN / WHEN / THEN row; split if multiple distinct outcomes.
- Preserve exact error or confirmation messages if explicitly provided in the epic; otherwise use neutral phrasing (e.g. "an error message indicating <reason>").
- Include negative / edge cases only if they are stated or clearly implied by the epic.
