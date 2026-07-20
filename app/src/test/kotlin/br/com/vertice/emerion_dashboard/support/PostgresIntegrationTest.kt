package br.com.vertice.emerion_dashboard.support

import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

/**
 * Base class for integration tests that need a real PostgreSQL instance
 * (Flyway migrations + JPA repositories). `@ServiceConnection` wires the
 * container into Spring Boot's datasource autoconfiguration automatically,
 * so no manual `spring.datasource.*` properties are needed here.
 *
 * Extend this instead of writing `@Testcontainers`/`@Container` boilerplate
 * in every integration test class.
 */
@Testcontainers
abstract class PostgresIntegrationTest {

    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))

        @JvmStatic
        @DynamicPropertySource
        fun overrideProperties(registry: DynamicPropertyRegistry) {
            // Reserved for properties `@ServiceConnection` doesn't cover (e.g. non-datasource config).
        }
    }
}
