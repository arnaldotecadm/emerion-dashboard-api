# Emerion Dashboard - Copilot Instructions

## Project Overview
**Emerion Dashboard** is a Kotlin/Spring Boot **ERP API**. Despite the repo
name `emerion-dashboard` (and the frontend repo name `stay-fit-web`, which is
a leftover from an unrelated earlier project — this is **not** a fitness
app), the domain is general-purpose ERP data (customers, and more resources
to come).

Responsibilities:
- **Receive** already-mapped data pushed by `emerion-load-service` (a
  separate Kotlin service that reads the legacy Firebird database and sends
  transformed JSON over REST) and **persist** it into PostgreSQL.
- **Serve** that data over REST to a React frontend (deployed separately,
  e.g. Vercel/Netlify — not served as static resources from this app).
- **Never talks to Firebird directly.** All legacy-schema knowledge lives in
  `emerion-load-service`. This API only sees clean, already-transformed
  JSON payloads on its ingestion endpoints.

## Tech Stack
- **Language**: Kotlin (2.3.x)
- **Framework**: Spring Boot 4.x, `spring-boot-starter-webmvc`
- **Build Tool**: Gradle (Kotlin DSL)
- **Database**: PostgreSQL only
- **Schema Migrations**: Flyway (`infrastructure/src/main/resources/db/migration`) —
  Hibernate `ddl-auto` is always `validate`, never `update`/`create`.
- **Persistence**: Spring Data JPA (Hibernate), always behind a domain port
  interface — see Architecture below.
- **API Contract**: **Contract-first OpenAPI**. The spec at
  `infrastructure/src/main/resources/openapi/api.yaml` is the single source of truth.
  Kotlin request/response models and controller interfaces are **generated**
  by the `openapi-generator-gradle-plugin` (`openApiGenerate` Gradle task,
  wired as a dependency of `compileKotlin`) into
  `infrastructure/build/generated/openapi/...`. **Never hand-edit generated
  code** — edit the YAML and regenerate.
- **Testing**: JUnit 5, MockK (unit tests), Testcontainers + PostgreSQL
  (integration tests), Spring Boot's `@ServiceConnection`.
- **Auth**: AWS Cognito JWT is active for query/admin surfaces; ingestion
  endpoints remain open for server-to-server calls from
  `emerion-load-service`.
- **CORS**: React app is a separate origin; configured via
  `app.cors.allowed-origins` in `application.yaml` and
  `infrastructure/src/main/kotlin/.../infrastructure/config/CorsConfig.kt`.

## Implemented Security + Notification Baseline
- Query endpoints require valid Cognito JWT + `ROLE_COMPANY` (mapped from
  `cognito:groups`).
- `/admin/**` requires `ROLE_ADMIN` (configured via
  `app.security.cognito.admin-group`).
- On startup, the app syncs Cognito users/groups into local tables
  (`cognito_user`, `cognito_user_group`) and can re-sync via
  `POST /admin/cognito-users/sync`.
- Notifications are per-user. When a **new** customer order is ingested,
  one `INGESTION` notification is created for each **active** local Cognito
  user (`enabled=true`).

## Architecture: Hexagonal (Ports & Adapters), 4 Gradle modules

This is a **true multi-module Gradle project** — not folders within one
module. Each layer is its own Gradle subproject with its own
`build.gradle.kts` and its own dependency set, so the dependency rule below
is enforced by the build itself (a forbidden import is a compile/resolution
error, not just a convention):

