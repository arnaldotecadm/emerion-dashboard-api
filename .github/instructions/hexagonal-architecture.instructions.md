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
domain/src/main/kotlin/br/com/vertice/emerion_dashboard/domain/x/X.kt                         # data class, plain Kotlin, no annotations
domain/src/main/kotlin/br/com/vertice/emerion_dashboard/domain/x/XRepository.kt               # outbound port interface
domain/src/main/kotlin/br/com/vertice/emerion_dashboard/domain/x/XNotFoundException.kt        # domain exception (optional per resource)

application/src/main/kotlin/br/com/vertice/emerion_dashboard/application/x/GetXUseCase.kt          # fun interface { fun getById(id: Long): X }
application/src/main/kotlin/br/com/vertice/emerion_dashboard/application/x/ListXUseCase.kt         # fun interface { fun list(query: ListXQuery): Page<X> }
application/src/main/kotlin/br/com/vertice/emerion_dashboard/application/x/IngestXUseCase.kt        # fun interface { fun ingest(cmd: IngestXBatchCommand): IngestXBatchResult }
application/src/main/kotlin/br/com/vertice/emerion_dashboard/application/x/XQueryService.kt         # @Service implementing Get.../List...UseCase
application/src/main/kotlin/br/com/vertice/emerion_dashboard/application/x/IngestXService.kt        # @Service implementing Ingest...UseCase
application/src/test/kotlin/br/com/vertice/emerion_dashboard/application/x/IngestXServiceTest.kt    # MockK unit test

infrastructure/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/persistence/x/XJpaEntity.kt
infrastructure/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/persistence/x/XSpringDataRepository.kt
infrastructure/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/persistence/x/XRepositoryAdapter.kt      # implements domain.x.XRepository
infrastructure/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/persistence/x/XPersistenceMapper.kt      # object, JPA entity <-> domain model
infrastructure/src/main/resources/db/migration/V<n>__create_x_table.sql

infrastructure/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/rest/x/XIngestionController.kt           # implements generated XIngestionApi
infrastructure/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/rest/x/XQueryController.kt               # implements generated XApi (or XsApi)
infrastructure/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/rest/x/XIngestionRestMapper.kt            # object, generated DTO <-> app command/result
infrastructure/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/rest/x/XQueryRestMapper.kt                # object, generated DTO <-> domain model
```

No files are needed in `app/` for a new resource unless it needs a
dedicated integration test (`app/src/test/kotlin/...`) — `app` only holds
the `@SpringBootApplication` main class, `application.properties`, and
full-context/Testcontainers tests, which cover every resource generically.

## Ports (interfaces) — naming and shape
- **Outbound port** (domain calls out to infrastructure): named
  `<Resource>Repository`, lives in `domain/<resource>/`. Expressed purely in
  domain types — never accept/return a JPA entity, a generated DTO, or a
  Spring `Pageable`/`Page`. Use `domain.shared.Page`/`PageRequest` instead.
- **Inbound port** (infrastructure calls into application): named
  `<Verb><Resource>UseCase`. Use Kotlin's `fun interface` (SAM) when the
  port has exactly one method — this keeps call sites terse
  (`ingestCustomersUseCase.ingest(cmd)`) and makes intent obvious. Use a
  regular `interface` only if a use case genuinely needs multiple methods
  that share state/dependencies (rare — prefer splitting into more use
  cases).

## Adapters — one job each
- **Controller** (`infrastructure/rest/<x>/...Controller.kt`): implements a
  generated OpenAPI interface. Its body is exactly: map request DTO →
  application command/query (via the REST mapper), call the use case, map
  result → response DTO (via the REST mapper). No `if`/business rules here.
- **Repository adapter** (`infrastructure/persistence/<x>/...RepositoryAdapter.kt`):
  implements the domain's outbound port on top of Spring Data JPA. Same
  rule — translate and delegate, no business rules.
- **Mappers** are plain Kotlin `object`s with pure functions (`toDomain`,
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
