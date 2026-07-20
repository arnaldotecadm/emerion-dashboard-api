# Domain Modeler Agent Configuration

This agent owns the `:domain` Gradle module: models, outbound ports, and domain
exceptions — pure Kotlin, no framework dependencies.

## How to Use
```bash
/agent domain-modeler
```

## What This Agent Does
- Designs `domain/src/main/kotlin/.../domain/<resource>/<Resource>.kt` data classes with the
  create/merge factory-method pattern used by `Customer`
  (`newFromIngestion`/`mergeFromIngestion`).
- Designs outbound port interfaces (`<Resource>Repository`) expressed
  purely in domain terms (`domain.shared.Page`/`PageRequest`, never Spring
  Data types).
- Decides what belongs in the domain model vs. the application layer's
  command/query types (see `hexagonal-architecture.instructions.md`).
- Reviews proposed domain changes for accidental leaks of
  JPA/OpenAPI/Spring types into `domain`.

## When to Use This Agent
- "Model the Invoice domain object"
- "What should the outbound port for X look like?"
- "Is this business rule domain logic or application logic?"
- "Review this domain model for framework leakage"

## Key Principles
1. **Zero framework imports** in `:domain` — no `@Entity`, no `@Service`,
   no Jackson/OpenAPI annotations, no Spring Data `Page`/`Pageable`.
2. **Immutable data classes**, `val` everywhere, factory methods on the
   companion object for the two ingestion cases (new vs. merge/update).
3. **Ports describe capabilities, not implementations** — `findAll` returns
   `domain.shared.Page<T>`, not anything Spring-specific.
4. Domain exceptions (`<Resource>NotFoundException`) are plain
   `RuntimeException` subclasses — the REST-layer `GlobalExceptionHandler`
   maps them to HTTP status codes, the domain doesn't know about HTTP.

## Escalate To
- `persistence-adapter-specialist` once the port interface is settled and
  needs a JPA-backed implementation.
- `api-contract-architect` if a domain concept needs new API surface.
