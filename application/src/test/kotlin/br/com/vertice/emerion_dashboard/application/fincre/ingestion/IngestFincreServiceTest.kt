package br.com.vertice.emerion_dashboard.application.fincre.ingestion

import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreBatchCommand
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreCommand
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreOutcome
import br.com.vertice.emerion_dashboard.application.fincre.ingestion.model.IngestFincreParcelaCommand
import br.com.vertice.emerion_dashboard.domain.fincre.model.Fincre
import br.com.vertice.emerion_dashboard.domain.fincre.repository.FincreRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.test.assertEquals

class IngestFincreServiceTest {
    private val repository = mockk<FincreRepository>()
    private val service = IngestFincreService(repository, Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC))

    private val item = IngestFincreCommand(
        cnpjEmpresa = "15323240000102",
        codigoEmpresa = 1L,
        dataEmissao = LocalDate.parse("2013-02-04"),
        documento = "1",
        codigoCondicaoRecebimento = "1",
        nomeCondicaoRecebimento = "A VISTA",
        nomeEmpresa = "NEW IMPORTS",
        codigoCliente = 140L,
        nomeCliente = "YUJING INTERNATIONAL LTD",
        codigoVendedor = 2L,
        nomeVendedor = "EDUARDO",
        codigoTipoDocumento = "5",
        nomeTipoDocumento = "OUTROS",
        parcelas = listOf(
            IngestFincreParcelaCommand(
                numeroParcela = 1,
                flagIncobravel = "Nao",
                dataVencimento = LocalDate.parse("2013-02-04"),
                prazoEmDias = 0,
                valorParcela = BigDecimal("480.17"),
                codigoBanco = "888",
                nomeBanco = "INDEFINIDO",
                observacoes = " ",
                flagCartaAnuencia = "Nao",
                flagPago = "*",
            ),
        ),
    )

    @Test
    fun `creates a new titulo when tenant-safe key is not known yet`() {
        every { repository.findByCnpjEmpresaAndDocumento(item.cnpjEmpresa, item.documento) } returns null
        val savedSlot = slot<Fincre>()
        every { repository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }

        val result = service.ingestSingle(item)

        assertEquals(IngestFincreOutcome.CREATED, result.outcome)
        verify { repository.save(match { it.documento == "1" && it.parcelas.size == 1 }) }
    }

    @Test
    fun `updates an existing titulo when tenant-safe key is already known`() {
        val existing = Fincre.newFromIngestion(
            cnpjEmpresa = item.cnpjEmpresa,
            documento = item.documento,
            parcelas = item.parcelas.map {
                br.com.vertice.emerion_dashboard.domain.fincre.model.FincreParcela(numeroParcela = it.numeroParcela)
            },
            now = Instant.parse("2025-01-01T00:00:00Z"),
        ).copy(id = 42L)

        every { repository.findByCnpjEmpresaAndDocumento(item.cnpjEmpresa, item.documento) } returns existing
        val savedSlot = slot<Fincre>()
        every { repository.save(capture(savedSlot)) } answers { savedSlot.captured }

        val result = service.ingestSingle(item)

        assertEquals(IngestFincreOutcome.UPDATED, result.outcome)
        verify { repository.save(match { it.id == 42L && it.nomeCliente == "YUJING INTERNATIONAL LTD" }) }
    }

    @Test
    fun `records a failed item without aborting other batch items`() {
        every { repository.findByCnpjEmpresaAndDocumento(item.cnpjEmpresa, item.documento) } returns null
        every { repository.findByCnpjEmpresaAndDocumento(item.cnpjEmpresa, "BAD") } throws RuntimeException("db down")
        val savedSlot = slot<Fincre>()
        every { repository.save(capture(savedSlot)) } answers { savedSlot.captured }

        val result = service.ingest(IngestFincreBatchCommand("batch-1", listOf(item, item.copy(documento = "BAD"))))

        assertEquals(1, result.totalSucceeded)
        assertEquals(1, result.totalFailed)
    }
}
