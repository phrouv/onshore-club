---
agent: agent
description: "Generate the technical notes section of a tech design document"
---

# Generate Tech Design Technical Notes Section

## Role
You are a senior technical architect specializing in documenting technical standards based STRICTLY on repository implementation and configuration.

## Task
Generate the technical notes section using ONLY standards, dependencies, and considerations found in the Onshore Airline GDS repository. DO NOT INVENT TECHNICAL REQUIREMENTS.

## Repository Sources to Analyze
Before generating content, you MUST scan:
- `.github/instructions/` - For actual coding standards and architectural rules
- `onshore-frontend/package.json` - For real frontend dependencies
- `microservices/pom.xml`, `microservices/services/*/pom.xml` and `microservices/common-lib/pom.xml` - For actual backend dependencies
- `microservices/services/*/src/` and `onshore-frontend/src/` - For implemented patterns and frameworks
- `config-repo/` - For configuration standards
- `docker-compose.yml` - For infrastructure dependencies

## Input Parameters
- `{{DATE}}` - Date for the document (format: YYYY-MM-DD)
- `{{VERSION}}` - Version number for the document (e.g., "1.0", "2.1")
- `{{STANDARDS_ALIGNMENT}}` - How the solution aligns with coding standards and organizational practices
- `{{KEY_CONSIDERATIONS}}` - Performance, security, and resiliency factors that must be addressed
- `{{DEPENDENCIES}}` - External libraries, systems, or resources required for implementation

## Instructions
1. **FIRST**: Extract actual coding standards from `/.github/instructions/` folder contents
2. List ONLY dependencies found in real `package.json` and `pom.xml` files with exact versions
3. Reference specific rule files for coding standards and patterns  
4. Extract performance/security requirements from actual repository documentation
5. Use ONLY frameworks and tools already implemented in the codebase
6. If standards are not documented, state "STANDARDS NOT FOUND IN REPOSITORY"

## Output Requirements
The generated content must:
- Follow markdown formatting standards
- Include quantifiable requirements where possible (response times, throughput, etc.)
- Address security and compliance requirements comprehensively
- List all dependencies with rationale for their selection
- Be actionable for development and operations teams

## Template to Follow
Use the technical notes template located at `../../documentation`

## Output Location
Save the generated technical notes section to:
`../../documentation`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- STANDARDS_ALIGNMENT: RESTful API design, Spring Boot microservices architecture, OAuth 2.0 security
- KEY_CONSIDERATIONS: Sub-300ms response times, PCI DSS compliance, 99.9% availability
- DEPENDENCIES: Spring Boot 3.x, PostgreSQL, Redis cache, Payment Provider SDK

This would create the file at:
`../../documentation`

## Quality Guidelines
- Include specific version numbers for all dependencies
- Quantify performance and availability requirements
- Address operational aspects (monitoring, deployment, scaling)
- Consider security implications of all technical choices
- Document any architectural trade-offs or constraints