```
emerion-dashboard/
├── domain/                     # Gradle module. Pure Kotlin (kotlin("jvm") only). No Spring, no JPA, no OpenAPI.
│   └── src/main/kotlin/br/com/vertice/emerion_dashboard/domain/
│       ├── <resource>/
│       │   ├── model/
│       │   │   └── <Resource>.kt              # domain model (data class)
│       │   ├── repository/
│       │   │   └── <Resource>Repository.kt    # OUTBOUND port (interface)
│       │   └── exception/
│       │       └── <Resource>NotFoundException.kt
│       └── shared/              # Page/PageRequest and other cross-cutting domain types
│
├── application/                # Gradle module. Use cases. implementation(project(":domain")) only.
│   └── src/main/kotlin/br/com/vertice/emerion_dashboard/application/
│       └── <resource>/
│           ├── <functional-concern>/       # e.g. ingestion/, query/ — group by functionality, not by declaration kind
│           │   ├── <Verb><Resource>UseCase.kt   # INBOUND port (interface); group methods for the same concern on one interface
│           │   ├── <Verb><Resource>Service.kt   # @Service implementing the use case, talks to domain ports
│           │   └── model/                       # data classes/enums (commands, results), one per file, even small ones
│           │       ├── <Resource>Command.kt
│           │       └── <Resource>Result.kt
│
├── infrastructure/              # Gradle module. Adapters. The only module allowed to depend on Spring Web/Data JPA/generated OpenAPI code.
│   └── src/main/
│       ├── kotlin/br/com/vertice/emerion_dashboard/infrastructure/
│       │   ├── rest/
│       │   │   ├── common/           # GlobalExceptionHandler, shared REST concerns
│       │   │   └── <resource>/
│       │   │       ├── controller/
│       │   │       │   └── <Resource>...Controller.kt   # implements a generated `...Api` interface, delegates to a use case
│       │   │       └── mapper/
│       │   │           └── <Resource>...RestMapper.kt   # generated DTO <-> domain model, object with pure functions
│       │   ├── persistence/
│       │   │   └── <resource>/
│       │   │       ├── model/
│       │   │       │   └── <Resource>JpaEntity.kt        # @Entity, pure data holder
│       │   │       ├── repository/
│       │   │       │   └── <Resource>SpringDataRepository.kt   # Spring Data JpaRepository
│       │   │       ├── adapter/
│       │   │       │   └── <Resource>RepositoryAdapter.kt       # implements the domain's outbound port
│       │   │       └── mapper/
│       │   │           └── <Resource>PersistenceMapper.kt   # JPA entity <-> domain model, object with pure functions
│       │   └── config/               # CorsConfig, OpenApiConfig, etc.
│       └── resources/
│           ├── openapi/api.yaml               # contract-first spec, codegen input + runtime static resource
│           └── db/migration/                  # Flyway migrations
│   (generated OpenAPI code lands in infrastructure/build/generated/openapi/... — DO NOT EDIT, DO NOT COMMIT)
│
└── app/                          # Gradle module. Bootstrap/composition-root only. Depends on all three above.
    └── src/main/
        ├── kotlin/br/com/vertice/emerion_dashboard/
        │   └── EmerionDashboardApplication.kt   # @SpringBootApplication, the only main class
        └── resources/application.properties
    (integration tests + Testcontainers base class also live here, in app/src/test/)
```

### Dependency rule (strict, build-enforced)
```
domain            implementation(project(":domain"))?           — no project deps at all
application       implementation(project(":domain"))
infrastructure    implementation(project(":domain"))
                  implementation(project(":application"))
app               implementation(project(":domain"))
                  implementation(project(":application"))
                  implementation(project(":infrastructure"))
```
Never the other way around. Only `infrastructure` may import generated
OpenAPI code or JPA annotations. Only `app` applies the real
`org.springframework.boot` plugin and produces a `bootJar` — `domain`,
`application`, and `infrastructure` are plain library jars pulled in as
Gradle `implementation` dependencies. Shared plugin/library versions live in
`gradle/libs.versions.toml` (a Gradle version catalog); every module's
`plugins {}` block declares plugins via bare `id(...)` (not `alias(...)`,
which would reload the Kotlin Gradle plugin once per module) — the actual
versions are pinned once in the root `build.gradle.kts` via
`alias(libs.plugins.x) apply false`.

### Why this shape
- The dependency rule is enforced by Gradle itself: `domain` physically
  cannot resolve a JPA or Spring Web class even if someone tries to import
  it, because those libraries are never on its compile classpath.
