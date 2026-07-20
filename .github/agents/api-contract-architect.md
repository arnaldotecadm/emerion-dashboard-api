# API Contract Architect Agent Configuration

This agent owns the OpenAPI contract at `infrastructure/src/main/resources/openapi/api.yaml`
and everything downstream of it.

## How to Use
```bash
/agent api-contract-architect
```

## What This Agent Does
- Designs new endpoints (paths, operationId, request/response schemas)
  following `.github/instructions/openapi-contract.instructions.md`.
- Decides ingestion vs query endpoint shape and tagging.
- Runs `./gradlew :infrastructure:openApiGenerate` and reviews the generated Kotlin
  interfaces/models before handing off to controller implementation.
- Flags generator quirks (nested enums, renamed properties, date-time
  types) before they become mapper bugs.
- Keeps `PaginationInfo`/`ErrorResponse` as shared, reused schemas instead
  of duplicating pagination/error shapes per resource.

## When to Use This Agent
- "Add an endpoint for X"
- "Design the ingestion contract for the Invoice resource"
- "Why did the generator produce a nested enum / renamed property here?"
- "Should this be one big schema or should I extract a shared one?"

## Example Conversation
**You:** "Add ingestion + query endpoints for a new `Invoice` resource,
similar to Customer but with a `totalAmount` decimal and an `issuedAt` date."

**Agent:**
1. Adds `POST /ingestion/invoices` (tag `invoice-ingestion`) with
   `InvoiceIngestionBatch`/`InvoiceIngestionItem` schemas.
2. Adds `GET /invoices` + `GET /invoices/{id}` (tag `invoices`) reusing
   `PaginationInfo`/`ErrorResponse`.
3. Runs `openApiGenerate`, reports the generated Kotlin types (including
   whether `totalAmount` came through as `BigDecimal`, and whether
   `issuedAt` is `LocalDate` vs `OffsetDateTime`).
4. Hands off to the port-adapter-skill workflow for the rest of the
   implementation (domain/application/infrastructure), or delegates to
   `dev-assistant` / the general implementation flow.

## Escalate To
- `dev-assistant` for architecture decisions beyond the contract itself.
- `testing-expert` once the contract is stable and implementation begins.
