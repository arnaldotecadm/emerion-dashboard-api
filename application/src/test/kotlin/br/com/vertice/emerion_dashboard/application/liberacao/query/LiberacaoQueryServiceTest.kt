package br.com.vertice.emerion_dashboard.application.liberacao.query

import br.com.vertice.emerion_dashboard.application.liberacao.query.model.ListLiberacoesQuery
import br.com.vertice.emerion_dashboard.domain.liberacao.exception.LiberacaoNotFoundException
import br.com.vertice.emerion_dashboard.domain.liberacao.model.Liberacao
import br.com.vertice.emerion_dashboard.domain.liberacao.repository.LiberacaoRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LiberacaoQueryServiceTest {
    private val liberacaoRepository = mockk<LiberacaoRepository>()
    private val service = LiberacaoQueryService(liberacaoRepository)
    private val liberacao = Liberacao(
        id = 1,
        cnpjEmpresa = "15323240000102",
        codigoEmpresa = 1,
        dataPedido = LocalDate.parse("2026-08-26"),
        numeroPedido = "25687",
        numeroLiberacao = 1,
        dataLiberacao = LocalDate.parse("2026-08-26"),
        horaLiberacao = LocalTime.parse("14:30:46"),
        codigoCliente = 3206,
        quantidadeSeparada = BigDecimal("2"),
        totalLiberadoSemImpostos = BigDecimal("721"),
        totalLiberadoComImpostos = BigDecimal("757.34"),
        situacaoLiberacao = "Faturado",
        codigoVendedor = 3,
        comissaoLiberacao = BigDecimal.ZERO,
        totalCusto = BigDecimal("282.24"),
        detalhes = emptyList(),
        createdAt = Instant.parse("2026-08-26T14:30:46Z"),
        updatedAt = Instant.parse("2026-08-26T14:30:46Z"),
    )

    @Test
    fun `getById returns the release when it exists`() {
        every { liberacaoRepository.findById(1) } returns liberacao

        assertEquals(liberacao, service.getById(1))
    }

    @Test
    fun `getById throws LiberacaoNotFoundException when the release does not exist`() {
        every { liberacaoRepository.findById(1) } returns null

        assertFailsWith<LiberacaoNotFoundException> { service.getById(1) }
    }

    @Test
    fun `list delegates pagination and optional order number to the repository`() {
        val page = Page(content = listOf(liberacao), page = 0, size = 20, totalElements = 1)
        every { liberacaoRepository.findAll(PageRequest(0, 20), "25687") } returns page

        assertEquals(page, service.list(ListLiberacoesQuery(page = 0, size = 20, numeroPedido = "25687")))
    }
}