- The domain model changing (e.g. a new business rule) never forces a
  change to the DB schema or the API contract, and vice versa — each has its
  own mapper.
- Controllers and repository adapters contain **zero business logic**; they
  translate and delegate. Business logic lives in `application/*Service.kt`.
- Testing is layered: `domain`/`application` unit-tested with MockK against
  port interfaces (no Spring context needed, and no Spring dependency even
  available in `domain`'s test classpath); `app` runs full-context /
  Testcontainers integration tests since it's the only module with every
  other module and the real Spring Boot plugin on its classpath.

### The `Customer` resource is the reference implementation
Every file under `domain/src/main/kotlin/.../domain/customer`,
`application/src/main/kotlin/.../application/customer`, and
`infrastructure/src/main/kotlin/.../infrastructure/{rest,persistence}/customer`
is a complete, working example of the full ingestion + query flow. **When
adding a new resource, copy this shape file-by-file into the same modules**
rather than inventing a new structure. Note: package names
(`br.com.vertice.emerion_dashboard.domain.customer`, etc.) stay the same
style across modules — only the physical Gradle module (and therefore the
top-level `src/main/kotlin/...` directory) changes.

## Reference: The Customer Ingestion + Query Flow
```
emerion-load-service
   │  POST /ingestion/customers  (CustomerIngestionBatch)
   ▼
CustomerIngestionController (infrastructure/rest/customer/controller)
   │  CustomerIngestionRestMapper.toCommand() (infrastructure/rest/customer/mapper)
   ▼
IngestCustomersUseCase / IngestCustomersService (application/customer/ingestion)
   │  upsert-by-externalId, per-item try/catch (partial-failure batch)
   ▼
CustomerRepository port (domain/customer/repository)
   │  implemented by
   ▼
CustomerRepositoryAdapter (infrastructure/persistence/customer/adapter)
   │  Spring Data JPA
   ▼
PostgreSQL `customer` table (Flyway V1__create_customer_table.sql)

React app
   │  GET /customers?page=&size=&status=&name=
   │  GET /customers/{id}
   ▼
CustomerQueryController → CustomerQueryUseCase (application/customer/query) → CustomerRepository → Postgres
```

Ingestion is **idempotent**: re-sending the same batch upserts by
`externalId` (the original Firebird primary key) instead of creating
duplicates. A failure on one item is captured in the response's
`results[]` array and does not abort the rest of the batch — this is
deliberate because `emerion-load-service` may batch hundreds of rows.

## OpenAPI Contract-First Workflow
1. Edit `infrastructure/src/main/resources/openapi/api.yaml` (add paths/schemas).
2. Run `./gradlew openApiGenerate` (or just `compileKotlin`, which depends
   on it) to regenerate interfaces/models under
   `infrastructure/build/generated/openapi/.../infrastructure/rest/generated/`.
3. Implement the new generated `...Api` interface in a
   `infrastructure/rest/<resource>/controller/<Resource>...Controller.kt`,
   delegating to a use case — never put logic in the controller.
4. Add a REST mapper (`object` with pure functions) in
   `infrastructure/rest/<resource>/mapper/<Resource>...RestMapper.kt` to
   convert generated
   DTOs <-> domain models.
5. The same `api.yaml` is also served at runtime as a static resource
   (`/openapi/api.yaml`) and rendered by Swagger UI
   (`springdoc.swagger-ui.url`) — there is only one spec, used for both
   codegen and documentation.

See `.github/instructions/openapi-contract.instructions.md` for details and
gotchas (e.g. reserved-word property renames like `size` → `propertySize`).

## Database & Migrations
- Flyway owns the schema. `spring.jpa.hibernate.ddl-auto=validate` — if
  Hibernate and Flyway disagree, the app fails to start (by design).
- New migrations: `infrastructure/src/main/resources/db/migration/V<n>__description.sql`,
  strictly incrementing, never edit a migration that has already shipped.
- See `.github/instructions/persistence-jpa.instructions.md` and
  `.github/instructions/flyway-migrations.instructions.md`.

## Testing Conventions
- Unit tests (domain/application logic): JUnit 5 + MockK, no Spring
  context, mock the port interfaces (`CustomerRepository`, use-case
  interfaces). Live in `application/src/test/kotlin/...` (or
  `domain/src/test/kotlin/...`). See `IngestCustomersServiceTest` as the
  reference example.
- Integration tests (persistence, full REST flow): live in
  `app/src/test/kotlin/...` (the only module with every layer + Spring Boot
  + Testcontainers on its test classpath) and extend
  `support.PostgresIntegrationTest` (Testcontainers + `@ServiceConnection`,
  no manual `spring.datasource.*` overrides needed).
- See `.github/instructions/testing.instructions.md`.

## Important Guidelines for Copilot
1. **Always ask clarifying questions** when a requirement doesn't specify
   which layer it belongs to, or introduces a new architectural decision
   (auth, new external dependency, new persistence tech).
2. **Follow the Customer resource's file layout exactly** for new resources,
   including which Gradle module each new file goes into.
3. **Never put business logic in controllers or repository adapters.**
4. **Never hand-edit anything under `infrastructure/build/generated/openapi`.**
5. **Write MockK unit tests for every new use-case/service** in the
   `application` module's test source set.
6. **Keep mappers as pure, stateless `object`s** — no Spring beans for
   mapping.
7. Prefer `fun interface` for single-method ports/use-cases (matches the
   existing style).
8. Use `Instant`/`OffsetDateTime` (java.time), never `java.util.Date`.
9. New Flyway migrations are additive-only; never edit a shipped migration.
10. This is an ERP domain — expect more resources beyond `Customer`
    (invoices, products, orders, etc.) as the load-service migration
    progresses; each gets the same domain/application/infrastructure
    treatment, split across the same 4 modules.
11. **Never add a `project(":x")` dependency that violates the dependency
    rule** (e.g. `domain` depending on `application` or `infrastructure`) —
    if a change seems to require this, the port/mapper boundary is wrong.

## Common Tasks

### Adding a new resource end-to-end (e.g. `Product`)
1. `infrastructure/src/main/resources/openapi/api.yaml`: add ingestion path
   + query paths + schemas, run `./gradlew :infrastructure:openApiGenerate`.
2. `domain/src/main/kotlin/.../domain/product/`: `model/Product.kt`,
   `repository/ProductRepository.kt` (port), `exception/ProductNotFoundException.kt`.
3. `application/src/main/kotlin/.../application/product/ingestion/`: ingestion
   use case (commands/results each in their own file) + service;
   `application/src/main/kotlin/.../application/product/query/`: query use
   case + service.
4. `infrastructure/src/main/kotlin/.../infrastructure/persistence/product/`:
   JPA entity, Spring Data repository, repository adapter, persistence
   mapper.
5. `infrastructure/src/main/kotlin/.../infrastructure/rest/product/`:
   controllers implementing the generated interfaces, REST mapper.
6. `infrastructure/src/main/resources/db/migration/V<n>__create_product_table.sql`.
7. MockK unit tests in `application/src/test/kotlin/.../application/product/ingestion/`
   for the use-case service(s).
8. Reference `.github/skills/port-adapter-skill.md` and
   `.github/skills/mapper-skill.md` while doing this.

### Adding a field to an existing resource
1. Add the field to the domain model (`Customer.kt`).
2. Add a Flyway migration (`ALTER TABLE ...`), update the JPA entity.
3. Update the OpenAPI schema, regenerate, update both REST mappers.
4. Update the persistence mapper.
5. Update/add tests.

## When to Ask for Clarification
- Whether a new field/endpoint is for ingestion (load-service → dashboard)
  or for the React-facing query API — they're separate controllers/paths.
- Ambiguous business rules (e.g. how to resolve conflicting `status` values
  on re-ingestion).
- Anything touching auth, security, or exposing new data publicly.
- Large schema changes or anything that isn't purely additive in Flyway.
