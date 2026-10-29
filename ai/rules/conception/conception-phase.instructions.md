---
description: Guidelines for the AI-assisted conception phase of development
applyTo: "specifications/**"
---

# AI-Assisted Conception Phase Rules

## Business Context

For all conception tasks, the AI should first review the /docs/epics folder to understand the overall project context, user personas, and functional requirements.

## Purpose

To establish a structured approach for initiating the conception phase of development with AI assistance, ensuring all stakeholders have a clear understanding of requirements before development begins.

## Conception Workflow

The conception phase follows this specific workflow. Each step must be completed in sequence:

### Epics Focus Workflow

### Template

Follow the template defined in `documentation/templates/epic-template.md`
Create or update the epic markdown file in `docs/epics/EPNNN - [EPIC_NAME]/EPNNN - [EPIC_NAME].md`

### [PO/Team/AI] Content

Follow all the section of the template


### User Story Focus Workflow

Pick up the list of user stories defined in the epic, each user story is handled completely before moving to the next. Work on 1 user story at a time.

### template

Follow the template defined in `documentation/templates/user-story-template.md`

### [AI] Conceive Main Scenario for Each User Story

For the selected user story, the AI will:

- Create a dedicated folder for the user story: `documentation/epics/EPNNN - [EPIC_NAME]/user-stories/USNNN - user-story-name/`
  - `NNN` is a 3-digit prefix matching the order in the epic's user story list (e.g., `US001 - list-organizations`, `US005 - create-organization`, etc.)
- The main user story description file must be named `description.md` inside each user story folder.

## Epic Documentation Structure

All epic documentation must now follow this structure:

```
documentation/epics/
└── EPNNN - [EPIC_NAME]/
    ├── EPNNN - [EPIC_NAME].md           # Contains personas, epic definition, and user story list
    └── use-cases/
        ├── US001 - list-organizations/
        │   └── description.md
        ├── US005 - create-organization/
        │   └── description.md
        └── ...
```

- Each user story must be in its own folder with a 3-digit prefix and kebab-case name, matching the order in the epic's user story list.
- The main user story file must be named `description.md`.
- All links and references in documentation must use the new folder and file names (e.g., `user-stories/US001 - list-organizations/description.md`).
- When updating or creating user story documentation, always follow the folder and file naming conventions as described above.
- When updating a field reference, check if the reference is updated in the corresponding user stories.
