# Hexagonal Architecture Instructions

## Description
Governs how code is organized across the 4 Gradle modules — `domain`,
`application`, `infrastructure`, `app` — in emerion-dashboard. Apply this
whenever creating a new resource, port, use case, or adapter.

## The Rule
```
domain          Gradle module. No project() dependencies at all.
application     Gradle module. implementation(project(":domain"))
infrastructure  Gradle module. implementation(project(":domain")), implementation(project(":application"))
app             Gradle module. implementation on all three above; the only module with the org.springframework.boot plugin.
```
Never the reverse. If you find yourself needing to add a `project(":x")`
dependency that isn't in this table (e.g. `domain` depending on
`application`), stop — that's a sign the port/mapper boundary is wrong.
This isn't just a convention: `domain`'s `build.gradle.kts` never declares
Spring/JPA/OpenAPI-generator dependencies, so importing those types there is
a compile error, not just a style violation.

## Module Template (copy for every new resource `<X>`)

```
domain/src/main/kotlin/br/com/vertice/emerion_dashboard/domain/x/model/X.kt                         # data class, plain Kotlin, no annotations
domain/src/main/kotlin/br/com/vertice/emerion_dashboard/domain/x/repository/XRepository.kt          # outbound port interface
domain/src/main/kotlin/br/com/vertice/emerion_dashboard/domain/x/exception/XNotFoundException.kt    # domain exception (optional per resource)

application/src/main/kotlin/br/com/vertice/emerion_dashboard/application/x/query/XQueryUseCase.kt          # interface { fun getById(id: Long): X; fun list(query: ListXQuery): Page<X> }
application/src/main/kotlin/br/com/vertice/emerion_dashboard/application/x/query/XQueryService.kt          # @Service implementing XQueryUseCase
application/src/main/kotlin/br/com/vertice/emerion_dashboard/application/x/query/model/ListXQuery.kt        # data class, one per file
application/src/main/kotlin/br/com/vertice/emerion_dashboard/application/x/ingestion/IngestXUseCase.kt      # interface { fun ingest(cmd: IngestXBatchCommand): IngestXBatchResult; fun ingestSingle(cmd: IngestXCommand): IngestXItemResult }
application/src/main/kotlin/br/com/vertice/emerion_dashboard/application/x/ingestion/IngestXService.kt      # @Service implementing IngestXUseCase
application/src/main/kotlin/br/com/vertice/emerion_dashboard/application/x/ingestion/model/IngestXCommand.kt      # data class, one per file
application/src/main/kotlin/br/com/vertice/emerion_dashboard/application/x/ingestion/model/IngestXBatchCommand.kt # data class, one per file
application/src/main/kotlin/br/com/vertice/emerion_dashboard/application/x/ingestion/model/IngestXOutcome.kt      # enum, its own file
application/src/main/kotlin/br/com/vertice/emerion_dashboard/application/x/ingestion/model/IngestXItemResult.kt   # data class, one per file
application/src/main/kotlin/br/com/vertice/emerion_dashboard/application/x/ingestion/model/IngestXBatchResult.kt  # data class, one per file
application/src/test/kotlin/br/com/vertice/emerion_dashboard/application/x/ingestion/IngestXServiceTest.kt  # MockK unit test

infrastructure/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/persistence/x/model/XJpaEntity.kt      # @Entity, data holder only
infrastructure/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/persistence/x/repository/XSpringDataRepository.kt
infrastructure/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/persistence/x/adapter/XRepositoryAdapter.kt      # implements domain.x.repository.XRepository
infrastructure/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/persistence/x/mapper/XPersistenceMapper.kt      # object, JPA entity <-> domain model
infrastructure/src/main/resources/db/migration/V<n>__create_x_table.sql

infrastructure/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/rest/x/controller/XIngestionController.kt           # implements generated XIngestionApi
infrastructure/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/rest/x/controller/XQueryController.kt               # implements generated XApi (or XsApi)
infrastructure/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/rest/x/mapper/XIngestionRestMapper.kt            # object, generated DTO <-> app command/result
infrastructure/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/rest/x/mapper/XQueryRestMapper.kt                # object, generated DTO <-> domain model
```

Note how `application/x/` is split into functional subpackages
(`ingestion/`, `query/`) rather than grouping files by class kind — every
data class/enum/interface gets its own file (see "File Granularity" below),
so the subpackage is what keeps a functional area's files together. Within
each functional subpackage, data classes/enums (commands, results,
queries, outcomes) live in a further `model/` subpackage, while the
use-case interface and its `@Service` implementation stay directly under
the functional subpackage — mirroring the domain module's `model/`
convention and keeping the "data" vs. "behavior" classes visibly separated.

No files are needed in `app/` for a new resource unless it needs a
dedicated integration test (`app/src/test/kotlin/...`) — `app` only holds
the `@SpringBootApplication` main class, `application.properties`, and
full-context/Testcontainers tests, which cover every resource generically.

## File Granularity
- **One class/interface/enum per file.** Even a small `data class` (e.g. a
  command or result type with 2-3 fields) gets its own file — never bundle
  multiple declarations "for convenience" in one file (see
  `IngestCustomerCommand.kt`, `IngestBatchCommand.kt`, `IngestOutcome.kt`,
  `IngestItemResult.kt`, `IngestBatchResult.kt` as the reference example:
  five separate files instead of one file with five declarations).
