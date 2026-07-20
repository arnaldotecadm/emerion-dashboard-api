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
  (`infrastructure/src/main/kotlin/.../infrastructure/rest/<x>/XIngestionController.kt`,
  in the `:infrastructure` module) does exactly three things: map request
  DTO → command (REST mapper), call `IngestXUseCase.ingest(command)`, map
  result → response DTO (REST mapper). No loops, no try/catch, no business
  rules in the controller — that all lives in the use-case service (in the
  `:application` module).

## Adding a New Ingestion Endpoint
1. Add the OpenAPI path/schemas (see
   `.github/instructions/openapi-contract.instructions.md`).
2. `application/src/main/kotlin/.../application/<x>/Ingest<X>UseCase.kt`
   (`:application` module) — command/result types + `fun
   interface Ingest<X>UseCase { fun ingest(command: Ingest<X>BatchCommand): Ingest<X>BatchResult }`.
3. `application/src/main/kotlin/.../application/<x>/Ingest<X>Service.kt`
   (`:application` module) — `@Service`, per-item try/catch, upsert via the
   domain repository port, `@Transactional`.
4. `infrastructure/src/main/kotlin/.../infrastructure/rest/<x>/<X>IngestionController.kt`
   + `<X>IngestionRestMapper.kt` (`:infrastructure` module).
5. Unit-test the service with MockK in
   `application/src/test/kotlin/.../application/<x>/` (see
   `IngestCustomersServiceTest` for the three required cases: create,
   update/idempotent re-run, partial failure).
