---
agent: agent
description: "Generate a complete unified tech design document in a single prompt"
---

```prompt
# Generate Tech Design Document - Unified Prompt

## First Step: Choose Your Section

**Which section would you like to generate?** (Enter a number from 1-13)

1. **Document Title** - Generate the document header with metadata
2. **Context** - Generate business context and problem description
3. **Overall Architecture** - Generate system architecture with Mermaid diagrams
4. **Technical Notes** - Generate standards, dependencies, and technical considerations
5. **Solution Approach** - Generate implementation plan and design decisions
6. **Diagrams** - Generate detailed component, sequence, and state diagrams
7. **Data Model Schema Update** - Generate data model changes and migration plans
8. **API Documentation** - Generate API endpoint specifications and examples
9. **Code Bootstrap** - Generate code examples and implementation patterns
10. **POC Insights** - Generate proof-of-concept findings and recommendations
11. **Data Migration** - Generate data migration strategies and scripts
12. **Security, Performance, Resiliency** - Generate operational excellence documentation
13. **Additional References** - Generate supporting documentation links and resources

---

# Section 1: Generate Tech Design Document Title

## Role
You are a technical documentation expert specializing in creating clear, professional document headers for technical design documents based STRICTLY on repository information.

## Task
Generate the document title section for a tech design document using ONLY information found in the Onshore Airline GDS repository. DO NOT INVENT ANY INFORMATION.

## Repository Sources to Analyze
Before generating content, you MUST scan:
- `specifications/` - For existing project documentation
- `microservices/README.md` - For project overview and team information
- `microservices/pom.xml` and `microservices/services/pom.xml` - For service/module definitions
- `onshore-frontend/README.md` - For frontend overview
- `specifications/epics/` - For epic names and authors

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
Use the document title template located at `specifications/templates/paragraphs/01-document-title-template.md`

## Output Location
Save the generated document title section to:
`specifications/documents-generator/technical-documention/{{DATE}}-v{{VERSION}}/01-document-title.md`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- TITLE: Integration with Payment Service Provider X
- AUTHORS: John Doe, Jane Smith

This would create the file at:
`specifications/documents-generator/technical-documention/2025-11-04-v1.0/01-document-title.md`

## Quality Guidelines
- Ensure the title is specific and descriptive
- Use consistent formatting for all metadata
- Verify all required fields are included
- Make the output ready for inclusion in larger technical documents

---

# Section 2: Generate Tech Design Context Section

## Role
You are a technical documentation expert specializing in creating comprehensive context sections based EXCLUSIVELY on repository information.

## Task
Generate the context section using ONLY business problems, use cases, and objectives found in the Onshore Airline GDS repository documentation. DO NOT INVENT BUSINESS REQUIREMENTS.

## Repository Sources to Analyze
Before generating content, you MUST scan:
- `specifications/epics/` - For business requirements and user stories
- `specifications/backlog/` - For backlog items and acceptance criteria
- `specifications/context/` - For business context documentation
- `microservices/README.md` - For project purpose, features and goals
- `specifications/personae/` - For user personas and use cases

## Input Parameters
- `{{DATE}}` - Date for the document (format: YYYY-MM-DD)
- `{{VERSION}}` - Version number for the document (e.g., "1.0", "2.1")
- `{{DESCRIPTION}}` - Business or technical problem description with sufficient context
- `{{USE_CASE}}` - One or more key use cases that necessitate this design
- `{{OBJECTIVE}}` - Primary goal of the technical design

## Instructions
1. **FIRST**: Extract business context from repository documentation sources
2. Use ONLY business problems and requirements found in epics and backlog documentation
3. Reference specific epic files, user stories, or backlog items by name/ID
4. Extract objectives from actual acceptance criteria in repository documentation
5. If business context is missing, state "BUSINESS CONTEXT NOT FOUND IN REPOSITORY"
6. Include file references for all business requirements mentioned

## Output Requirements
The generated content must:
- Follow markdown formatting standards
- Provide enough context for stakeholders to understand the problem
- Clearly articulate business value and technical necessity
- Include specific, measurable objectives
- Be professionally written and technically accurate

## Template to Follow
Use the context template located at `specifications/templates/paragraphs/02-context-template.md`

## Output Location
Save the generated context section to:
`specifications/documents-generator/technical-documention/{{DATE}}-v{{VERSION}}/02-context.md`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- DESCRIPTION: Our e-commerce platform needs to integrate with a new payment service provider to support additional payment methods and improve security compliance
- USE_CASE: Enable customers to make secure payments using credit cards, digital wallets, and bank transfers during the checkout process
- OBJECTIVE: Implement seamless payment processing with 99.9% uptime and PCI DSS compliance

This would create the file at:
`specifications/documents-generator/technical-documention/2025-11-04-v1.0/02-context.md`

## Quality Guidelines
- Include business drivers and technical motivations
- Reference compliance requirements and regulatory constraints
- Explain the current state and desired future state
- Quantify success criteria where possible
- Consider the audience and their familiarity with the domain

---

# Section 3: Generate Tech Design Overall Architecture Section

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
- `specifications/database/` - For data model and database architecture

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
Use the overall architecture template located at `specifications/templates/paragraphs/03-overall-architecture-template.md`

## Output Location
Save the generated architecture section to:
`specifications/documents-generator/technical-documention/{{DATE}}-v{{VERSION}}/03-overall-architecture.md`

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
`specifications/documents-generator/technical-documention/2025-11-04-v1.0/03-overall-architecture.md`

## Quality Guidelines
- Ensure diagrams are readable and not overly complex
- Use consistent color schemes and styling
- Include both happy path and error flow scenarios
- Document security boundaries and trust zones
- Provide sufficient detail for implementation teams

---

# Section 4: Generate Tech Design Technical Notes Section

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
Use the technical notes template located at `specifications/templates/paragraphs/04-technical-notes-template.md`

## Output Location
Save the generated technical notes section to:
`specifications/documents-generator/technical-documention/{{DATE}}-v{{VERSION}}/04-technical-notes.md`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- STANDARDS_ALIGNMENT: RESTful API design, Spring Boot microservices architecture, OAuth 2.0 security
- KEY_CONSIDERATIONS: Sub-300ms response times, PCI DSS compliance, 99.9% availability
- DEPENDENCIES: Spring Boot 3.x, PostgreSQL, Redis cache, Payment Provider SDK

This would create the file at:
`specifications/documents-generator/technical-documention/2025-11-04-v1.0/04-technical-notes.md`

## Quality Guidelines
- Include specific version numbers for all dependencies
- Quantify performance and availability requirements
- Address operational aspects (monitoring, deployment, scaling)
- Consider security implications of all technical choices
- Document any architectural trade-offs or constraints

---

# Section 5: Generate Tech Design Solution Approach Section

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
Use the solution approach template located at `specifications/templates/paragraphs/05-solution-approach-template.md`

## Output Location
Save the generated solution approach section to:
`specifications/documents-generator/technical-documention/{{DATE}}-v{{VERSION}}/05-solution-approach.md`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- DETAILED_STEPS: Phase 1: Setup Spring Boot project and database schema, Phase 2: Implement core payment processing, Phase 3: PSP integration, Phase 4: Testing and deployment
- KEY_DESIGN_DECISIONS: Use synchronous processing for real-time confirmation, implement circuit breaker pattern, choose populator over direct mapping

This would create the file at:
`specifications/documents-generator/technical-documention/2025-11-04-v1.0/05-solution-approach.md`

## Quality Guidelines
- Organize phases logically with clear dependencies
- Include testing strategy throughout the implementation
- Document rollback and contingency procedures
- Consider team capacity and skill requirements
- Include integration and deployment considerations from the start

---

# Section 6: Generate Tech Design Diagrams Section

## Role
You are a technical architect specializing in creating detailed system diagrams using Mermaid syntax for complex technical systems.

## Task
Generate the diagrams section with comprehensive Mermaid diagrams showing component interactions, sequence flows, and state transitions.

## Input Parameters
- `{{DATE}}` - Date for the document (format: YYYY-MM-DD)
- `{{VERSION}}` - Version number for the document (e.g., "1.0", "2.1")
- `{{COMPONENT_DIAGRAM}}` - Mermaid diagram code showing detailed component interactions and relationships
- `{{SEQUENCE_DIAGRAM}}` - Mermaid sequence diagram illustrating step-by-step execution order
- `{{STATE_DIAGRAM}}` - Mermaid state diagram for workflows with multiple states (optional)

## Instructions
1. Create detailed component interaction diagrams showing all system parts
2. Develop sequence diagrams that illustrate the complete flow of operations
3. Include state diagrams for complex workflows with multiple states and transitions
4. Use appropriate Mermaid syntax for each diagram type
5. Ensure diagrams are complementary and show different perspectives of the system
6. Include error flows and edge cases in the diagrams where relevant

## Output Requirements
The generated content must:
- Follow markdown formatting standards
- Include properly formatted Mermaid diagrams that render correctly
- Show comprehensive system interactions from multiple perspectives
- Use consistent naming and styling across all diagrams
- Include both happy path and error scenario flows

## Template to Follow
Use the diagrams template located at `specifications/templates/paragraphs/06-diagrams-template.md`

## Output Location
Save the generated diagrams section to:
`specifications/documents-generator/technical-documention/{{DATE}}-v{{VERSION}}/06-diagrams.md`

## Mermaid Syntax Guidelines
- Component diagrams: Use `graph TB` with subgraphs for logical grouping
- Sequence diagrams: Use `sequenceDiagram` with proper participant definitions
- State diagrams: Use `stateDiagram-v2` for modern state machine syntax
- Class diagrams: Use `classDiagram` for object relationships when needed

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- COMPONENT_DIAGRAM: Detailed component interaction showing UI, API, services, and external systems
- SEQUENCE_DIAGRAM: Complete payment processing sequence from initiation to completion
- STATE_DIAGRAM: Payment state transitions (initiated → processing → completed/failed)

This would create the file at:
`specifications/documents-generator/technical-documention/2025-11-04-v1.0/06-diagrams.md`

## Quality Guidelines
- Keep diagrams focused and not overly complex
- Use descriptive names for all components and interactions
- Include decision points and conditional flows
- Show both synchronous and asynchronous interactions
- Group related components using subgraphs for better organization

---

# Section 7: Generate Tech Design Data Model Schema Update Section

## Role
You are a database architect specializing in documenting data model changes, schema migrations, and data transformation strategies.

## Task
Generate the data model schema update section that documents entity changes, relationships, and comprehensive migration plans.

## Input Parameters
- `{{DATE}}` - Date for the document (format: YYYY-MM-DD)
- `{{VERSION}}` - Version number for the document (e.g., "1.0", "2.1")
- `{{MODEL_CHANGES}}` - Detailed description of new entities, modified entities, and relationship changes
- `{{MIGRATION_PLAN}}` - Step-by-step plan for migrating existing data including rollback strategies

## Instructions
1. Document all new entities with complete field specifications
2. Detail modifications to existing entities including new fields and relationships
3. Provide complete SQL schema changes with CREATE, ALTER statements
4. Create comprehensive migration plan with phases and timeline
5. Include data validation and integrity checks
6. Plan for both zero-downtime and maintenance window scenarios
7. Provide rollback procedures and recovery strategies

## Output Requirements
The generated content must:
- Follow markdown formatting standards
- Include complete SQL statements for all schema changes
- Provide realistic time estimates based on data volumes
- Include comprehensive data validation procedures
- Address performance impact during migration
- Be executable by database administrators

## Template to Follow
Use the data model template located at `specifications/templates/paragraphs/07-data-model-template.md`

## Output Location
Save the generated data model section to:
`specifications/documents-generator/technical-documention/{{DATE}}-v{{VERSION}}/07-data-model-schema-update.md`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- MODEL_CHANGES: New PaymentModel entity, modified OrderModel with payment_reference field, new relationships between orders and payments
- MIGRATION_PLAN: 3-phase migration with schema updates, data backfill, and application deployment

This would create the file at:
`specifications/documents-generator/technical-documention/2025-11-04-v1.0/07-data-model-schema-update.md`

## Quality Guidelines
- Include complete field specifications with data types and constraints
- Provide realistic time estimates based on data volumes
- Include index creation strategies for optimal performance
- Plan for data integrity validation throughout migration
- Consider impact on application performance during migration
- Document all assumptions about existing data quality

---

# Section 8: Generate Tech Design API Documentation Section

## Role
You are an API documentation specialist creating documentation based EXCLUSIVELY on actual implemented APIs in the repository.

## Task
Generate API documentation using ONLY endpoints, request/response formats, and error handling found in the actual Onshore Airline GDS codebase. DO NOT INVENT API ENDPOINTS.

## Repository Sources to Analyze
Before generating content, you MUST scan:
- `microservices/services/*/src/main/java/` - For actual Spring Boot controller endpoints
- `microservices/cloud/api-gateway/` and `config-repo/api-gateway.yml` - For gateway routes exposed to the frontend
- `onshore-frontend/src/services/` - For how the frontend calls the APIs
- `specifications/api-specs/` - For existing API specifications
- `microservices/services/*/src/main/resources/` - For OpenAPI specifications or configurations
- `microservices/services/*/src/test/` - For API test examples showing request/response formats

## Input Parameters
- `{{DATE}}` - Date for the document (format: YYYY-MM-DD)
- `{{VERSION}}` - Version number for the document (e.g., "1.0", "2.1")
- `{{ENDPOINT_DETAILS}}` - Complete endpoint specifications including URLs, methods, headers, parameters, and response codes
- `{{EXAMPLE_REQUEST}}` - Sample request payload in JSON format with realistic data
- `{{EXAMPLE_RESPONSE}}` - Sample response payload in JSON format with all relevant fields

## Instructions
1. **FIRST**: Extract actual API endpoints from controller classes and route files
2. Document ONLY endpoints that exist in the codebase with exact paths and methods
3. Use ONLY authentication patterns found in actual security configuration
4. Extract request/response examples from existing test files or controller annotations
5. Reference specific controller classes and method names for each endpoint
6. If API documentation is missing, state "API NOT IMPLEMENTED" rather than creating fictional endpoints
7. Include line numbers and file paths for all API references

## Output Requirements
The generated content must:
- Follow markdown formatting standards and REST API documentation best practices
- Include complete endpoint specifications that developers can implement from
- Provide realistic JSON examples that validate against actual schemas
- Document all error conditions with appropriate HTTP status codes
- Include authentication, rate limiting, and security considerations
- Be suitable for both internal developers and external API consumers

## Template to Follow
Use the API documentation template located at `specifications/templates/paragraphs/08-api-documentation-template.md`

## Output Location
Save the generated API documentation section to:
`specifications/documents-generator/technical-documention/{{DATE}}-v{{VERSION}}/08-api-documentation.md`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- ENDPOINT_DETAILS: POST /api/v1/payments for payment initiation, GET /payments/{id} for status, authentication via Bearer tokens
- EXAMPLE_REQUEST: Payment request with orderId, amount, currency, customer details
- EXAMPLE_RESPONSE: Payment response with paymentId, status, timestamps, and metadata

This would create the file at:
`specifications/documents-generator/technical-documention/2025-11-04-v1.0/08-api-documentation.md`

## Quality Guidelines
- Use consistent HTTP status codes following REST conventions
- Include comprehensive error response formats with actionable messages
- Provide examples that demonstrate real-world usage scenarios
- Document any async operations and callback mechanisms
- Include OpenAPI/Swagger specifications where applicable
- Consider API client SDK generation requirements

---

# Section 9: Generate Tech Design Code Bootstrap Section

## Role
You are a senior software engineer creating code examples based STRICTLY on actual implementation patterns found in the repository.

## Task
Generate code bootstrap section using ONLY patterns, configurations, and implementations already present in the Onshore Airline GDS codebase. DO NOT INVENT CODE PATTERNS.

## Repository Sources to Analyze
Before generating content, you MUST scan:
- `microservices/services/*/src/main/java/` - For actual controller, service, and configuration patterns
- `microservices/services/*/src/main/resources/` and `config-repo/` - For configuration file examples
- `microservices/common-lib/` - For shared code patterns
- `.github/instructions/backend/` - For established coding patterns and rules
- `.github/instructions/frontend/` - For frontend implementation patterns
- `microservices/**/pom.xml` - For actual dependency configurations
- `onshore-frontend/package.json` and `onshore-frontend/src/` - For frontend build, dependency and code patterns

## Input Parameters
- `{{DATE}}` - Date for the document (format: YYYY-MM-DD)
- `{{VERSION}}` - Version number for the document (e.g., "1.0", "2.1")
- `{{CONTROLLER_CODE}}` - Sample REST controller implementation with proper annotations and error handling
- `{{SERVICE_CODE}}` - Sample service implementation with business logic, validation, and external service integration
- `{{CONFIGURATION_CODE}}` - Configuration classes, properties, and setup code for the solution

## Instructions
1. **FIRST**: Extract actual code patterns from existing controller and service implementations
2. Use ONLY design patterns already implemented in the codebase
3. Copy configuration examples from actual `application.properties` or configuration classes
4. Reference specific source files and line numbers for all code examples
5. Use ONLY dependencies and annotations found in existing code
6. If implementation patterns are missing, state "IMPLEMENTATION NOT FOUND" rather than creating examples
7. Include exact file paths for all referenced code patterns

## Output Requirements
The generated content must:
- Follow markdown formatting standards and code highlighting
- Include complete, production-ready code examples
- Demonstrate proper use of frameworks and libraries
- Include error handling for all failure scenarios
- Follow organizational coding standards and patterns
- Be immediately usable by development teams

## Template to Follow
Use the code bootstrap template located at `specifications/templates/paragraphs/09-code-bootstrap-template.md`

## Output Location
Save the generated code bootstrap section to:
`specifications/documents-generator/technical-documention/{{DATE}}-v{{VERSION}}/09-code-bootstrap.md`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- CONTROLLER_CODE: PaymentController with POST/GET endpoints, validation, exception handling
- SERVICE_CODE: PaymentService with business logic, PSP integration, transaction management
- CONFIGURATION_CODE: PaymentConfiguration with beans, properties, and conditional configurations

This would create the file at:
`specifications/documents-generator/technical-documention/2025-11-04-v1.0/09-code-bootstrap.md`

## Quality Guidelines
- Use modern Java features and Spring Boot best practices
- Include comprehensive exception handling with appropriate HTTP status codes
- Implement proper logging with structured log messages
- Use dependency injection and avoid tight coupling
- Include performance considerations (connection pooling, caching, etc.)
- Demonstrate security best practices (input validation, sanitization)
- Provide configuration examples for multiple environments

---

# Section 10: Generate Tech Design POC Insights Section

## Role
You are a senior technical consultant specializing in analyzing proof-of-concept results and providing actionable technical recommendations.

## Task
Generate the POC insights section that summarizes validation findings, performance metrics, and identified limitations with actionable recommendations.

## Input Parameters
- `{{DATE}}` - Date for the document (format: YYYY-MM-DD)
- `{{VERSION}}` - Version number for the document (e.g., "1.0", "2.1")
- `{{POC_FINDINGS}}` - Summary of proof-of-concept validation results, key discoveries, and technical validation outcomes
- `{{PERFORMANCE_METRICS}}` - Detailed performance measurements, benchmarks, and comparison with requirements
- `{{LIMITATIONS}}` - Identified technical, business, and infrastructure limitations with impact assessment

## Instructions
1. Summarize POC objectives and scope clearly
2. Document key technical discoveries and validation results
3. Provide quantitative performance metrics with proper context
4. Compare results against original requirements and expectations
5. Be honest about limitations and potential risks identified
6. Include cost implications and business impact assessment
7. Provide clear go/no-go recommendations with supporting data

## Output Requirements
The generated content must:
- Follow markdown formatting standards
- Include quantitative performance data with statistical context
- Provide honest assessment of limitations and risks
- Compare results against business and technical requirements
- Include actionable recommendations for production implementation
- Consider scalability, cost, and operational factors

## Template to Follow
Use the POC insights template located at `specifications/templates/paragraphs/10-poc-insights-template.md`

## Output Location
Save the generated POC insights section to:
`specifications/documents-generator/technical-documention/{{DATE}}-v{{VERSION}}/10-poc-insights.md`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- POC_FINDINGS: Successful PSP integration validation, API performance meets requirements, fraud detection capabilities confirmed
- PERFORMANCE_METRICS: 247ms average response time, 120 TPS sustained throughput, 99.98% availability
- LIMITATIONS: Batch processing constraints, geographic restrictions, rate limiting during peak hours

This would create the file at:
`specifications/documents-generator/technical-documention/2025-11-04-v1.0/10-poc-insights.md`

## Quality Guidelines
- Include specific metrics with confidence intervals where available
- Document test scenarios and conditions that generated the metrics
- Provide context for limitations (severity, workarounds, mitigation strategies)
- Include lessons learned and best practices discovered during POC
- Consider both technical and business implications of findings
- Provide clear recommendations for production readiness

---

# Section 11: Generate Tech Design Data Migration Section

## Role
You are a database migration specialist creating migration documentation based STRICTLY on actual repository database schemas and migration files.

## Task
Generate data migration documentation using ONLY database schemas, migration scripts, and data models found in the Onshore Airline GDS repository. DO NOT INVENT DATABASE SCHEMAS.

## Repository Sources to Analyze
Before generating content, you MUST scan:
- `specifications/database/` - For actual database schema documentation
- `init-databases.sql` and `docker-compose.yml` - For database provisioning
- `microservices/services/*/src/main/resources/db/changelog/` - For existing Liquibase migration files
- `microservices/services/*/src/main/java/com/onshore/services/model/` - For actual JPA entity definitions and data models
- `.github/instructions/database/` - For established database migration practices

## Input Parameters
- `{{DATE}}` - Date for the document (format: YYYY-MM-DD)
- `{{VERSION}}` - Version number for the document (e.g., "1.0", "2.1")
- `{{MANUAL_MIGRATION}}` - One-time migration steps, procedures, and maintenance window requirements
- `{{DYNAMIC_MIGRATION}}` - Live data synchronization approach, zero-downtime strategies, and continuous migration techniques
- `{{SAMPLE_SCRIPTS}}` - Automation scripts, SQL statements, and validation queries for the migration

## Instructions
1. Plan for both maintenance window and zero-downtime migration scenarios
2. Provide realistic time estimates based on actual data volumes
3. Include comprehensive data validation and integrity verification steps
4. Document rollback procedures with specific timelines and triggers
5. Address performance impact during migration and optimization strategies
6. Include monitoring and alerting requirements during migration process
7. Plan for incremental migration of large datasets with progress tracking

## Output Requirements
The generated content must:
- Follow markdown formatting standards
- Include complete, executable SQL scripts and automation procedures
- Provide realistic timeline estimates based on data volumes and system capacity
- Include comprehensive validation procedures and success criteria
- Address both technical and operational aspects of the migration
- Be executable by database administrators and operations teams

## Template to Follow
Use the data migration template located at `specifications/templates/paragraphs/11-data-migration-template.md`

## Output Location
Save the generated data migration section to:
`specifications/documents-generator/technical-documention/{{DATE}}-v{{VERSION}}/11-data-migration.md`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- MANUAL_MIGRATION: 4-hour maintenance window for schema updates and historical data migration of 2.5M records
- DYNAMIC_MIGRATION: Zero-downtime approach using triggers and dual-write patterns for continuous operations
- SAMPLE_SCRIPTS: SQL scripts for payment reference generation, historical payment record creation, validation queries

This would create the file at:
`specifications/documents-generator/technical-documention/2025-11-04-v1.0/11-data-migration.md`

## Quality Guidelines
- Test all migration scripts thoroughly in staging environments
- Include progress monitoring and checkpoint procedures
- Plan for database performance optimization during and after migration
- Document all assumptions about existing data quality and consistency
- Include communication plans for stakeholders during migration events
- Provide detailed rollback procedures with decision criteria

---

# Section 12: Generate Tech Design Security, Performance, and Resiliency Section

## Role
You are a senior systems architect documenting security, performance, and resiliency based STRICTLY on actual repository implementation and configuration.

## Task
Generate security, performance, and resiliency documentation using ONLY measures, configurations, and mechanisms found in the Onshore Airline GDS repository. DO NOT INVENT SECURITY POLICIES.

## Repository Sources to Analyze
Before generating content, you MUST scan:
- `microservices/cloud/api-gateway/` and `microservices/services/user-service/` - For authentication/authorization and gateway security
- `config-repo/` - For centralized security and performance configuration
- `.github/instructions/` - For established security and performance rules
- `microservices/services/*/src/main/java/com/onshore/services/config/` - For security configuration classes
- `docker-compose.yml` - For infrastructure security and performance settings
- `microservices/services/*/src/main/resources/application*.yaml` - For actual performance configurations

## Input Parameters
- `{{DATE}}` - Date for the document (format: YYYY-MM-DD)
- `{{VERSION}}` - Version number for the document (e.g., "1.0", "2.1")
- `{{SECURITY_MEASURES}}` - Security implementations, compliance requirements, authentication, authorization, and risk mitigation strategies
- `{{PERFORMANCE_OPTIMIZATIONS}}` - Performance enhancements, caching strategies, database optimizations, and scalability measures
- `{{RESILIENCY_MECHANISMS}}` - Fallback mechanisms, circuit breakers, disaster recovery, and fault tolerance patterns

## Instructions
1. Address security comprehensively including authentication, authorization, data protection, and compliance
2. Include specific performance metrics, targets, and optimization strategies
3. Document all failure scenarios and corresponding recovery procedures
4. Provide concrete implementation examples for critical patterns (circuit breakers, retry logic)
5. Include monitoring and alerting strategies for each operational aspect
6. Address disaster recovery with specific RTO/RPO targets
7. Consider operational procedures and incident response plans

## Output Requirements
The generated content must:
- Follow markdown formatting standards with proper code highlighting
- Include specific, measurable targets for performance and availability
- Provide implementation examples for critical resilience patterns
- Address compliance requirements with specific standards (PCI DSS, GDPR, SOX)
- Include comprehensive monitoring and operational procedures
- Be actionable for security, development, and operations teams

## Template to Follow
Use the security, performance, and resiliency template located at `specifications/templates/paragraphs/12-security-performance-resiliency-template.md`

## Output Location
Save the generated security, performance, and resiliency section to:
`specifications/documents-generator/technical-documention/{{DATE}}-v{{VERSION}}/12-security-performance-resiliency.md`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- SECURITY_MEASURES: OAuth 2.0 authentication, PCI DSS compliance, AES-256 encryption, role-based access control
- PERFORMANCE_OPTIMIZATIONS: Redis caching, connection pooling, database optimization, auto-scaling policies
- RESILIENCY_MECHANISMS: Circuit breaker pattern, retry logic, multi-region failover, disaster recovery procedures

This would create the file at:
`specifications/documents-generator/technical-documention/2025-11-04-v1.0/12-security-performance-resiliency.md`

## Quality Guidelines
- Include specific compliance frameworks and certification requirements
- Provide quantifiable performance targets with measurement methods
- Document all failure modes and recovery procedures
- Include code examples for critical resilience patterns
- Address both technical and operational security measures
- Consider trade-offs between security, performance, and usability

---

# Section 13: Generate Tech Design Additional References Section

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
Use the additional references template located at `specifications/templates/paragraphs/13-additional-references-template.md`

## Output Location
Save the generated additional references section to:
`specifications/documents-generator/technical-documention/{{DATE}}-v{{VERSION}}/13-additional-references.md`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- REFERENCES: PSP API documentation, internal architecture docs, Spring Boot references, monitoring tools, training materials, compliance guidelines

This would create the file at:
`specifications/documents-generator/technical-documention/2025-11-04-v1.0/13-additional-references.md`

## Quality Guidelines
- Verify all links are accessible and current
- Group references logically by purpose and audience
- Include internal knowledge base articles and wikis
- Provide context for tools and frameworks that may be new to readers
- Include contact information for subject matter experts
- Consider including QR codes for mobile-accessible resources
- Update references regularly as documentation evolves
```
