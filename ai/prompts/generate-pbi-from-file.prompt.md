---
agent: agent
description: "Generate a Product Backlog Item (PBI) for Azure DevOps from a provided source file."
---

## Objective

Create and persist (not just draft) a fully detailed Product Backlog Item (PBI) in Azure DevOps (ADO) based strictly on the content of a provided reference file. The reference file will typically be a markdown file (e.g. an Epic, User Story draft, specification, design note, or backlog CSV row excerpt) located somewhere in the repository.

Primary outcome: a new ADO Work Item of type "Product Backlog Item" is created in project `GDS-PROJECTS` (Board: AdminTool) using MCP ADO tools.

Secondary fallback: if creation cannot proceed (missing source file, missing mandatory data, tool failure), return an actionable markdown draft plus a clear error / required input notice.

## Mandatory Azure DevOps Metadata
You MUST always set / infer and include these fields in the PBI output structure:

- Project: GDS-PROJECTS
- Board / Application Context: AdminTool
- Project Involved Region: all
- Project Involved Divisions: Corporate
- Project Application: AdminTool
- Work Item Type: Product Backlog Item (PBI)
- State: Backlog (unless the user explicitly instructs otherwise)
- Area Path: GDS-PROJECTS\\AdminTool (set explicitly unless user provides an override)

### REQUIRED CUSTOM FIELD PERSISTENCE (IMPORTANT)
The above metadata MUST NOT live only in the textual markdown. They must be persisted into their dedicated ADO custom fields in addition to being echoed in `System.Description`.

Assumed (and REQUIRED) custom field reference names (update here if the organization uses different internal names):

| Display Label (in this prompt) | ADO Field Reference Name (assumed) | Example Value |
|--------------------------------|------------------------------------|---------------|
| Project Involved Region        | `Custom.InvolvedRegions`           | `all`         |
| Project Involved Divisions     | `Custom.InvolvedDivisions`         | `Corporate`   |
| Project Application            | `Custom.Application`               | `AdminTool`   |

If creation fails with a rule error referencing any of these display names, automatically retry (once) by attempting the following fallback reference name patterns in order until success or exhaustion (observed live field names favor `Custom.Application` as primary):
1. `Custom.InvolvedRegions` / `Custom.InvolvedDivisions` / `Custom.ProjectApplication` (legacy variant)
2. `Custom.InvolvedRegion` / `Custom.InvolvedDivision` / `Custom.ProjectApplication`
3. `GDS.InvolvedRegions` / `GDS.InvolvedDivisions` / `GDS.ProjectApplication`
4. `Extension.InvolvedRegions` / `Extension.InvolvedDivisions` / `Extension.ProjectApplication`

Log (in the assistant reasoning only, not user output) which set succeeded. If all fail, return the draft with a clear "Creation Skipped" message listing all attempted field names.

NEVER omit these fields from the `mcp_ado_wit_create_work_item` call unless explicitly instructed they are not present in the process template.

## Required PBI Fields To Produce
Return a structured markdown section that clearly identifies each of the following fields. Only use information present (or logically inferable with high confidence) from the source file. Do NOT introduce speculative scope.

1. Title
    - MUST preserve any explicit backlog identifier prefix found in the source (e.g., `EP001`, `EP012`, `US007`, `US120`).
       - Detection order (first match wins):
          1. Leading token in the source file main heading (`#` / first line) matching regex: `^(EP|US)\d{3,}` (case-insensitive)
          2. Leading token in the source file name (without extension) matching same pattern
          3. Explicit ID column/label inside the file (e.g., a table cell starting with `EP` or `US` followed by digits)
       - When detected, prepend exactly that token plus a space to the synthesized title if it is not already present.
       - Never alter the numeric part (zero padding retained). Do NOT invent a prefix if none is present with high confidence.
    - Short, action-oriented, value-focused after the prefix.
    - If the source file already contains a suitable title (with or without prefix), reuse/adapt it ensuring the prefix rule above.
    - Keep total length < 110 chars (if longer, trim the tail but keep the prefix intact).
2. Description
   - Concise high-level summary (2-4 sentences) of intent & business value.
3. Business Value / Rationale
   - Why this matters. Quote or reference source wording when available.
4. Context / Background
   - Key contextual elements from the file: domain objects, constraints, stakeholders.
