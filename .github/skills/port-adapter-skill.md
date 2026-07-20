# Port & Adapter Skill

## Description
Copy-paste-ready template for adding a brand-new resource end-to-end,
following the hexagonal architecture used throughout emerion-dashboard.
Use this whenever the request is "add a new resource/entity" (e.g.
"migrate the PRODUCT table", "add Invoice ingestion").

## When to Use
- A new business resource is being added to the ERP (beyond `Customer`).
- You need the domain model, port, use cases, persistence adapter, and REST
  adapter for that resource, wired consistently with the rest of the
  codebase.

## Step 0 — OpenAPI First
Add to `infrastructure/src/main/resources/openapi/api.yaml` (see
`.github/instructions/openapi-contract.instructions.md`):
```yaml
paths:
  /ingestion/widgets:
    post:
      tags: [widget-ingestion]
      operationId: ingestWidgets
      requestBody:
        content:
          application/json:
            schema: { $ref: '#/components/schemas/WidgetIngestionBatch' }
      responses:
        '200':
          content:
            application/json:
              schema: { $ref: '#/components/schemas/IngestionResult' }  # reuse the generic shape if fields match
  /widgets:
    get: { ... paginated list, same shape as /customers ... }
  /widgets/{id}:
    get: { ... same shape as /customers/{id} ... }
```
Run `./gradlew :infrastructure:openApiGenerate` and read the generated
`WidgetIngestionApi.kt` / `WidgetsApi.kt` / model classes (under
`infrastructure/build/generated/openapi/...`) before writing anything else —
the exact generated types (nullability, nested enums, renamed properties)
drive the mapper code below.

## Step 1 — Domain (`:domain` module)
```kotlin
// domain/src/main/kotlin/br/com/vertice/emerion_dashboard/domain/widget/Widget.kt
package br.com.vertice.emerion_dashboard.domain.widget

import java.time.Instant

enum class WidgetStatus { ACTIVE, INACTIVE, UNKNOWN }

data class Widget(
    val id: Long?,
    val externalId: String,
    val name: String,
    val status: WidgetStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun newFromIngestion(externalId: String, name: String, status: WidgetStatus, createdAt: Instant?, now: Instant) =
            Widget(id = null, externalId = externalId, name = name, status = status, createdAt = createdAt ?: now, updatedAt = now)
    }
    fun mergeFromIngestion(name: String, status: WidgetStatus, now: Instant) =
        copy(name = name, status = status, updatedAt = now)
}
```
```kotlin
// domain/src/main/kotlin/br/com/vertice/emerion_dashboard/domain/widget/WidgetRepository.kt
package br.com.vertice.emerion_dashboard.domain.widget

import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest

interface WidgetRepository {
    fun findById(id: Long): Widget?
    fun findByExternalId(externalId: String): Widget?
    fun findAll(pageRequest: PageRequest, status: WidgetStatus?, nameContains: String?): Page<Widget>
    fun save(widget: Widget): Widget
}
```

## Step 2 — Application (use cases, `:application` module)
Copy `application/src/main/kotlin/.../application/customer/IngestCustomersUseCase.kt`,
`IngestCustomersService.kt` and `CustomerQueryService.kt` verbatim into
`application/src/main/kotlin/.../application/widget/`, renaming `Customer` →
`Widget` throughout. Keep the per-item try/catch in the ingestion service
(partial failure handling) and the fixed `Clock` constructor parameter.

## Step 3 — Persistence Adapter (`:infrastructure` module)
Copy `infrastructure/src/main/kotlin/.../infrastructure/persistence/customer/CustomerJpaEntity.kt`,
`CustomerSpringDataRepository.kt`, `CustomerRepositoryAdapter.kt`,
`CustomerPersistenceMapper.kt` verbatim into
`infrastructure/src/main/kotlin/.../infrastructure/persistence/widget/`,
renaming. Add a matching Flyway migration
(`infrastructure/src/main/resources/db/migration/V<n>__create_widget_table.sql`,
see `.github/instructions/flyway-migrations.instructions.md`).

## Step 4 — REST Adapter (`:infrastructure` module)
Copy `infrastructure/src/main/kotlin/.../infrastructure/rest/customer/controller/CustomerIngestionController.kt`
+ `CustomerQueryController.kt` into
`infrastructure/src/main/kotlin/.../infrastructure/rest/widget/controller/`,
and `infrastructure/src/main/kotlin/.../infrastructure/rest/customer/mapper/CustomerIngestionRestMapper.kt`
+ `CustomerQueryRestMapper.kt` into
`infrastructure/src/main/kotlin/.../infrastructure/rest/widget/mapper/`,
renaming and adjusting to the generated `Widget...Api`/model types from Step 0.
Double-check generated property names for renames (e.g. reserved words)
before wiring the mapper — see the "Known Generator Gotchas" section of
`openapi-contract.instructions.md`.

## Step 5 — Tests
Copy `application/src/test/kotlin/.../application/customer/IngestCustomersServiceTest.kt`
into `application/src/test/kotlin/.../application/widget/`, renaming,
keeping all three cases (create / idempotent update / partial failure). Add
an integration test in `app/src/test/kotlin/...` extending
`support.PostgresIntegrationTest` if the resource has any non-trivial query
logic worth covering against a real Postgres.

## Checklist
- [ ] OpenAPI paths + schemas added, `:infrastructure:openApiGenerate` run
- [ ] `domain/src/main/kotlin/.../domain/<x>/` — model + port
- [ ] `application/src/main/kotlin/.../application/<x>/` — use cases + services
- [ ] `infrastructure/src/main/kotlin/.../infrastructure/persistence/<x>/` — entity, Spring Data repo, adapter, mapper
- [ ] Flyway migration (`infrastructure/src/main/resources/db/migration/`)
- [ ] `infrastructure/src/main/kotlin/.../infrastructure/rest/<x>/` — controllers + REST mappers
- [ ] MockK unit tests in `application/src/test/kotlin/.../application/<x>/` for the ingestion service (3 cases minimum)
- [ ] `./gradlew build` passes
