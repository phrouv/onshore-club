# Rules

Rules are `*.instructions.md` files that teach AI assistants the conventions of this repository. Each file declares a `description` and an `applyTo` glob in its frontmatter, and is loaded automatically when a file matching `applyTo` is in context.

```yaml
---
description: Short summary of what the rule covers
applyTo: "microservices/**"
---
```

## Structure

```
ai/rules/
├─ repository.instructions.md
├─ backend/
│  ├─ spring-boot.instructions.md
│  └─ references/          ← detail files linked from spring-boot.instructions.md
├─ code-quality/
├─ conception/
├─ database/liquibase/
├─ design-patterns/
├─ e2e/
├─ frontend/
├─ jpa-patterns/
├─ logging-patterns/
├─ security/
└─ version-control/
```

## Available Rules

### Global

| Rule                                                               | applyTo | Description                                                       |
| ------------------------------------------------------------------ | ------- | ----------------------------------------------------------------- |
| [repository](repository.instructions.md)                           | `**`    | Repository structure and contribution conventions                 |
| [version-control](version-control/version-control.instructions.md) | `**`    | Gitmoji + conventional commits                                    |
| [e2e/bdd](e2e/bdd.instructions.md)                                 | `**`    | Generate Gherkin feature files from use cases                     |
| [e2e/e2e](e2e/e2e.instructions.md)                                 | `**`    | Execute E2E scenarios with Playwright MCP and generate spec files |

### Backend (`microservices/**`)

| Rule                                                                       | Description                                                                       |
| -------------------------------------------------------------------------- | --------------------------------------------------------------------------------- |
| [backend/spring-boot](backend/spring-boot.instructions.md)                 | Spring Boot 4.x: REST APIs, JPA, security, testing, cloud-native patterns         |
| [backend/references/web](backend/references/web.instructions.md)           | Controllers, validation, REST APIs                                                |
| [backend/references/data](backend/references/data.instructions.md)         | Spring Data JPA, repositories, transactions                                       |
| [backend/references/security](backend/references/security.instructions.md) | Spring Security 6, OAuth2, JWT                                                    |
| [backend/references/cloud](backend/references/cloud.instructions.md)       | Spring Cloud: config server, discovery, resilience                                |
| [backend/references/testing](backend/references/testing.instructions.md)   | Unit, integration and slice tests                                                 |
| [backend/references/errors](backend/references/errors.instructions.md)     | `ErrorMessage`, `ApiException` and exception type enums from `common-lib`         |
| [code-quality](code-quality/code-quality.instructions.md)                  | Java code review: clean code, API contracts, null safety, exceptions, performance |
| [design-patterns](design-patterns/design-patterns.instructions.md)         | Factory, Builder, Strategy, Observer, Decorator, etc.                             |
| [jpa-patterns](jpa-patterns/jpa-patterns.instructions.md)                  | JPA/Hibernate pitfalls: N+1, lazy loading, transactions                           |
| [logging-patterns](logging-patterns/logging-patterns.instructions.md)      | SLF4J, structured JSON logging, MDC                                               |
| [database/liquibase](database/liquibase/liquibase.instructions.md)         | Liquibase migration management                                                    |

### Frontend (`onshore-frontend/**`)

| Rule                                                             | Description                                     |
| ---------------------------------------------------------------- | ----------------------------------------------- |
| [frontend/react](frontend/react.instructions.md)                 | React with Vite, React Router and Redux Toolkit |
| [frontend/design-system](frontend/design-system.instructions.md) | Figma design system integration                 |

### E2E (`e2e/**`)

| Rule                                                                 | Description                      |
| -------------------------------------------------------------------- | -------------------------------- |
| [e2e/e2e-folder-structure](e2e/e2e-folder-structure.instructions.md) | Folder structure and file naming |
| [e2e/e2e-utils](e2e/e2e-utils.instructions.md)                       | Reusable test utilities          |

### Specifications

| Rule                                                                       | applyTo                                        | Description                                                |
| -------------------------------------------------------------------------- | ---------------------------------------------- | ---------------------------------------------------------- |
| [conception/conception-phase](conception/conception-phase.instructions.md) | `specifications/**`                            | AI-assisted conception phase                               |
| [security/security-context](security/security-context.instructions.md)     | `specifications/epics/**/evil-user-stories/**` | Attack surface and security controls for evil user stories |

## Adding a New Rule

1. Check the tables above for overlap with an existing rule.
2. Create `<topic>.instructions.md` in the folder matching its area. Use a `references/` subfolder for detail files linked from a main rule.
3. Set `applyTo` as narrowly as possible.
4. Add the rule to the tables above.

## Related

- [`../prompts/`](../prompts/): reusable prompts and agent prompts
- [`repository.instructions.md`](repository.instructions.md): where each artifact belongs
