# Emerion Dashboard — Copilot Instructions

## Project
Kotlin/Spring Boot ERP API. It accepts already-transformed JSON from
`emerion-load-service`, stores it in PostgreSQL, and serves a separately
deployed React app. This API never connects to Firebird. Flyway owns schema
changes; Hibernate uses `ddl-auto=validate`.

## Current architecture
Gradle modules: `domain`, `application`, `adapter`, `infrastructure`, `app`.

```text
domain          Pure business models and shared types; no framework/project dependencies.
application     Services and outbound ports; depends on domain only.
adapter         New REST/OpenAPI and persistence adapters; depends on application and domain.
infrastructure  App-wide config, Flyway, and adapters not yet migrated; depends on adapter/application/domain.
app             Spring Boot composition root and full-context tests.
```

The active lightweight pattern is demonstrated by Customer and SmartStock:

- Keep domain models in `domain/<resource>/`.
- Put services under `application/<resource>/`. Keep application-specific
  input models with their service; existing query models may still be
  domain-owned. Keep outbound ports under `application/outbound/port/`, named
  `<Resource>RepositoryPort`; use domain types only.
- Put new REST adapters in `adapter/inbound/rest/`. For a simple endpoint,
  inject the application service and do straightforward generated-DTO
  mapping in the controller. Extract an inbound use-case interface or REST
  mapper only when it provides a useful seam (complex/reused mapping,
  multiple adapters, or distinct ingestion behavior).
- Group persistence files by technical type, not resource:
  `adapter/outbound/persistence/entity/`,
  `projection/`, `repository/`, `mapper/`, and `port/`.
  Use one Spring Data `<Resource>Repository` per entity for reads and writes;
  it may return projections for native read queries. Implement the
  application port as `<Resource>RepositoryPortAdapter`.
- Keep persistence/API/framework types out of domain and application ports.
  Controllers and adapters translate/delegate; application services own
  orchestration and business rules.

Customer ingestion controllers and other unmigrated resource adapters may
still live in `:infrastructure`. Treat those as legacy placement, not the
template for new adapters. New migrations remain in
`infrastructure/src/main/resources/db/migration/`.

## API and security
`adapter/src/main/resources/openapi/api.yaml` is the contract and codegen
input. Run `./gradlew :adapter:openApiGenerate` after editing it; never edit
`adapter/build/generated/openapi/`. The same spec is served at
`/openapi/api.yaml`.

Cognito JWT protects query/admin endpoints. Ingestion is authenticated by
the `X-API-Key` filter; `/admin/**` requires the configured admin role.
Do not change endpoint exposure or authorization without explicit
requirements. See `skills/cognito-notification-skill.md` and
`API_KEY_SETUP.md` for the distinct operational flows.

## Working rules
- Follow the focused instructions for the task: architecture,
  persistence/JPA, OpenAPI, ingestion, Flyway, or testing.
- Keep design proportional: do not add pass-through interfaces, services,
  or mappers without a useful boundary.
- Add MockK tests for application behavior; use
  `support.PostgresIntegrationTest` for behavior that needs PostgreSQL.
- Never edit generated code or an already-shipped Flyway migration.
- Ask before making ambiguous business-rule or security decisions.
