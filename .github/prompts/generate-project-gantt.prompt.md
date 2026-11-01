---
agent: agent
description: "This prompt is designed to generate the project Gantt"
---

# Instructions

Generate a mermaid Gantt chart, based on the project Epic and user stories: specifications/epics/, and the following rules:
- the start date is: Monday October 6th 2025
- We only work 5 days a week
- the team has a capacity of 3 man days, per working day
- each developper can finalise 1 User Story per day
- the User Stories are located in the specifications/epics/ subfolders, each Epic has a number of User Stories

For the order of the Epics, use the following order: EP000, EP001, EP002, EP003, EP007, EP004, EP005, EP006, EP008, EP009

Can you create a new file, named project-plan-epic-YYYY-MM-DD-Vx.md, with a mermaid diagram inside. Display only the start and end date for each Epic. Add a table with stat date of each Epic, and the detail for each User Story. Vx is the version of the file, starting at 1. Increment it if you create a new version of the file.

The file must be created in the specifications/planification/ folder.
