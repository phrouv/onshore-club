# Data Model Schema Update Template

## Purpose

Generate documentation for data model changes and migration strategies.

## Template Variables

- `{{MODEL_CHANGES}}` - Detailed description of changes to existing data models
- `{{MIGRATION_PLAN}}` - Step-by-step plan for migrating existing data

## Output Format

```markdown
## Data Model Schema Update

### Model Changes

{{MODEL_CHANGES}}

### Migration Plan

{{MIGRATION_PLAN}}
```

## Example Usage

```markdown
## Data Model Schema Update

### Model Changes

**New Entities:**
- `PaymentModel`: Core payment entity with fields for amount, currency, status, and timestamps
- `PaymentRequestModel`: Request payload entity with customer and payment details
- `PaymentResponseModel`: Response entity capturing PSP response data

**Modified Entities:**
- `OrderModel`: Added new field `paymentReference` (String, unique, nullable)
- `CustomerModel`: Added `preferredPaymentMethod` field for user preferences

**New Relationships:**
- One-to-Many relationship between `OrderModel` and `PaymentModel`
- Many-to-One relationship between `PaymentModel` and `CustomerModel`

**Database Schema Changes:**
```sql
-- New tables
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id),
    customer_id BIGINT REFERENCES customers(id),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Modify existing tables
ALTER TABLE orders ADD COLUMN payment_reference VARCHAR(255) UNIQUE;
ALTER TABLE customers ADD COLUMN preferred_payment_method VARCHAR(50);
```

### Migration Plan

**Phase 1: Schema Migration (Maintenance Window Required)**

1. Deploy Liquibase migration scripts to add new tables and columns
2. Create database indexes for performance optimization
3. Verify schema changes in staging environment

**Phase 2: Data Backfill (Zero Downtime)**
4. Run data migration script to populate `payment_reference` for existing orders
5. Set default `preferredPaymentMethod` for existing customers
6. Validate data integrity with automated checks

**Phase 3: Application Deployment**
7. Deploy new application version with updated models
8. Run smoke tests to verify functionality
9. Monitor application logs for any migration-related issues

**Rollback Strategy:**

- Maintain backward compatibility for 2 release cycles
- Keep migration rollback scripts ready
- Implement feature flags for gradual rollout

**Migration Scripts:**

```sql
-- Backfill payment references for existing orders
UPDATE orders 
SET payment_reference = CONCAT('MIGR-', id, '-', EXTRACT(EPOCH FROM created_at))
WHERE payment_reference IS NULL;

-- Set default payment method for existing customers
UPDATE customers 
SET preferred_payment_method = 'credit_card'
WHERE preferred_payment_method IS NULL AND created_at < '2025-01-01';
```

```

## Guidelines

- Document all new entities, modified entities, and relationships
- Include database schema changes with SQL statements
- Provide both forward and rollback migration scripts
- Consider data integrity and consistency during migration
- Plan for zero-downtime deployments where possible
- Include performance considerations for large datasets
- Test migration scripts on production-like data volumes
- Document rollback procedures and timelines
- Consider data archival strategies for large migrations
