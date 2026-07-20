# OpenAPI Contract-First Instructions

## Description
Governs how to evolve the API contract at
`infrastructure/src/main/resources/openapi/api.yaml` and how generated code is consumed.
Apply this whenever adding/changing an endpoint, request, or response shape.

## Single Source of Truth
`infrastructure/src/main/resources/openapi/api.yaml` is:
1. The **codegen input** — `infrastructure/build.gradle.kts`'s `openApiGenerate` task
   (generator `kotlin-spring`, `interfaceOnly=true`) reads it and produces
   Kotlin interfaces (`...Api`) and data classes (models) under
   `infrastructure/build/generated/openapi/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/rest/generated/`.
2. The **runtime-served spec** — it's also a static classpath resource, so
   it's reachable at `/openapi/api.yaml` at runtime, and Swagger UI is
   configured (`springdoc.swagger-ui.url`) to render that exact file. There
   is no separate annotation-driven spec (`springdoc.api-docs.enabled=false`).

**Never hand-edit anything under `infrastructure/build/generated/openapi/...`.**
Regenerate with `./gradlew :infrastructure:openApiGenerate` (or just `:infrastructure:compileKotlin`, which
depends on it) after editing the YAML.

## Adding a New Endpoint
1. Add the `path` + `operationId` under the right `tags` group in
   `api.yaml`. `operationId` drives the generated method name — pick it
   like a Kotlin function name (`listCustomers`, `getCustomerById`).
2. Define/extend `components.schemas` for request/response bodies.
   - Batch ingestion payloads: request schema should include a `batchId`
     string (for load-service tracing/logging) and an `items` array.
   - Query/list responses: wrap in a `<Resource>Page` schema with `data`
     and a shared `PaginationInfo` schema (`total`, `page`, `size`,
     `totalPages`) — matches `CustomerPage`/`PaginationInfo`.
   - Errors: reuse the existing `ErrorResponse` schema (`error.code`,
     `error.message`, `error.details`, `timestamp`) — don't invent a new
     error shape per endpoint.
3. Run `./gradlew :infrastructure:openApiGenerate` and inspect the generated file under
   `infrastructure/build/generated/openapi/.../api/` and `.../model/` before writing the
   controller — the exact Kotlin types/nullability matter.
4. Implement the generated `...Api` interface in a new or existing
   controller (`infrastructure/rest/<resource>/controller/`). Add a REST
   mapper (`object`) in `infrastructure/rest/<resource>/mapper/` to
   translate to/from the application layer.

## Known Generator Gotchas (kotlin-spring, openapi-generator 7.9.0)
- **Reserved-word property renaming**: a schema property literally named
  `size` gets renamed to `propertySize` in the generated Kotlin class
  (`PaginationInfo.propertySize`), while the wire JSON property name stays
  `size` (`@get:JsonProperty("size")`). Always check the generated file for
  renamed properties before wiring a mapper — don't guess from the YAML.
- **Nested enums**: an inline `type: string, enum: [...]` property (not a
  reusable `$ref`'d schema) generates a **nested enum class** named
  `<Model>.<PropertyName Capitalized>`, e.g. `IngestionItemResult.Outcome`,
  not a top-level type. If you want a top-level reusable enum (like
  `CustomerStatus`), extract it into its own named schema in
  `components.schemas` and `$ref` it.
- **date-time format** → `java.time.OffsetDateTime` (not `Instant`). Domain
  models use `Instant`; REST mappers convert with
  `instant.atOffset(ZoneOffset.UTC)` / `offsetDateTime.toInstant()`.
- **`invokerPackage`** is ignored by the `kotlin-spring` generator — use
  `packageName` if you ever need to change it (not currently used, see
  `build.gradle.kts`).
- Controller interfaces are generated with `@RestController @Validated`
  already on the interface — do **not** re-add `@RestController` behavior
  conflicts, just implement the interface plainly (see
  `CustomerIngestionController`).

## Contract Design Conventions
- Base path is `/api/v1` via `server.servlet.context-path` — do **not**
  prefix individual OpenAPI `paths` with `/api/v1` (the `servers:` block in
  `api.yaml` documents it, but generated `@RequestMapping` values stay
  relative, e.g. `/customers`).
- Ingestion endpoints (load-service → dashboard): `POST
  /ingestion/<resource>`, tag `<resource>-ingestion`, batch-shaped request
  (`batchId` + `items`), response reports per-item outcome
  (`CREATED`/`UPDATED`/`FAILED`) — never a bare 200 with no detail, since
  load-service needs to know which rows failed.
- Query endpoints (React-facing): `GET /<resources>` (paginated, filterable
  via query params) and `GET /<resources>/{id}`, tag `<resources>`.
- Pagination query params: `page` (default 0), `size` (default 20, max
  100) — matches `api-structure` conventions from the load-service project.

## Validation Annotations
The generator applies `jakarta.validation` annotations
(`@NotNull`/`@Min`/`@Max`/etc.) from the YAML's `required`/`minimum`/
`maximum` keywords directly onto the generated interface method
parameters — you get request validation "for free" as long as the YAML
constraints are accurate. Keep constraints in the YAML, not in the
controller.
