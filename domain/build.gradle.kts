// Pure domain layer: models, outbound ports, domain exceptions.
// No Spring, no JPA, no OpenAPI — plain Kotlin only. Never add a
// dependency here on `application` or `infrastructure`.

plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.mockk)
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
