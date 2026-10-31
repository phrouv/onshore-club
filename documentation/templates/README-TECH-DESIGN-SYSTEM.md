# Tech Design Documentation System

This folder contains templates and prompts for generating comprehensive technical design documentation.

## Overview

The system provides a structured approach to creating technical design documents through:

1. **Main Template**: `tech-design-template.md` - Complete template with all sections
2. **Paragraph Templates**: Individual templates for each section (01-13)
3. **Generation Prompts**: AI prompts for generating each section
4. **Output Structure**: Organized folder structure for generated documents

## Generated Documents Structure

Generated documents are organized in versioned folders:

```
documentation/documents-generator/functional-documention/
└── {DATE}-v{VERSION}/
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
    ├── 13-additional-references.md
    └── COMPLETE-TECH-DESIGN.md
```

## Usage

1. Use individual prompts to generate specific sections
2. Use the master prompt to generate complete documents
3. All sections use Mermaid diagrams for technical illustrations
4. Generated content follows organizational standards and best practices

## Section Details

| Section | Template | Prompt | Description |
|---------|----------|---------|-------------|
| 01 | Document Title | generate-tech-design-01-document-title.prompt.md | Document header and metadata |
| 02 | Context | generate-tech-design-02-context.prompt.md | Problem description and objectives |
| 03 | Overall Architecture | generate-tech-design-03-overall-architecture.prompt.md | High-level system architecture |
| 04 | Technical Notes | generate-tech-design-04-technical-notes.prompt.md | Standards, considerations, dependencies |
| 05 | Solution Approach | generate-tech-design-05-solution-approach.prompt.md | Implementation steps and decisions |
| 06 | Diagrams | generate-tech-design-06-diagrams.prompt.md | Detailed technical diagrams |
| 07 | Data Model | generate-tech-design-07-data-model.prompt.md | Database schema and migration |
| 08 | API Documentation | generate-tech-design-08-api-documentation.prompt.md | REST API specifications |
| 09 | Code Bootstrap | generate-tech-design-09-code-bootstrap.prompt.md | Sample implementations |
| 10 | POC Insights | generate-tech-design-10-poc-insights.prompt.md | Proof of concept findings |
| 11 | Data Migration | generate-tech-design-11-data-migration.prompt.md | Migration strategies and scripts |
| 12 | Security/Performance/Resiliency | generate-tech-design-12-security-performance-resiliency.prompt.md | Operational considerations |
| 13 | Additional References | generate-tech-design-13-additional-references.prompt.md | Supporting documentation links |
