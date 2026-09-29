# OpenAPI Contract-First Instructions

## Description
Governs how to evolve the API contract at
`../../adapter/src/main/resources/openapi` and how generated code is consumed.
Apply this whenever adding/changing an endpoint, request, or response shape.

## Single Source of Truth
`../../adapter/src/main/resources/openapi` is:
1. The **codegen input** — `adapter/build.gradle.kts`'s `openApiGenerate` task
   (generator `kotlin-spring`, `interfaceOnly=true`) reads it and produces
   Kotlin interfaces (`...Api`) and data classes (models) under
   `adapter/build/generated/openapi/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/rest/generated/`.
2. The **runtime-served spec** — it's also a static classpath resource, so
   it's reachable at `/openapi/api.yaml` at runtime, and Swagger UI is
   configured (`springdoc.swagger-ui.url`) to render that exact file. There
   is no separate annotation-driven spec (`springdoc.api-docs.enabled=false`).

**Never hand-edit anything under `adapter/build/generated/openapi/...`.**
Regenerate with `./gradlew :adapter:openApiGenerate` (or just `:adapter:compileKotlin`, which
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
3. Run `./gradlew :adapter:openApiGenerate` and inspect the generated file under
   `adapter/build/generated/openapi/.../api/` and `.../model/` before writing the
   controller — the exact Kotlin types/nullability matter.
4. Implement the generated `...Api` interface in a controller under
   `adapter/src/main/kotlin/.../adapter/inbound/rest/`. For simple query
   endpoints, inject the concrete application service and map response DTOs
   in the controller, following `CustomerController` and
   `SmartStockController`. For ingestion, use a REST mapper (`object`) when
   translating request/result models; keep that mapper in the adapter module.

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
- Implement the generated interface in a Spring REST controller, following
  the existing `@RestController` implementations. Inspect generated
  annotations rather than assuming the interface supplies all controller
  behavior.

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
