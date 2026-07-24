package br.com.vertice.emerion_dashboard.infrastructure.persistence.cognitouser

import br.com.vertice.emerion_dashboard.domain.cognitouser.model.CognitoUser
import br.com.vertice.emerion_dashboard.domain.cognitouser.repository.CognitoUserRepository
import br.com.vertice.emerion_dashboard.support.PostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Exercises `CognitoUserRepositoryAdapter` against a real Postgres instance,
 * covering the `V13` migration (`cognito_user` + `cognito_user_group`
 * tables) and the `groups` element-collection round-trip.
 */
@SpringBootTest
class CognitoUserRepositoryAdapterIntegrationTest(
    @Autowired private val cognitoUserRepository: CognitoUserRepository,
) : PostgresIntegrationTest() {

    @Test
    fun `saves a new cognito user and reads it back by sub, including groups`() {
        val now = Instant.parse("2026-01-01T00:00:00Z")
        val saved = cognitoUserRepository.save(
            CognitoUser(
                id = null,
                sub = "sub-int-1",
                username = "jane.doe",
                email = "jane.doe@example.com",
                enabled = true,
                groups = listOf("COMPANY", "ADMIN"),
                lastSyncedAt = now,
                createdAt = now,
                updatedAt = now,
            ),
        )

        assertTrue(saved.id != null)
        val found = cognitoUserRepository.findBySub("sub-int-1")
        assertEquals(saved.username, found?.username)
        assertEquals(setOf("COMPANY", "ADMIN"), found?.groups?.toSet())
    }

    @Test
    fun `returns null when the sub is not known`() {
        assertNull(cognitoUserRepository.findBySub("does-not-exist"))
    }

    @Test
    fun `updating an existing user by sub preserves its surrogate id and replaces its groups`() {
        val now = Instant.parse("2026-01-01T00:00:00Z")
        val saved = cognitoUserRepository.save(
            CognitoUser(
                id = null,
                sub = "sub-int-2",
                username = "old.username",
                email = "old@example.com",
                enabled = false,
                groups = listOf("COMPANY"),
                lastSyncedAt = now,
                createdAt = now,
                updatedAt = now,
            ),
        )

        val refreshed = cognitoUserRepository.save(
            saved.refreshedFrom(
                br.com.vertice.emerion_dashboard.domain.cognitouser.model.CognitoDirectoryUser(
                    sub = "sub-int-2",
                    username = "new.username",
                    email = "new@example.com",
                    enabled = true,
                    groups = listOf("ADMIN"),
                ),
                now.plusSeconds(60),
            ),
        )

        assertEquals(saved.id, refreshed.id)
        assertEquals("new.username", refreshed.username)
        assertEquals(listOf("ADMIN"), refreshed.groups)

        val found = cognitoUserRepository.findBySub("sub-int-2")
        assertEquals(saved.id, found?.id)
        assertEquals(listOf("ADMIN"), found?.groups)
    }

    @Test
    fun `findAll returns every synced user`() {
        val now = Instant.parse("2026-01-01T00:00:00Z")
        cognitoUserRepository.save(
            CognitoUser(
                id = null,
                sub = "sub-int-3",
                username = "user.three",
                email = null,
                enabled = true,
                groups = emptyList(),
                lastSyncedAt = now,
                createdAt = now,
                updatedAt = now,
            ),
        )

        val all = cognitoUserRepository.findAll()

        assertTrue(all.any { it.sub == "sub-int-3" })
    }
}
