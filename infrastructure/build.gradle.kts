// Infrastructure layer: all adapters (REST controllers implementing the
// generated OpenAPI interfaces, JPA persistence, CORS/config). Depends on
// `domain` and `application`. This is the only module allowed to depend on
// Spring Web/Data JPA/generated OpenAPI code.
//
// Also owns the contract-first OpenAPI codegen: the hand-written spec at
// src/main/resources/openapi/api.yaml is the codegen input AND is served at
// runtime as a static resource (see infrastructure/.../config and
// app/src/main/resources/application.properties for springdoc wiring).

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.spring")
    id("org.jetbrains.kotlin.plugin.jpa")
    id("io.spring.dependency-management")
    id("org.openapi.generator")
}

val openApiGeneratedDir = layout.buildDirectory.dir("generated/openapi")
val openApiBasePackage = "br.com.vertice.emerion_dashboard.infrastructure.rest.generated"

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))

    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation(libs.springdoc.openapi.webmvc.ui)
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation(libs.swagger.annotations)
    implementation(libs.aws.sdk.cognitoidentityprovider)
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.mockk)
}

// This module doesn't apply the `org.springframework.boot` plugin (it isn't
// runnable on its own — `app` is), so the BOM is imported explicitly.
dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.versions.springBoot.get()}")
    }
}

sourceSets {
    main {
        kotlin.srcDir(openApiGeneratedDir.map { it.dir("src/main/kotlin") })
    }
}

openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$projectDir/src/main/resources/openapi/api.yaml")
    outputDir.set(openApiGeneratedDir.map { it.asFile.path })
    apiPackage.set("$openApiBasePackage.api")
    modelPackage.set("$openApiBasePackage.model")
    invokerPackage.set("$openApiBasePackage.invoker")
    configOptions.set(
        mapOf(
            "interfaceOnly" to "true",
            "useSpringBoot3" to "true",
            "useTags" to "true",
            "enumPropertyNaming" to "UPPERCASE",
            "serializationLibrary" to "jackson",
            "documentationProvider" to "none",
            "useBeanValidation" to "true",
        )
    )
    globalProperties.set(
        mapOf(
            "apis" to "",
            "models" to "",
        )
    )
}

tasks.named("compileKotlin") {
    dependsOn("openApiGenerate")
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
