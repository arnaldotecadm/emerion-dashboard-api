package br.com.vertice.emerion_dashboard.application.ipi.ingestion

import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiBatchCommand
import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiCommand
import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiOutcome
import br.com.vertice.emerion_dashboard.domain.ipi.model.Ipi
import br.com.vertice.emerion_dashboard.domain.ipi.repository.IpiRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals

class IngestIpiServiceTest {
    private val repository = mockk<IpiRepository>()
    private val service = IngestIpiService(repository, Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC))
    private val item = IngestIpiCommand(
        cnpjEmpresa = "15323240000102",
        codigoIpi = "22071010E0",
        tipoIpi = "Entrada",
        nomeIpi = "REGRA IPI ENTRADA 0",
        ncmIpi = "22071010",
        cstIpi = "49",
        descricaoSituacaoTributariaIpi = "OUTRAS ENTRADAS",
        aliquotaIpi = BigDecimal("0.0"),
        percentualBaseCalculoIpi = BigDecimal("100.0"),
        cstPis = "01",
        aliquotaPis = BigDecimal("0.65"),
        incluiDescontoSuframaPis = "S",
        cstCofins = "01",
        aliquotaCofins = BigDecimal("3.0"),
    )

    @Test
    fun `creates a new IPI rule when tenant-safe key is not known yet`() {
        every { repository.findByCnpjEmpresaAndCodigoIpi(item.cnpjEmpresa, item.codigoIpi) } returns null
        val savedSlot = slot<Ipi>()
        every { repository.save(capture(savedSlot)) } answers { savedSlot.captured.copy(id = 1L) }

        val result = service.ingestSingle(item)

        assertEquals(IngestIpiOutcome.CREATED, result.outcome)
        verify { repository.save(match { it.cnpjEmpresa == item.cnpjEmpresa && it.codigoIpi == item.codigoIpi }) }
    }

    @Test
    fun `updates an existing IPI rule when tenant-safe key is already known`() {
        val existing = Ipi(
            id = 42L,
            cnpjEmpresa = item.cnpjEmpresa,
            codigoIpi = item.codigoIpi,
            flgAtivo = null,
            tipoIpi = null,
            nomeIpi = "Old",
            ncmIpi = null,
            codigoEnquadramentoLegal = null,
            cstIpi = null,
            descricaoSituacaoTributariaIpi = null,
            aliquotaIpi = null,
            percentualBaseCalculoIpi = null,
            flgSineif20 = null,
            codigoTextoFiscal = null,
            cstPis = null,
            descricaoSituacaoTributariaPis = null,
            aliquotaPis = null,
            incluiDescontoSuframaPis = null,
            cstCofins = null,
            descricaoSituacaoTributariaCofins = null,
            aliquotaCofins = null,
            incluiDescontoSuframaCofins = null,
            createdAt = Instant.parse("2025-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2025-01-01T00:00:00Z"),
        )
        every { repository.findByCnpjEmpresaAndCodigoIpi(item.cnpjEmpresa, item.codigoIpi) } returns existing
        val savedSlot = slot<Ipi>()
        every { repository.save(capture(savedSlot)) } answers { savedSlot.captured }

        val result = service.ingestSingle(item)

        assertEquals(IngestIpiOutcome.UPDATED, result.outcome)
        verify { repository.save(match { it.id == 42L && it.nomeIpi == "REGRA IPI ENTRADA 0" }) }
    }

    @Test
    fun `records a failed IPI item without aborting other batch items`() {
        every { repository.findByCnpjEmpresaAndCodigoIpi(item.cnpjEmpresa, item.codigoIpi) } returns null
        every { repository.findByCnpjEmpresaAndCodigoIpi(item.cnpjEmpresa, "BAD") } throws RuntimeException("db down")
        val savedSlot = slot<Ipi>()
        every { repository.save(capture(savedSlot)) } answers { savedSlot.captured }

        val result = service.ingest(
            IngestIpiBatchCommand("batch-1", listOf(item, item.copy(codigoIpi = "BAD"))),
        )

        assertEquals(1, result.totalSucceeded)
        assertEquals(1, result.totalFailed)
    }
}
