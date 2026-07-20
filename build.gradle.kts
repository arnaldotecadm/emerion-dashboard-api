// Root aggregator project. No source of its own — see domain/, application/,
// infrastructure/, app/ for the four hexagonal Gradle modules. Shared
// versions live in gradle/libs.versions.toml.
//
// Kotlin/Spring/OpenAPI plugins are declared here with `apply false` so the
// Kotlin Gradle plugin is only ever loaded once; each module then applies
// them without a version via `id(...)`.
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.plugin.spring) apply false
    alias(libs.plugins.kotlin.plugin.jpa) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.openapi.generator) apply false
}

allprojects {
    group = "br.com.vertice"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}
