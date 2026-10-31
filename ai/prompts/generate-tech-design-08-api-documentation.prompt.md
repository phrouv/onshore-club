# Generate Tech Design API Documentation Section

## Role
You are an API documentation specialist creating documentation based EXCLUSIVELY on actual implemented APIs in the repository.

## Task
Generate API documentation using ONLY endpoints, request/response formats, and error handling found in the actual Onshore Airline GDS codebase. DO NOT INVENT API ENDPOINTS.

## Repository Sources to Analyze
Before generating content, you MUST scan:
- `microservices/services/*/src/main/java/` - For actual Spring Boot controller endpoints
- `microservices/cloud/api-gateway/` and `config-repo/api-gateway.yml` - For gateway routes exposed to the frontend
- `onshore-frontend/src/services/` - For how the frontend calls the APIs
- `../../documentation` - For existing API specifications
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
Use the API documentation template located at `../../documentation`

## Output Location
Save the generated API documentation section to:
`../../documentation`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- ENDPOINT_DETAILS: POST /api/v1/payments for payment initiation, GET /payments/{id} for status, authentication via Bearer tokens
- EXAMPLE_REQUEST: Payment request with orderId, amount, currency, customer details
- EXAMPLE_RESPONSE: Payment response with paymentId, status, timestamps, and metadata

This would create the file at:
`../../documentation`

## Quality Guidelines
- Use consistent HTTP status codes following REST conventions
- Include comprehensive error response formats with actionable messages
- Provide examples that demonstrate real-world usage scenarios
- Document any async operations and callback mechanisms
- Include OpenAPI/Swagger specifications where applicable
- Consider API client SDK generation requirements
