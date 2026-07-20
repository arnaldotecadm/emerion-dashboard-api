# Emerion Dashboard - Copilot Context Setup

## What This Is

A comprehensive Copilot CLI context for **emerion-dashboard**, a Kotlin/
Spring Boot ERP API that:
1. **Receives** already-transformed data pushed by `emerion-load-service`
   (which extracts from a legacy Firebird database) and persists it into
   **PostgreSQL**.
2. **Serves** that data over REST to a **React frontend** deployed
   separately (CORS, not same-origin static hosting).

Architecture: **hexagonal (ports & adapters), as 4 Gradle modules** —
`:domain` / `:application` / `:infrastructure` / `:app` (not folders in one
module — real, separately-built Gradle subprojects, so the dependency rule
is compiler-enforced). API contract: **OpenAPI, contract-first** — the
spec is hand-written, Kotlin interfaces/models are generated from it.

> Note: the repo/frontend name references ("dashboard", "stay-fit-web") are
> historical leftovers — this is a general ERP system, not fitness-specific.

## Files in `.github/`

### Core Instructions (auto-loaded by Copilot)
| File | Covers |
|---|---|
| `copilot-instructions.md` | Project overview, architecture, conventions, common tasks |
| `instructions/hexagonal-architecture.instructions.md` | `:domain`/`:application`/`:infrastructure`/`:app` module rules, ports/adapters |
| `instructions/openapi-contract.instructions.md` | Contract-first workflow, codegen gotchas, contract design conventions |
| `instructions/rest-ingestion.instructions.md` | Ingestion endpoint design, idempotency, partial-failure batches |
| `instructions/persistence-jpa.instructions.md` | JPA entity/repository/adapter/mapper conventions |
| `instructions/flyway-migrations.instructions.md` | Migration numbering, table conventions |
| `instructions/testing.instructions.md` | MockK unit tests, Testcontainers integration tests |

### Agents
| Agent | Use For |
|---|---|
| `agents/api-contract-architect.md` | Designing/evolving the OpenAPI spec |
| `agents/domain-modeler.md` | Domain models and outbound ports |
| `agents/persistence-adapter-specialist.md` | JPA entities, repositories, Flyway |
| `agents/ingestion-integrator.md` | Ingestion endpoints from emerion-load-service |
| `agents/testing-expert.md` | Test strategy and implementation |
| `agents/dev-assistant.md` | General architecture guidance |

### Skills
| Skill | Purpose |
|---|---|
| `skills/port-adapter-skill.md` | End-to-end recipe for adding a new resource |
| `skills/mapper-skill.md` | REST/persistence mapper templates |
| `skills/openapi-codegen-skill.md` | Generator config and troubleshooting |

### Database Metadata
| File | Purpose |
|---|---|
| `database-metadata/README.md` | Current Postgres schema (owned by this service, via Flyway) |

## Reference Implementation: `Customer`
The entire `Customer` resource — spread across all 4 modules
(`domain/src/main/kotlin/.../domain/customer/`,
`application/src/main/kotlin/.../application/customer/`,
`infrastructure/src/main/kotlin/.../infrastructure/{rest,persistence}/customer/`),
plus `infrastructure/src/main/resources/db/migration/V1__create_customer_table.sql`
and `application/src/test/kotlin/.../IngestCustomersServiceTest.kt` — is a
complete, working, tested example of every convention in this folder. New
resources should copy it file-by-file, module-by-module (see
`skills/port-adapter-skill.md`).

## Quick Start

```bash
# See loaded instructions
/env

# List available agents
/agent

# Use a specific agent
/agent api-contract-architect
/agent domain-modeler
/agent persistence-adapter-specialist
/agent ingestion-integrator
/agent testing-expert
/agent dev-assistant
```

### Example request
```
Add ingestion + query endpoints for an Invoice resource: externalId, amount
(decimal), issuedAt (date), status. Follow port-adapter-skill.md and
mapper-skill.md, and hexagonal-architecture.instructions.md /
openapi-contract.instructions.md for the rules. Use Customer as the
reference implementation.
```

## Runnable Scaffold (already in the repo, not just docs)
This isn't only documentation — the actual working scaffold is already in
place and builds as a **4-module Gradle project**:
- `settings.gradle.kts` — `include("domain", "application", "infrastructure", "app")`.
- `gradle/libs.versions.toml` — the Gradle version catalog: Kotlin 2.3.21,
  Spring Boot 4.1.0, `openapi-generator-gradle-plugin`, springdoc-openapi,
  MockK, Testcontainers versions, shared across all 4 modules.
- `domain/build.gradle.kts` — pure `kotlin("jvm")`, zero Spring/JPA deps.
- `application/build.gradle.kts` — Kotlin + `kotlin.plugin.spring`, depends
  on `:domain` only.
- `infrastructure/build.gradle.kts` — owns the `openApiGenerate` task
  (`infrastructure/src/main/resources/openapi/api.yaml` is the contract,
  also served live at `/openapi/api.yaml` + Swagger UI), Flyway migrations
  under `infrastructure/src/main/resources/db/migration/`, depends on
  `:domain` and `:application`.
- `app/build.gradle.kts` — the only module with the real
  `org.springframework.boot` plugin (produces `bootJar`), depends on all
  three other modules, owns `application.properties` and the
  `@SpringBootApplication` main class.
- Full `Customer` hexagonal implementation spread across all 4 modules +
  `IngestCustomersServiceTest.kt` (in `application`) +
  `support/PostgresIntegrationTest.kt` (in `app`).

Run `./gradlew build` to verify (`:infrastructure:compileKotlin` depends on
`:infrastructure:openApiGenerate`, so the contract is always regenerated
before compilation).
