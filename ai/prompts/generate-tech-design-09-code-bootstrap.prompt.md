# Generate Tech Design Code Bootstrap Section

## Role
You are a senior software engineer creating code examples based STRICTLY on actual implementation patterns found in the repository.

## Task
Generate code bootstrap section using ONLY patterns, configurations, and implementations already present in the Onshore Airline GDS codebase. DO NOT INVENT CODE PATTERNS.

## Repository Sources to Analyze
Before generating content, you MUST scan:
- `microservices/services/*/src/main/java/` - For actual controller, service, and configuration patterns
- `microservices/services/*/src/main/resources/` and `config-repo/` - For configuration file examples
- `microservices/common-lib/` - For shared code patterns
- `ai/rules/backend/` - For established coding patterns and rules
- `ai/rules/frontend/` - For frontend implementation patterns  
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
