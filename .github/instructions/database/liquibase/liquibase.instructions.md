---
description: Rules and Best Practices for Liquibase database migration management
applyTo: "microservices/**"
---

# Liquibase Database Migration Rules

## Purpose

These rules define standards for managing database schema changes using Liquibase to ensure consistency, maintainability, version control, and safe database evolution across all environments. They cover migration file structure, naming conventions, change management, and deployment strategies.

## Framework and Technology Stack

- Liquibase 4.x or later for database change management
- Spring Boot integration with Liquibase auto-configuration
- YAML format for all changesets (preferred format)
- Maven/Gradle integration for build-time validation
- Multi-environment support (dev, test, staging, prod)

## Project Structure

```
src/
├── main/
│   └── resources/
│       └── db/
│           └── changelog/
│               ├── db.changelog-master.yaml        # Master changelog
│               ├── v1.0/
│               │   ├── v1.0-changelog.yaml         # Version-specific changelog
│               │   ├── 001-create-user-table.yaml  # Individual changesets
│               │   ├── 002-add-user-indexes.yaml
│               │   └── 003-insert-default-users.yaml
│               ├── v1.1/
│               │   ├── v1.1-changelog.yaml
│               │   ├── 004-add-role-table.yaml
│               │   └── 005-alter-user-table.yaml
│               └── data/
│                   ├── reference/                  # Reference data
│                   │   ├── roles.csv
│                   │   └── permissions.csv
│                   └── test/                       # Test data
│                       └── sample-users.csv
└── test/
    └── resources/
        └── db/
            └── changelog/
                └── test-data/                      # Test-specific data
                    └── integration-test-data.yaml
```

## File Naming Conventions

### Master Changelog

1. **Master File**
   - Name: `db.changelog-master.yaml`
   - Location: `src/main/resources/db/changelog/`
   - Purpose: Include all version-specific changelogs

2. **Version Changelogs**
   - Format: `v{major}.{minor}-changelog.yaml`
   - Examples: `v1.0-changelog.yaml`, `v1.1-changelog.yaml`, `v2.0-changelog.yaml`

### Individual Changesets

1. **Naming Convention**
   - Format: `{sequence}-{action}-{object}.yaml`
   - Sequence: 3-digit zero-padded number (001, 002, 003...)
   - Action: Descriptive verb (create, add, alter, drop, insert, update)
   - Object: Database object name (table, index, constraint, etc.)

2. **Examples**
   ```
   001-create-user-table.yaml
   002-create-role-table.yaml
   003-add-user-role-relationship.yaml
   004-add-user-email-index.yaml
   005-alter-user-add-created-date.yaml
   006-insert-default-roles.yaml
   007-drop-deprecated-status-column.yaml
   ```

## Changeset Structure and Best Practices

### YAML Format Changeset Structure

```yaml
databaseChangeLog:
  - changeSet:
      id: 001-create-user-table
      author: developer.name
      comment: Create users table with basic user information
      changes:
        - createTable:
            tableName: users
            columns:
              - column:
                  name: id
                  type: BIGINT
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: email
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
                    unique: true
              - column:
                  name: name
                  type: VARCHAR(100)
                  constraints:
                    nullable: false
              - column:
                  name: password_hash
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
              - column:
                  name: active
                  type: BOOLEAN
                  defaultValueBoolean: true
                  constraints:
                    nullable: false
              - column:
                  name: created_at
                  type: TIMESTAMP
                  defaultValueComputed: CURRENT_TIMESTAMP
                  constraints:
                    nullable: false
              - column:
                  name: updated_at
                  type: TIMESTAMP
                  defaultValueComputed: CURRENT_TIMESTAMP
                  constraints:
                    nullable: false
      rollback:
        - dropTable:
            tableName: users
```

## Changeset ID and Author Rules

### ID Conventions

1. **Format Requirements**
   - Must be unique across the entire project
   - Use descriptive, kebab-case format
   - Include sequence number for ordering
   - Format: `{sequence}-{descriptive-name}`

2. **Examples**
   ```yaml
   - changeSet:
       id: 001-create-user-table
       author: john.doe
   - changeSet:
       id: 002-add-user-indexes
       author: jane.smith
   - changeSet:
       id: 003-insert-default-roles
       author: system
   ```

### Author Guidelines

1. **Author Identification**
   - Use real developer names in format: `firstname.lastname`
   - For system/automated changes: use `system`
   - For data migrations: use `data-migration`
   - Be consistent across all changesets

