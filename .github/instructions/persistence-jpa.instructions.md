# Persistence (Spring Data JPA)

New persistence adapters belong in `:adapter` and implement outbound ports
declared in `:application`. Group files by technical role, not by resource:

```text
adapter/outbound/persistence/
  entity/<Resource>JpaEntity.kt
  projection/<Resource>Projection.kt
  repository/<Resource>Repository.kt
  mapper/<Resource>PersistenceMapper.kt
  port/<Resource>RepositoryPortAdapter.kt
```

## Entity

- Keep JPA entities in `entity/`; use `var` properties with defaults and
  Kotlin's JPA plugin (configured in `adapter/build.gradle.kts`).
- Use `@Id @GeneratedValue(IDENTITY)` with PostgreSQL identity columns.
- For ingested resources, enforce uniqueness on the external/natural key
  used for upserts.
- Use `Instant` for `TIMESTAMPTZ`. Keep entity-local enum types with
  `@Enumerated(EnumType.STRING)` rather than persisting domain enum classes.

## One repository per entity

Define one `<Resource>Repository : JpaRepository<...>` in `repository/`.
Keep the entity's derived lookups, upsert/CRUD operations, and native
projection-based read queries together in this interface. Do not create
separate read and write repository interfaces for the same entity.

- Use inherited `findById` and derived natural-key lookups to preserve the
  existing row identity on upsert.
- Use native `@Query` methods for filtered/paginated reads, returning
  projection interfaces or `Page<Projection>`—not JPA entities.
- Give native selected columns aliases matching projection getter names.
  Provide a matching `countQuery` for paginated queries.
- Use `:param IS NULL OR ...` for a modest number of optional filters;
  switch to a query builder/full-text approach if filter complexity grows.
- Spring `Page`/`Pageable` stay within `:adapter`; translate to/from
  `domain.shared.Page`/`PageRequest` at the port adapter boundary.

PostgreSQL supports pageable native queries directly; no custom
`JdbcTemplate`/implementation class is needed for projections.

## Projection, mapper, and port adapter

- Put read projections in `projection/` as read-only interfaces with a
  getter for each selected column.
- Put persistence conversion in a pure `object` under `mapper/`, with
  overloads to map entity and projection results to domain types and to map
  domain types to entities. Pass the existing entity when saving so an
  update preserves its generated id.
- Put `<Resource>RepositoryPortAdapter` in `port/`. It implements the
  application port and translates/delegates; it owns no business rules and
  does not expose JPA, projection, or Spring pagination types.

## Testing

Test pure persistence mappers with plain JUnit assertions. Test repository
and adapter behavior against PostgreSQL in `:app` using
`support.PostgresIntegrationTest`; this validates native projections,
upserts, and Flyway-created schema together. See Customer for the current
reference implementation.