5. Detailed Requirements
   - Bullet list of clear, testable functional requirements.
   - Each requirement should be atomic and phrased in the present tense.
6. Non-Functional / Constraints (if any)
   - Performance, security, compliance, UX, interoperability points explicitly mentioned.
7. Acceptance Criteria (Gherkin Style)
   - Extract ALL explicit acceptance criteria present in the file.
   - If the file lists AC IDs, preserve them (AC1, AC2...).
   - If acceptance criteria aren't explicitly stated, derive 3-6 core scenarios (label them Derived-1, Derived-2...).
   - Acceptance Criteria MUST be expressed ONLY in a single Markdown table (no duplicated bullet lists or separate scenario blocks) with columns EXACTLY: `ID | GIVEN | WHEN | THEN`.
   - The markdown table is the sole canonical source used for the ADO Acceptance Criteria field. Do NOT output additional scenario headings or lists.
   - Each cell should be concise; multiline logical steps inside a cell must use `<br>` for line breaks (avoid raw line breaks that break the table in ADO).
   - Keep ID values exactly as in source (case + numbering). Derived ones use `Derived-#`.
      
8. Out of Scope
   - Explicitly mention items the source excludes. If none stated, write: "None explicitly stated in source."
9. Dependencies / Links
   - Other epics/stories/components/APIs referenced; provide relative file paths if present.
10. Open Questions / Clarifications
    - Only list genuine ambiguities surfaced by the source. If none: "None identified based on source." 
11. Impacted Components / Domains
    - Infer from file (e.g., frontend admin UI, core service, DB schema, integration with WSA, etc.).
12. Risks / Assumptions
    - Separate subsections for Risks and Assumptions.
13. Estimation Guidance (Do NOT estimate)
    - Provide factors affecting sizing (e.g., data model changes, external API dependencies).

## Output Format (Draft Structure Before Creation)
First internally build this canonical structure (used both for validation and to assemble ADO field values). Only output it if you must fall back (see Fallback Conditions below) OR if the user explicitly asks for "draft only".

```
# PBI: <Title>

## Metadata
- Project: GDS-PROJECTS
- Board: AdminTool
- Project Involved Region: all
- Project Involved Divisions: Corporate
- Project Application: AdminTool
- Work Item Type: Product Backlog Item
- State: Backlog
- Area Path: GDS-PROJECTS\\AdminTool

## Description
...text...

## Business Value / Rationale
...text...

## Context / Background
...text...

## Detailed Requirements
- ...

## Non-Functional / Constraints
- ... (or "None stated")

## Acceptance Criteria

| ID | GIVEN | WHEN | THEN |
|----|-------|------|------|
| AC1 | ... | ... | ... |
| AC2 | ... | ... | ... |
| Derived-1 | ... | ... | ... |
| Derived-2 | ... | ... | ... |

(repeat rows as needed; no other scenario representations allowed)

## Out of Scope
- ...

## Dependencies / Links
- ...

## Open Questions / Clarifications
- ...

## Impacted Components / Domains
- ...

## Risks
- ...

## Assumptions
- ...

## Estimation Guidance (Do NOT estimate)
- ...
```

## Azure DevOps Creation Flow
After constructing and validating the PBI structure:

PRE: Type should follow what is givven in the source file.
- If it is an Epic it should be an Epic (in state `Inactive`). 
- If it is a User Story it should be a Product Backlog Ite (in state: `Backlog`)
- If it is a User Story it should be linked to the epic

### Mandatory Parent Epic Linking (NON-OPTIONAL)
If the analyzed source (user story) references an Epic code (regex `EP\d{3,}`):
1. Extract the Epic code from (priority order): heading/title, explicit "Related Epic" section, path segments, inline references.
2. Use `mcp_ado_search_workitem` with a search text combining the Epic code and (if available) a distinctive title fragment (e.g. `"EP001 Socle"`).
3. Select the work item of type `Epic` whose title begins with that code.
4. After PBI creation succeeds, immediately create the parent relationship using `mcp_ado_wit_work_items_link`:
   ```json
   { "project": "GDS-PROJECTS", "updates": [ { "id": <PBI_ID>, "linkToId": <EPIC_ID>, "type": "parent" } ] }
   ```
5. Re-fetch the PBI to verify `System.Parent` or a Hierarchy-Reverse relation is present.
6. If the Epic is not found after two searches (full text then code only), proceed but:
   - Add an Open Question: "Epic <CODE> not found in ADO; linkage pending." 
   - Output a warning line in the assistant response: `WARNING: Epic <CODE> not found; parent link not created.`
