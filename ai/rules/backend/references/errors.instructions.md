---
description: "Onshore centralized error handling: ErrorMessage, ApiException and exception type enums from common-lib"
applyTo: "microservices/**"
---

# Error Handling

The Onshore backend MUST use the centralized error handling & error code format defined in the `microservices/common-lib` module (package `com.onshore.common_lib.exception`). Do NOT create ad-hoc `ErrorResponse` POJOs or diverging structures in feature modules.

#### 1. Standard Error Representation (RFC 9457 problem+json)

Errors MUST be serialized using the `ErrorMessage` class (located in `microservices/common-lib/.../exception/`). It implements the Problem Details ("problem+json") pattern enriched with a stable business `code`.

Fields semantics:
| Field | Required | Source | Description |
|-------|----------|--------|-------------|
| `type` | yes | computed | Stable absolute URI: `${onshore.exception.base-url}/{CODE}` |
| `title` | yes | enum | Short constant summary from `CommonExceptionType` or domain enum |
| `status` | yes | enum/HTTP | Numeric HTTP status (mirrors response status) |
| `code` | yes | enum | UPPER_SNAKE_CASE business error code (unique per category) |
| `detail` | optional | dynamic | Contextual human-readable occurrence explanation |
| `instance` | yes | request | Request path (obtained from the current WebRequest or servlet path) |
| `errors` | optional | validation | Array of field-level validation errors (only for payload validation) |

#### 2. Common Exception Types

The enum `CommonExceptionType` defines framework-wide codes (e.g. `INVALID_PAYLOAD`, `UNAUTHORIZED_OPERATION`, `UNKNOWN_EXCEPTION`). Always reuse these for cross-cutting situations. For domain-specific errors, introduce a NEW enum in your module (e.g. `LocaleExceptionType`) following the exact pattern:

```java
@AllArgsConstructor
@Getter
public enum LocaleExceptionType {
    LOCALE_NOT_FOUND("LOCALE_NOT_FOUND", HttpStatus.NOT_FOUND, "Locale not found"),
    LOCALE_DELETE_CONSTRAINT("LOCALE_DELETE_CONSTRAINT", HttpStatus.CONFLICT, "Locale delete constraint"),
    ;
    private final String code;
    private final HttpStatus status;
    private final String title;
}
```

Rules:

1. Code format: UPPER_SNAKE_CASE (ASCII letters, digits + underscore) – no spaces, no hyphens.
2. Title: short, stable, Capitalized phrase (no trailing period, no dynamic values).
3. Status: choose the MOST specific HTTP status (avoid always using 400/500). Use 422 only for semantic validation beyond basic bean validation; keep `INVALID_PAYLOAD` (400) for structural/bean validation.

#### 3. Throwing Exceptions

Use the `ApiException` builder when raising domain errors:

```java
throw ApiException.builder()
        .code(LocaleExceptionType.LOCALE_DELETE_CONSTRAINT.getCode())
        .status(LocaleExceptionType.LOCALE_DELETE_CONSTRAINT.getStatus())
        .title(LocaleExceptionType.LOCALE_DELETE_CONSTRAINT.getTitle())
        .detail("Locale " + id + " is still linked to 2 sites and cannot be deleted")
        .build();
```

Never hardcode HTTP numeric statuses or free-form strings—always derive from the enum.

#### 4. Validation Errors

Bean validation failures are handled centrally by the global ControllerExceptionHandler (method that maps bean validation errors). DO NOT replicate logic locally. For custom cross-field validation failures inside services, prefer throwing an ApiException with a dedicated domain code instead of returning partial successes.

Field errors population pattern (already implemented):

```java
List<FieldError> errors = ex.getFieldErrors().stream()
    .map(err -> FieldError.builder()
            .field(err.getField())
            .message(err.getDefaultMessage())
            .build())
    .toList();
```

#### 5. Global Controller Advice

NEVER redefine a second `@ControllerAdvice` for generic concerns in feature modules. Extend only if you need to catch a NEW checked/third-party exception type; then convert it into an `ApiException` or map directly to `ErrorMessage` using an existing/common enum code.

Allowed additional handlers should:

1. Use existing code first (e.g., map security access errors to `UNAUTHORIZED_OPERATION`).
2. Log exceptions at appropriate level (`warn` for client misuse, `error` for unexpected server faults).
3. Avoid leaking internal stack traces or class names in `detail`.

#### 6. Configuration

Property `onshore.exception.base-url` MUST be defined in each service's `application.yml` (or inherited from parent configuration) to build the `type` URI. Example:

```yaml
onshore:
  exception:
    base-url: https://onshore.errors
```

Use an internal documentation service or static site behind this base URL to describe codes.

#### 7. Adding New Error Codes Checklist

- [ ] Enum created (pattern: `<Domain>ExceptionType`) with `code`, `status`, `title`.
- [ ] Business logic throws `ApiException` referencing the enum (no raw strings).
- [ ] OpenAPI schema leverages existing `ErrorMessage` model (no duplicates).
- [ ] Documentation of code added/updated at `${base-url}/{CODE}`.
- [ ] Tests assert both HTTP status AND body fields (`code`, optional `detail`).

#### 8. Example Response

```json
{
  "type": "https://onshore.errors/validation/INVALID_PAYLOAD",
  "title": "Structural or semantic validation failure",
  "status": 400,
  "code": "INVALID_PAYLOAD",
  "detail": null,
  "instance": "/v1/locales",
  "errors": [{ "field": "localeId", "message": "Must match pattern ll_RR" }]
}
```

#### 9. DOs and DON'Ts

DO:

- Reuse `CommonExceptionType` when applicable.
- Provide actionable `detail` only when it adds context (omit for generic 404 if not helpful).
- Centralize all mapping in one ControllerAdvice.

DON'T:

- Invent new JSON structures or rename fields.
- Reuse the same code with different HTTP statuses.
- Include PII or security sensitive data in `detail`.