## Master Changelog Organization

### Master Changelog Structure

```yaml
databaseChangeLog:
  # Version 1.0 - Initial Release
  - include:
      file: v1.0/v1.0-changelog.yaml
      relativeToChangelogFile: true

  # Version 1.1 - User Management Enhancements
  - include:
      file: v1.1/v1.1-changelog.yaml
      relativeToChangelogFile: true

  # Version 1.2 - Performance Improvements
  - include:
      file: v1.2/v1.2-changelog.yaml
      relativeToChangelogFile: true
```

### Version-Specific Changelog

```yaml
databaseChangeLog:
  # Schema Changes
  - include:
      file: 001-create-user-table.yaml
      relativeToChangelogFile: true
  - include:
      file: 002-create-role-table.yaml
      relativeToChangelogFile: true
  - include:
      file: 003-add-user-role-relationship.yaml
      relativeToChangelogFile: true

  # Indexes and Constraints
  - include:
      file: 004-add-user-email-index.yaml
      relativeToChangelogFile: true
  - include:
      file: 005-add-performance-indexes.yaml
      relativeToChangelogFile: true

  # Data Changes
  - include:
      file: 006-insert-default-roles.yaml
      relativeToChangelogFile: true
  - include:
      file: 007-migrate-legacy-user-data.yaml
      relativeToChangelogFile: true
```

## Change Types and Best Practices

### Schema Changes

1. **Table Creation**

   ```yaml
   databaseChangeLog:
     - changeSet:
         id: 001-create-organization-table
         author: developer.name
         comment: Create organizations table for multi-tenancy support
         changes:
           - createTable:
               tableName: organizations
               columns:
                 - column:
                     name: id
                     type: BIGINT
                     autoIncrement: true
                     constraints:
                       primaryKey: true
                       nullable: false
                 - column:
                     name: name
                     type: VARCHAR(255)
                     constraints:
                       nullable: false
                 - column:
                     name: code
                     type: VARCHAR(50)
                     constraints:
                       nullable: false
                       unique: true
                 - column:
                     name: active
                     type: BOOLEAN
                     defaultValueBoolean: true
         rollback:
           - dropTable:
               tableName: organizations
   ```

2. **Column Addition**

   ```yaml
   databaseChangeLog:
     - changeSet:
         id: 002-add-user-organization-id
         author: developer.name
         comment: Add organization_id to users table for multi-tenancy
         changes:
           - addColumn:
               tableName: users
               columns:
                 - column:
                     name: organization_id
                     type: BIGINT
                     constraints:
                       nullable: true
                       foreignKeyName: fk_user_organization
                       references: organizations(id)
         rollback:
           - dropColumn:
               tableName: users
               columnName: organization_id
   ```

3. **Index Creation**
   ```yaml
   databaseChangeLog:
     - changeSet:
         id: 003-add-user-email-index
         author: developer.name
         comment: Add index on user email for faster lookups
         changes:
           - createIndex:
               tableName: users
               indexName: idx_users_email
               columns:
                 - column:
                     name: email
         rollback:
           - dropIndex:
               tableName: users
               indexName: idx_users_email
   ```

### Data Changes

1. **Reference Data Insertion**

   ```yaml
   databaseChangeLog:
     - changeSet:
         id: 004-insert-default-roles
         author: system
         comment: Insert default system roles
         changes:
           - insert:
               tableName: roles
               columns:
                 - column:
                     name: name
                     value: ADMIN
                 - column:
                     name: description
                     value: System Administrator
                 - column:
                     name: active
                     valueBoolean: true
           - insert:
               tableName: roles
               columns:
                 - column:
                     name: name
                     value: USER
                 - column:
                     name: description
                     value: Standard User
                 - column:
                     name: active
                     valueBoolean: true
         rollback:
           - delete:
               tableName: roles
               where: "name IN ('ADMIN', 'USER')"
   ```

2. **Data from CSV File**

   ```yaml
   databaseChangeLog:
     - changeSet:
         id: 005-load-country-data
         author: data-migration
         comment: Load country reference data from CSV
         changes:
           - loadData:
               tableName: countries
               file: data/reference/countries.csv
               relativeToChangelogFile: true
               columns:
                 - column:
                     name: code
                     type: STRING
                 - column:
                     name: name
                     type: STRING
                 - column:
                     name: active
                     type: BOOLEAN
         rollback:
           - delete:
               tableName: countries
   ```

