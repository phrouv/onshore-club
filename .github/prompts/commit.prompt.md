---
agent: agent
description: "This prompt is designed to create well-formatted git commits following project standards."
---

# Git Commit Creation Prompt

## Purpose

This prompt is designed to create proper git commits following the project's gitmoji and conventional commit standards. You will analyze the current git state, identify changes, and create meaningful commit messages that clearly describe what was accomplished.

## Input Requirements

- **Git Repository State**: Current working directory with git changes (staged or unstaged)
- **Change Context**: Understanding of what modifications were made and why
- **Project Knowledge**: Awareness of the codebase structure and conventions

## Output Requirements

- Properly formatted commit message following gitmoji + conventional commit format
- Clear, descriptive commit body when needed for complex changes
- Appropriate git commands executed to stage and commit changes
- Verification that commit was successfully created

## Instructions

You are an expert Git practitioner tasked with creating clean, meaningful commits that follow project standards. 

Follow the instructions given in the file `.github/instructions/version-control/version-control.instructions.md`.

### Ultra-Simple Workflow (Single Command Philosophy)

Objective: ALWAYS stage everything, run ONE synthesis command, derive the commit message, commit.

1. Stage all work (no partial commits): `git add .`
2. Generate a single unified view of staged changes (patch content + rename/mode summary + diffstat) using ONE command:
	`git diff --cached -p --stat --summary`
	This single command provides:
	- Full patch hunks (needed to extract added/removed symbols, error messages, API changes)
	- A summary of structural changes (renames, mode changes) via `--summary`
	- Diffstat for quick sizing / dominant area estimation
3. From that output, infer:
	- Dominant intent (feat / fix / refactor / docs / test / chore / style / perf / build / ci)
	- Optional scope (top-level folder or domain if obvious, else omit)
	- Whether there is a breaking change (look for removed or renamed key files / contract changes); if yes, append `!` and add a `BREAKING CHANGE:` paragraph.
4. Compose subject line: `[gitmoji] type(scope?): imperative summary`
5. If explanation is needed (non-trivial change, breaking, multi-area), add a short body with bullet list of key changes.
6. Create the commit immediately with the generated message.

Do NOT run per-file inspection commands; rely on the unified diff above for all needed context. Prefer speed and consistency.

Single source of truth for classification: the unified staged diff (`git diff --cached -p --stat --summary`).

## Important Notes

- Always: `git add .` (no partial staging in this workflow).
- Use exactly one synthesis command (includes patches, summary, diffstat): `git diff --cached -p --stat --summary`.
- Do not enumerate or inspect files individually unless absolutely required to disambiguate type or breaking change.
- Keep the commit message minimal but standards-compliant.
- Use body only when necessary (breaking change, complex refactor, rationale needed).
- Do NOT include literal `\n` escape sequences inside commit messages. Use real newlines. When constructing the commit command, prefer multiple `-m` flags (one per paragraph / bullet block) rather than embedding `\n`.
