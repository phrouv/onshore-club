---
description: E2E folder structure, organization, and file naming conventions

applyTo: "e2e/**"
---

# E2E Folder Structure and Organization

## Directory Overview

```
e2e/
├── package.json                    # E2E dependencies and scripts
├── playwright.config.ts            # Playwright test configuration
├── tsconfig.json                   # TypeScript configuration for E2E
├── features/                       # Gherkin BDD feature files
│   ├── 001-*.feature              # Sequential feature files
│   ├── EP###-*.feature            # Epic-based feature files
│   └── US###-*.feature            # User story feature files
├── tests/                          # Playwright test specifications
│   ├── 001-*.spec.ts              # Sequential test specs
│   ├── EP###-*.spec.ts            # Epic-based test specs
│   └── example-*.spec.ts          # Example/reference tests
├── utils/                          # Reusable test utilities
│   ├── auth.ts                    # Authentication utilities
│   ├── cleanup.ts                 # UI cleanup utilities
│   ├── constants.ts               # Shared constants
│   ├── ui-helpers.ts              # UI interaction helpers
│   ├── features.ts                # Feature management utilities
│   ├── templates.ts               # Template utilities
│   ├── locales.ts                 # Locale utilities
│   ├── markets.ts                 # Market utilities
│   ├── sites.ts                   # Site utilities
│   ├── site-configurations.ts   # Market config utilities
│   └── selectors/                 # Selector constants
├── test-data/                      # Test data files
│   ├── site-configurations/     # Market config test data
│   └── tmp/                       # Temporary test files
├── test-results/                   # Test execution results (gitignored)
├── playwright-report/              # HTML test reports (gitignored)
└── .gitignore                      # E2E-specific gitignore
```

## File Naming Conventions

### Feature Files (`features/`)

Feature files use Gherkin syntax and follow these naming patterns:

#### Pattern 1: Sequential Number

```
[sequential-number]-[kebab-case-description].feature
```

**Examples:**

- `001-frontend-homepage-sso.feature`
- `002-locale-management.feature`

#### Pattern 2: Epic-Based

```
EP[epic-number]-[US-numbers]-[kebab-case-description].feature
```

**Examples:**

- `EP007-US005-US014-feature-and-property-management.feature`
- `EP007-US032-inline-property-management.feature`
- `EP007-US033-US034-inline-form-import-export.feature`

#### Pattern 3: User Story-Based

```
US[story-number]-[kebab-case-description].feature
```

**Examples:**

- `US015-024-wsa-management.feature`

**Naming Rules:**

- Use lowercase for the entire filename
- Use kebab-case (hyphens) for multi-word descriptions
- Include epic/user story numbers when applicable
- Be descriptive but concise
- File extension: `.feature`

### Test Specification Files (`tests/`)

Test specs use TypeScript and Playwright, following similar patterns:

#### Pattern 1: Sequential Number

```
[sequential-number]-[kebab-case-description].spec.ts
```

**Examples:**

- `001-frontend-homepage-sso.spec.ts`

#### Pattern 2: Epic and User Story-Based

```
EP[epic-number]-US[story-number]-[kebab-case-description].spec.ts
```

**Examples:**

- `EP007-US032-inline-property-management.spec.ts`
- `EP007-US015-US024-wsa-management.spec.ts`
- `EP007-US033-US034-market-config-inline-form-import-export.spec.ts`

#### Pattern 3: Example/Reference Tests

```
example-[purpose].spec.ts
```

**Examples:**

- `example-using-utilities.spec.ts`

**Naming Rules:**

- Use kebab-case for the entire filename
- Match the corresponding feature file name (without .feature extension)
- Include epic and user story numbers when applicable
- File extension: `.spec.ts`

### Utility Files (`utils/`)

Utility files follow domain-based naming:

```
[domain-name].ts
```

**Examples:**

- `auth.ts` - Authentication and login
- `cleanup.ts` - Cleanup operations
- `constants.ts` - Shared constants
- `ui-helpers.ts` - Generic UI helpers
- `features.ts` - Feature management
- `templates.ts` - Template management
- `locales.ts` - Locale management
- `markets.ts` - Market management
- `sites.ts` - Site management
- `site-configurations.ts` - Market config management