3. **Data Migration**

   ```yaml
   databaseChangeLog:
     - changeSet:
         id: 006-migrate-user-status
         author: data-migration
         comment: Migrate old status values to new format
         changes:
           - sql:
               sql: |
                 UPDATE users 
                 SET status = 'ACTIVE' 
                 WHERE old_status = 1;

                 UPDATE users 
                 SET status = 'INACTIVE' 
                 WHERE old_status = 0;
         rollback:
           - sql:
               sql: |
                 UPDATE users 
                 SET old_status = 1 
                 WHERE status = 'ACTIVE';

                 UPDATE users 
                 SET old_status = 0 
                 WHERE status = 'INACTIVE';
   ```

## Preconditions and Contexts

### Using Preconditions

1. **Table Existence Check**

   ```yaml
   databaseChangeLog:
     - changeSet:
         id: 007-add-column-if-table-exists
         author: developer.name
         preConditions:
           onFail: MARK_RAN
           tableExists:
             tableName: users
         changes:
           - addColumn:
               tableName: users
               columns:
                 - column:
                     name: last_login
                     type: TIMESTAMP
   ```

2. **Column Existence Check**
   ```yaml
   databaseChangeLog:
     - changeSet:
         id: 008-create-index-if-column-exists
         author: developer.name
         preConditions:
           onFail: MARK_RAN
           columnExists:
             tableName: users
             columnName: email
         changes:
           - createIndex:
               tableName: users
               indexName: idx_users_email
               columns:
                 - column:
                     name: email
   ```

### Context Usage

1. **Environment-Specific Changes**

   ```yaml
   databaseChangeLog:
     # Production only
     - changeSet:
         id: 009-add-production-indexes
         author: dba.team
         context: prod
         changes:
           - createIndex:
               tableName: audit_log
               indexName: idx_audit_log_date
               columns:
                 - column:
                     name: created_date

     # Test environments only
     - changeSet:
         id: 010-insert-test-data
         author: test.team
         context: test
         changes:
           - loadData:
               tableName: users
               file: data/test/sample-users.csv

     # Exclude from test
     - changeSet:
         id: 011-production-config
         author: system
         context: "data"
         changes:
           - insert:
               tableName: configuration
               columns:
                 - column:
                     name: key
                     value: max_connections
                 - column:
                     name: value
                     value: "100"
   ```

## Rollback Strategies

### Automatic Rollback

1. **Simple Operations**
   - CREATE TABLE → DROP TABLE (automatic)
   - ADD COLUMN → DROP COLUMN (automatic)
   - CREATE INDEX → DROP INDEX (automatic)

2. **Custom Rollback**

   ```yaml
   databaseChangeLog:
     - changeSet:
         id: 012-complex-data-migration
         author: data-migration
         comment: Complex user data restructuring
         changes:
           - sql:
               sql: |
                 -- Forward migration
                 INSERT INTO user_profiles (user_id, profile_data)
                 SELECT id, CONCAT(first_name, ' ', last_name)
                 FROM users;
         rollback:
           - sql:
               sql: |
                 -- Reverse migration
                 UPDATE users u 
                 SET first_name = SUBSTRING_INDEX(up.profile_data, ' ', 1),
                     last_name = SUBSTRING_INDEX(up.profile_data, ' ', -1)
                 FROM user_profiles up 
                 WHERE u.id = up.user_id;

                 DELETE FROM user_profiles;
   ```

### Rollback Testing

1. **Rollback Validation**
   ```yaml
   databaseChangeLog:
     - changeSet:
         id: 013-validated-change
         author: developer.name
         validCheckSum: 8:d41d8cd98f00b204e9800998ecf8427e
         changes:
           - createTable:
               tableName: temp_table
               # table definition here
         rollback:
           - dropTable:
               tableName: temp_table
   ```

## Data Types and Cross-Database Compatibility

### Database-Agnostic Types

1. **Recommended Types**

   ```yaml
   # Use these types for cross-database compatibility
   - column:
       name: id
       type: BIGINT # Long integers
   - column:
       name: name
       type: VARCHAR(255) # Strings
   - column:
       name: description
       type: TEXT # Long text
   - column:
       name: amount
       type: DECIMAL(19,2) # Money/precise decimals
   - column:
       name: active
       type: BOOLEAN # Boolean values
   - column:
       name: created_at
       type: TIMESTAMP # Date/time
   - column:
       name: birth_date
       type: DATE # Dates only
   ```

