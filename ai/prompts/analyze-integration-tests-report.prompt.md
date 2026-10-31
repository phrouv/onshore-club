---
agent: agent
description: "This prompt analyzes all integration and unit tests across the backend microservices (microservices/services/*, microservices/common-lib) and generates a comprehensive test analysis report using a Python extraction script."
---

You are a Test Analysis Expert specialized in Java Spring Boot applications and integration and unit testing patterns.

Your task is to comprehensively analyze all integration and unit test files across the backend Spring Boot microservices:

- `microservices/services/*/src/test/` (one module per service: booking-service, flight-ops-service, seat-service, pricing-service, payment-service, ancillary-service, location-service, notification-service, subscription-service, user-service)
- `microservices/common-lib/src/test/`
- `microservices/cloud/*/src/test/` (api-gateway, config-server, service-registry)

---

## Strategy: Python-First Extraction

**Do NOT manually read every test file.** Instead, follow this two-phase strategy:

1. **Phase 1 — Write and run a Python extraction script** that parses all Java test files and outputs structured JSON with every test method's metadata (including test data).
2. **Phase 2 — Use the JSON output** to generate the final markdown report with quality analysis, recommendations, and statistics.

This approach is faster, accurate, and repeatable.

---

## Phase 1 — Python Extraction Script

### Step 1: Create the script

Create the Python script at `scripts/extract_test_metadata.py`. The script must:

1. **Discover all Java test files** in the backend test directories (skip `testsupport/` classes — include them in a separate support-classes list).

2. **For each `.java` file**, parse its content with regex to extract:

   #### File-level fields:

- `module`: the service/module name derived from the path (e.g. `booking-service`, `seat-service`, `common-lib`, `api-gateway`)
- `test_type`: `integration`, `unit-service`, `unit-util`, `support`
- `class_name`: Java class name
- `package`: full package string
- `file_path`: relative path from repo root

#### Method-level fields (for each `@Test` or `@ParameterizedTest` block):

- `method_name`: Java method name
- `display_name`: text from `@DisplayName(...)` immediately preceding the method (empty string if absent)
- `http_method`: detected from `mockMvc.perform(get|post|put|delete|patch(...)` — use uppercase e.g. `GET`. Empty for non-HTTP tests.
- `endpoint`: URL string extracted from the MockMvc call (e.g. `/v1/bookings/{id}`). Extract path template by replacing UUID/ID variable substitutions with `{id}`. Empty for non-HTTP tests.
- `expected_status`: HTTP status integer extracted from `.andExpect(status().isOk())` / `.andExpect(status().isCreated())` etc., or from `status().is(NNN)`. Use `0` for non-HTTP tests.
- `error_code`: string extracted from `.andExpect(jsonPath("$.code").value("..."))` if present, else empty string.
- `key_test_data`: **comma-separated list of notable data values referenced inside the test method body**. Look for:
  - Airport / route codes: 3-letter IATA codes (e.g. `CDG`, `JFK`) and airline codes (2 letters, e.g. `AF`)
  - Currencies and cabin classes (e.g. `EUR`, `ECONOMY`, `BUSINESS`)
  - Booking, payment and flight status values (e.g. `CONFIRMED`, `CANCELLED`, `PAID`, `SCHEDULED`)
  - Roles (e.g. `ADMIN`, `TRAVELER`, `AIRLINE_ADMIN`) — use the actual role names found in the tests
  - Cron expressions: strings matching `[\d\*\/\-\,]+ [\d\*\/\-\,]+ [\d\*\/\-\,]+` (simplified)
  - Any other string literal that appears to be a business identifier (UUID-format strings excluded)
  - Deduplicate and join with `, `
- `test_scenario`: inferred from `display_name` if present, otherwise derived from `method_name` by converting camelCase to readable sentence.

3. **Classify `expected_status`** using these mappings from MockMvc status matchers:
   - `isOk()` → 200, `isCreated()` → 201, `isNoContent()` → 204
   - `isBadRequest()` → 400, `isUnauthorized()` → 401, `isForbidden()` → 403
   - `isNotFound()` → 404, `isConflict()` → 409
   - `isInternalServerError()` → 500
   - `.is(NNN)` → use the literal integer NNN

4. **Output** a JSON file at `scripts/test_metadata.json` with this top-level structure:

   ```json
   {
     "generated_at": "ISO-8601 timestamp",
     "summary": {
       "total_files": 0,
       "total_tests": 0,
       "by_module": { "<service-or-module-name>": 0 },
       "by_test_type": {
         "integration": 0,
         "unit-service": 0,
         "unit-util": 0,
         "support": 0
       },
       "by_http_method": {
         "GET": 0,
         "POST": 0,
         "PUT": 0,
         "DELETE": 0,
         "PATCH": 0,
         "N/A": 0
       },
       "by_expected_status": {}
     },
     "files": [
       {
         "module": "booking-service",
         "test_type": "integration",
         "class_name": "BookingControllerTest",
         "package": "com.onshore.services.controller",
         "file_path": "microservices/services/booking-service/src/test/java/...",
         "tests": [
           {
             "method_name": "getBookingNotFound404",
             "display_name": "GET /v1/bookings/{id} returns 404 when unknown",
             "http_method": "GET",
             "endpoint": "/v1/bookings/{id}",
             "expected_status": 404,
             "error_code": "",
             "key_test_data": "",
             "test_scenario": "Returns not found for an unknown booking"
           }
         ]
       }
     ],
     "support_classes": ["QuartzTestCleaner"]
   }
   ```

