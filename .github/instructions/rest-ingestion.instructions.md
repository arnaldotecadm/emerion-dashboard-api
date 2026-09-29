# REST Ingestion

Apply when adding or changing an endpoint called by
`emerion-load-service`. The load service owns Firebird extraction and
transformation; this API accepts clean JSON and persists it to PostgreSQL.
New ingestion controllers belong in `:adapter`; existing Customer
ingestion REST code in `:infrastructure` is legacy.

## Contract and controller

- Define batch and single-record paths/schemas in
  `adapter/src/main/resources/openapi/api.yaml`; follow
  `openapi-contract.instructions.md`.
- Implement the generated interface in
  `adapter/inbound/rest/<Resource>IngestionController.kt`.
- Keep controllers thin: translate request to application input, call the
  ingestion service/use case, translate the result. Put reusable or
  non-trivial DTO conversions in a stateless REST mapper under
  `adapter/inbound/rest/mapper/`.

## Application behavior

- Group ingestion behavior in `application/<resource>/ingestion/`.
  Use one interface for related ingestion operations when an inbound port
  is useful; keep command/result models in the same feature package.
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