2. **Avoid Database-Specific Types**
   ```yaml
   # Avoid these database-specific types
   - column:
       name: data
       type: JSON # MySQL/PostgreSQL specific
   - column:
       name: id
       type: UUID # PostgreSQL specific
   - column:
       name: content
       type: LONGTEXT # MySQL specific
   ```

### Default Values

1. **Consistent Default Values**
   ```yaml
   - column:
       name: active
       type: BOOLEAN
       defaultValueBoolean: true
   - column:
       name: created_at
       type: TIMESTAMP
       defaultValueComputed: CURRENT_TIMESTAMP
   - column:
       name: version
       type: INT
       defaultValueNumeric: 1
   - column:
       name: status
       type: VARCHAR(20)
       defaultValue: PENDING
   ```

## Spring Boot Integration

### Application Configuration

1. **application.yml Configuration**

   ```yaml
   spring:
     liquibase:
       change-log: classpath:db/changelog/db.changelog-master.yaml
       contexts: dev
       default-schema: admintool
       drop-first: false
       enabled: true
       labels:
       liquibase-schema: liquibase
       liquibase-tablespace:
       parameters:
         app.version: 1.0.0
       url: ${spring.datasource.url}
       user: ${spring.datasource.username}
       password: ${spring.datasource.password}
   ```

2. **Profile-Specific Configuration**

   ```yaml
   # application-dev.yml
   spring:
     liquibase:
       contexts: dev,test-data
       drop-first: true  # Only for development

   # application-test.yml
   spring:
     liquibase:
       contexts: test

   # application-prod.yml
   spring:
     liquibase:
       contexts: prod
       drop-first: false  # Never drop in production
   ```

### Maven Integration

1. **pom.xml Plugin Configuration**
   ```xml
   <plugin>
       <groupId>org.liquibase</groupId>
       <artifactId>liquibase-maven-plugin</artifactId>
       <version>4.20.0</version>
       <configuration>
           <changeLogFile>src/main/resources/db/changelog/db.changelog-master.yaml</changeLogFile>
           <driver>org.postgresql.Driver</driver>
           <url>${liquibase.url}</url>
           <username>${liquibase.username}</username>
           <password>${liquibase.password}</password>
           <contexts>dev</contexts>
       </configuration>
       <executions>
           <execution>
               <phase>process-resources</phase>
               <goals>
                   <goal>update</goal>
               </goals>
           </execution>
       </executions>
   </plugin>
   ```

## Testing and Validation

### Changeset Testing

1. **Test Data Changesets**

   ```xml
   <changeSet id="test-001-sample-users" author="test.team" context="test">
       <comment>Insert sample users for testing</comment>
       <loadData tableName="users" file="data/test/sample-users.csv" relativeToChangelogFile="true"/>
       <rollback>
           <delete tableName="users">
               <where>email LIKE '%@test.com'</where>
           </delete>
       </rollback>
   </changeSet>
   ```

2. **Integration Test Setup**

   ```java
   @TestPropertySource(properties = {
       "spring.liquibase.contexts=test",
       "spring.liquibase.change-log=classpath:db/changelog/test-changelog.yaml"
   })
   @SpringBootTest
   class DatabaseIntegrationTest {

       @Test
       void shouldHaveCorrectSchemaAfterMigration() {
           // Test database schema
       }
   }
   ```

### Validation Commands

1. **Maven Commands**

   ```bash
   # Validate changesets
   mvn liquibase:validate

   # Check pending changes
   mvn liquibase:status

   # Generate diff between schemas
   mvn liquibase:diff

   # Rollback last N changes
   mvn liquibase:rollback -Dliquibase.rollbackCount=3

   # Update database
   mvn liquibase:update
   ```

## Performance Considerations

### Index Strategy

1. **Performance Indexes**

   ```yaml
   databaseChangeLog:
     - changeSet:
         id: perf-001-user-lookup-indexes
         author: performance.team
         comment: Add indexes for common user lookup patterns
         changes:
           # Composite index for multi-column searches
           - createIndex:
               tableName: users
               indexName: idx_users_org_status
               columns:
                 - column:
                     name: organization_id
                 - column:
                     name: status

           # Partial index for active users only
           - sql:
               dbms: postgresql
               sql: |
                 CREATE INDEX idx_users_active_email 
                 ON users (email) 
                 WHERE active = true;
   ```