5. **Print a summary** to stdout after writing the JSON:
   ```
   === Test Extraction Complete ===
   Total files  : XX
   Total tests  : XX
   Core         : XX tests across XX files
   Commons      : XX tests across XX files (no test dir found)
   JSON written : scripts/test_metadata.json
   ```

### Step 2: Run the script

```bash
cd /Users/philipperouvray/dev/onshore
python scripts/extract_test_metadata.py
```

Verify the output JSON exists and contains a non-zero `total_tests` count. If any module directory is missing, the script must log a clear warning but continue without crashing.

---

## Phase 2 — Report Generation

Using the `scripts/test_metadata.json` output as the **authoritative data source**, generate the final markdown report.

### Report structure

```markdown
# Integration & Unit Tests Analysis Report

**Generated on**: {current_date}
**Script**: `scripts/extract_test_metadata.py`
**Version**: v1.0
**Scope**: Backend Integration & Unit Tests (microservices and common-lib)

## Executive Summary

| Metric                    | Count                                   |
| ------------------------- | --------------------------------------- |
| Total Test Files Analyzed | {total_files}                           |
| Total Test Methods        | {total_tests}                           |
| Tests per Module          | {by_module.<name>} (one row per module) |

---

## Detailed Test Inventory

### Module Tests (one section per module, e.g. `booking-service`)

| Test Class            | Method Name           | Display Name                                   | HTTP | Endpoint          | Status | Error Code | Key Test Data  | Test Scenario                            |
| --------------------- | --------------------- | ---------------------------------------------- | ---- | ----------------- | ------ | ---------- | -------------- | ---------------------------------------- |
| BookingControllerTest | getBookingNotFound404 | GET /v1/bookings/{id} returns 404 when unknown | GET  | /v1/bookings/{id} | 404    |            |                | Returns not found for an unknown booking |
| BookingControllerTest | listBookings200       | GET /v1/bookings returns 200 with bookings...  | GET  | /v1/bookings      | 200    |            | CONFIRMED, EUR | Lists bookings with their status         |
| ...                   | ...                   | ...                                            | ...  | ...               | ...    | ...        | ...            | ...                                      |

> **Key Test Data** column lists airport/airline codes, currencies, cabin classes, status values, roles, and other business-domain identifiers referenced in the test body.

---

### Modules Without Tests

> ⚠️ For each module (service, common-lib, cloud component) with no `src/test/` directory, list it here — this is a coverage gap.

---

## Test Coverage Analysis

### By HTTP Method (API tests only)

| HTTP Method | Count   |
| ----------- | ------- |
| GET         | {count} |
| POST        | {count} |
| PUT         | {count} |
| DELETE      | {count} |
| PATCH       | {count} |

### By Expected HTTP Status

| Status                    | Count   |
| ------------------------- | ------- |
| 200 OK                    | {count} |
| 201 Created               | {count} |
| 204 No Content            | {count} |
| 400 Bad Request           | {count} |
| 401 Unauthorized          | {count} |
| 403 Forbidden             | {count} |
| 404 Not Found             | {count} |
| 409 Conflict              | {count} |
| 500 Internal Server Error | {count} |

### Test Data Coverage

List all distinct `key_test_data` values found across tests, grouped by type:

- **Airports / airlines tested**: list unique IATA airport and airline codes found
- **Currencies and cabin classes tested**: list unique values found
- **Roles tested**: list unique roles found (ADMIN, TRAVELER, etc.)
- **Status values tested**: list unique booking / payment / flight status values found

---

## Test Quality Observations

### Positive Patterns

- Observed good practices from the extracted data

### Improvement Opportunities

- Coverage gaps inferred from missing test data values or endpoints
- Inconsistent naming patterns detected

## Recommendations

1. **Coverage Enhancement**: specific recommendations based on gaps found
2. **Test Data Breadth**: airports, currencies, cabin classes or status values not covered by any test
3. **Test Organization**: structural improvements

---

**Analysis Completed**: {timestamp}
**Data Source**: `scripts/test_metadata.json`
```

---

## File Naming and Location

- **Script**: `scripts/extract_test_metadata.py`
- **JSON output**: `scripts/test_metadata.json`
- **Report location**: `../../documentation`
- **Report filename**: `{YYYY-MM-DD}-integration-tests-analysis-report-v1.0.md`

---

## Quality Standards

- The Python script is the **single source of truth** — all counts in the report must match the JSON summary.
- The `key_test_data` column must reflect values extracted from the **method body**, not guessed from method names.
- The script must be idempotent: re-running it overwrites `test_metadata.json` with fresh data.
- If a module has no test directory, report it as a finding, not an error.

---

## Execution Order

1. Write `scripts/extract_test_metadata.py`
2. Run the script: `python scripts/extract_test_metadata.py`
3. Verify `scripts/test_metadata.json` is valid and complete
4. Generate the markdown report from the JSON data
5. Save the report to `../../documentation`
