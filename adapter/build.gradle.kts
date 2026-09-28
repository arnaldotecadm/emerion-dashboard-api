plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.spring")
    id(libs.plugins.openapi.generator.get().pluginId)
    id(libs.plugins.spring.dependency.management.get().pluginId)
}

group = "br.com.vertice"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":application"))
    implementation(project(":domain"))

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.springdoc.openapi.webmvc.ui)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

val openApiGeneratedDir = layout.buildDirectory.dir("generated/openapi")
val openApiBasePackage = "br.com.vertice.emerion_dashboard.infrastructure.rest.generated"

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

tasks.test {
    useJUnitPlatform()
}