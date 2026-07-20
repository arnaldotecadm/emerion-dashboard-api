// Application layer: use cases (inbound ports) + their @Service
// implementations. Depends on `domain` only — never on `infrastructure`.
// Needs Spring's core/context/tx (for @Service/@Transactional) but NOT
// spring-boot-starter-web/data-jpa — those are infrastructure concerns.

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.spring")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":domain"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.slf4j:slf4j-api")

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.mockk)
}

// This module doesn't apply the `org.springframework.boot` plugin (it isn't
// runnable), so the Spring Boot BOM has to be imported explicitly to align
// `spring-context`/`spring-tx` versions with the rest of the app.
dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.versions.springBoot.get()}")
    }
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
