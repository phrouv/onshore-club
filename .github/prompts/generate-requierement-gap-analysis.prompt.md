---
agent: agent
description: "This prompt is designed to generate the requirement - User Story gap analysis"
---

# Instructions

First, we must have an Epic folder and a specification file provided.

STOP AND WAIT for the specification and the Epic folder before proceeding. If you don't see tthe specification or the Epic folder: do nothing and warn the user.

When you have the specification and the Epic folder, your responsibility is to create a new file named `traceability-{{SPECIFICATION-FILE_NAME}}.md` in the specification directory, that maps the requirements described in the specification file and the Acceptance Criteria of the `{{EPIC_CODE}} - {{EPIC_NAME}}` User Story. Please use `trcaceability-template.md` as a template.
