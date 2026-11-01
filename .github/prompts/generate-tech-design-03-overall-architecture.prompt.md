---
agent: agent
description: "Generate the overall architecture section of a tech design document"
---

# Generate Tech Design Overall Architecture Section

## Role
You are a technical architect specializing in creating system architecture documentation based STRICTLY on actual repository implementation.

## Task
Generate the overall architecture section with Mermaid diagrams that reflect the ACTUAL system components, data flow, and integration points found in the Onshore Airline GDS repository. DO NOT INVENT ARCHITECTURE.

## Repository Sources to Analyze
Before generating content, you MUST scan:
- `microservices/services/` - For actual microservices (one folder per service)
- `microservices/cloud/` - For api-gateway, config-server and service-registry
- `microservices/common-lib/` - For shared code
- `onshore-frontend/src/` - For the React frontend structure
- `docker-compose.yml` and `docker-compose.dev.yml` - For infrastructure and service definitions
- `services.sh` - For service names, folders and ports
- `microservices/services/*/src/` - For actual service implementations and APIs
- `config-repo/` - For centralized configuration and integration settings
- `../../documentation` - For data model and database architecture

## Input Parameters
- `{{DATE}}` - Date for the document (format: YYYY-MM-DD)
- `{{VERSION}}` - Version number for the document (e.g., "1.0", "2.1")
- `{{HIGH_LEVEL_DIAGRAM}}` - Mermaid diagram code showing the system architecture
- `{{FLOW_DESCRIPTION}}` - Detailed explanation of data and control flow between components
- `{{INTEGRATION_POINTS}}` - List and description of external systems and their integration methods

## Instructions
1. **FIRST**: Analyze actual application structure in `microservices/` and `onshore-frontend/` to understand real components
2. Create Mermaid diagrams that reflect ONLY the actual services found in `docker-compose.yml`
3. Use component names that match actual folder/service names in repository
4. Document ONLY integration points found in configuration files or source code
5. Reference specific files/folders for each component mentioned in the diagram
6. If architecture details are missing, state "ARCHITECTURE NOT IMPLEMENTED" rather than assuming

## Output Requirements
The generated content must:
- Follow markdown formatting standards
- Include properly formatted Mermaid diagrams that render correctly
- Provide comprehensive flow descriptions that complement the diagrams
- Document all integration points with external systems
- Use consistent terminology throughout the section

## Template to Follow
Use the overall architecture template located at `../../documentation`

## Output Location
Save the generated architecture section to:
`../../documentation`

## Mermaid Diagram Guidelines
- Use `graph TB` for top-bottom architecture diagrams
- Use `sequenceDiagram` for interaction flows
- Use subgraphs to group related components
- Include clear labels and descriptive component names
- Show data flow direction with appropriate arrows

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- HIGH_LEVEL_DIAGRAM: A Mermaid graph showing frontend, backend, and external payment service
- FLOW_DESCRIPTION: Description of payment request flow from user to PSP
- INTEGRATION_POINTS: REST API integration with Payment Service Provider X

This would create the file at:
`../../documentation`

## Quality Guidelines
- Ensure diagrams are readable and not overly complex
- Use consistent color schemes and styling
- Include both happy path and error flow scenarios
- Document security boundaries and trust zones
- Provide sufficient detail for implementation teams
