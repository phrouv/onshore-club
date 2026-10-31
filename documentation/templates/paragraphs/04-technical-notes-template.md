# Technical Notes Template

## Purpose

Generate the technical notes section that covers standards, considerations, and dependencies.

## Template Variables

- `{{STANDARDS_ALIGNMENT}}` - How the solution aligns with coding standards and organizational practices
- `{{KEY_CONSIDERATIONS}}` - Performance, security, and resiliency factors
- `{{DEPENDENCIES}}` - External libraries, systems, or resources required

## Output Format

```markdown
## Technical Notes

### Standards Alignment

{{STANDARDS_ALIGNMENT}}

### Key Considerations

{{KEY_CONSIDERATIONS}}

### Dependencies

{{DEPENDENCIES}}
```

## Example Usage

```markdown
## Technical Notes

### Standards Alignment

All APIs must follow RESTful principles and be stateless, supporting JSON responses. The solution adheres to our organization's microservices architecture patterns, using Spring Boot for backend services and React for frontend components. Security implementation follows OAuth 2.0 standards with HTTPS for all communications.

### Key Considerations

- **Performance**: API response times must be under 300ms for 95% of requests
- **Security**: All payment data must be encrypted in transit and at rest, with PCI DSS compliance
- **Resiliency**: Implement circuit breakers for external service calls with automatic retry mechanisms
- **Scalability**: Design for horizontal scaling to handle peak traffic loads

### Dependencies

- **External Libraries**: Spring Boot 3.x, Spring Security, Jackson for JSON processing
- **External Systems**: Payment Service Provider X API, PostgreSQL database
- **Infrastructure**: Docker containers, Kubernetes orchestration, Redis for caching
- **Monitoring**: Prometheus for metrics, ELK stack for logging
```

## Guidelines

- Reference specific coding standards and design patterns used in the organization
- Include quantifiable performance requirements where possible
- Address security, compliance, and regulatory requirements
- List all external dependencies with versions where applicable
- Consider operational aspects like monitoring, logging, and deployment
- Include any architectural constraints or organizational policies that must be followed
