# Repository-Based Tech Design Documentation - Modification Summary

## Overview

All tech design generation prompts have been modified to STRICTLY use only information from the AdminTool repository and prevent any invention of technical details.

## Key Changes Made

### 1. Master Prompt (`generate-complete-tech-design-document.prompt.md`)

- **Added mandatory repository analysis phase**
- **Defined specific documentation sources to scan**
- **Enforced strict "no invention" policy**
- **Added evidence-based requirements**

### 2. Repository Sources Mapping

Each prompt now requires scanning specific repository locations:

| Documentation Type | Repository Path | Purpose |
|-------------------|----------------|---------|
| Business Requirements | `/documentation/epics/` | Epic specifications and requirements |
| User Stories | `/documentation/backlog/` | Backlog items and acceptance criteria |
| API Specifications | `/documentation/api-specs-conception/` | Existing API documentation |
| Database Schemas | `/documentation/database/` | Data models and schema definitions |
| Technical Rules | `/ai/rules/` | Coding standards and architectural patterns |
| Application Code | `/apps/*/src/` | Actual implementation patterns |
| Dependencies | `/apps/*/package.json`, `/apps/*/pom.xml` | Real dependency lists |
| Infrastructure | `/docker-compose.yml`, `/config/` | Service and configuration definitions |

### 3. Strict Content Rules Applied to All Prompts

#### ✅ MUST DO

- Extract information from actual repository files
- Reference specific files, folders, and line numbers
- Use only implemented patterns and dependencies
- Scan documentation folders before generating content
- Identify gaps as "NOT FOUND IN REPOSITORY"

#### ❌ MUST NOT DO

- Invent technical details not in repository
- Assume business requirements
- Create fictional API endpoints
- Add dependencies not in actual files
- Make up security policies or performance metrics

### 4. Modified Prompts List

| Section | Prompt File | Key Repository Sources Added |
|---------|-------------|------------------------------|
| 01 | `generate-tech-design-01-document-title.prompt.md` | `/documentation/`, `/README.md`, `/apps.json` |
| 02 | `generate-tech-design-02-context.prompt.md` | `/documentation/epics/`, `/documentation/backlog/`, `/documentation/personae/` |
| 03 | `generate-tech-design-03-overall-architecture.prompt.md` | `/apps/`, `/docker-compose.yml`, `/apps.json` |
| 04 | `generate-tech-design-04-technical-notes.prompt.md` | `/ai/rules/`, `/apps/*/package.json`, `/apps/*/pom.xml` |
| 08 | `generate-tech-design-08-api-documentation.prompt.md` | `/apps/*/src/`, `/documentation/api-specs-conception/` |
| 09 | `generate-tech-design-09-code-bootstrap.prompt.md` | `/apps/*/src/`, `/ai/rules/backend/`, `/ai/rules/frontend/` |
| 11 | `generate-tech-design-11-data-migration.prompt.md` | `/documentation/database/`, `/apps/*/src/main/resources/db/` |
| 12 | `generate-tech-design-12-security-performance-resiliency.prompt.md` | `/config/keycloak/`, `/ai/rules/`, security configurations |

### 5. Quality Assurance Updates

Each prompt now includes:

- **Repository verification requirements**
- **Source traceability mandates**
- **Gap identification protocols**
- **File reference requirements**

### 6. Mandatory Analysis Phase

Before any content generation, prompts now require:

1. **Documentation scanning** using `semantic_search` and `grep_search`
2. **Codebase analysis** for actual implementation details
3. **Dependency extraction** from real configuration files
4. **Pattern identification** from existing code
5. **API mapping** from actual controller classes

## Usage Impact

### Before Changes

- Generated fictional technical details
- Invented business requirements
- Created non-existent API endpoints
- Assumed architectural patterns

### After Changes

- Uses only repository-derived information
- References actual files and implementations
- Identifies information gaps clearly
- Provides traceable documentation sources

## Example Usage

When generating a tech design document:

1. **Repository Analysis Phase**: Scans `/documentation/epics/` for business context
2. **Architecture Discovery**: Analyzes `/apps/` structure and `/docker-compose.yml`
3. **API Documentation**: Extracts endpoints from actual controller classes
4. **Dependency Listing**: Reads real `package.json` and `pom.xml` files
5. **Code Examples**: Uses patterns from existing service implementations

## Benefits

- **Accuracy**: All technical details match actual implementation
- **Traceability**: Every claim references specific repository files  
- **Maintainability**: Documentation stays synchronized with codebase
- **Gap Identification**: Clearly shows where information is missing
- **Evidence-Based**: All architectural decisions have repository evidence
