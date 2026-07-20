# Mapper Skill

## Description
Copy-paste-ready patterns for the two mapper types every resource needs:
REST mappers (generated DTO <-> application command/domain model) and
persistence mappers (JPA entity <-> domain model). Use whenever adding a
field, a new resource, or debugging a "why didn't this field come through"
issue.

## When to Use
- Adding/changing a field that crosses a layer boundary.
- Writing the mapper for a brand-new resource (paired with
  `port-adapter-skill.md`).
- A generated OpenAPI model has an unexpected Kotlin property name/type
  (see "Known Generator Gotchas" in
  `.github/instructions/openapi-contract.instructions.md`) and the mapper
  needs to account for it.

## Rule: Mappers Are Pure `object`s
Never `@Component`/`@Service` a mapper. They have no dependencies and no
state — keeping them as `object`s with pure functions makes them trivially
unit-testable with plain `assertEquals`, no Spring context, no mocking.

## Persistence Mapper Template
```kotlin
object WidgetPersistenceMapper {
    fun toDomain(entity: WidgetJpaEntity): Widget = Widget(
        id = entity.id,
        externalId = entity.externalId,
        name = entity.name,
        status = toDomainStatus(entity.status),
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
    )

    // `existing` lets save() preserve the generated id across an update instead of inserting a duplicate row.
    fun toEntity(domain: Widget, existing: WidgetJpaEntity?): WidgetJpaEntity = WidgetJpaEntity(
        id = existing?.id ?: domain.id,
        externalId = domain.externalId,
        name = domain.name,
        status = toJpaStatus(domain.status),
        createdAt = domain.createdAt,
        updatedAt = domain.updatedAt,
    )

    private fun toDomainStatus(status: WidgetStatusJpa): WidgetStatus = when (status) {
        WidgetStatusJpa.ACTIVE -> WidgetStatus.ACTIVE
        WidgetStatusJpa.INACTIVE -> WidgetStatus.INACTIVE
        WidgetStatusJpa.UNKNOWN -> WidgetStatus.UNKNOWN
    }

    fun toJpaStatus(status: WidgetStatus): WidgetStatusJpa = when (status) {
        WidgetStatus.ACTIVE -> WidgetStatusJpa.ACTIVE
        WidgetStatus.INACTIVE -> WidgetStatusJpa.INACTIVE
        WidgetStatus.UNKNOWN -> WidgetStatusJpa.UNKNOWN
    }
}
```
Note: the JPA-side enum (`WidgetStatusJpa`) and the domain enum
(`WidgetStatus`) are **separate types on purpose** — see
`persistence-jpa.instructions.md`. Exhaustive `when` blocks mean the
compiler forces every mapper to be updated when a new enum value is added
anywhere.

## REST Mapper Template (ingestion side)
```kotlin
object WidgetIngestionRestMapper {
    fun toCommand(dto: WidgetIngestionBatch): IngestBatchCommand = IngestBatchCommand(
        batchId = dto.batchId,
        items = dto.items.map(::toItemCommand),
    )

    private fun toItemCommand(dto: WidgetIngestionItem): IngestWidgetCommand = IngestWidgetCommand(
        externalId = dto.externalId,
        name = dto.name,
        status = toDomainStatus(dto.status),
        createdAt = dto.createdAt?.toInstant(),   // OpenAPI date-time -> OffsetDateTime -> domain Instant
    )

    fun toResponse(result: IngestBatchResult): IngestionResult = IngestionResult(
        batchId = result.batchId,
        totalReceived = result.totalReceived,
        totalSucceeded = result.totalSucceeded,
        totalFailed = result.totalFailed,
        results = result.results.map {
            IngestionItemResult(
                externalId = it.externalId,
                // Nested-enum gotcha: `IngestionItemResult.Outcome`, not a top-level type — see openapi-contract.instructions.md
                outcome = IngestionItemResult.Outcome.valueOf(it.outcome.name),
                errorMessage = it.errorMessage,
            )
        },
    )

    private fun toDomainStatus(status: ApiWidgetStatus?): WidgetStatus = when (status) {
        ApiWidgetStatus.ACTIVE -> WidgetStatus.ACTIVE
        ApiWidgetStatus.INACTIVE -> WidgetStatus.INACTIVE
        ApiWidgetStatus.UNKNOWN, null -> WidgetStatus.UNKNOWN
    }
}
```

## REST Mapper Template (query side)
```kotlin
object WidgetQueryRestMapper {
    fun toResponse(widget: Widget): WidgetResponse = WidgetResponse(
        id = widget.id,
        externalId = widget.externalId,
        name = widget.name,
        status = toApiStatus(widget.status),
        createdAt = widget.createdAt.atOffset(ZoneOffset.UTC),
        updatedAt = widget.updatedAt.atOffset(ZoneOffset.UTC),
    )

    fun toPageResponse(page: DomainPage<Widget>): WidgetPage = WidgetPage(
        data = page.content.map(::toResponse),
        pagination = PaginationInfo(
            total = page.totalElements,
            page = page.page,
            propertySize = page.size,   // check the generated model for renamed properties like this one!
            totalPages = page.totalPages,
        ),
    )

    fun toDomainStatus(status: ApiWidgetStatus?): WidgetStatus? = when (status) {
        ApiWidgetStatus.ACTIVE -> WidgetStatus.ACTIVE
        ApiWidgetStatus.INACTIVE -> WidgetStatus.INACTIVE
        ApiWidgetStatus.UNKNOWN -> WidgetStatus.UNKNOWN
        null -> null
    }

    private fun toApiStatus(status: WidgetStatus): ApiWidgetStatus = when (status) {
        WidgetStatus.ACTIVE -> ApiWidgetStatus.ACTIVE
        WidgetStatus.INACTIVE -> ApiWidgetStatus.INACTIVE
        WidgetStatus.UNKNOWN -> ApiWidgetStatus.UNKNOWN
    }
}
```

## Import Aliasing Convention
When a mapper needs both the domain type and the generated/JPA type of the
same conceptual name (e.g. `CustomerStatus` exists in both `domain.customer`
and `infrastructure.rest.generated.model`), alias the imports for clarity
instead of using fully-qualified names inline:
```kotlin
import br.com.vertice.emerion_dashboard.domain.customer.CustomerStatus as DomainCustomerStatus
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerStatus as ApiCustomerStatus
```
See `CustomerQueryRestMapper.kt` / `CustomerIngestionRestMapper.kt` for the
real example.

## Testing Mappers
```kotlin
class WidgetPersistenceMapperTest {
    @Test
    fun `round-trips through entity without losing data`() {
        val domain = Widget(id = 1L, externalId = "FB-1", name = "Bolt", status = WidgetStatus.ACTIVE, createdAt = ..., updatedAt = ...)
        val entity = WidgetPersistenceMapper.toEntity(domain, existing = null)
        assertEquals(domain, WidgetPersistenceMapper.toDomain(entity))
    }
}
```
No mocking needed — mappers are pure functions.
