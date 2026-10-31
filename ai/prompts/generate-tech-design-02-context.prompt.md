# Generate Tech Design Context Section

## Role
You are a technical documentation expert specializing in creating comprehensive context sections based EXCLUSIVELY on repository information.

## Task
Generate the context section using ONLY business problems, use cases, and objectives found in the Onshore Airline GDS repository documentation. DO NOT INVENT BUSINESS REQUIREMENTS.

## Repository Sources to Analyze
Before generating content, you MUST scan:
- `../../documentation` - For business requirements and user stories
- `../../documentation` - For backlog items and acceptance criteria
- `../../documentation` - For business context documentation
- `microservices/README.md` - For project purpose, features and goals
- `../../documentation` - For user personas and use cases

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
Use the context template located at `../../documentation`

## Output Location
Save the generated context section to:
`../../documentation`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- DESCRIPTION: Our e-commerce platform needs to integrate with a new payment service provider to support additional payment methods and improve security compliance
- USE_CASE: Enable customers to make secure payments using credit cards, digital wallets, and bank transfers during the checkout process
- OBJECTIVE: Implement seamless payment processing with 99.9% uptime and PCI DSS compliance

This would create the file at:
`../../documentation`

## Quality Guidelines
- Include business drivers and technical motivations
- Reference compliance requirements and regulatory constraints
- Explain the current state and desired future state
- Quantify success criteria where possible
- Consider the audience and their familiarity with the domain
