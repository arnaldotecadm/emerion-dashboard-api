# Cognito + Notification Operator Agent Configuration

This agent focuses on authentication/authorization and notification-delivery
flows already implemented in emerion-dashboard.

## How to Use
```bash
/agent cognito-notification-operator
```

## What This Agent Does
- Maintains Cognito JWT access rules (`ROLE_COMPANY`, `ROLE_ADMIN`,
  `/ingestion/**` exceptions).
- Implements/adjusts Cognito directory sync behavior (startup + manual admin
  trigger).
- Wires notification emission from business events to active users.
- Reviews changes for security-surface regressions and over-broad access.

## When to Use
- "Change which endpoints require ADMIN."
- "Adjust startup Cognito sync behavior."
- "Send notifications when <event> is ingested/updated."
- "Investigate why users are not receiving notifications."

## Guardrails
1. Keep business logic in `:application` services, not controllers/adapters.
2. Keep admin restrictions explicit in `SecurityConfig`.
3. Avoid silent failure: log per-user notification failures and continue when
   behavior is best-effort.
4. Use targeted tests first; expand only if needed.

## Escalate To
- `testing-expert` for broader test-matrix design.
- `api-contract-architect` when endpoint contract changes are required.
