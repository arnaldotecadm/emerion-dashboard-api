# Testing

Use JUnit 5 and MockK. Prefer the smallest test that exercises the behavior;
do not start Spring for pure application logic.

## Unit tests

- Put application tests in the matching package under
  `application/src/test/kotlin/`; put pure domain tests under
  `domain/src/test/kotlin/`.
- Mock application outbound ports, not persistence adapters.
- If a service accepts `Clock`, inject `Clock.fixed(...)` for deterministic
  timestamp assertions.
- For ingestion services, cover create, idempotent update, and per-item
  failure without aborting the batch.
- Test pure mapper objects directly with JUnit assertions.

## PostgreSQL / full-context tests

- Put tests requiring Spring Boot, PostgreSQL, migrations, or complete REST
  wiring in `app/src/test/kotlin/`.
- Extend `support.PostgresIntegrationTest`; it provides PostgreSQL through
  Testcontainers and `@ServiceConnection`, and runs the Flyway migration
  chain. Do not configure a second datasource unless the test requires
  something `@ServiceConnection` cannot provide.
- Use full-context tests only when behavior depends on Spring wiring or the
  database. A missing Docker daemon is an environment issue, not a reason to
  remove the test.

Name tests as `<ClassUnderTest>Test` and use descriptive backtick test
names. Run targeted Gradle tests for the changed behavior, then broaden
validation only when needed.
