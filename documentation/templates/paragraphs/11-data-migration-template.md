# Data Migration Template

## Purpose

Generate documentation for data migration strategies, scripts, and procedures.

## Template Variables

- `{{MANUAL_MIGRATION}}` - One-time migration steps and procedures
- `{{DYNAMIC_MIGRATION}}` - Live data synchronization approach and strategies
- `{{SAMPLE_SCRIPTS}}` - Automation scripts and database migration code

## Output Format

```markdown
## Data Migration Section

### Manual Migration Strategy

{{MANUAL_MIGRATION}}

### Dynamic Migration Approach

{{DYNAMIC_MIGRATION}}

### Migration Scripts

```sql
{{SAMPLE_SCRIPTS}}
```

```

## Example Usage

```markdown
## Data Migration Section

### Manual Migration Strategy

**Migration Overview**:
The data migration involves moving existing order and customer data to support the new payment processing system. This includes creating payment records for historical orders and updating customer preferences.

**Pre-Migration Requirements**:
1. Complete database backup of production environment
2. Verify data integrity in source systems
3. Set up staging environment with production data copy
4. Prepare rollback procedures and scripts
5. Schedule maintenance window (estimated 4 hours)

**Migration Steps**:

**Phase 1: Schema Updates (Maintenance Window)**
1. Apply Liquibase migration scripts to create new payment-related tables
2. Add new columns to existing tables (orders.payment_reference, customers.preferred_payment_method)
3. Create database indexes for optimal query performance
4. Update application configuration to use new schema

**Phase 2: Historical Data Migration (Maintenance Window)**
5. Generate payment references for all existing orders without payment data
6. Create payment records for completed historical orders
7. Set default payment methods for existing customers based on order history
8. Validate referential integrity between orders, customers, and payments

**Phase 3: Application Deployment (Rolling Deployment)**
9. Deploy new application version with backward compatibility
10. Enable new payment functionality with feature flags
11. Run automated smoke tests to verify system functionality
12. Monitor system performance and error rates

**Data Volume Estimates**:
- Orders to migrate: ~2.5M records
- Customers to update: ~850K records  
- New payment records: ~2.5M records
- Estimated processing time: 2.5 hours

**Validation Procedures**:
- Row count verification between source and target tables
- Data sampling and manual verification (1% random sample)
- Business logic validation (payment totals match order totals)
- Performance testing with migrated data

### Dynamic Migration Approach

**Zero-Downtime Strategy**:
For environments requiring continuous availability, implement a phased migration approach using database triggers and dual-write patterns.

**Phase 1: Preparation (No Downtime)**
1. Deploy shadow tables alongside existing schema
2. Implement database triggers to capture changes during migration
3. Set up data validation jobs to ensure consistency

**Phase 2: Incremental Migration (No Downtime)**
4. Migrate data in small batches during low-traffic periods
5. Use application-level dual writes for new transactions
6. Continuously validate data consistency between old and new systems

**Phase 3: Cutover (Minimal Downtime - 5 minutes)**
7. Stop application writes temporarily
8. Process remaining trigger-captured changes
9. Switch application to use new schema
10. Remove triggers and cleanup shadow tables

**Monitoring and Validation**:
- Real-time data consistency checks
- Application performance monitoring during migration
- Automated rollback triggers if error thresholds exceeded
- Business metric tracking to ensure no revenue impact

**Sync Strategy Details**:
```sql
-- Create trigger for real-time sync during migration
CREATE OR REPLACE FUNCTION sync_payment_data()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' OR TG_OP = 'UPDATE' THEN
        INSERT INTO payment_sync_queue (table_name, operation, record_id, created_at)
        VALUES (TG_TABLE_NAME, TG_OP, NEW.id, NOW());
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO payment_sync_queue (table_name, operation, record_id, created_at)
        VALUES (TG_TABLE_NAME, TG_OP, OLD.id, NOW());
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;
```

### Migration Scripts

```sql
-- 1. Create payment reference for existing orders
UPDATE orders 
SET payment_reference = CONCAT('HIST-', LPAD(id::text, 10, '0'), '-', EXTRACT(EPOCH FROM created_at)::bigint)
WHERE payment_reference IS NULL
  AND status IN ('COMPLETED', 'SHIPPED', 'DELIVERED');