2. **Foreign Key Indexes**
   ```yaml
   databaseChangeLog:
     - changeSet:
         id: perf-002-foreign-key-indexes
         author: performance.team
         comment: Add indexes on foreign key columns for join performance
         changes:
           - createIndex:
               tableName: user_roles
               indexName: idx_user_roles_user_id
               columns:
                 - column:
                     name: user_id
           - createIndex:
               tableName: user_roles
               indexName: idx_user_roles_role_id
               columns:
                 - column:
                     name: role_id
   ```

### Large Data Migrations

1. **Batch Processing**

   ```yaml
   databaseChangeLog:
     - changeSet:
         id: data-001-migrate-large-dataset
         author: data-migration
         comment: Migrate large user dataset in batches
         changes:
           - sql:
               splitStatements: true
               stripComments: true
               sql: |
                 -- Process in batches of 10,000
                 UPDATE users SET status = 'MIGRATED' 
                 WHERE id BETWEEN 1 AND 10000 AND status = 'OLD_STATUS';

                 UPDATE users SET status = 'MIGRATED' 
                 WHERE id BETWEEN 10001 AND 20000 AND status = 'OLD_STATUS';

                 -- Continue for all batches...
   ```

## Security Best Practices

### Sensitive Data Handling

1. **No Sensitive Data in Changesets**

   ```yaml
   # WRONG: Don't put sensitive data directly
   databaseChangeLog:
     - changeSet:
         id: bad-001-admin-user
         author: system
         changes:
           - insert:
               tableName: users
               columns:
                 - column:
                     name: username
                     value: admin
                 - column:
                     name: password
                     value: pass123  # NEVER DO THIS

   # RIGHT: Use environment variables or secure vaults
   databaseChangeLog:
     - changeSet:
         id: good-001-admin-user
         author: system
         changes:
           - insert:
               tableName: users
               columns:
                 - column:
                     name: username
                     value: admin
                 - column:
                     name: password
                     value: ${admin.password.hash}  # From secure config
   ```

2. **Parameterized Changesets**
   ```yaml
   databaseChangeLog:
     - changeSet:
         id: config-001-app-settings
         author: system
         comment: Insert application configuration
         changes:
           - insert:
               tableName: app_config
               columns:
                 - column:
                     name: key
                     value: jwt.secret
                 - column:
                     name: value
                     value: ${jwt.secret}
           - insert:
               tableName: app_config
               columns:
                 - column:
                     name: key
                     value: encryption.key
                 - column:
                     name: value
                     value: ${encryption.key}
   ```

## Error Handling and Monitoring

### Error Prevention

1. **Comprehensive Preconditions**

   ```yaml
   databaseChangeLog:
     - changeSet:
         id: safe-001-conditional-change
         author: developer.name
         preConditions:
           onFail: HALT
           and:
             - tableExists:
                 tableName: users
             - columnExists:
                 tableName: users
                 columnName: email
             - not:
                 columnExists:
                   tableName: users
                   columnName: email_verified
         changes:
           - addColumn:
               tableName: users
               columns:
                 - column:
                     name: email_verified
                     type: BOOLEAN
                     defaultValueBoolean: false
   ```

2. **Data Validation**
   ```yaml
   databaseChangeLog:
     - changeSet:
         id: validate-001-data-integrity
         author: data-migration
         comment: Validate data integrity after migration
         changes:
           - sql:
               sql: |
                 -- Ensure no orphaned records
                 INSERT INTO migration_validation (check_name, status)
                 SELECT 'orphaned_user_roles', 
                        CASE WHEN COUNT(*) = 0 THEN 'PASS' ELSE 'FAIL' END
                 FROM user_roles ur
                 LEFT JOIN users u ON ur.user_id = u.id
                 WHERE u.id IS NULL;
   ```

### Monitoring and Logging

1. **Changeset Documentation**
   ```yaml
   databaseChangeLog:
     - changeSet:
         id: doc-001-schema-version
         author: system
         comment: Record schema version and deployment information
         changes:
           - insert:
               tableName: schema_version
               columns:
                 - column:
                     name: version
                     value: "1.2.0"
                 - column:
                     name: deployed_at
                     valueComputed: CURRENT_TIMESTAMP
                 - column:
                     name: deployed_by
                     value: ${user.name}
                 - column:
                     name: environment
                     value: ${environment}
   ```

## Deployment Strategies

### CI/CD Integration

