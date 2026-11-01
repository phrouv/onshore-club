---
description: Rules and Best Practices for E2E test execution And generation

applyTo: "**"
---

# E2E

## Step-by-Step Instructions

### Step 1: Environment Setup
1. Start the whole stack using the start script:
   ```bash
   nohup ./services.sh -up &
   ```
2. Ensure all services are running correctly:
   - Wait for 1 minute to allow services to initialize
   - API gateway health check:
     ```bash
     curl -f http://localhost:5000/actuator/health || echo "API gateway is not healthy"
     ```
   - Service registry (all services registered):
     ```bash
     curl -f http://localhost:8761 || echo "Service registry is not healthy"
     ```
   - Each microservice (ports 5001-5010, notification 8094):
     ```bash
     for p in 5001 5002 5003 5004 5005 5006 5007 5008 5009 5010 8094; do curl -sf http://localhost:$p/actuator/health >/dev/null || echo "Service on $p is not healthy"; done
     ```
   - Frontend service health check (Vite default port, adjust if configured differently):
     ```bash
     curl -f http://localhost:5173 || echo "Frontend service is not healthy"
     ```

### Step 2: Test Execution with Playwright MCP Server

1. **Execute tests using the Playwright MCP server** (NOT the Playwright library):
   - Use the MCP Playwright tools to navigate and interact with the application
   - Follow each scenario step-by-step as described in the feature file
   - Respect all the Given-When-Then, if there is a difference in the outcome of the test, report an error
   - Document all interactions, selectors, and assertions
   - Record any edge cases or error conditions encountered

### Step 3: Generate Reusable Playwright Spec File

Based on your test execution experience, create a comprehensive Playwright test specification file:

1. **Create the spec file** in `e2e/tests/`
2. **File naming convention**: `[feature-number]-[feature-name].spec.ts`

### Step 4: Environment Teardown

1. **Teardown Docker Compose**:
Stop the stack using the stop script:

```bash
./services.sh -s
```

## Requirements and Best Practices

### Test Quality Standards

- **Reliability**: Tests should be deterministic and not flaky
- **Maintainability**: Use clear selectors and avoid brittle element targeting
- **Coverage**: Cover all scenarios mentioned in the feature file
- **Performance**: Optimize wait times and use appropriate Playwright waiting strategies

### Selector Strategy

1. **Priority order for selectors**:
   - `data-testid` attributes (highest priority)
   - ARIA roles and labels
   - Semantic HTML elements
   - Text content (stable text only)
   - CSS selectors (lowest priority)

### Error Handling

- Include try-catch blocks for critical operations
- Add custom error messages for failed assertions
- Handle timeout scenarios gracefully
- Document known limitations or browser-specific issues

### Documentation

- Add JSDoc comments for complex test logic
- Include references to the original feature file
- Document any assumptions or prerequisites
- Note any deviations from the feature file requirements

## Expected Deliverables

1. **Execution Report**: Summary of test execution results
2. **Generated Spec File**: Complete Playwright test specification
3. **Issues Identified**: Any bugs or inconsistencies found during testing

## Important Notes

- **Do NOT run existing tests** - only generate new ones based on your manual execution
- **Use MCP Playwright server exclusively** - do not use the standard Playwright library
- **Document all assumptions** made during test creation
- **Ensure cross-browser compatibility** considerations are noted
- **Follow the project's existing code style** and conventions
- **Follow exactly the same flow you learned executing the tests by the MCP** when you generate the spec file. The generate tests in the spec file should respect 100% what was discovered during by the MCP
