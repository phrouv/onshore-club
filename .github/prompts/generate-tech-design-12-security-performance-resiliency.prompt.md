---
agent: agent
description: "Generate the security, performance, and resiliency section of a tech design document"
---

# Generate Tech Design Security, Performance, and Resiliency Section

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

Use the security, performance, and resiliency template located at `../../documentation`

## Output Location

Save the generated security, performance, and resiliency section to:
`../../documentation`

## Example Usage

Input:

- DATE: 2025-11-04
- VERSION: 1.0
- SECURITY_MEASURES: OAuth 2.0 authentication, PCI DSS compliance, AES-256 encryption, role-based access control
- PERFORMANCE_OPTIMIZATIONS: Redis caching, connection pooling, database optimization, auto-scaling policies
- RESILIENCY_MECHANISMS: Circuit breaker pattern, retry logic, multi-region failover, disaster recovery procedures

This would create the file at:
`../../documentation`

## Quality Guidelines

- Include specific compliance frameworks and certification requirements
- Provide quantifiable performance targets with measurement methods
- Document all failure modes and recovery procedures
- Include code examples for critical resilience patterns
- Address both technical and operational security measures
- Consider trade-offs between security, performance, and usability