- **Group by functionality, not by declaration kind.** Don't create
  top-level `commands/`, `results/`, `enums/` folders that group by
  Kotlin-language-construct across the whole module — instead, group files
  by the functional concern they serve (e.g. `application/customer/ingestion/`
  vs `application/customer/query/`), matching the domain package split of
  `model/`/`repository/`/`exception/`.
- **Within a functional subpackage, separate data from behavior.** Data
  classes and enums (commands, results, queries, outcomes) go in a `model/`
  subpackage of the functional package (e.g.
  `application/customer/ingestion/model/`,
  `application/customer/query/model/`); the use-case interface and its
  `@Service` implementation stay directly under the functional package
  (e.g. `application/customer/ingestion/IngestCustomersUseCase.kt`,
  `IngestCustomersService.kt`) since they hold behavior, not data. The same
  data/behavior split applies in `:infrastructure`'s persistence adapters:
  the `@Entity` (pure data holder) lives in
  `infrastructure/persistence/<resource>/model/`, while the Spring Data
  repository interface and the `@Component` adapter (both behavior) stay
  directly under `infrastructure/persistence/<resource>/`.
- **One interface per domain concern, not one interface per method.** When
  two use-case methods operate on the same functional concern (e.g.
  `getById` + `list` are both "query the customer"; `ingest` + `ingestSingle`
  are both "ingest a customer"), declare them on the *same* interface
  (`CustomerQueryUseCase`, `IngestCustomersUseCase`) instead of splitting
  into `GetCustomerUseCase`/`ListCustomersUseCase`. Only split into separate
  interfaces when the methods serve genuinely unrelated concerns.

## Ports (interfaces) — naming and shape
- **Outbound port** (domain calls out to infrastructure): named
  `<Resource>Repository`, lives in `domain/<resource>/repository/`. Expressed purely in
  domain types — never accept/return a JPA entity, a generated DTO, or a
  Spring `Pageable`/`Page`. Use `domain.shared.Page`/`PageRequest` instead.
- **Inbound port** (infrastructure calls into application): named
  `<Verb><Resource>UseCase` (or `<Resource>QueryUseCase` for the read side).
  Group every method that serves the same functional concern on one
  interface (see "File Granularity" above) rather than declaring a `fun
  interface` per method — this keeps related use-case methods discoverable
  together and avoids an explosion of near-identical single-method
  interfaces as a resource grows. Reserve Kotlin's `fun interface` (SAM) for
  the rare case where a concern truly has (and will keep) exactly one
  method.

## Adapters — one job each
- **Controller** (`infrastructure/rest/<x>/controller/...Controller.kt`):
  implements a generated OpenAPI interface. Its body is exactly: map
  request DTO → application command/query (via the REST mapper, imported
  from the sibling `mapper` package), call the use case, map result →
  response DTO (via the REST mapper). No `if`/business rules here.
- **Repository adapter** (`infrastructure/persistence/<x>/adapter/...RepositoryAdapter.kt`):
  implements the domain's outbound port on top of Spring Data JPA (the
  Spring Data repository interface lives alongside it in a sibling
  `infrastructure/persistence/<x>/repository/` package). Same
  rule — translate and delegate, no business rules.
- **Mappers** (`infrastructure/rest/<x>/mapper/...RestMapper.kt` and
  `infrastructure/persistence/<x>/mapper/...PersistenceMapper.kt`) are plain
  Kotlin `object`s with pure functions (`toDomain`,
  `toEntity`, `toResponse`, `toCommand`). Never annotate them `@Component` —
  they don't need DI, and keeping them as `object`s makes them trivially
  unit-testable without a Spring context.

## Use-case services — where business logic lives
- Annotated `@Service`, implement one or more use-case interfaces.
- Take domain ports (not adapters) as constructor dependencies — depend on
  `CustomerRepository` (the interface), never on `CustomerRepositoryAdapter`
  (the concrete adapter class) or `CustomerSpringDataRepository`.
- `@Transactional` on write operations, `@Transactional(readOnly = true)` on
  reads — see `IngestCustomersService` / `CustomerQueryService` for the
  pattern.
- Accept a `Clock` as a constructor parameter with a
  `Clock.systemUTC()` default (see `IngestCustomersService`) so tests can
  inject `Clock.fixed(...)` instead of depending on wall-clock time.

## What NOT to do
- ❌ Don't import `infrastructure.rest.generated.*` or
  `infrastructure.persistence.*` types into `domain` or `application`.
- ❌ Don't put `@RestController`/`@Repository`/business validation logic in
  the same class — each adapter class has exactly one responsibility.
- ❌ Don't let a use-case service depend on a concrete adapter class instead
  of the port interface — this breaks testability with MockK.
- ❌ Don't skip the mapper and pass a generated DTO or a JPA entity across a
  layer boundary "just this once."

## Worked Example
The `Customer` resource (search for "customer" across `domain/`,
`application/`, and `infrastructure/`) is a complete reference
implementation of every rule above. Copy it file-by-file for new resources.
