package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.application.liberacao.ingestion.IngestLiberacaoService
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestOutcome
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoIngestionItem
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertEquals

class LiberacaoIngestionControllerTest {
    @Test
    fun `maps a batch into commands and returns queued outcomes`() {
        val service = mockk<IngestLiberacaoService>()
        val controller = LiberacaoIngestionController(service)
        val first = item(100L, 1)
        val second = item(101L, 2)
        every { service.ingest("batch-1", any()) } returns IngestBatchResult(
            "batch-1",
            listOf(
                IngestItemResult("100:1", IngestOutcome.QUEUED, null),
                IngestItemResult("101:2", IngestOutcome.QUEUED, null),
            ),
        )

        val result = controller.ingestLiberacoes(LiberacaoIngestionBatch("batch-1", listOf(first, second))).body!!

        assertEquals(2, result.totalReceived)
        assertEquals(listOf("100:1", "101:2"), result.results!!.map { it.externalId })
        assertEquals(listOf(IngestionItemResult.Outcome.QUEUED, IngestionItemResult.Outcome.QUEUED), result.results!!.map { it.outcome })
        verify {
            service.ingest("batch-1", match {
                it.map { command -> command.numeroPedido } == listOf(100L, 101L) &&
                    it.all { command -> command.horaLiberacao == LocalTime.parse("08:50:13") }
            })
        }
    }

    private fun item(numeroPedido: Long, numeroLiberacao: Int) = LiberacaoIngestionItem(
        cnpjEmpresa = "test",
        codigoEmpresa = 1,
        dataPedido = LocalDate.parse("2026-08-26"),
        dataFaturamento = LocalDate.parse("2026-08-26"),
        numeroPedido = numeroPedido,
        numeroLiberacao = numeroLiberacao,
        dataLiberacao = LocalDate.parse("2026-08-27"),
        horaLiberacao = "08:50:13",
        codigoCliente = 2,
        quantidadeSeparada = BigDecimal.ONE,
        totalLiberadoSemImpostos = BigDecimal.ONE,
        totalLiberadoComImpostos = BigDecimal.ONE,
        situacaoLiberacao = "open",
        codigoVendedor = 3,
        comissaoLiberacao = BigDecimal.ZERO,
        totalCusto = BigDecimal.ONE,
        detalhes = emptyList(),
    )
}