**Documentation Files (in documentation/e2e/):**

- `README.md` - Main utilities documentation
- `FEATURES_GUIDE.md` - Detailed feature utilities guide
- `ASSERTIONS_AND_FIXTURES_GUIDE.md` - Assertions and fixtures guide
- `UTILS_INVENTORY.md` - Complete inventory of all utilities
- `ASSERTION_REFACTORING_GUIDE.md` - Guide for refactoring assertions

**Naming Rules:**

- Use kebab-case for multi-word filenames
- Use singular form for domain names (e.g., `locale.ts` not `locales.ts`) - **Exception: current files use plural**
- File extension: `.ts`
- Documentation: `.md`

## File Organization Rules

### 1. Feature Files

**Location:** `e2e/features/`  
**Rules:** See `bdd.instructions.md` for structure and conventions

### 2. Test Specification Organization

**Location:** `e2e/tests/`

**Structure:**

```typescript
/**
 * E2E Test: [Test Suite Name]
 *
 * Description of what this test suite covers
 * Reference: [Epic/US numbers]
 * Feature File: [corresponding .feature file]
 */

import { test, expect } from "@playwright/test";
import { login } from "../utils/auth";
import /* other utilities */ "../utils/[domain]";

test.describe("[Test Suite Name]", () => {
  test.beforeEach(async ({ page }) => {
    // Setup
  });

  test.afterEach(async ({ page }) => {
    // Cleanup
  });

  test("AC1: [Scenario description]", async ({ page }) => {
    // Test implementation
  });

  test("AC2: [Scenario description]", async ({ page }) => {
    // Test implementation
  });
});
```

**Rules:**

- One spec file per feature file
- Use `test.describe()` for logical grouping
- Include JSDoc header with context
- Reference acceptance criteria in test names
- Use utilities from `e2e/utils/` (avoid duplication)
- Include proper setup and teardown

### 3. Utility Files Organization

**Location:** `e2e/utils/`  
**Rules:** See `e2e-utils.instructions.md` for structure, standards, and patterns

### 4. Test Data Organization

**Location:** `e2e/test-data/`

**Structure:**

```
test-data/
├── [domain]/              # Domain-specific test data
│   ├── [scenario].json   # Test data files
│   └── README.md         # Documentation
└── tmp/                   # Temporary files (gitignored)
```

**Rules:**

- Organize by domain or feature
- Use JSON format for structured data
- Include README for complex data structures
- Don't commit sensitive data
- Use descriptive filenames
- Document data schema and purpose

## Configuration Files

### playwright.config.ts

**Purpose:** Playwright test runner configuration

**Key Settings:**

- `testDir: './tests'` - Test specifications location
- `fullyParallel: true` - Run tests in parallel
- `retries: 1` - Retry failed tests once
- `workers: 1` - Number of parallel workers
- `reporter: [['html'], ['junit']]` - Test reporters
- `baseURL: 'http://localhost:4001'` - Base URL for tests

**Rules:**

- Keep configuration minimal and clear
- Document any custom settings
- Environment-specific settings via env vars

### package.json

**Purpose:** E2E project dependencies and scripts

**Key Scripts:**

```json
{
  "scripts": {
    "test": "npx playwright test",
    "show-report": "npx playwright show-report",
    "start-dev": "nohup ../scripts/start-dev.sh &",
    "stop-dev": "../scripts/stop-dev.sh"
  }
}
```

**Rules:**

- Keep scripts simple and well-named
- Document complex scripts in README
- Pin dependency versions
- Separate dev and prod dependencies

### tsconfig.json

**Purpose:** TypeScript configuration for E2E tests

**Rules:**

- Extend from root tsconfig if available
- Include type definitions for Playwright
- Set strict type checking
- Configure path aliases if needed

## Gitignore Rules

**File:** `e2e/.gitignore`

**Always Ignore:**

```gitignore
node_modules/
test-results/
playwright-report/
playwright/.cache/
test-data/tmp/
*.log
.env.local
.DS_Store
```

**Never Ignore:**

- Feature files (`features/`)
- Test specifications (`tests/`)
- Utility files (`utils/`)
- Configuration files (`*.config.ts`, `package.json`)
- Shared test data (non-sensitive)

## Best Practices

### 1. File Creation Workflow

