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
            // Integration tests spin up a full Spring context without real AWS
            // credentials/network access, so the startup Cognito user sync
            // (CognitoUserStartupSyncRunner) must not run here.
            registry.add("app.cognito-sync.enabled") { "false" }
            // CognitoAdminConfig still creates the AWS client bean at context
            // bootstrap time; provide non-blank dummy credentials so bean
            // construction succeeds without depending on real secrets.
            registry.add("app.aws.access-key-id") { "test-access-key" }
            registry.add("app.aws.secret-access-key") { "test-secret-key" }
        }
    }
}
