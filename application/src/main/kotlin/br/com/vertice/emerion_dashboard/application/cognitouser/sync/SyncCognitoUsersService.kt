package br.com.vertice.emerion_dashboard.application.cognitouser.sync

import br.com.vertice.emerion_dashboard.application.cognitouser.sync.model.SyncCognitoUsersResult
import br.com.vertice.emerion_dashboard.domain.cognitouser.model.CognitoUser
import br.com.vertice.emerion_dashboard.domain.cognitouser.repository.CognitoUserDirectory
import br.com.vertice.emerion_dashboard.domain.cognitouser.repository.CognitoUserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

/**
 * Fetches the full user directory from Cognito (`CognitoUserDirectory`,
 * an external-system call that is allowed to fail loudly - see
 * `sync()`'s docs) and upserts each user into the local cache
 * (`CognitoUserRepository`, by `sub`). Mirrors the ingestion services'
 * partial-failure pattern for the per-user upsert step: one bad record
 * doesn't abort the whole sync.
 */
@Service
class SyncCognitoUsersService(
    private val cognitoUserDirectory: CognitoUserDirectory,
    private val cognitoUserRepository: CognitoUserRepository,
    private val clock: Clock = Clock.systemUTC(),
) : SyncCognitoUsersUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Fetching the directory itself (`cognitoUserDirectory.listAllUsers()`)
     * is intentionally NOT wrapped in try/catch here: if Cognito is
     * unreachable, this exception must propagate so the startup runner
     * fails application startup instead of silently leaving the cache
     * empty/stale.
     */
    @Transactional
    override fun sync(): SyncCognitoUsersResult {
        val now = Instant.now(clock)
        val directoryUsers = cognitoUserDirectory.listAllUsers()

        var synced = 0
        var failed = 0
        for (directoryUser in directoryUsers) {
            try {
                val existing = cognitoUserRepository.findBySub(directoryUser.sub)
                val user = existing?.refreshedFrom(directoryUser, now)
                    ?: CognitoUser.fromDirectory(directoryUser, now)
                cognitoUserRepository.save(user)
                synced++
            } catch (ex: Exception) {
                failed++
                logger.error("Failed to sync Cognito user with sub={}", directoryUser.sub, ex)
            }
        }

        logger.info(
            "Cognito user sync complete: fetched={}, synced={}, failed={}",
            directoryUsers.size,
            synced,
            failed,
        )
        return SyncCognitoUsersResult(
            totalFetched = directoryUsers.size,
            totalSynced = synced,
            totalFailed = failed,
            syncedAt = now,
        )
    }
}
