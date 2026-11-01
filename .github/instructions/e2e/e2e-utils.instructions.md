---
description: Rules and Best Practices for E2E test utilities and reusable functions

applyTo: "e2e/**"
---

# E2E Test Utilities

## Purpose

E2E test utilities are reusable functions that reduce code duplication and improve test maintainability. They encapsulate common patterns, UI interactions, and workflows into well-tested, documented functions.

## Utility Files Location

`e2e/utils/` - Domain-specific utilities (auth, cleanup, constants, ui-helpers, features, templates, locales, markets, sites, site-configurations)  
See `e2e-folder-structure.instructions.md` for complete directory overview

## Core Principles

### 1. DRY (Don't Repeat Yourself)

**Before:**

```typescript
test("create feature", async ({ page }) => {
  await page.getByRole("button", { name: "Create" }).click();
  await page.getByLabel("Feature ID").fill("MY_FEATURE");
  await page.getByRole("button", { name: "Save" }).click();
  await page.waitForTimeout(500);
  await expect(page.getByText("Feature created")).toBeVisible();
  // 15+ more lines for properties...
});
```

**After:**

```typescript
import { createFeatureWithProperties } from "../utils/features";

test("create feature", async ({ page }) => {
  await createFeatureWithProperties(page, "MY_FEATURE", [
    { id: "prop1", defaultValue: "value1" },
  ]);
});
```

### 2. Single Responsibility

Each utility function should do ONE thing well:

✅ **Good:**

```typescript
export async function navigateToTemplates(page: Page): Promise<void>;
export async function createTemplate(
  page: Page,
  templateId: string,
): Promise<void>;
export async function deleteTemplate(
  page: Page,
  templateId: string,
): Promise<void>;
```

❌ **Bad:**

```typescript
export async function manageTemplates(
  page: Page,
  action: "create" | "delete" | "update",
  templateId: string,
  updates?: any,
): Promise<void>;
```

### 3. Composability

Functions should be composable to build complex workflows:

```typescript
// Basic building blocks
await navigateToTemplates(page);
await createTemplate(page, "TEMPLATE_1");
await selectTemplate(page, "TEMPLATE_1");
await updateTemplate(page, "TEMPLATE_1", {
  /* updates */
});
await deleteTemplate(page, "TEMPLATE_1");
```

## Function Categories

### Navigation Functions

Functions that navigate to specific pages or sections.

**Naming Convention:** `navigateTo{Resource}(page: Page)`

**Example:**

```typescript
export async function navigateToTemplates(page: Page): Promise<void> {
  await page.getByRole("button", { name: /CONFIGURATIONS/i }).click();
  await page.getByRole("menuitem", { name: /Template/i }).click();
  await page.waitForURL(/.*template-configurations/);
  await page.waitForLoadState("networkidle");
}
```

### CRUD Functions

Functions for Create, Read, Update, Delete operations.

**Naming Convention:**

- Create: `create{Resource}(page, id, options?)`
- Read/Select: `select{Resource}(page, id)` or `{resource}Exists(page, id)`
- Update: `update{Resource}(page, id, updates)`
- Delete: `delete{Resource}(page, id, options?)`

**Example:**

```typescript
export async function createLocale(
  page: Page,
  localeId: string,
  label: string,
  options: { waitForSuccess?: boolean } = {},
): Promise<boolean>;

export async function localeExists(
  page: Page,
  localeId: string,
): Promise<boolean>;

export async function updateLocale(
  page: Page,
  localeId: string,
  updates: { label?: string },
): Promise<void>;

export async function deleteLocale(
  page: Page,
  localeId: string,
  options: { confirm?: boolean } = {},
): Promise<void>;
```

### Assertion Functions

Functions that verify expected state.

**Naming Convention:** `assert{Resource}{State}(page, id)`

**Example:**

