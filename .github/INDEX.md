# Emerion Dashboard - Copilot Resources Index

Quick links to everything in `.github/`.

## Start Here
1. First time? → [`README.md`](README.md)
2. Architecture rules → [`instructions/hexagonal-architecture.instructions.md`](instructions/hexagonal-architecture.instructions.md)
3. Adding a new resource → [`skills/port-adapter-skill.md`](skills/port-adapter-skill.md)

## Complete File Directory

### Main Documentation
| File | Purpose |
|---|---|
| [`README.md`](README.md) | Setup overview, what Copilot understands |
| [`copilot-instructions.md`](copilot-instructions.md) | Main project context (auto-loaded) |
| [`INDEX.md`](INDEX.md) | This file |

### Instructions (Auto-Loaded)
| File | When to Reference |
|---|---|
| [`instructions/hexagonal-architecture.instructions.md`](instructions/hexagonal-architecture.instructions.md) | `:domain`/`:application`/`:infrastructure`/`:app` module rules, ports/adapters, package placement questions |
| [`instructions/openapi-contract.instructions.md`](instructions/openapi-contract.instructions.md) | Adding/changing endpoints, codegen gotchas |
| [`instructions/rest-ingestion.instructions.md`](instructions/rest-ingestion.instructions.md) | Ingestion endpoints from emerion-load-service |
| [`instructions/persistence-jpa.instructions.md`](instructions/persistence-jpa.instructions.md) | JPA entities/repositories/adapters |
| [`instructions/flyway-migrations.instructions.md`](instructions/flyway-migrations.instructions.md) | Schema changes |
| [`instructions/testing.instructions.md`](instructions/testing.instructions.md) | Unit/integration test conventions |

### Agents
| Agent | File | When to Use |
|---|---|---|
| api-contract-architect | [`agents/api-contract-architect.md`](agents/api-contract-architect.md) | Designing OpenAPI endpoints/schemas |
| domain-modeler | [`agents/domain-modeler.md`](agents/domain-modeler.md) | Domain models and ports |
| persistence-adapter-specialist | [`agents/persistence-adapter-specialist.md`](agents/persistence-adapter-specialist.md) | JPA/Flyway |
| ingestion-integrator | [`agents/ingestion-integrator.md`](agents/ingestion-integrator.md) | Ingestion endpoints |
| testing-expert | [`agents/testing-expert.md`](agents/testing-expert.md) | Test strategy |
| cognito-notification-operator | [`agents/cognito-notification-operator.md`](agents/cognito-notification-operator.md) | Cognito sync + notification fanout operations |
| dev-assistant | [`agents/dev-assistant.md`](agents/dev-assistant.md) | General architecture guidance |

### Skills
| Skill | File | Purpose |
|---|---|---|
| Port & Adapter | [`skills/port-adapter-skill.md`](skills/port-adapter-skill.md) | End-to-end new-resource recipe |
| Mapper | [`skills/mapper-skill.md`](skills/mapper-skill.md) | REST/persistence mapper templates |
| OpenAPI Codegen | [`skills/openapi-codegen-skill.md`](skills/openapi-codegen-skill.md) | Generator config/troubleshooting |
| Cognito + Notifications | [`skills/cognito-notification-skill.md`](skills/cognito-notification-skill.md) | Token-efficient recipes for auth/sync/notification work |
| Skills Guide | [`skills/README.md`](skills/README.md) | How skills relate to instructions |

### Database Metadata
| File | Contains |
|---|---|
| [`database-metadata/README.md`](database-metadata/README.md) | Current Postgres schema owned by this service |

## Architectural Quick Reference

```
emerion-load-service (Firebird → transform)
        │  POST /ingestion/<resource>   (batch, idempotent by externalId)
        ▼
infrastructure/.../rest/<resource>/controller/...IngestionController   (generated OpenAPI interface, :infrastructure module)
        │  REST mapper
        ▼
application/.../<resource>/Ingest...UseCase/Service          (per-item try/catch, upsert, :application module)
        │  domain port
        ▼
domain/.../<resource>/<Resource>Repository  (:domain module)  ⇄  infrastructure/.../persistence/<resource>/...RepositoryAdapter (:infrastructure module)
        │
        ▼
PostgreSQL (Flyway-managed schema, migrations in :infrastructure)

React app (separate origin, CORS)
        │  GET /<resources>?page=&size=&...   GET /<resources>/{id}
        ▼
infrastructure/.../rest/<resource>/controller/...QueryController  →  Get/List...UseCase  →  domain port  →  Postgres
```

`:app` wires all of the above together (composition root + Spring Boot
autoconfig); it has no business logic of its own.

## Quick Commands

```bash
# Copilot CLI
/env                              # loaded instructions
/agent                            # list agents
/agent api-contract-architect
/agent domain-modeler
/agent persistence-adapter-specialist
/agent ingestion-integrator
/agent testing-expert
/agent cognito-notification-operator
/agent dev-assistant

# Gradle (4 modules: domain, application, infrastructure, app)
./gradlew :infrastructure:openApiGenerate   # regenerate from infrastructure/src/main/resources/openapi/api.yaml
./gradlew build                             # full build (:infrastructure:compileKotlin depends on openApiGenerate)
./gradlew test                              # unit (domain/application) + Testcontainers integration tests (app, needs Docker)
```

## Common Tasks

### "Add a new resource end-to-end"
1. Read [`skills/port-adapter-skill.md`](skills/port-adapter-skill.md).
2. Use `/agent api-contract-architect` for the OpenAPI contract first.
3. Use `/agent domain-modeler` → `/agent persistence-adapter-specialist` →
   `/agent ingestion-integrator` for the rest, in that order.
4. Use `/agent testing-expert` to close out with tests.

### "Add a field to Customer"
1. Read [`instructions/flyway-migrations.instructions.md`](instructions/flyway-migrations.instructions.md)
   (new migration, never edit a shipped one).
2. Update `api.yaml`, regenerate, update both REST mappers, the JPA entity,
   the persistence mapper, and the domain model.
3. Update `IngestCustomersServiceTest.kt`.

### "Why is the generated code weird?"
→ [`instructions/openapi-contract.instructions.md`](instructions/openapi-contract.instructions.md)
"Known Generator Gotchas" section, or `/agent api-contract-architect`.

### "Set up auth"
→ Cognito JWT validation is already implemented. For changes in this area,
use `/agent cognito-notification-operator` first, then
`instructions/testing.instructions.md` for validation/runtime notes.

## FAQ

**Q: Where do I start?**
A: `README.md`, then `instructions/hexagonal-architecture.instructions.md`.

**Q: How do I add a new resource?**
A: `skills/port-adapter-skill.md`, using `Customer` as the reference
implementation.

**Q: Why does the generated model have `propertySize` instead of `size`?**
A: openapi-generator renames reserved-word-colliding properties — see
`instructions/openapi-contract.instructions.md`.

**Q: Does this service talk to Firebird?**
A: No — that's entirely `emerion-load-service`'s job. This service only
receives already-transformed JSON.

**Q: Is the React app served from this app?**
A: No — deployed separately, CORS is configured via
`app.cors.allowed-origins`.

**Q: Testcontainers can't find Docker — is my code broken?**
A: Almost certainly not — check `docker info` works in the same shell
Gradle runs in. See `instructions/testing.instructions.md`.

**Q: Where is Cognito user sync documented?**
A: `skills/cognito-notification-skill.md` and
`agents/cognito-notification-operator.md`.