7. Never create a placeholder Epic to satisfy linkage; only link to existing epics.
8. Do NOT use artifact placeholder (branch) links as a substitute for the hierarchy link.
9. If linking fails due to permissions or validation, retry once; if still failing, surface concise error and mark linkage pending.


1. Map fields to ADO standard fields:
   - System.Title = Title (including preserved prefix token when detected as per Title rules)
   - System.Description = A rich markdown amalgamation containing (Description, Business Value, Context, Detailed Requirements, Non-Functional, Out of Scope, Dependencies, Impacted Components, Risks, Assumptions, Estimation Guidance header)
   - System.AreaPath = GDS-PROJECTS\\AdminTool (explicit to avoid permission / default mismatches)
   - Microsoft.VSTS.Common.AcceptanceCriteria = The EXACT markdown acceptance criteria table (ID | GIVEN | WHEN | THEN) reproduced verbatim from the draft. No additional scenario lists or duplicated representations.
   - Tags: DO NOT add `System.Tags` (temporarily disabled while configuration is in progress).
   - Custom.InvolvedRegions (or fallback variant) = Project Involved Region value (use `ALL` uppercase, matching existing data)
   - Custom.InvolvedDivisions (or fallback variant) = Project Involved Divisions value (e.g. `Corporate`)
   - Custom.Application (or fallback variant) = Project Application value (e.g. `AdminTool`)
2. Always include meta footer inside System.Description:
   ```
   ---
   Metadata:
   Project Involved Region: ALL
   Project Involved Divisions: Corporate
   Project Application: AdminTool
   Board: AdminTool
   Source File: <relative path>
   Generation Timestamp (UTC): <iso8601>
   ---
   ```
3. Use the MCP ADO tool `mcp_ado_wit_create_work_item` with:
   - project = GDS-PROJECTS
   - workItemType = Product Backlog Item
   - fields array containing objects with `name` and `value` (and `format: Markdown` for markdown bodies when appropriate):
     - System.Title
     - System.Description (Markdown)
     - Microsoft.VSTS.Common.AcceptanceCriteria (Markdown)
   - Custom.InvolvedRegions (or fallback variant)
   - Custom.InvolvedDivisions (or fallback variant)
   - Custom.ProjectApplication (or fallback variant)
   - IMPORTANT: Do NOT include `System.Tags` field at all (pending configuration). Any previous guidance about optional tags is superseded by this rule.
4. On success: Output a minimal confirmation block:
   ```
   PBI Created
   ID: <id>
   Title: <title>
   URL: <ado-work-item-url>
   ```
5. Do NOT reprint the entire draft after successful creation unless explicitly requested.
6. Perform Epic parent linking (if applicable) BEFORE emitting the success confirmation block. If linking ultimately fails, append a single WARNING line after the confirmation.

## Fallback Conditions (When NOT to Create Immediately)
Abort creation and output a markdown draft + guidance if ANY of these apply:
- Source file not provided / unreadable
- Cannot extract a meaningful Title
- Fewer than 1 acceptance criteria (explicit + derived) after analysis
- ADO tool invocation fails (include error summary and instruct user to retry or adjust)

### Automatic Retry Logic for Custom Fields
If the only failure cause is a required custom field rule error for Involved Regions / Divisions / Application, perform the fallback name sequence described earlier and retry automatically (maximum 3 total attempts). Only surface the draft to the user if all retries fail.

## Processing Instructions
1. WAIT for the user (or invoking process) to provide the exact path to the source file (or its raw content). If you don't have it yet, ask for it and STOP. unless this is a test then no need for a file makeup the test data.
2. Read and parse the entire file.
3. Extract structured data (titles, bullet lists, tables, IDs, domain vocabulary).
4. Preserve exact wording for any error messages, field labels, identifiers, and enumerations.
5. Maintain traceability between Acceptance Criteria in the source and the output: never drop or merge them.
6. Do not fabricate features or technical details not present in the source.
7. If critical required information is missing (e.g., there is no discernible objective), explicitly call it out in Open Questions.
8. Keep language clear, testable, implementation-agnostic.
9. Avoid future tense; prefer present tense.
10. Avoid subjective adjectives ("robust", "intuitive") unless sourced.
11. If all validation passes, proceed with ADO creation; otherwise fallback.

