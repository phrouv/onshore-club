---
applyTo: "**"
description: "Repository structure and contribution conventions for all AI and developer contributions."
---

# Repository Conventions

All repository contributions must follow these unified rules so every AI / developer knows exactly where things belong.

## 1. Directory Purpose Overview

| Path                                           | Purpose                                                               |
| ---------------------------------------------- | --------------------------------------------------------------------- |
| `microservices/`                               | Java Spring Boot backend (Maven multi-module, parent `pom.xml`)       |
| `microservices/services/`                      | Domain microservices (user, partner, planning, booking, credit, payout, admin, ...)   |
| `microservices/common-lib/`                    | Shared code (exceptions, DTOs, enums, events)                         |
| `microservices/cloud/`                         | api-gateway, config-server, service-registry                          |
| `config-repo/`                                 | Centralized Spring Cloud Config files (`*.yml`)                       |
| `onshore-frontend/`                            | React (Vite, Redux Toolkit, React Router, Tailwind) frontend          |
| `.github/instructions/`                        | Instruction rule sets (this file resides here)                        |
| `.github/prompts/`                             | Reusable prompt files callable as slash commands in the chat          |
| `../../documentation`                              | Functional & technical specs (epics, backlog, api-specs, tech design) |
| `docker-compose.yml`, `docker-compose.dev.yml` | Local stack definition                                                |
| `services.sh`                                  | Build / start / stop script (`-l`, `-b`, `-up`, `-s`)                 |
| `init-databases.sql`                           | Per-service database initialization                                   |

## 2. Curated Repository Tree Snapshot

This snapshot (partial) is a reference for where to place new artifacts. Keep it pruned and updated when structural changes occur. Do NOT copy vendor / build artifacts into docs.

```
onshore/
├─ services.sh
├─ docker-compose.yml
├─ docker-compose.dev.yml
├─ init-databases.sql
├─ config-repo/                 ← Spring Cloud Config files
├─ .github/
│  ├─ prompts/                  ← Chat slash-command prompt files
│  └─ instructions/             ← Instruction rule sets (this file resides here)
│     └─ repository.instructions.md
├─ microservices/
│  ├─ pom.xml
│  ├─ common-lib/
│  ├─ cloud/
│  │  ├─ api-gateway/          (port 5000)
│  │  ├─ config-server/        (port 8888)
│  │  └─ service-registry/     (port 8761)
│  └─ services/
│     └─ <service-name>/       (e.g. booking-service)
│        ├─ pom.xml
│        └─ src/
│           ├─ main/java/com/onshore/services/{controller,service,repository,mapper,model,config,clients,event}
│           ├─ main/resources/db/changelog/   ← Liquibase
│           └─ test/
├─ onshore-frontend/
│  ├─ package.json
│  └─ src/{pages,components,services,Redux,hooks,utils}
└─ specifications/
   └─ api-specs/<service>/     ← OpenAPI exports
```

## 7. Updating This Tree

When structure changes (add/remove top-level directories or important subtrees):

1. Update the snapshot in Section 3.
2. Incrementally adjust placement rules if a new domain emerges.
3. Ensure related instruction files (e.g., backend/frontend guidelines) remain consistent.
