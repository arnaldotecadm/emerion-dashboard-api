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
- For a simple query endpoint, the REST controller injects the concrete
  application service and maps the domain model to the generated response
  DTO via a `<Resource>RestMapper` object in the centralized `adapter/mapper/`
  package (see below), as `CustomerController`/`CustomerRestMapper` do.
  Prefer an extension function (`fun Customer.toResponse(): CustomerResponse`)
  over a standalone/private function so call sites read as
  `customer.toResponse()`. Do not pass generated DTOs into application code
  or put business rules in controllers.
- Add an inbound use-case interface only when it creates a useful boundary,
  such as multiple driving adapters or a distinct ingestion workflow.
- Application services depend on ports, never adapter implementations.
  They own business orchestration and transaction boundaries.

## Persistence package layout

Group persistence code by technical role, not by entity:

```text
adapter/outbound/persistence/
  entity/      # all JPA entities
  projection/  # all native-query projections
  repository/ # one combined Spring Data repository per entity
  port/        # <Resource>RepositoryPortAdapter implementations
```

The combined `<Resource>Repository` owns the JPA CRUD/upsert operations and
native projection queries for its entity. The port adapter translates
between repository/JPA/projection types and application/domain types. Keep
JPA, Spring Data pagination, and generated DTO types inside `:adapter`.

All mappers — persistence and REST alike — live together in a single
centralized `adapter/mapper/` package, not split by technical layer or
resource. Each mapper is a pure Kotlin `object` exposing extension
functions (`fun <Type>.toX(): Y`), never private controller methods:

```text
adapter/mapper/<Resource>PersistenceMapper.kt  # entity/projection <-> domain
adapter/mapper/<Resource>RestMapper.kt         # domain -> generated response DTO
```

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
