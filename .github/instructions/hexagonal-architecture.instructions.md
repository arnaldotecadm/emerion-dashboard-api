# Architecture Instructions

Apply these rules when adding or changing domain, application, REST, or
persistence code. Keep the architecture lightweight: add a boundary only
when it protects a dependency, behavior, or mapping that benefits from
being isolated.

## Module boundaries

| Module | Responsibility | Allowed project dependencies |
|---|---|---|
| `:domain` | Business models, rules, shared domain types | None |
| `:application` | Entity-level services and outbound ports | `:domain` |
| `:adapter` | New REST/OpenAPI and persistence adapters | `:application`, `:domain` |
| `:infrastructure` | Flyway, app-wide configuration, legacy adapters | `:adapter`, `:application`, `:domain` |
| `:app` | Spring Boot composition root and full-context tests | Composes the modules |

Dependencies point inward. `:domain` and `:application` must not depend on
Spring Data, JPA, generated API models, or `:adapter`/`:infrastructure`.
Flyway migrations stay in `:infrastructure`; new REST and persistence
adapters go in `:adapter`. Existing unmigrated adapters may remain in
`:infrastructure`; do not extend that legacy placement for new resources.

## Default resource flow

```text
generated API interface
  -> adapter/inbound/rest/<Resource>Controller
  -> application/<resource>/<Resource>Service
  -> application/outbound/port/<Resource>RepositoryPort
  -> adapter/outbound/persistence/port/<Resource>RepositoryPortAdapter
  -> adapter/outbound/persistence/repository/<Resource>Repository
  -> PostgreSQL
```

- Declare outbound ports in `application/outbound/port/`, named
  `<Resource>RepositoryPort`. Signatures use domain/shared types only.
- Group services and their resource-specific input models under
  `application/<resource>/`. Keep ingestion workflows under
  `application/<resource>/ingestion/` when their batch behavior warrants it.
- For a simple query endpoint, the REST controller may inject the concrete
  application service and map generated response DTOs locally, as
  `CustomerController` and `SmartStockController` do. Do not pass generated
  DTOs into application code or put business rules in controllers.
- Add an inbound use-case interface or a separate REST mapper only when it
  creates a useful boundary, such as multiple driving adapters, complex or
  reused mapping, or a distinct ingestion workflow.
- Application services depend on ports, never adapter implementations.
  They own business orchestration and transaction boundaries.

## Persistence package layout

Group persistence code by technical role, not by entity:

```text
adapter/outbound/persistence/
  entity/      # all JPA entities
  projection/  # all native-query projections
  repository/ # one combined Spring Data repository per entity
  mapper/      # persistence mappers
  port/        # <Resource>RepositoryPortAdapter implementations
```

The combined `<Resource>Repository` owns the JPA CRUD/upsert operations and
native projection queries for its entity. The port adapter translates
between repository/JPA/projection types and application/domain types. Keep
JPA, Spring Data pagination, and generated DTO types inside `:adapter`.
Persistence mappers are pure Kotlin `object`s; REST mappers are optional
when simple controller-local mapping is sufficient.

## Contract, schema, and tests

- Edit `adapter/src/main/resources/openapi/api.yaml`, then run
  `./gradlew :adapter:openApiGenerate`. Never edit generated output.
- Add additive Flyway changes under
  `infrastructure/src/main/resources/db/migration/`; never edit a shipped
  migration. Hibernate schema mode stays `validate`.
- Unit-test application behavior with JUnit 5 and MockK against ports.
  Integration tests that need PostgreSQL live under `app/src/test/` and use
  `support.PostgresIntegrationTest`.

See the focused OpenAPI, persistence/JPA, ingestion, Flyway, and testing
instructions for their specific rules. Customer and SmartStock are the
reference flows; distinguish their current `:adapter` pattern from legacy
resources still in `:infrastructure`.
