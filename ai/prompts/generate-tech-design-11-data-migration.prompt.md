# Generate Tech Design Data Migration Section

## Role

You are a database migration specialist creating migration documentation based STRICTLY on actual repository database schemas and migration files.

## Task

Generate data migration documentation using ONLY database schemas, migration scripts, and data models found in the Onshore Airline GDS repository. DO NOT INVENT DATABASE SCHEMAS.

## Repository Sources to Analyze

Before generating content, you MUST scan:

- `specifications/database/` - For actual database schema documentation
- `init-databases.sql` and `docker-compose.yml` - For database provisioning
- `microservices/services/*/src/main/resources/db/changelog/` - For existing Liquibase migration files
- `microservices/services/*/src/main/java/com/onshore/services/model/` - For actual JPA entity definitions and data models
- `ai/rules/database/` - For established database migration practices

## Input Parameters

- `{{DATE}}` - Date for the document (format: YYYY-MM-DD)
- `{{VERSION}}` - Version number for the document (e.g., "1.0", "2.1")
- `{{MANUAL_MIGRATION}}` - One-time migration steps, procedures, and maintenance window requirements
- `{{DYNAMIC_MIGRATION}}` - Live data synchronization approach, zero-downtime strategies, and continuous migration techniques
- `{{SAMPLE_SCRIPTS}}` - Automation scripts, SQL statements, and validation queries for the migration

## Instructions

1. Plan for both maintenance window and zero-downtime migration scenarios
2. Provide realistic time estimates based on actual data volumes
3. Include comprehensive data validation and integrity verification steps
4. Document rollback procedures with specific timelines and triggers
5. Address performance impact during migration and optimization strategies
6. Include monitoring and alerting requirements during migration process
7. Plan for incremental migration of large datasets with progress tracking

## Output Requirements

The generated content must:

- Follow markdown formatting standards
- Include complete, executable SQL scripts and automation procedures
- Provide realistic timeline estimates based on data volumes and system capacity
- Include comprehensive validation procedures and success criteria
- Address both technical and operational aspects of the migration
- Be executable by database administrators and operations teams

## Template to Follow

Use the data migration template located at `specifications/templates/paragraphs/11-data-migration-template.md`

## Output Location

Save the generated data migration section to:
`specifications/documents-generator/technical-documention/{{DATE}}-v{{VERSION}}/11-data-migration.md`

## Example Usage

Input:

- DATE: 2025-11-04
- VERSION: 1.0
- MANUAL_MIGRATION: 4-hour maintenance window for schema updates and historical data migration of 2.5M records
- DYNAMIC_MIGRATION: Zero-downtime approach using triggers and dual-write patterns for continuous operations
- SAMPLE_SCRIPTS: SQL scripts for payment reference generation, historical payment record creation, validation queries

This would create the file at:
`specifications/documents-generator/technical-documention/2025-11-04-v1.0/11-data-migration.md`

## Quality Guidelines

- Test all migration scripts thoroughly in staging environments
- Include progress monitoring and checkpoint procedures
- Plan for database performance optimization during and after migration
- Document all assumptions about existing data quality and consistency
- Include communication plans for stakeholders during migration events
- Provide detailed rollback procedures with decision criteria
