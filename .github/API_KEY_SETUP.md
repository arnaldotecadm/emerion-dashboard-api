# API Keys for Ingestion

Ingestion requests require `X-API-Key`. `SecurityConfig` permits the
`/ingestion/**` route through Spring Security, then
`ApiKeyAuthenticationFilter` validates the supplied key. Query routes use
Cognito JWT; `/admin/**` requires the configured admin group.

## Provision keys

The `api_key` table is created by
`infrastructure/src/main/resources/db/migration/V1__initial_schema.sql`.
After the application has migrated the database, insert a unique,
high-entropy key for each calling service and store it in that service's
secret manager:

```sql
INSERT INTO api_key (key_value, server_name, enabled, description)
VALUES ('<generated-secret>', '<service-name>', TRUE, '<description>');
```

Do not commit actual keys to source control or send them in logs. To revoke
or restore a key:

```sql
UPDATE api_key SET enabled = FALSE WHERE server_name = '<service-name>';
UPDATE api_key SET enabled = TRUE WHERE server_name = '<service-name>';
```

Successful validation updates `last_used_at`; this audit update is
best-effort and does not reject an otherwise valid request if the timestamp
write fails.

## Request

```http
POST /api/v1/ingestion/customers
X-API-Key: <generated-secret>
Content-Type: application/json
```

Missing or invalid keys receive `401 Unauthorized`. Use HTTPS in deployed
environments. Keys are currently compared as stored; protect database
access and plan key rotation operationally.

## Architecture boundary

This is an existing legacy security/persistence flow:
`ApiKeyAuthenticationFilter` in `:infrastructure` depends on
`domain.apikey.repository.ApiKeyRepository`, implemented by the legacy
`infrastructure` persistence adapter. Do not use this as the template for
new resource persistence: new outbound ports belong in `:application`, and
new adapters belong in `:adapter`. See
`instructions/hexagonal-architecture.instructions.md`.