```typescript
export async function assertTemplateVisible(
  page: Page,
  templateId: string,
): Promise<void> {
  await expect(page.getByRole("link", { name: templateId })).toBeVisible();
}

export async function assertTemplateNotVisible(
  page: Page,
  templateId: string,
): Promise<void> {
  await expect(page.getByRole("link", { name: templateId })).not.toBeVisible();
}
```

### UI Interaction Functions

Functions for common UI interactions.

**Naming Convention:** Descriptive verb-noun pattern

**Example:**

```typescript
export async function expandSection(
  page: Page,
  sectionName: string,
): Promise<void>;
export async function fillField(
  page: Page,
  fieldName: string,
  value: string,
): Promise<void>;
export async function waitForSuccessNotification(
  page: Page,
  pattern?: string | RegExp,
): Promise<void>;
export async function closeOpenDialogs(page: Page): Promise<void>;
```

## Required Standards

### 1. Type Safety

Always use TypeScript types:

```typescript
import { Page, expect } from "@playwright/test";

export async function createFeature(
  page: Page,
  featureId: string,
  options: {
    description?: string;
    tags?: string[];
    waitForSuccess?: boolean;
  } = {},
): Promise<void> {
  // Implementation
}
```

### 2. JSDoc Documentation

All exported functions MUST have JSDoc comments:

```typescript
/**
 * Create a new feature with optional properties
 *
 * This function handles the complete feature creation flow including:
 * - Opening the create form
 * - Filling in feature details
 * - Adding properties if provided
 * - Waiting for success confirmation
 *
 * @param page - Playwright Page object
 * @param featureId - Feature identifier (uppercase with underscores)
 * @param properties - Array of properties to add to the feature
 * @param options - Optional configuration
 * @param options.description - Feature description text
 * @param options.tags - Array of tags to associate with feature
 * @param options.waitForSuccess - Wait for success notification (default: true)
 *
 * @example
 * await createFeatureWithProperties(page, 'MY_FEATURE', [
 *   { id: 'prop1', defaultValue: 'value1', dataType: 'String' }
 * ]);
 */
export async function createFeatureWithProperties(
  page: Page,
  featureId: string,
  properties: Array<{
    id: string;
    defaultValue?: string;
    description?: string;
    dataType?: "String" | "Boolean" | "Integer" | "Long" | "Double" | "Json";
    isFlag?: boolean;
  }>,
  options: {
    description?: string;
    tags?: string[];
    waitForSuccess?: boolean;
  } = {},
): Promise<void>;
```

### 3. Error Handling

Include appropriate error handling and timeouts:

```typescript
export async function selectFeature(
  page: Page,
  featureId: string,
  options: {
    useSearch?: boolean;
    waitForEditForm?: boolean;
  } = {},
): Promise<void> {
  try {
    if (options.useSearch) {
      // Use search if feature list is long
      await page.getByPlaceholder("Search").fill(featureId);
      await page.waitForTimeout(TIMEOUTS.SHORT_WAIT);
    }

    const featureLink = page.getByRole("link", { name: featureId }).first();
    await featureLink.click({ timeout: TIMEOUTS.LONG_WAIT });

    if (options.waitForEditForm) {
      await page.getByText("Edit Feature").waitFor({
        state: "visible",
        timeout: TIMEOUTS.LONG_WAIT,
      });
    }
  } catch (error) {
    throw new Error(
      `Failed to select feature "${featureId}": ${error.message}`,
    );
  }
}
```

### 4. Use Shared Constants

Never hardcode timeouts, URLs, or credentials:

```typescript
import { TIMEOUTS, URLS, CREDENTIALS } from "./constants";

// ✅ Good
await page.goto(URLS.TEMPLATES);
await page.waitForTimeout(TIMEOUTS.NAVIGATION_DELAY);
await login(page, CREDENTIALS.ADMIN.username, CREDENTIALS.ADMIN.password);

// ❌ Bad
await page.goto("http://localhost:5173/#/template-configurations");
await page.waitForTimeout(300);
await login(page, "admin", "pass123");
```

### 5. Options Pattern

Use options objects for flexibility:

