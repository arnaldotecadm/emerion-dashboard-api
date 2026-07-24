# Cognito + Notification Skill

## Description
Compact recipe for work involving Cognito JWT auth, Cognito user directory
sync, and per-user notification creation.

## Use This For
- Changing endpoint authorization rules (`ROLE_COMPANY`, `ROLE_ADMIN`).
- Adjusting startup/manual Cognito sync behavior.
- Emitting notifications from business events (for example: new order
  ingestion fanout to active users).

## Canonical Files
- `infrastructure/src/main/kotlin/.../config/SecurityConfig.kt`
- `infrastructure/src/main/kotlin/.../config/CognitoJwtConfig.kt`
- `infrastructure/src/main/kotlin/.../config/CognitoUserStartupSyncRunner.kt`
- `infrastructure/src/main/kotlin/.../cognito/adapter/CognitoUserDirectoryAdapter.kt`
- `application/src/main/kotlin/.../cognitouser/sync/SyncCognitoUsersService.kt`
- `application/src/main/kotlin/.../customerorder/ingestion/IngestCustomerOrdersService.kt`
- `application/src/main/kotlin/.../notification/creation/CreateNotificationService.kt`

## Behavior Baseline
1. Query endpoints require valid Cognito JWT + group-based role checks.
2. `/admin/**` requires `ROLE_ADMIN`.
3. Startup sync loads Cognito users/groups into local tables.
4. `POST /admin/cognito-users/sync` performs manual re-sync.
5. New customer-order ingestion creates one `INGESTION` notification per
   active local user (`enabled=true`).

## Token-Efficient Prompt Template
```text
Use cognito-notification-skill.md.
Scope: <exact files>.
Change: <one behavior delta>.
Constraints:
- Keep ingestion endpoints public unless explicitly asked.
- Keep admin endpoints ROLE_ADMIN.
- Use targeted tests only.
Validation:
- <specific gradle commands>.
```

## Minimal Validation Set
- Service-only changes:
  `./gradlew :application:test --tests "*<ServiceTest>*"`
- Cross-module compile check:
  `./gradlew compileKotlin compileTestKotlin`
- Integration check (when persistence/security wiring changed):
  `./gradlew :app:test --tests "*<IntegrationTest>*"`
