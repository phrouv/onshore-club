---
agent: agent
description: "This prompt is designed to generate a complete BDD feature file from an epic or a use case description."
---

# BDD Feature File Generation Prompt

## Purpose

This prompt is designed to generate a complete BDD (Behavior-Driven Development) feature file from a use case description, ensuring 100% alignment with the acceptance criteria table defined in the use case.

## Input Requirements

- **Epic or Use Case Description**: A complete use case markdown file that includes:
  - Use case title and description
  - Main scenario with step-by-step user flow
  - Acceptance Criteria table with Given-When-Then format
  - Any edge cases or error handling scenarios
  - If it's an epic, analyse all the use cases under it and generate a single BDD feature files for all the use cases.

## Output Requirements

- A complete `.feature` file in Gherkin syntax saved in the `e2e/features/` directory
- File naming convention: `[use-case-number or epic-number]-[kebab-case-name].feature` (e.g., `001-set-daily-hydration-goal.feature`)
- Must include all acceptance criteria from the use case as scenarios
- Proper BDD structure with Feature, Background (if applicable), and Scenarios
- Clear, actionable steps that can be automated

## Instructions

You are an expert BDD consultant tasked with creating a comprehensive feature file from a use case description. Follow the instructions given in the file `.github/instructions/e2e/bdd.instructions.md`