```typescript
// ✅ Good - Flexible and extensible
export async function createTemplate(
  page: Page,
  templateId: string,
  options: {
    waitForSuccess?: boolean;
    skipValidation?: boolean;
    timeout?: number;
  } = {},
): Promise<void>;

// ❌ Bad - Not extensible
export async function createTemplate(
  page: Page,
  templateId: string,
  waitForSuccess: boolean,
  skipValidation: boolean,
  timeout: number,
): Promise<void>;
```

### 6. Proper Waiting Strategies

Use appropriate Playwright waiting mechanisms:

```typescript
// ✅ Good - Wait for specific conditions
await page.waitForURL(/.*templates/);
await page.waitForLoadState("networkidle");
await page.getByRole("alert").waitFor({ state: "visible" });
await expect(page.getByText("Success")).toBeVisible();

// ❌ Bad - Arbitrary timeouts
await page.waitForTimeout(3000);
```

## When to Create a Utility

### Create a utility if:

✅ The code is used in 2+ test files  
✅ The workflow has 5+ lines of code  
✅ The logic is complex or error-prone  
✅ The pattern will be reused for similar features  
✅ It improves test readability significantly

### Don't create a utility if:

❌ Used only once in a single test  
❌ Too specific to one test scenario  
❌ Makes the code harder to understand  
❌ Adds unnecessary abstraction

## Utility File Organization

### Domain-Specific Files

Each domain resource gets its own file:

- `auth.ts` - Authentication flows
- `locales.ts` - Locale management
- `markets.ts` - Market management
- `sites.ts` - Site management
- `features.ts` - Feature and property management
- `templates.ts` - Template configurations
- `site-configurations.ts` - Market-specific configurations

### Cross-Cutting Concerns

Shared utilities:

- `constants.ts` - Configuration and constants
- `ui-helpers.ts` - Generic UI interactions
- `cleanup.ts` - Cleanup and teardown utilities

### Documentation to Keep Up-to-Date

- `UTILS_INVENTORY.md` - Comprehensive list of all utility functions (at e2e/ root)
- `README.md` - Overview and examples for all utilities
- `FEATURES_GUIDE.md` - Detailed guide for complex utilities

## Test Structure with Utilities

```typescript
import { test } from "@playwright/test";
import { login } from "../utils/auth";
import { createTemplate, deleteTemplate } from "../utils/templates";
import { createTestDataTracker, deleteAllTestData } from "../utils/cleanup";
import { standardAfterAll } from "../utils/test-setup";

// Test data tracking for automatic cleanup
const testData = createTestDataTracker();

test.describe("Template Management", () => {
  // Use deleteAllTestData in beforeAll to ensure clean state
  test.beforeAll(async ({ browser }) => {
    await deleteAllTestData(browser, { verbose: true });
  });

  test.beforeEach(async ({ page }) => await login(page));

  test.afterAll(async ({ browser }) => {
    await standardAfterAll(browser, testData);
  });

  test("AC1: Create template", async ({ page }) => {
    await createTemplate(page, "TEST_TEMPLATE");
    testData.templates.push("TEST_TEMPLATE");
  });
});
```

### Important: Cleanup Strategy in Test Hooks

**`test.beforeAll`**: Always use `deleteAllTestData` to ensure a clean slate before test execution:

```typescript
test.beforeAll(async ({ browser }) => {
  await deleteAllTestData(browser, { verbose: true });
});
```

**`test.afterAll`**: Use `standardAfterAll` or `cleanupTestData` to clean up test-specific data:

```typescript
test.afterAll(async ({ browser }) => {
  await standardAfterAll(browser, testData);
  // OR
  await cleanupTestData(browser, testData, { verbose: true });
});
```

**Why this matters:**

- ❌ **DON'T**: `cleanupTestData(browser, testData, ...)` in `beforeAll` - `testData` is empty at this point
- ✅ **DO**: `deleteAllTestData(browser, ...)` in `beforeAll` - Deletes ALL resources regardless of tracking
- ✅ **DO**: `cleanupTestData(browser, testData, ...)` in `afterAll` - Cleans up tracked test data

