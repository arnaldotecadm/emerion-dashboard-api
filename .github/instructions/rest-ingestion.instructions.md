# REST Ingestion (Receiving Data from emerion-load-service) Instructions

## Description
Governs the ingestion side of the API: endpoints called by
`emerion-load-service` to push already-transformed Firebird data into
Postgres. Apply this whenever adding an ingestion endpoint for a new
resource.

## Context
`emerion-load-service` already did the hard work: it queried Firebird,
mapped rows into strongly-typed models, and now POSTs them here as JSON.
This API's ingestion controllers are **not** an ETL pipeline — they are a
thin, idempotent upsert layer. All Firebird-specific concerns (native SQL,
NULL semantics, encoding, batching against the legacy DB) live in
`emerion-load-service`, not here — do not reintroduce that complexity in
this repo.

## Contract Shape (see openapi-contract.instructions.md for schema rules)
```
POST /ingestion/<resource>
{
  "batchId": "correlation id from load-service",
  "items": [ { ...one resource record... }, ... ]
}
→
{
  "batchId": "...",
  "totalReceived": N,
  "totalSucceeded": N,
  "totalFailed": N,
  "results": [ { "externalId": "...", "outcome": "CREATED|UPDATED|FAILED", "errorMessage": "..." } ]
}
```

### Single-Record Variant
Every ingestion resource also gets a `POST /ingestion/<resource>/single`
endpoint that accepts one record directly (no `batchId`/`items` envelope)
and returns a single `{ "externalId", "outcome", "errorMessage" }` result —
for callers (like the first version of emerion-load-service) that send
records one at a time instead of batching them. It is not a separate code
path: the inbound use-case interface exposes both `ingest(batchCommand)`
and `ingestSingle(itemCommand)`, and `ingestSingle` is implemented by
reusing the exact same per-item upsert logic as the batch path (see
`IngestCustomersUseCase`/`IngestCustomersService`) — never duplicate the
upsert/idempotency logic between the two.

## Idempotency Is Mandatory
- Every ingested item must carry an `externalId` (the original Firebird
  primary key) — this is the upsert key.
- Re-POSTing the exact same batch must be a no-op in terms of row count
  (upserts existing rows instead of duplicating them). This is what makes
  it safe for `emerion-load-service` to retry on timeout/network errors.
- Never generate a new `id` for an `externalId` that already exists — look
  it up first (see `CustomerRepositoryAdapter.save` /
  `IngestCustomersService.ingestItem`).

## Partial-Failure Batches
- One bad item must **not** abort the rest of the batch.
- Wrap each item's processing in its own `try`/`catch` inside the use-case
  service (see `IngestCustomersService.ingestItem`), collect an
  `IngestItemResult` per item (`CREATED`/`UPDATED`/`FAILED` +
  `errorMessage`), and always return `200 OK` with the full per-item
  breakdown — HTTP-level failure (4xx/5xx) is reserved for malformed
  requests (e.g. missing `batchId`), not per-item business failures.
- Log both the batch-level summary (`batchId`, counts) and each individual
  failure with its `externalId` — `emerion-load-service` has its own
  batch-tracking, but our logs are the second line of defense for tracing a
  specific failed record.

## Controller Responsibility
- The controller
  (`infrastructure/src/main/kotlin/.../infrastructure/rest/<x>/controller/XIngestionController.kt`,
  in the `:infrastructure` module) does exactly three things: map request
  DTO → command (REST mapper, in the sibling `rest/<x>/mapper/` package),
  call `IngestXUseCase.ingest(command)`, map
  result → response DTO (REST mapper). No loops, no try/catch, no business
  rules in the controller — that all lives in the use-case service (in the
  `:application` module).

## Business Side-Effects from Ingestion (Current Baseline)
- Side-effects, when needed, belong in the **application ingestion service**
  after successful persistence, never in the controller/mapper.
- Current implemented rule: when a **new** `CustomerOrder` is ingested
  (`CREATED` outcome), create an `INGESTION` notification for each **active**
  local Cognito user (`cognito_user.enabled=true`).
- Notification fanout is best-effort per user (log each failed user, continue
  the ingestion flow).

## Adding a New Ingestion Endpoint
1. Add the OpenAPI path/schemas (see
   `.github/instructions/openapi-contract.instructions.md`).
2. `application/src/main/kotlin/.../application/<x>/ingestion/model/` (`:application`
   module) — each command/result type in its own file
   (`Ingest<X>Command.kt`, `Ingest<X>BatchCommand.kt`, `Ingest<X>Outcome.kt`,
   `Ingest<X>ItemResult.kt`, `Ingest<X>BatchResult.kt`); plus
   `application/src/main/kotlin/.../application/<x>/ingestion/Ingest<X>UseCase.kt`
   — a single `interface` grouping both `ingest(batch)` and
   `ingestSingle(item)` (same functional concern, one interface).
3. `application/src/main/kotlin/.../application/<x>/ingestion/Ingest<X>Service.kt`
   (`:application` module) — `@Service`, per-item try/catch, upsert via the
   domain repository port, `@Transactional`.
4. `infrastructure/src/main/kotlin/.../infrastructure/rest/<x>/controller/<X>IngestionController.kt`
   + `infrastructure/src/main/kotlin/.../infrastructure/rest/<x>/mapper/<X>IngestionRestMapper.kt`
   (`:infrastructure` module).
5. Unit-test the service with MockK in
   `application/src/test/kotlin/.../application/<x>/ingestion/` (see
   `IngestCustomersServiceTest` for the three required cases: create,
   update/idempotent re-run, partial failure).