## Validation Checklist (Internal)
Before returning the PBI, internally verify:
- Title < 110 characters and conveys action/value and (when applicable) includes the detected `EP###` or `US###` prefix
- Every requirement maps to at least one acceptance criterion (implicitly or explicitly)
- All mandatory metadata values present
- No speculative features added
- Acceptance criteria table present (ID|GIVEN|WHEN|THEN) and well-formed Given/When/Then with no duplicate scenario listings
- No unexplained placeholders like "TBD" unless genuinely missing from source

## If Source File Is Missing
Respond with ONLY (verbatim) unless this is a test then no need for a file makeup the test data.
2. Read and parse the entire file.:
Source file required: Please provide the path or paste the content of the reference file to generate the PBI.
and stop (no creation attempt). 

## If Multiple Candidate Items Found
If the source file actually contains multiple distinct backlog-worthy items:
- Split into separate subsections under a heading: `## Multiple PBIs Detected`
- For each, produce the full PBI structure (Title through Estimation Guidance)

## Prohibited
- Do NOT perform effort estimation (story points, days, T-shirt sizes)
- Do NOT invent acceptance criteria beyond necessary derived core scenarios
- Do NOT alter mandated metadata values

## Example (Abbreviated)
```
# PBI: Manage WSA Region Mapping
... (rest of structure as specified) ...
```

## Final Output Rules
- Success path: return only the concise creation confirmation block (see step 4 in Creation Flow).
- Fallback path: return the full PBI markdown draft in the specified structure PLUS a short "Creation Skipped: <reason>" line at the top.
- Waiting path (missing file): return the exact waiting message only.
- Add the metada and PIB id at the end of the original file.
- BEFORE FINISHING YOU MUST: 
-- Verify that if it is a user story it is linked to an epic if the epic is mentioned in the file.
-- Add the link to the ADO work item at the end of the original file.
-- Add the id to the ADO work item at the end of the original file.
 - If Epic linkage attempted and failed, append a WARNING line in the file footer indicating the pending link.

### Standard File Footer Format (MANDATORY)

When appending ADO linkage metadata to the source markdown file, ALWAYS (no exceptions) use exactly this footer block format. This guarantees downstream tooling can parse it reliably and prevents format drift:

```
---

<!-- ADO Work Item Link Appended -->
PBI ID: <PBI_NUMERIC_ID>  
PBI URL: https://dev.azure.com/<org>/<project>/_workitems/edit/<PBI_NUMERIC_ID>
```

If the item is a User Story (PBI) successfully linked to an Epic, append the Epic lines directly after the PBI URL line (still inside the same footer block) in this order:

```
Epic ID: <EPIC_NUMERIC_ID>  
Epic URL: https://dev.azure.com/<org>/<project>/_workitems/edit/<EPIC_NUMERIC_ID>
```

If Epic linkage fails after the mandated retries, append a single WARNING line after the URL lines:

```
WARNING: Epic <EP_CODE> not found or link creation failed
```

Spacing / markdown specifics:
- The line consisting of three dashes `---` must precede the comment marker (acts as a delimiter from previous content if not already present at end of file).
- Preserve two trailing spaces at the end of each `ID:` line to enforce an intentional markdown line break (as shown above).
- Do NOT add extra blank lines between the footer lines except the single blank line after the opening `---` (exactly as in the example).
- Do NOT wrap IDs or URLs in backticks.
- Do NOT alter the comment text `<!-- ADO Work Item Link Appended -->`.

Example (PBI with Epic link):

```
---

<!-- ADO Work Item Link Appended -->
PBI ID: 1652979  
PBI URL: https://dev.azure.com/lpl-sources/_workitems/edit/1652979
Epic ID: 1652000  
Epic URL: https://dev.azure.com/lpl-sources/_workitems/edit/1652000
```

Example (PBI without Epic or linkage failed):

```
---

<!-- ADO Work Item Link Appended -->
PBI ID: 1652979  
PBI URL: https://dev.azure.com/lpl-sources/_workitems/edit/1652979
WARNING: Epic EP001 not found or link creation failed
```

Any deviation from this format should trigger a corrective rewrite (delete previous variant and write the standardized block anew).

Do NOT include internal reasoning or tool invocation details in the final user-facing output.
