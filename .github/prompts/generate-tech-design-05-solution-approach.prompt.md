---
agent: agent
description: "Generate the solution approach section of a tech design document"
---

# Generate Tech Design Solution Approach Section

## Role
You are a senior technical lead specializing in creating detailed implementation plans and documenting key architectural decisions.

## Task
Generate the solution approach section that breaks down implementation into actionable steps and documents key design decisions with rationale.

## Input Parameters
- `{{DATE}}` - Date for the document (format: YYYY-MM-DD)
- `{{VERSION}}` - Version number for the document (e.g., "1.0", "2.1")
- `{{DETAILED_STEPS}}` - Actionable implementation steps broken down into phases from setup to deployment
- `{{KEY_DESIGN_DECISIONS}}` - Significant architectural and design decisions with reasoning and alternatives considered

## Instructions
1. Break implementation into logical phases (foundation, core implementation, integration, testing)
2. Make each step specific, actionable, and time-bound where possible
3. Document major design decisions with clear reasoning
4. Include alternatives that were considered but not chosen
5. Address risk mitigation strategies and contingency plans
6. Reference any proof-of-concept work that informed the approach

## Output Requirements
The generated content must:
- Follow markdown formatting standards
- Provide a clear roadmap from start to finish
- Include specific technologies, patterns, and approaches for each phase
- Document decision rationale to help future maintainers understand choices
- Be detailed enough for development teams to begin implementation

## Template to Follow
Use the solution approach template located at `../../documentation`

## Output Location
Save the generated solution approach section to:
`../../documentation`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- DETAILED_STEPS: Phase 1: Setup Spring Boot project and database schema, Phase 2: Implement core payment processing, Phase 3: PSP integration, Phase 4: Testing and deployment
- KEY_DESIGN_DECISIONS: Use synchronous processing for real-time confirmation, implement circuit breaker pattern, choose populator over direct mapping

This would create the file at:
`../../documentation`

## Quality Guidelines
- Organize phases logically with clear dependencies
- Include testing strategy throughout the implementation
- Document rollback and contingency procedures
- Consider team capacity and skill requirements
- Include integration and deployment considerations from the start
