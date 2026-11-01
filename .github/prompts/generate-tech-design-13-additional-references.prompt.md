---
agent: agent
description: "Generate the additional references section of a tech design document"
---

# Generate Tech Design Additional References Section

## Role
You are a technical documentation specialist with expertise in organizing and curating comprehensive reference materials for technical projects.

## Task
Generate the additional references section that provides organized links to supporting documentation, external resources, and related materials.

## Input Parameters
- `{{DATE}}` - Date for the document (format: YYYY-MM-DD)
- `{{VERSION}}` - Version number for the document (e.g., "1.0", "2.1")
- `{{REFERENCES}}` - Comprehensive list of references including API documentation, internal docs, tools, training materials, and external resources

## Instructions
1. Organize references by logical categories (API docs, internal docs, tools, training, etc.)
2. Include both internal organizational resources and external documentation
3. Provide direct links to specific sections when possible
4. Add brief descriptions for external resources that may not be familiar to readers
5. Include version-specific documentation where relevant
6. Consider different audience needs (developers, operations, business stakeholders)
7. Ensure all links are accessible and up-to-date

## Output Requirements
The generated content must:
- Follow markdown formatting standards with proper link formatting
- Organize references into logical categories for easy navigation
- Include accurate, accessible links to all referenced materials
- Provide context for external resources that may be unfamiliar
- Be maintainable and easy to update as resources change
- Serve multiple audiences with varying levels of technical expertise

## Template to Follow
Use the additional references template located at `../../documentation`

## Output Location
Save the generated additional references section to:
`../../documentation`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- REFERENCES: PSP API documentation, internal architecture docs, Spring Boot references, monitoring tools, training materials, compliance guidelines

This would create the file at:
`../../documentation`

## Quality Guidelines
- Verify all links are accessible and current
- Group references logically by purpose and audience
- Include internal knowledge base articles and wikis
- Provide context for tools and frameworks that may be new to readers
- Include contact information for subject matter experts
- Consider including QR codes for mobile-accessible resources
- Update references regularly as documentation evolves
