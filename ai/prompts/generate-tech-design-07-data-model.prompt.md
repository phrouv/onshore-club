# Generate Tech Design Data Model Schema Update Section

## Role
You are a database architect specializing in documenting data model changes, schema migrations, and data transformation strategies.

## Task
Generate the data model schema update section that documents entity changes, relationships, and comprehensive migration plans.

## Input Parameters
- `{{DATE}}` - Date for the document (format: YYYY-MM-DD)
- `{{VERSION}}` - Version number for the document (e.g., "1.0", "2.1")
- `{{MODEL_CHANGES}}` - Detailed description of new entities, modified entities, and relationship changes
- `{{MIGRATION_PLAN}}` - Step-by-step plan for migrating existing data including rollback strategies

## Instructions
1. Document all new entities with complete field specifications
2. Detail modifications to existing entities including new fields and relationships
3. Provide complete SQL schema changes with CREATE, ALTER statements
4. Create comprehensive migration plan with phases and timeline
5. Include data validation and integrity checks
6. Plan for both zero-downtime and maintenance window scenarios
7. Provide rollback procedures and recovery strategies

## Output Requirements
The generated content must:
- Follow markdown formatting standards
- Include complete SQL statements for all schema changes
- Provide realistic time estimates based on data volumes
- Include comprehensive data validation procedures
- Address performance impact during migration
- Be executable by database administrators

## Template to Follow
Use the data model template located at `../../documentation`

## Output Location
Save the generated data model section to:
`../../documentation`

## Example Usage
Input:
- DATE: 2025-11-04
- VERSION: 1.0
- MODEL_CHANGES: New PaymentModel entity, modified OrderModel with payment_reference field, new relationships between orders and payments
- MIGRATION_PLAN: 3-phase migration with schema updates, data backfill, and application deployment

This would create the file at:
`../../documentation`

## Quality Guidelines
- Include complete field specifications with data types and constraints
- Provide realistic time estimates based on data volumes
- Include index creation strategies for optimal performance
- Plan for data integrity validation throughout migration
- Consider impact on application performance during migration
- Document all assumptions about existing data quality