1. **Pipeline Validation**

   ```yaml
   # GitHub Actions example
   - name: Validate Liquibase Changes
     run: |
       mvn liquibase:validate
       mvn liquibase:status

   - name: Run Database Tests
     run: |
       mvn test -Dspring.profiles.active=test

   - name: Deploy to Staging
     run: |
       mvn liquibase:update -Dspring.profiles.active=staging
   ```

2. **Production Deployment**
   ```yaml
   - name: Production Database Update
     run: |
       mvn liquibase:updateSQL > migration-script.sql
       # Review migration-script.sql
       mvn liquibase:update -Dspring.profiles.active=prod
   ```

### Blue-Green Deployment Support

1. **Backward Compatible Changes**

   ```yaml
   databaseChangeLog:
     # Phase 1: Add new column (nullable)
     - changeSet:
         id: bg-001-add-new-column
         author: developer.name
         changes:
           - addColumn:
               tableName: users
               columns:
                 - column:
                     name: new_email_format
                     type: VARCHAR(255)

     # Phase 2: Populate new column
     - changeSet:
         id: bg-002-populate-new-column
         author: data-migration
         changes:
           - sql:
               sql: |
                 UPDATE users 
                 SET new_email_format = LOWER(email) 
                 WHERE new_email_format IS NULL;

     # Phase 3: Make new column non-nullable (after app deployment)
     - changeSet:
         id: bg-003-make-column-required
         author: developer.name
         changes:
           - addNotNullConstraint:
               tableName: users
               columnName: new_email_format

     # Phase 4: Drop old column (after full migration)
     - changeSet:
         id: bg-004-drop-old-column
         author: developer.name
         changes:
           - dropColumn:
               tableName: users
               columnName: email
   ```

## Examples

### Complete Feature Migration

```yaml
# Organization Management Feature
databaseChangeLog:
  - changeSet:
      id: org-001-create-organizations-table
      author: feature.team
      comment: Create organizations table for multi-tenancy support
      changes:
        - createTable:
            tableName: organizations
            columns:
              - column:
                  name: id
                  type: BIGINT
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: name
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
              - column:
                  name: code
                  type: VARCHAR(50)
                  constraints:
                    nullable: false
                    unique: true
              - column:
                  name: active
                  type: BOOLEAN
                  defaultValueBoolean: true
                  constraints:
                    nullable: false
              - column:
                  name: created_at
                  type: TIMESTAMP
                  defaultValueComputed: CURRENT_TIMESTAMP
                  constraints:
                    nullable: false
              - column:
                  name: updated_at
                  type: TIMESTAMP
                  defaultValueComputed: CURRENT_TIMESTAMP
                  constraints:
                    nullable: false

  - changeSet:
      id: org-002-add-user-organization-link
      author: feature.team
      comment: Add organization_id to users table
      changes:
        - addColumn:
            tableName: users
            columns:
              - column:
                  name: organization_id
                  type: BIGINT
                  constraints:
                    nullable: true
                    foreignKeyName: fk_user_organization
                    references: organizations(id)

  - changeSet:
      id: org-003-create-organization-indexes
      author: performance.team
      comment: Add performance indexes for organization queries
      changes:
        - createIndex:
            tableName: organizations
            indexName: idx_organizations_code
            columns:
              - column:
                  name: code
        - createIndex:
            tableName: users
            indexName: idx_users_organization_id
            columns:
              - column:
                  name: organization_id

  - changeSet:
      id: org-004-insert-default-organization
      author: data-migration
      comment: Create default organization for existing users
      changes:
        - insert:
            tableName: organizations
            columns:
              - column:
                  name: name
                  value: Default Organization
              - column:
                  name: code
                  value: DEFAULT
              - column:
                  name: active
                  valueBoolean: true
        - sql:
            sql: |
              UPDATE users 
              SET organization_id = (SELECT id FROM organizations WHERE code = 'DEFAULT')
              WHERE organization_id IS NULL;

  - changeSet:
      id: org-005-make-organization-required
      author: feature.team
      comment: Make organization_id required after data migration
      changes:
        - addNotNullConstraint:
            tableName: users
            columnName: organization_id
            columnDataType: BIGINT
```

## References

- [Liquibase Documentation](https://docs.liquibase.com/)
- [Liquibase Best Strategies](https://www.liquibase.org/get-started/best-practices)
- [Spring Boot Liquibase Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.liquibase)
- [Database Refactoring Best Practices](https://www.martinfowler.com/articles/evodb.html)
- [Liquibase Maven Plugin](https://docs.liquibase.com/tools-integrations/maven/home.html)
