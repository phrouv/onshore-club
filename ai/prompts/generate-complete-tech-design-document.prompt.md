# Generate Complete Tech Design Document

## Role
You are a senior technical documentation coordinator responsible for orchestrating the creation of comprehensive technical design documents based strictly on existing repository information.

## Task
Generate a complete technical design document by coordinating the creation of all 13 sections using the individual paragraph prompts and templates. ALL CONTENT MUST BE DERIVED FROM EXISTING REPOSITORY DOCUMENTATION - DO NOT INVENT OR ASSUME ANY INFORMATION.

## Input Parameters
- `{{DATE}}` - Date for the document (format: YYYY-MM-DD)
- `{{VERSION}}` - Version number for the document (e.g., "1.0", "2.1")
- `{{PROJECT_NAME}}` - Name of the technical project or integration (must exist in repository)
- `{{DOCUMENT_CONTEXT}}` - Overall context and requirements for the technical design (must reference existing documentation)

## Repository Information Requirements
Before generating any content, you MUST scan and analyze the following repository sources:

### Primary Documentation Sources
- `specifications/` - Complete specifications folder
- `specifications/epics/` - Epic specifications and requirements
- `specifications/backlog/` - Backlog items and user stories
- `specifications/api-specs/` - API specifications
- `specifications/database/` - Database models and schemas

### Technical Implementation Sources
- `microservices/services/` - Spring Boot microservices source code
- `microservices/cloud/` - API gateway, config server, service registry
- `microservices/common-lib/` - Shared backend code
- `onshore-frontend/` - React frontend source code
- `docker-compose.yml` - Infrastructure configuration
- `microservices/README.md` - Project overview and setup

### Configuration and Standards
- `ai/rules/` - Development rules and standards
- `config-repo/` - Centralized service configuration

## Process Overview
This prompt coordinates the execution of 13 individual section prompts to create a comprehensive technical design document based EXCLUSIVELY on repository information. Each section will be generated in sequence and saved to a versioned folder structure.

**STRICT REQUIREMENTS**:
1. **NO INVENTION**: Do not create, assume, or invent any technical details not present in the repository
2. **REPOSITORY-ONLY**: All information must be extracted from existing documentation, code, and configuration files
3. **EVIDENCE-BASED**: Reference specific files, line numbers, or documentation sections for all claims
4. **GAP IDENTIFICATION**: Clearly identify where information is missing rather than inventing it

## Execution Sequence
1. **Document Title** (Section 01) - `generate-tech-design-01-document-title.prompt.md`
2. **Context** (Section 02) - `generate-tech-design-02-context.prompt.md`
3. **Overall Architecture** (Section 03) - `generate-tech-design-03-overall-architecture.prompt.md`
4. **Technical Notes** (Section 04) - `generate-tech-design-04-technical-notes.prompt.md`
5. **Solution Approach** (Section 05) - `generate-tech-design-05-solution-approach.prompt.md`
6. **Diagrams** (Section 06) - `generate-tech-design-06-diagrams.prompt.md`
7. **Data Model Schema Update** (Section 07) - `generate-tech-design-07-data-model.prompt.md`
8. **API Documentation** (Section 08) - `generate-tech-design-08-api-documentation.prompt.md`
9. **Code Bootstrap** (Section 09) - `generate-tech-design-09-code-bootstrap.prompt.md`
10. **POC Insights** (Section 10) - `generate-tech-design-10-poc-insights.prompt.md`
11. **Data Migration** (Section 11) - `generate-tech-design-11-data-migration.prompt.md`
12. **Security, Performance, Resiliency** (Section 12) - `generate-tech-design-12-security-performance-resiliency.prompt.md`
13. **Additional References** (Section 13) - `generate-tech-design-13-additional-references.prompt.md`

## Output Structure
All sections will be created in a versioned folder:
```
specifications/documents-generator/technical-documention/{{DATE}}-v{{VERSION}}/
├── 01-document-title.md
├── 02-context.md
├── 03-overall-architecture.md
├── 04-technical-notes.md
├── 05-solution-approach.md
├── 06-diagrams.md
├── 07-data-model-schema-update.md
├── 08-api-documentation.md
├── 09-code-bootstrap.md
├── 10-poc-insights.md
├── 11-data-migration.md
├── 12-security-performance-resiliency.md
└── 13-additional-references.md
```

## Final Assembly
After all sections are generated, create a master document:
```
specifications/documents-generator/technical-documention/{{DATE}}-v{{VERSION}}/COMPLETE-TECH-DESIGN.md
```

## Usage Instructions

### MANDATORY FIRST STEP: Repository Analysis
Before generating any sections, you MUST:
1. **Scan Documentation**: Use `semantic_search`, `grep_search`, and `read_file` tools to analyze all documentation folders
2. **Analyze Codebase**: Review application structure in `microservices/` and `onshore-frontend/` for actual implementation details
3. **Extract Dependencies**: Read actual `package.json`, `pom.xml`, and configuration files for real dependencies
4. **Identify Patterns**: Review `/ai/rules/` for established coding standards and architectural patterns
5. **Map Existing APIs**: Find actual API endpoints and specifications in codebase and documentation

### Section Generation Process
1. Execute repository analysis phase first
2. Generate each section using ONLY repository-derived information
3. Each section prompt will be called in sequence with repository context
4. Review each generated section for repository accuracy
5. The final master document will combine all sections with source references
6. Update cross-references to point to actual repository files

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- PROJECT_NAME: Payment Service Provider Integration
- DOCUMENT_CONTEXT: Integration with external PSP for enhanced payment processing capabilities

This would create a complete tech design document set in:
`specifications/documents-generator/technical-documention/2025-11-04-v1.0/`

## Quality Assurance
After generation:
- **Repository Verification**: Confirm all information comes from repository sources with file references
- **No Fictional Content**: Verify no invented technical details, APIs, or configurations are present
- **Gap Documentation**: Ensure missing information is clearly marked as "NOT FOUND IN REPOSITORY" 
- **Source Traceability**: Each technical claim must reference specific repository files or documentation
- **Mermaid Accuracy**: Diagrams must reflect actual architecture found in repository code/config
- **Existing Standards**: Follow only coding standards and patterns found in `/ai/rules/` folder
- **Real Dependencies**: List only dependencies found in actual `package.json`, `pom.xml`, or config files
