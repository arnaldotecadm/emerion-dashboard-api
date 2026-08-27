package br.com.vertice.emerion_dashboard.application.liberacao.ingestion

import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoCommand
import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoDetalheCommand
import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model.IngestLiberacaoOutcome
import br.com.vertice.emerion_dashboard.domain.liberacao.model.Liberacao
import br.com.vertice.emerion_dashboard.domain.liberacao.repository.LiberacaoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.test.assertEquals

class IngestLiberacaoServiceTest {
    private val clock = Clock.fixed(Instant.parse("2026-08-26T10:00:00Z"), ZoneOffset.UTC)
    private val liberacaoRepository = mockk<LiberacaoRepository>()
    private val service = IngestLiberacaoService(liberacaoRepository, clock)

    private fun command(numeroPedido: String = "25687", numeroLiberacao: Int = 1) =
        IngestLiberacaoCommand(
            cnpjEmpresa = "15323240000102",
            codigoEmpresa = 1,
            dataPedido = LocalDate.parse("2026-08-26"),
            numeroPedido = numeroPedido,
            numeroLiberacao = numeroLiberacao,
            dataLiberacao = LocalDate.parse("2026-08-26"),
            horaLiberacao = LocalTime.parse("08:50:13"),
            codigoCliente = 3206,
            quantidadeSeparada = BigDecimal("2"),
            totalLiberadoSemImpostos = BigDecimal("862.5"),
            totalLiberadoComImpostos = BigDecimal("905.97"),
            situacaoLiberacao = "Nao Concluido",
            codigoVendedor = 3,
            comissaoLiberacao = BigDecimal.ZERO,
            totalCusto = BigDecimal("282.24"),
            detalhes = listOf(
                IngestLiberacaoDetalheCommand(
                    numeroSequenciaLiberacao = 1,
                    classificacaoItem = "1",
                    codigoGrupo = "101",
                    codigoSubGrupo = "0001",
                    codigoProduto = "00009",
                    descricaoItemLiberacao = "CABO 2+2 RCA 1,50 MT",
                    quantidadeNoPedido = BigDecimal("100"),
                    totalSeparado = BigDecimal("75"),
                    quantidadeRestante = BigDecimal("25"),
                    totalValorLiquido = BigDecimal("245"),
                    totalValorBruto = BigDecimal("257.35"),
                    percentualDesconto = BigDecimal.ZERO,
                    totalCusto = BigDecimal("101.34"),
                    percentualDeAcrescimo = BigDecimal.ZERO,
                    precoVendaItem = BigDecimal("3.5"),
                    precoPraticado = BigDecimal("3.5"),
                    custoPraticado = BigDecimal("1.4477"),
                ),
            ),
        )

    @Test
    fun `creates a release when its composite key is not known yet`() {
        every { liberacaoRepository.findByNumeroPedidoAndNumeroLiberacao("25687", 1) } returns null
        val saved = slot<Liberacao>()
        every { liberacaoRepository.save(capture(saved)) } answers { saved.captured.copy(id = 1) }

        val result = service.ingestSingle(command())

        assertEquals(IngestLiberacaoOutcome.CREATED, result.outcome)
        assertEquals("25687:1", result.externalId)
        assertEquals(1, saved.captured.detalhes.size)
        assertEquals(Instant.parse("2026-08-26T10:00:00Z"), saved.captured.createdAt)
    }

    @Test
    fun `updates an existing release while preserving its id`() {
        val existing = Liberacao.newFromIngestion(
            cnpjEmpresa = "15323240000102",
            codigoEmpresa = 1,
            dataPedido = LocalDate.parse("2026-08-01"),
            numeroPedido = "25687",
            numeroLiberacao = 1,
            dataLiberacao = LocalDate.parse("2026-08-01"),
            horaLiberacao = LocalTime.MIDNIGHT,
            codigoCliente = 1,
            quantidadeSeparada = BigDecimal.ONE,
            totalLiberadoSemImpostos = BigDecimal.ONE,
            totalLiberadoComImpostos = BigDecimal.ONE,
            situacaoLiberacao = "Pendente",
            codigoVendedor = 1,
            comissaoLiberacao = BigDecimal.ZERO,
            totalCusto = BigDecimal.ONE,
            detalhes = emptyList(),
            now = Instant.parse("2026-08-01T00:00:00Z"),
        ).copy(id = 42)
        every { liberacaoRepository.findByNumeroPedidoAndNumeroLiberacao("25687", 1) } returns existing
        every { liberacaoRepository.save(any()) } answers { firstArg() }

        val result = service.ingestSingle(command())

        assertEquals(IngestLiberacaoOutcome.UPDATED, result.outcome)
        verify {
            liberacaoRepository.save(match {
                it.id == 42L &&
                    it.detalhes.single().numeroSequenciaLiberacao == 1 &&
                    it.updatedAt == Instant.parse("2026-08-26T10:00:00Z")
            })
        }
    }

    @Test
    fun `returns a failure result when persistence fails`() {
        every { liberacaoRepository.findByNumeroPedidoAndNumeroLiberacao("25687", 1) } throws RuntimeException("database unavailable")

        val result = service.ingestSingle(command())

        assertEquals(IngestLiberacaoOutcome.FAILED, result.outcome)
        assertEquals("database unavailable", result.errorMessage)
        verify(exactly = 0) { liberacaoRepository.save(any()) }
    }
}
