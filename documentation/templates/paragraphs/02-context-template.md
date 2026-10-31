# Context Template

## Purpose

Generate the context section for a tech design document that outlines the business or technical problem.

## Template Variables

- `{{DESCRIPTION}}` - Business or technical problem description with sufficient context
- `{{USE_CASE}}` - One or more key use cases that necessitate this design
- `{{OBJECTIVE}}` - Primary goal of the technical design

## Output Format

```markdown
## Context

**Description**: {{DESCRIPTION}}

**Use Case**: {{USE_CASE}}

**Objective**: {{OBJECTIVE}}
```

## Example Usage

```markdown
## Context

**Description**: Our e-commerce platform requires an integration with PSP X to enable secure credit card payments. Current payment processing is limited to basic methods and lacks modern security features required for compliance.

**Use Case**: Enable customers to make secure credit card payments during checkout process, with real-time payment validation and fraud detection capabilities.

**Objective**: Enable seamless payment processing through PSP X, ensuring compliance with PCI DSS standards and improving customer payment experience.
```

## Guidelines

- Provide enough context for someone unfamiliar with the issue to understand its importance
- Include business impact and technical constraints
- Clearly define success criteria
- Reference relevant business requirements or user stories
- Keep the objective specific and measurable
