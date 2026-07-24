# Skills Reference Guide

This folder contains reusable, copy-paste-ready patterns for building
emerion-dashboard resources consistently with the hexagonal architecture and
contract-first OpenAPI approach.

## Available Skills

### 1. `port-adapter-skill.md`
**Purpose**: End-to-end recipe for adding a brand-new resource (domain,
application, persistence adapter, REST adapter, Flyway migration, tests).

**When to use**: "Add a new resource/entity", "migrate the X table",
"add Y ingestion + query endpoints".

### 2. `mapper-skill.md`
**Purpose**: Templates for the two mapper types every resource needs
(persistence mapper: JPA entity <-> domain; REST mapper: generated DTO <->
domain/application types), including the enum-separation and
import-aliasing conventions.

**When to use**: Adding/changing a field across a layer boundary, writing
mappers for a new resource, debugging a field that isn't coming through
correctly.

### 3. `openapi-codegen-skill.md`
**Purpose**: How the `openapi-generator-gradle-plugin` is configured in
this project, how to regenerate, and how to diagnose unexpected generated
output.

**When to use**: Adding a new shared OpenAPI schema, changing generator
config, debugging codegen surprises (nested enums, renamed properties).

### 4. `cognito-notification-skill.md`
**Purpose**: Compact playbook for Cognito JWT/security rules, startup/manual
directory sync, and notification fanout behavior (including order-ingestion
triggering).

**When to use**: "change auth rules", "sync Cognito users", "send
notifications to users on business events", "why did admin sync fail".

## How These Relate to `.github/instructions/*.instructions.md`
Instructions files describe the *rules and conventions* (what must always
be true). Skills files describe the *recipe* (step-by-step, copy-pasteable)
for applying those rules to a new piece of work. Use both together:

```
"Add a Product resource: ingestion + query endpoints, following
port-adapter-skill.md and mapper-skill.md. Reference
hexagonal-architecture.instructions.md and openapi-contract.instructions.md
for the rules."
```

For token-efficient prompting, name only the needed skill + affected files,
for example:

```
Use cognito-notification-skill.md. Update only:
- infrastructure/config/SecurityConfig.kt
- infrastructure/config/CognitoUserStartupSyncRunner.kt
- application/customerorder/ingestion/IngestCustomerOrdersService.kt
Run targeted tests only.
```

## Reference Implementation
The `Customer` resource (across `domain/customer/{model,repository,exception}`,
`application/customer`,
`infrastructure/rest/customer/{controller,mapper}`,
`infrastructure/persistence/customer/{,mapper}`) is
the working, tested example every skill in this folder points back to.
When in doubt, read the actual Customer files before following a skill's
template — the skill exists to save you from re-deriving the pattern, not
to replace looking at real code.
