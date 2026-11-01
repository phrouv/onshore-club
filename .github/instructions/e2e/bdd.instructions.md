---
description: Rules and Best Practices for bdd file generation

applyTo: "**"
---

# BDD

### 1. Analysis Phase

- Carefully read the entire use case description
- Identify the main feature being tested
- Extract ALL acceptance criteria from the acceptance criteria table
- Note any edge cases, error handling, and validation scenarios
- Understand the user personas and context

### 2. Feature File Structure

Generate a feature file that follows this structure:

```gherkin
Feature: [Feature Name - derived from use case title]
  As a [user persona from use case]
  I want to [main capability from use case]
  So that [business value from use case]

  Background:
    [Common setup steps that apply to all scenarios, if any]

  Scenario: [Scenario name for AC1]
    Given [precondition from AC1]
    When [action from AC1]
    Then [expected outcome from AC1]

  Scenario: [Scenario name for AC2]
    Given [precondition from AC2]
    When [action from AC2]
    Then [expected outcome from AC2]

  [Continue for ALL acceptance criteria...]

  Scenario Outline: [Use for scenarios with multiple data sets]
    Given [parameterized precondition]
    When [parameterized action]
    Then [parameterized outcome]

    Examples:
      | parameter1 | parameter2 | expected_result |
      | value1     | value2     | result1         |
      | value3     | value4     | result2         |
```

### 3. Mapping Rules

#### From Acceptance Criteria Table to Scenarios:

- **Each row in the acceptance criteria table becomes ONE scenario**
- Use the AC ID as reference in scenario naming: "AC1: [Descriptive name]"
- Map the "Given" column directly to Gherkin "Given" steps
- Map the "When" column directly to Gherkin "When" steps
- Map the "Then" column directly to Gherkin "Then" steps

#### Scenario Naming Convention:

- Format: "AC[ID]: [Brief descriptive name]"
- Example: "AC1: User accesses goal setting interface"
- Example: "AC7: System validates negative number input"

#### Step Writing Guidelines:

- Use active voice and present tense
- Be specific and measurable
- Include exact error messages when specified in acceptance criteria
- Use parameterization for similar scenarios with different data
- Ensure steps are automatable (avoid vague language like "appropriate" or "relevant")

### 4. Data Handling

- Extract specific values from acceptance criteria (numbers, text, error messages)
- Use Scenario Outlines for scenarios that test multiple similar conditions
- Include exact error messages as specified in the acceptance criteria
- Preserve measurement units and ranges exactly as defined

### 5. Error and Edge Cases

- Create separate scenarios for each error condition
- Include validation scenarios for all input fields
- Test boundary conditions (min/max values)
- Cover network/system failure scenarios if mentioned in acceptance criteria

### 6. File Organization

- Save the generated feature file in the `e2e/features/` directory
- Use naming convention: `[use-case-number]-[kebab-case-name].feature`
- Example: For "Use Case 001: Set Daily Hydration Goal" → `001-set-daily-hydration-goal.feature`
- Ensure the e2e/features directory exists before creating the file

### 7. Quality Checklist

Before finalizing the feature file, ensure:

- [ ] Every acceptance criteria (AC1, AC2, AC3, etc.) has a corresponding scenario
- [ ] No acceptance criteria are missed or combined inappropriately
- [ ] All specific values, error messages, and conditions are preserved exactly
- [ ] Scenarios are independent and can run in any order
- [ ] Steps use clear, unambiguous language
- [ ] Background section includes only truly common setup
- [ ] Feature description accurately reflects the use case purpose
- [ ] User persona is correctly identified and used
- [ ] File is saved in the correct `e2e/features/` directory with proper naming

### 8. Example Mapping

If the acceptance criteria table contains:

```
| ID  | Given | When | Then |
| AC1 | User is on the main dashboard | User clicks "Edit Goal" button | Goal setting interface should be displayed |
| AC7 | User is on goal setting interface | User enters a negative number | Error message should display "Please enter a positive number" |
```

This should generate in `e2e/features/001-set-daily-hydration-goal.feature`:

```gherkin
Scenario: AC1: User accesses goal setting interface
  Given the user is on the main dashboard
  When the user clicks the "Edit Goal" button
  Then the goal setting interface should be displayed

Scenario: AC7: System validates negative number input
  Given the user is on the goal setting interface
  When the user enters a negative number
  Then an error message should display "Please enter a positive number"
```

## Important Notes

- **Completeness**: Every single acceptance criteria must be represented as a scenario
- **Accuracy**: Preserve exact wording for error messages, labels, and specific values
- **Consistency**: Use consistent terminology throughout the feature file
- **Testability**: Ensure all scenarios can be automated with testing tools
- **Traceability**: Maintain clear mapping between AC IDs and scenario names
