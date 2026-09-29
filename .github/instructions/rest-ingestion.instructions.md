# REST Ingestion

Apply when adding or changing an endpoint called by
`emerion-load-service`. The load service owns Firebird extraction and
transformation; this API accepts clean JSON and persists it to PostgreSQL.
New ingestion controllers belong in `:adapter`; Fincre, ICMS, and IPI have
been migrated there as reference flows. Customer's and Vendedor's ingestion
REST code still lives in `:infrastructure` as legacy, pending migration.

## Contract and controller

- Define batch and single-record paths/schemas in
  `adapter/src/main/resources/openapi/api.yaml`; follow
  `openapi-contract.instructions.md`.
- Implement the generated interface in
  `adapter/inbound/rest/<Resource>IngestionController.kt`, injecting the
  concrete `Ingest<Resource>Service` directly — do not add an
  `Ingest<Resource>UseCase` interface for a single controller/single-adapter
  flow; that indirection only earns its keep with multiple driving adapters
  or a genuinely distinct ingestion workflow (see
  `hexagonal-architecture.instructions.md`).
- Keep controllers thin: translate request to application input, call the
  ingestion service, translate the result. Put DTO conversions in a
  `<Resource>IngestionRestMapper` object in the centralized `adapter/mapper/`
  package, using extension functions (`fun <Dto>.toCommand(): <Command>`,
  `fun IngestBatchResult.toResponse(): IngestionResult`) rather than
  standalone functions.

## Application behavior

- Group ingestion behavior in `application/<resource>/ingestion/` as a
  single `Ingest<Resource>Service` `@Service` — no separate use-case
  interface unless a second driving adapter needs it. Keep command/result
  models in the same feature package.
- Upsert by `externalId` (or the resource's natural key) so retries do not
  create duplicate rows. Keep the persistence abstraction as an
  `application/outbound/port/<Resource>RepositoryPort`.
- Process each batch item independently. Return one outcome per item;
  a failure on one item must not prevent processing the rest. Log the batch
  summary and each failed item with its correlation key.
- Reuse the same per-item upsert logic for batch and single-record calls.
  Do not duplicate ingestion rules in controllers.
- Business side-effects belong in the application service after successful
  persistence. Current behavior: newly created customer orders generate an
  ingestion notification for each enabled local Cognito user; notification
  delivery failures are logged per user and do not abort the batch.

Ingestion is authenticated by the `X-API-Key` filter; preserve that security
boundary. Do not add Firebird-specific parsing, NULL handling, or extraction
logic here.

## Tests

Use MockK unit tests for create, idempotent update, and partial batch
failure. Use a PostgreSQL integration test when validating persistence,
migrations, or full request wiring; see `testing.instructions.md`.
