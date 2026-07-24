package br.com.vertice.emerion_dashboard.application.cognitouser.sync

import br.com.vertice.emerion_dashboard.domain.cognitouser.model.CognitoDirectoryUser
import br.com.vertice.emerion_dashboard.domain.cognitouser.model.CognitoUser
import br.com.vertice.emerion_dashboard.domain.cognitouser.repository.CognitoUserDirectory
import br.com.vertice.emerion_dashboard.domain.cognitouser.repository.CognitoUserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SyncCognitoUsersServiceTest {

    private val fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
    private val cognitoUserDirectory = mockk<CognitoUserDirectory>()
    private val cognitoUserRepository = mockk<CognitoUserRepository>()
    private val service = SyncCognitoUsersService(cognitoUserDirectory, cognitoUserRepository, fixedClock)

    @Test
    fun `creates a new local record when the sub is not yet known`() {
        val directoryUser = CognitoDirectoryUser(
            sub = "sub-1",
            username = "jane.doe",
            email = "jane.doe@example.com",
            enabled = true,
            groups = listOf("COMPANY"),
        )
        every { cognitoUserDirectory.listAllUsers() } returns listOf(directoryUser)
        every { cognitoUserRepository.findBySub("sub-1") } returns null
        val savedSlot = slot<CognitoUser>()
        every { cognitoUserRepository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }

        val result = service.sync()

        assertEquals(1, result.totalFetched)
        assertEquals(1, result.totalSynced)
        assertEquals(0, result.totalFailed)
        assertEquals(Instant.parse("2026-01-01T00:00:00Z"), result.syncedAt)
        assertEquals("sub-1", savedSlot.captured.sub)
        assertEquals(null, savedSlot.captured.id)
        verify(exactly = 1) { cognitoUserRepository.save(any()) }
    }

    @Test
    fun `refreshes an existing local record when the sub is already known`() {
        val existing = CognitoUser(
            id = 42L,
            sub = "sub-2",
            username = "old.username",
            email = "old@example.com",
            enabled = false,
            groups = listOf("COMPANY"),
            lastSyncedAt = Instant.parse("2025-01-01T00:00:00Z"),
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T00:00:00Z"),
        )
        val directoryUser = CognitoDirectoryUser(
            sub = "sub-2",
            username = "new.username",
            email = "new@example.com",
            enabled = true,
            groups = listOf("COMPANY", "ADMIN"),
        )
        every { cognitoUserDirectory.listAllUsers() } returns listOf(directoryUser)
        every { cognitoUserRepository.findBySub("sub-2") } returns existing
        every { cognitoUserRepository.save(any()) } answers { firstArg() }

        val result = service.sync()

        assertEquals(1, result.totalSynced)
        verify(exactly = 1) {
            cognitoUserRepository.save(
                match {
                    it.id == 42L && it.username == "new.username" && it.groups == listOf("COMPANY", "ADMIN")
                },
            )
        }
    }

    @Test
    fun `records a failure for one user without aborting the rest of the sync`() {
        val okUser = CognitoDirectoryUser("sub-ok", "ok.user", "ok@example.com", true, listOf("COMPANY"))
        val badUser = CognitoDirectoryUser("sub-bad", "bad.user", "bad@example.com", true, listOf("COMPANY"))
        every { cognitoUserDirectory.listAllUsers() } returns listOf(okUser, badUser)
        every { cognitoUserRepository.findBySub("sub-ok") } returns null
        every { cognitoUserRepository.findBySub("sub-bad") } throws RuntimeException("db down")
        every { cognitoUserRepository.save(any()) } answers { firstArg<CognitoUser>().copy(id = 1L) }

        val result = service.sync()

        assertEquals(2, result.totalFetched)
        assertEquals(1, result.totalSynced)
        assertEquals(1, result.totalFailed)
    }

    @Test
    fun `propagates the exception when fetching the directory itself fails`() {
        every { cognitoUserDirectory.listAllUsers() } throws RuntimeException("Cognito unreachable")

        assertFailsWith<RuntimeException> { service.sync() }
        verify(exactly = 0) { cognitoUserRepository.save(any()) }
    }
}
