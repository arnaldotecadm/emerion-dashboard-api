# Testing Instructions

## Description
Governs test structure and tooling: JUnit 5, MockK, Testcontainers +
PostgreSQL, `@ServiceConnection`. Apply whenever writing a new test.

## Two Kinds of Tests, Two Different Setups

### 1. Unit tests (domain/application layer) — no Spring context
- Location mirrors the class under test in the **`application` module's**
  test source set: `application/src/test/kotlin/.../application/<x>/<X>ServiceTest.kt`.
  A pure `domain` model test would go in `domain/src/test/kotlin/...`
  instead — either way, no Spring context or `app`-module dependency is
  needed since `domain`/`application` don't have Spring Web/JPA on their
  classpath at all.
- Use MockK (`io.mockk.mockk<T>()`), not Mockito — this project standardizes
  on MockK for its more idiomatic Kotlin API (`every { }`, `verify { }`,
  `slot<T>()`, `match { }`).
- Mock the **domain port** (e.g. `CustomerRepository`), never a concrete
  adapter — that's the whole point of the port/adapter split: services are
  testable with zero Spring/DB dependency.
- Inject a fixed `Clock` (`Clock.fixed(Instant.parse("..."), ZoneOffset.UTC)`)
  wherever the service takes one, so timestamp assertions are deterministic.
  See `IngestCustomersServiceTest`.
- For every ingestion use-case service, always cover these three cases at
  minimum (see `IngestCustomersServiceTest` as the template):
  1. **Create** — externalId not yet known → `CREATED` outcome.
  2. **Update / idempotent re-run** — externalId already known → `UPDATED`
     outcome, existing surrogate `id` preserved.
  3. **Partial failure** — one item throws, the rest of the batch still
     succeeds; `totalFailed`/`totalSucceeded` counts are correct.

### 2. Integration tests (persistence, full REST flow) — real Postgres
- Live in the **`app` module's** test source set
  (`app/src/test/kotlin/...`) — that's the only module with `domain`,
  `application`, `infrastructure`, Spring Boot, and Testcontainers all on
  the same classpath.
- Extend `br.com.vertice.emerion_dashboard.support.PostgresIntegrationTest`
  (already annotated `@Testcontainers`, spins up a `postgres:16-alpine`
  container with `@ServiceConnection` — Spring Boot wires
  `spring.datasource.*` automatically, **do not** set datasource properties
  by hand or with `@DynamicPropertySource` unless it's something
  `@ServiceConnection` doesn't cover).
- Flyway runs the full migration chain against the container on context
  startup — this is intentional, it catches broken migrations as test
  failures.
- Use `@SpringBootTest` (full context) for REST-flow tests via
  `TestRestTemplate`/`MockMvc`; a `@DataJpaTest`-style slice test isn't
  wired up in this project — prefer the full context via
  `PostgresIntegrationTest` for consistency, unless test runtime becomes a
  real problem.
- Requires a working Docker daemon locally (Docker Desktop / Colima /
  Testcontainers Cloud). If `./gradlew test` fails with
  `IllegalStateException: Could not find a valid Docker environment`, that's
  an environment/tooling issue, not a code defect — check `docker info`
  works from the same shell Gradle runs in.

## Naming & Structure
- Test class name: `<ClassUnderTest>Test.kt`.
- Test method names: backtick sentence style,
  `` `creates a new customer when externalId is not known yet`() ``
  (matches `IngestCustomersServiceTest`) — readable as living documentation,
  no `test` prefix.
- One assertion focus per test; prefer several small tests over one test
  with many unrelated assertions.

## Mapper Tests
- Mappers are pure `object`s — test them directly with plain JUnit
  assertions, no mocking needed at all.

## What NOT to Do
- ❌ Don't use Mockito — MockK is the standard for this project.
- ❌ Don't spin up a full `@SpringBootTest` context for pure business-logic
  tests — that's what unit tests + MockK are for; reserve
  `PostgresIntegrationTest` for things that actually need a database.
- ❌ Don't hardcode `LocalDateTime.now()`/`Instant.now()` inside a service
  under test without a way to inject a fixed `Clock` — this makes tests
  flaky/hard to assert on.
