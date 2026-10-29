---
agent: agent
---
# OpenAPI Contract Generator Prompt

You are an expert API designer. Generate a complete OpenAPI 3.0 specification based on the feature description provided. Follow these guidelines:

## Output Directory

- Save the generated OpenAPI spec in `specifications/api-specs/[feature-name]-api.yaml`

## Input Requirements
Analyze the feature description and extract:
- **Entities/Resources**: What data objects are involved?
- **Operations**: What actions can be performed (CRUD operations)?
- **Business Logic**: What are the validation rules and constraints?
- **User Roles**: Who can access what endpoints?
- **Integration Points**: What external systems are involved?

If you don't have enough information, ask clarifying questions to gather more details.

## Output Requirements
Generate a complete OpenAPI 3.2.0 specification that includes:

### 1. Basic Information
```yaml
openapi: 3.2.0
info:
  title: [Feature Name] API
  description: [Brief description of the feature]
  version: 1.0.0
  contact:
    name: API Team
    email: api-team@company.com
servers:
  - url: https://api.company.com/v1
    description: Production server
  - url: https://staging-api.company.com/v1
    description: Staging server
```

### 2. Authentication & Security
- Define appropriate security schemes (Bearer token, API key, etc.)
- Apply security requirements to endpoints based on user roles

### 3. Paths & Operations
For each endpoint, include:
- **HTTP method** and **path**
- **Summary** and **description**
- **Parameters** (path, query, header)
- **Request body** with schema validation
- **Response schemas** for all status codes (200, 201, 400, 401, 403, 404, 422, 500)
- **Tags** for logical grouping
- **operationId** for code generation

### 4. Data Models (Components/Schemas)
- Define reusable schemas for all entities
- Include proper data types, formats, and validation rules
- Use composition (allOf, oneOf, anyOf) where appropriate
- Define enum values for constrained fields
- Include examples for complex objects

### 5. Error Handling
Standard error response schema:
```yaml
components:
  schemas:
    Error:
      type: object
      required:
        - code
        - message
      properties:
        code:
          type: string
          description: Error code
        message:
          type: string
          description: Human-readable error message
        details:
          type: array
          items:
            type: string
          description: Additional error details
```

### 6. Validation Rules
- Required vs optional fields
- String length constraints (minLength, maxLength)
- Numeric constraints (minimum, maximum)
- Pattern validation for emails, phone numbers, etc.
- Array constraints (minItems, maxItems)

### 7. Best Practices
- Use consistent naming conventions (camelCase for JSON properties)
- Include pagination for list endpoints (limit, offset, total)
- Add filtering and sorting parameters where applicable
- Use appropriate HTTP status codes
- Include rate limiting headers in responses
- Add deprecation notices for endpoints being phased out

## Example Usage

**Input**: "Create a user management feature that allows admins to create, update, view, and delete users. Users have email, name, role (admin/user), and status (active/inactive). Only admins can manage users."

**Expected Output**: Complete OpenAPI spec with:
- POST /users (create user)
- GET /users (list users with pagination)
- GET /users/{userId} (get user by ID)
- PUT /users/{userId} (update user)
- DELETE /users/{userId} (delete user)
- User schema with validation rules
- Admin-only security requirements
- Proper error responses

## Additional Considerations
- Ask clarifying questions if the feature description is ambiguous
- Suggest additional endpoints that might be needed
- Consider bulk operations where appropriate
- Include webhook endpoints if real-time notifications are needed
- Add health check and metrics endpoints for operational concerns

---