-- 2. Create historical payment records for completed orders
INSERT INTO payments (id, order_id, customer_id, amount, currency, status, psp_transaction_id, created_at, updated_at)
SELECT 
    CONCAT('PAY-HIST-', o.id) as id,
    o.id as order_id,
    o.customer_id,
    o.total_amount as amount,
    COALESCE(o.currency, 'USD') as currency,
    'COMPLETED' as status,
    CONCAT('HIST-TXN-', o.id) as psp_transaction_id,
    o.created_at,
    o.updated_at
FROM orders o 
WHERE o.status IN ('COMPLETED', 'SHIPPED', 'DELIVERED')
  AND o.total_amount > 0
  AND NOT EXISTS (SELECT 1 FROM payments p WHERE p.order_id = o.id);

-- 3. Set preferred payment method based on historical data
WITH customer_payment_history AS (
    SELECT 
        customer_id,
        CASE 
            WHEN COUNT(*) > 10 THEN 'credit_card'
            WHEN AVG(total_amount) > 100 THEN 'credit_card' 
            ELSE 'debit_card'
        END as suggested_method
    FROM orders 
    WHERE status IN ('COMPLETED', 'SHIPPED', 'DELIVERED')
      AND created_at > NOW() - INTERVAL '12 months'
    GROUP BY customer_id
)
UPDATE customers c
SET preferred_payment_method = h.suggested_method
FROM customer_payment_history h
WHERE c.id = h.customer_id 
  AND c.preferred_payment_method IS NULL;

-- 4. Create indexes for optimal performance
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_payments_order_id ON payments(order_id);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_payments_customer_id ON payments(customer_id);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_payments_status_created ON payments(status, created_at);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_orders_payment_reference ON orders(payment_reference);

-- 5. Data validation queries
-- Verify payment records match order totals
SELECT 'Payment/Order Amount Mismatch' as check_name,
       COUNT(*) as mismatch_count
FROM orders o
JOIN payments p ON o.id = p.order_id
WHERE ABS(o.total_amount - p.amount) > 0.01;

-- Verify all completed orders have payment records
SELECT 'Orders Missing Payments' as check_name,
       COUNT(*) as missing_count  
FROM orders o
LEFT JOIN payments p ON o.id = p.order_id
WHERE o.status IN ('COMPLETED', 'SHIPPED', 'DELIVERED')
  AND o.total_amount > 0
  AND p.id IS NULL;

-- 6. Rollback script (if needed)
-- DROP TABLE IF EXISTS payments;
-- ALTER TABLE orders DROP COLUMN IF EXISTS payment_reference;
-- ALTER TABLE customers DROP COLUMN IF EXISTS preferred_payment_method;
```

**Script Execution Order**:

1. Run schema migration scripts first
2. Execute data migration in batches of 10,000 records
3. Create indexes after data migration for optimal performance  
4. Run validation scripts to ensure data integrity
5. Keep rollback scripts ready but test thoroughly before use

**Performance Optimization**:

- Use `VACUUM ANALYZE` after large data inserts
- Monitor database connection pool during migration
- Consider temporarily increasing database resources during migration
- Use `pg_stat_progress_create_index` to monitor index creation

```

## Guidelines

- Plan for both maintenance window and zero-downtime scenarios
- Include detailed rollback procedures and timelines
- Provide realistic time estimates based on data volumes
- Include comprehensive data validation steps
- Consider performance impact during migration
- Document all assumptions and prerequisites
- Include monitoring and alerting during migration process  
- Test migration scripts thoroughly in staging environment
- Plan for incremental migration of large datasets
- Document post-migration cleanup and optimization steps