## Updating Utilities

1. Update source file with implementation and JSDoc
2. Update UTILS_INVENTORY.md with the new function(s)
3. Update README.md with examples
4. Update related spec files
5. Test utility through specs
6. Consider backward compatibility

## Common Patterns

### Pattern: Prerequisites Creation

Create prerequisite data within utilities:

```typescript
export async function createSiteConfiguration(
  page: Page,
  marketId: string,
  options: { templateId?: string } = {},
): Promise<void> {
  // Ensure prerequisites exist
  await ensureMarketExists(page, marketId);

  if (options.templateId) {
    await ensureTemplateExists(page, options.templateId);
  }

  // Now create the market configuration
  await navigateToMarketConfigurations(page);
  // ... rest of implementation
}
```

### Pattern: Conditional Execution

Handle existence checks gracefully:

```typescript
export async function createLocale(
  page: Page,
  localeId: string,
  label: string,
  options: { skipIfExists?: boolean } = {},
): Promise<boolean> {
  const exists = await localeExists(page, localeId);

  if (exists && options.skipIfExists) {
    return false; // Already exists, skipped
  }

  // Create the locale
  // ...
  return true; // Created successfully
}
```

### Pattern: Cleanup Utilities

Batch deletion for teardown:

```typescript
export async function deleteAllTemplates(page: Page): Promise<void> {
  await navigateToTemplates(page);

  while (true) {
    const firstTemplate = page.getByRole("link").first();
    const count = await page.getByRole("link").count();

    if (count === 0) break;

    await firstTemplate.click();
    await page.getByRole("button", { name: "Delete" }).click();
    await page.getByRole("button", { name: "Confirm" }).click();
    await waitForSuccessNotification(page);
  }
}
```

## Anti-Patterns to Avoid

### ❌ God Functions

Don't create functions that do too much:

```typescript
// BAD
async function manageFeature(
  action: "create" | "update" | "delete",
  featureId: string,
  properties?: any,
  tags?: string[],
) {
  /* ... */
}
```

### ❌ Boolean Parameters

Avoid boolean flags that change behavior:

```typescript
// BAD
async function createTemplate(page: Page, id: string, withProperties: boolean);

// GOOD
async function createTemplate(page: Page, id: string);
async function createTemplateWithProperties(
  page: Page,
  id: string,
  properties: Property[],
);
```

### ❌ Hardcoded Values

Never hardcode configuration:

```typescript
// BAD
await page.goto("http://localhost:5173/#/templates");
await page.waitForTimeout(5000);

// GOOD
await page.goto(URLS.TEMPLATES);
await page.waitForTimeout(TIMEOUTS.LONG_WAIT);
```

### ❌ Overly Generic Names

Use descriptive names:

```typescript
// BAD
async function doStuff(page: Page, data: any);

// GOOD
async function createTemplateConfiguration(page: Page, templateId: string);
```

## Documentation Requirements

**UTILS_INVENTORY.md:** Comprehensive list of all functions organized by category (located at e2e/UTILS_INVENTORY.md)  
**README.md:** Purpose, functions, parameters, examples, patterns  
**Inline JSDoc:** Description, @param, @returns, @example for all exported functions  
**Quality:** Tested through specs, reliable, debuggable, maintainable

## Summary Checklist

When creating or updating utilities:

- [ ] Function has single, clear responsibility
- [ ] Uses TypeScript types for all parameters and return values
- [ ] Has complete JSDoc documentation
- [ ] Added to UTILS_INVENTORY.md with function signature and description
- [ ] Includes usage examples in README.md
- [ ] Uses shared constants from `constants.ts`
- [ ] Has appropriate error handling and timeouts
- [ ] Follows established naming conventions
- [ ] Is tested through spec files
- [ ] Includes proper waiting strategies (no arbitrary timeouts)
- [ ] Documented in README.md with examples
- [ ] Follows DRY principle - eliminates code duplication
