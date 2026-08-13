# API Key Authentication for Ingestion Endpoints

## Overview
The emerion-dashboard API now requires valid API keys for all `/ingestion/**` endpoints. This protects the server-to-server ingestion interface from unauthorized callers while keeping the implementation simple for senders (no AWS complexity, no token caching).

## Architecture

### Components Created

1. **Domain Layer** (`domain/apikey/`)
   - `ApiKey.kt` — Domain model representing an API key
   - `ApiKeyRepository.kt` — Outbound port interface
   - `ApiKeyInvalidException.kt` — Domain exception

2. **Infrastructure Layer** (`infrastructure/persistence/apikey/`)
   - `ApiKeyJpaEntity.kt` — JPA entity mapping to the `api_key` table
   - `ApiKeySpringDataRepository.kt` — Spring Data JPA repository
   - `ApiKeyRepositoryAdapter.kt` — Implements the domain port
   - `ApiKeyPersistenceMapper.kt` — Maps between JPA entity and domain model

3. **Security Filter**
   - `ApiKeyAuthenticationFilter.kt` — Validates `X-API-Key` header on ingestion requests

4. **Database**
   - `V15__create_api_key_table.sql` — Flyway migration creating the `api_key` table

### Flow

```
Sender (server01, server02)
    │
    ├─ POST /ingestion/customers
    │  Header: X-API-Key: sk_server01_abc123def456
    │  Body: { "batchId": "...", "items": [...] }
    │
    ▼
SecurityFilterChain
    │
    ├─ ApiKeyAuthenticationFilter
    │  (checks if path contains /ingestion/)
    │  ├─ Find API key in database
    │  ├─ Verify key is enabled
    │  └─ Update last_used_at for audit trail
    │
    ▼
Controller (if key valid)
    │
    ├─ Process request normally
    │  (e.g., CustomerIngestionController)
    │
    ▼
200 OK (or error if invalid key)
```

## Setup

### 1. Start the Application

The Flyway migration `V15__create_api_key_table.sql` runs automatically on startup, creating the `api_key` table.

### 2. Add API Keys to the Database

Once the table is created, insert keys for each server:

```sql
INSERT INTO api_key (key_value, server_name, enabled, description)
VALUES 
    ('sk_server01_abc123def456789', 'server01', true, 'Production load service - server01'),
    ('sk_server02_xyz789abc456123', 'server02', true, 'Production load service - server02');
```

**Key Format:**
- Use a meaningful prefix (e.g., `sk_` for "server key")
- Include the server name for readability
- Generate cryptographically strong random values (min 32 chars recommended)
- Store securely (e.g., HashiCorp Vault, AWS Secrets Manager)

Example key generation:
```bash
openssl rand -hex 32
# Output: a1b2c3d4e5f6... (64 hex chars)
```

### 3. Disable Keys (Without Code Changes)

To temporarily revoke a server's access:

```sql
UPDATE api_key SET enabled = false WHERE server_name = 'server01';
```

To re-enable:

```sql
UPDATE api_key SET enabled = true WHERE server_name = 'server01';
```

## Usage

### For Senders (emerion-load-service)

Every ingestion request must include the `X-API-Key` header:

```bash
curl -X POST http://dashboard/api/v1/ingestion/customers \
  -H "X-API-Key: sk_server01_abc123def456789" \
  -H "Content-Type: application/json" \
  -d '{
    "batchId": "batch_20240807_001",
    "items": [...]
  }'
```

### Error Responses

**Missing API Key:**
```
HTTP 401 Unauthorized
{
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Missing API key header"
  }
}
```

**Invalid API Key:**
```
HTTP 401 Unauthorized
{
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Invalid API key"
  }
}
```

**Disabled API Key:**
```
HTTP 401 Unauthorized
{
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Invalid API key"
  }
}
```

## Audit Trail

Every successful API key use updates the `last_used_at` column. Query usage history:

```sql
SELECT 
    server_name,
    last_used_at,
    created_at,
    enabled
FROM api_key
ORDER BY last_used_at DESC;
```

Example output:
```
server_name | last_used_at                    | created_at                      | enabled
------------|-------------------------------|---------------------------------|--------
server01    | 2024-08-07 16:45:23.123+01:00 | 2024-08-07 10:00:00.000+01:00 | t
server02    | 2024-08-07 16:42:15.456+01:00 | 2024-08-07 10:00:00.000+01:00 | t
```

## Implementation Details

### Hexagonal Architecture Compliance

This implementation follows the project's port/adapter pattern:

- **Domain Port:** `ApiKeyRepository` (outbound, in `:domain` module)
- **Infrastructure Adapter:** `ApiKeyRepositoryAdapter` (in `:infrastructure` module)
- **Filter:** Uses the domain port, zero knowledge of JPA/Spring Data
- **No Business Logic in Filters:** The filter only validates; actual logic is in the adapter

### Security Considerations

1. **Keys are compared as plain strings** — use HTTPS to prevent eavesdropping
2. **No rate limiting** — if needed, implement per-key rate limiting in the filter
3. **No key rotation policies** — manage key lifecycle via operational procedures
4. **No IP whitelisting** — add firewall rules at the infrastructure layer if needed

### Database Indexes

The migration creates three indexes for performance:
- `idx_api_key_value` — O(1) lookups by key value
- `idx_api_key_server_name` — Fast filtering by server
- `idx_api_key_enabled` — Fast filtering by status

## Testing

Unit tests for the API key system are in the application layer (TODO: add after this is integrated).

Integration tests via Testcontainers verify:
- Valid key allows ingestion
- Missing key returns 401
- Invalid key returns 401
- Disabled key returns 401
- `last_used_at` is updated on each valid request

## Troubleshooting

### Tests Fail with "Could not find a valid Docker environment"
Testcontainers cannot reach Docker. Ensure:
- Docker Desktop / Colima / equivalent is running
- `docker info` works from the same terminal as Gradle

### API Key Not Found in Database
Check:
```sql
SELECT * FROM api_key WHERE server_name = 'server01';
```
Verify the key was inserted and `enabled = true`.

### "X-API-Key" Header Not Recognized
Ensure the header name is exact (case-sensitive in HTTP/2, case-insensitive in HTTP/1.1, but we follow HTTP/2 convention).

### Requests to Query Endpoints (Not Ingestion) Fail
The API key filter only applies to `/ingestion/**` paths. Query endpoints require Cognito JWT (not API keys). This is by design.

## Future Enhancements

- **Per-key rate limiting** — throttle each server individually
- **Scope restrictions** — restrict a key to certain ingestion types (e.g., only customers, not orders)
- **Key rotation** — automatic expiry + replacement workflows
- **Webhook notifications** — alert on unusual usage patterns
- **Encrypted key storage** — hash keys in the database (like passwords)
