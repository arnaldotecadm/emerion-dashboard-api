# OpenAPI Codegen Skill

## Description
Copy-paste-ready recipes for working with the `openapi-generator-gradle-plugin`
configuration in the `:infrastructure` Gradle module (`kotlin-spring`
generator, contract-first).
Use whenever the codegen config itself needs to change (new tag, new shared
schema, generator option) — not for day-to-day endpoint additions (see
`port-adapter-skill.md` for that).

## When to Use
- Adding a genuinely new *shared* schema (e.g. a new reusable error/pagination
  shape) that several resources will `$ref`.
- Diagnosing a codegen output you didn't expect (wrong package, wrong
  nullability, wrong Kotlin type).
- Changing the `openApiGenerate` Gradle configuration itself.

## Regenerating After a Spec Change
```bash
./gradlew :infrastructure:openApiGenerate   # or: ./gradlew :infrastructure:compileKotlin (depends on it)
```
Generated output lands in
`infrastructure/build/generated/openapi/src/main/kotlin/br/com/vertice/emerion_dashboard/infrastructure/rest/generated/{api,model}/`.
This directory is wired into the `:infrastructure` module's main source set
(`sourceSets.main.kotlin.srcDir(...)` in `infrastructure/build.gradle.kts`) —
IDEs may need a Gradle refresh/sync after the first generation to index it.

## Current Generator Config (infrastructure/build.gradle.kts, `openApiGenerate` block)
```kotlin
openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$projectDir/src/main/resources/openapi/api.yaml")
    outputDir.set(openApiGeneratedDir.map { it.asFile.path })
    apiPackage.set("$openApiBasePackage.api")
    modelPackage.set("$openApiBasePackage.model")
    configOptions.set(mapOf(
        "interfaceOnly" to "true",       // generate interfaces only, no server stubs/impl classes
        "useSpringBoot3" to "true",      // jakarta.* namespace
        "useTags" to "true",             // group operations into one interface per tag
        "enumPropertyNaming" to "UPPERCASE",
        "serializationLibrary" to "jackson",
        "documentationProvider" to "none",
        "useBeanValidation" to "true",   // jakarta.validation annotations from required/min/max
    ))
}
tasks.named("compileKotlin") { dependsOn("openApiGenerate") }
```
`useTags = true` is why `CustomerIngestionApi` and `CustomersApi` are
separate interfaces — one per `tags:` entry in the YAML. Keep ingestion and
query operations under different tags so they generate into different
interfaces (matches the controller split:
`XIngestionController`/`XQueryController`).

## Adding a New Shared Schema
Put it under `components.schemas` in `api.yaml` (not inline in a single
path) if more than one resource will reuse it — e.g. `PaginationInfo` and
`ErrorResponse` are already shared this way. Reference with `$ref:
'#/components/schemas/SchemaName'`.

## If Generated Output Looks Wrong
1. Re-check the YAML — most surprises (nested enums, renamed properties,
   `OffsetDateTime` vs `Instant`) are generator behavior, not bugs — see
   "Known Generator Gotchas" in
   `.github/instructions/openapi-contract.instructions.md` first.
2. Delete `infrastructure/build/generated/openapi` and re-run
   `./gradlew :infrastructure:openApiGenerate` to rule out a stale-cache
   issue (Gradle's `UP-TO-DATE` check is based on the input spec file's
   content hash, but a full clean rules out any doubt):
   `rm -rf infrastructure/build/generated/openapi && ./gradlew :infrastructure:openApiGenerate`.
3. If a config option needs to change project-wide, change it once in
   `infrastructure/build.gradle.kts` — don't work around a generator quirk
   by hand-editing generated files (they get wiped and regenerated).

## Do Not
- ❌ Don't set `interfaceOnly=false` — this project deliberately generates
  interfaces only and hand-writes the controllers/mappers for full control
  over the hexagonal boundary.
- ❌ Don't add a second OpenAPI spec file — one spec (`api.yaml`) for the
  whole API, one `openApiGenerate` task.
- ❌ Don't commit anything under any module's `build/` (already
  gitignored) — generated code is reproducible from `api.yaml` and must
  never be hand-patched.
