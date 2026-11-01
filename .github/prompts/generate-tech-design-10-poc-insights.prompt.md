---
agent: agent
description: "Generate the POC insights section of a tech design document"
---

# Generate Tech Design POC Insights Section

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
Use the POC insights template located at `../../documentation`

## Output Location
Save the generated POC insights section to:
`../../documentation`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- POC_FINDINGS: Successful PSP integration validation, API performance meets requirements, fraud detection capabilities confirmed
- PERFORMANCE_METRICS: 247ms average response time, 120 TPS sustained throughput, 99.98% availability
- LIMITATIONS: Batch processing constraints, geographic restrictions, rate limiting during peak hours

This would create the file at:
`../../documentation`

## Quality Guidelines
- Include specific metrics with confidence intervals where available
- Document test scenarios and conditions that generated the metrics
- Provide context for limitations (severity, workarounds, mitigation strategies)
- Include lessons learned and best practices discovered during POC
- Consider both technical and business implications of findings
- Provide clear recommendations for production readiness