When creating new E2E tests:

1. **Start with Feature File:**

   ```bash
   # Create feature file following BDD conventions
   touch e2e/features/EP###-US###-feature-name.feature
   ```

2. **Generate Test Spec:**

   ```bash
   # Create corresponding spec file
   touch e2e/tests/EP###-US###-feature-name.spec.ts
   ```

3. **Use Utilities:**

   ```typescript
   // Import and use existing utilities
   import { login } from "../utils/auth";
   import { navigateToTemplates } from "../utils/templates";
   ```

4. **Create New Utilities if Needed:**
   ```bash
   # Add to existing utility file or create new one
   # Document in documentation/e2e/README.md
   ```

### 2. Maintaining Feature-Spec Alignment

- Feature files and spec files should have matching names
- Each scenario in feature file should have corresponding test
- Keep acceptance criteria IDs consistent
- Update both files when requirements change

### 3. Utility File Guidelines

**When to create a new utility file:**

- New domain/resource type (e.g., new entity in system)
- 5+ related functions for a specific domain
- Clear separation of concerns

**When to add to existing utility file:**

- Related to existing domain
- Extends current functionality
- 1-4 related functions

### 4. Documentation Requirements

- **Feature Files:** See `bdd.instructions.md`
- **Test Specs:** JSDoc header with context, reference to feature file
- **Utility Files:** See `e2e-utils.instructions.md`

### 5. Test Data Management

**Principles:**

- Keep test data close to tests using it
- Use factories/builders for complex data
- Avoid hardcoded data in test specs
- Clean up test data after test execution

**Example Structure:**

```
test-data/
├── site-configurations/
│   ├── valid-config.json
│   ├── invalid-config.json
│   └── README.md
└── tmp/
    └── [temporary files during test run]
```

## Common Patterns

### Pattern 1: Feature + Spec Pair

```
e2e/
├── features/
│   └── EP007-US032-inline-property-management.feature
└── tests/
    └── EP007-US032-inline-property-management.spec.ts
```

### Pattern 2: Multi-Story Feature

```
e2e/
├── features/
│   └── EP007-US015-US024-wsa-management.feature
└── tests/
    └── EP007-US015-US024-wsa-management.spec.ts
```

### Pattern 3: Example/Reference Tests

```
e2e/
└── tests/
    └── example-using-utilities.spec.ts
```

### Pattern 4: Shared Test Data

```
e2e/
├── test-data/
│   └── site-configurations/
│       ├── template-not-persisted.json
│       └── valid-market-config.json
└── tests/
    └── EP007-US033-market-config.spec.ts
```

## Validation Checklist

Before committing E2E changes:

- [ ] Feature file exists in `e2e/features/` with proper naming
- [ ] Spec file exists in `e2e/tests/` with matching name
- [ ] All scenarios from feature file have corresponding tests
- [ ] Tests use utilities from `e2e/utils/` where applicable
- [ ] New utilities are documented in `documentation/e2e/README.md`
- [ ] Test data is properly organized in `test-data/`
- [ ] No sensitive data is committed
- [ ] Tests follow the established patterns
- [ ] JSDoc comments are complete
- [ ] Files follow naming conventions

## Migration Guidelines

### Moving from Old Structure to New Structure

If restructuring existing tests:

1. **Audit Existing Files:**

   - List all feature and spec files
   - Identify naming inconsistencies
   - Check for missing documentation

2. **Rename Files:**

   ```bash
   # Follow new naming conventions
   git mv old-name.spec.ts EP007-US032-new-name.spec.ts
   ```

3. **Extract Utilities:**

   - Identify duplicate code
   - Create utility functions
   - Update tests to use utilities
   - Document new utilities

4. **Update Documentation:**
   - Update README files
   - Add migration notes
   - Document breaking changes

## Related Documentation to keep up-to-date

- **BDD Guidelines:** `.github/instructions/e2e/bdd.instructions.md`
- **E2E Test Execution:** `.github/instructions/e2e/e2e.instructions.md`
- **E2E Utilities:** `.github/instructions/e2e/e2e-utils.instructions.md`
- **Utilities Documentation:** `documentation/e2e/README.md`
- **Feature Utilities Guide:** `e2e/utils/FEATURES_GUIDE.md`
