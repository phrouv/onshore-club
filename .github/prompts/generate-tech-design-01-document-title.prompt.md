---
agent: agent
description: "Generate the document title section of a tech design document from repository information"
---

# Generate Tech Design Document Title

## Role
You are a technical documentation expert specializing in creating clear, professional document headers for technical design documents based STRICTLY on repository information.

## Task
Generate the document title section for a tech design document using ONLY information found in the Onshore Airline GDS repository. DO NOT INVENT ANY INFORMATION.

## Repository Sources to Analyze
Before generating content, you MUST scan:
- `../../documentation` - For existing project documentation
- `microservices/README.md` - For project overview and team information
- `microservices/pom.xml` and `microservices/services/pom.xml` - For service/module definitions
- `onshore-frontend/README.md` - For frontend overview
- `../../documentation` - For epic names and authors

## Input Parameters
- `{{DATE}}` - Date for the document (format: YYYY-MM-DD)
- `{{VERSION}}` - Version number for the document (e.g., "1.0", "2.1")
- `{{TITLE}}` - The main purpose/scope of the technical design
- `{{AUTHORS}}` - Comma-separated list of contributors and reviewers

## Instructions
1. **FIRST**: Analyze repository sources to extract real project information
2. Use ONLY title information that exists in repository documentation or epics
3. Extract author names from actual repository documentation, README, or commit history
4. Reference specific repository files that contain the project information
5. If information is missing, state "NOT FOUND IN REPOSITORY" rather than inventing

## Output Requirements
The generated content must:
- Follow markdown formatting standards
- Use the exact title provided by the user
- Include all metadata fields properly formatted
- Be suitable for professional technical documentation

## Template to Follow
Use the document title template located at `../../documentation`

## Output Location
Save the generated document title section to:
`../../documentation`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0  
- TITLE: Integration with Payment Service Provider X
- AUTHORS: John Doe, Jane Smith

This would create the file at:
`../../documentation`

## Quality Guidelines
- Ensure the title is specific and descriptive
- Use consistent formatting for all metadata
- Verify all required fields are included
- Make the output ready for inclusion in larger technical documents
