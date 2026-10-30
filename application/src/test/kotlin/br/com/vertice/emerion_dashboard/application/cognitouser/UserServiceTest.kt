package br.com.vertice.emerion_dashboard.application.cognitouser

import br.com.vertice.emerion_dashboard.application.outbound.port.CognitoUserRepository
import br.com.vertice.emerion_dashboard.domain.cognitouser.model.CognitoUser
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import java.time.Instant
import kotlin.test.assertEquals

class UserServiceTest {
    private val cognitoUserRepository = mockk<CognitoUserRepository>()
    private val service = UserService(cognitoUserRepository)

    @AfterEach
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `returns the authenticated users company cnpj`() {
        SecurityContextHolder.getContext().authentication = TestingAuthenticationToken("user-sub", null)
        every { cognitoUserRepository.findBySub("user-sub") } returns user(cnpjEmpresa = "12345678000199")

        assertEquals("12345678000199", service.getCurrentCompanyCnpj())
    }

    @Test
    fun `resolves the user for each authenticated request`() {
        SecurityContextHolder.getContext().authentication = TestingAuthenticationToken("first-user", null)
        every { cognitoUserRepository.findBySub("first-user") } returns user(cnpjEmpresa = "11111111000111")
        every { cognitoUserRepository.findBySub("second-user") } returns user(cnpjEmpresa = "22222222000122")

        assertEquals("11111111000111", service.getCurrentCompanyCnpj())

        SecurityContextHolder.getContext().authentication = TestingAuthenticationToken("second-user", null)

        assertEquals("22222222000122", service.getCurrentCompanyCnpj())
    }

    @Test
    fun `uses a non-existent company cnpj when the authenticated user has none`() {
        SecurityContextHolder.getContext().authentication = TestingAuthenticationToken("user-sub", null)
        every { cognitoUserRepository.findBySub("user-sub") } returns user(cnpjEmpresa = null)

        assertEquals("00000000000000", service.getCurrentCompanyCnpj())
    }

    private fun user(cnpjEmpresa: String?) = CognitoUser(
        id = 1,
        sub = "user-sub",
        username = "user",
        email = null,
        enabled = true,
        cnpjEmpresa = cnpjEmpresa,
        groups = emptyList(),
        lastSyncedAt = Instant.EPOCH,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH,
    )
}
