package br.com.vertice.emerion_dashboard.infrastructure.config

import br.com.vertice.emerion_dashboard.application.cognitouser.sync.SyncCognitoUsersUseCase
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Runs the initial Cognito user directory sync once, right after the
 * application context starts. Deliberately lets any exception from
 * `SyncCognitoUsersUseCase.sync()` propagate: Spring Boot fails application
 * startup if an `ApplicationRunner` throws, which is the desired behavior
 * here (an unreachable/misconfigured Cognito should not silently leave the
 * user cache empty). Use `POST /admin/cognito-users/sync` afterwards for any
 * later refresh.
 *
 * Gated behind `app.cognito-sync.enabled` (default true) so integration
 * tests - which spin up a full `@SpringBootTest` context without real AWS
 * credentials/network access - can disable it; see
 * `support.PostgresIntegrationTest`, which sets this to `false`.
 */
@Component
@ConditionalOnProperty(prefix = "app.cognito-sync", name = ["enabled"], havingValue = "true", matchIfMissing = true)
class CognitoUserStartupSyncRunner(
    private val syncCognitoUsersUseCase: SyncCognitoUsersUseCase,
) : ApplicationRunner {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run(args: ApplicationArguments) {
        logger.info("Running initial Cognito user directory sync...")
        val result = syncCognitoUsersUseCase.sync()
        logger.info(
            "Initial Cognito user directory sync done: fetched={}, synced={}, failed={}",
            result.totalFetched,
            result.totalSynced,
            result.totalFailed,
        )
    }
}
