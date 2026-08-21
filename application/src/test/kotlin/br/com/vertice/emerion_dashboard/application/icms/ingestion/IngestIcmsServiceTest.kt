package br.com.vertice.emerion_dashboard.application.icms.ingestion

import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsBatchCommand
import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsCommand
import br.com.vertice.emerion_dashboard.application.icms.ingestion.model.IngestIcmsOutcome
import br.com.vertice.emerion_dashboard.domain.icms.model.Icms
import br.com.vertice.emerion_dashboard.domain.icms.repository.IcmsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals

class IngestIcmsServiceTest {
    private val repository = mockk<IcmsRepository>()
    private val service = IngestIcmsService(repository, Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC))
    private val item = IngestIcmsCommand("15323240000102", "22071010E0", "Entrada", "REGRA ICMS ENTRADA 0", "SP", "3",
        BigDecimal("18.0"), BigDecimal("0.0"), BigDecimal("100.0"), "00 ")

    @Test
    fun `creates a new ICMS rule when tenant-safe key is not known yet`() {
        every { repository.findByCnpjEmpresaAndCodigoIcms(item.cnpjEmpresa, item.codigoIcms) } returns null
        every { repository.save(any()) } answers { firstArg<Icms>().copy(id = 1L) }

        val result = service.ingestSingle(item)

        assertEquals(IngestIcmsOutcome.CREATED, result.outcome)
        verify { repository.save(match { it.cnpjEmpresa == item.cnpjEmpresa && it.codigoIcms == item.codigoIcms && it.aliquotaIcms == BigDecimal("18.0") }) }
    }

    @Test
    fun `updates an existing ICMS rule when tenant-safe key is already known`() {
        val existing = Icms(42L, item.cnpjEmpresa, item.codigoIcms, null, "Old", null, null, null, null, null, null,
            Instant.parse("2025-01-01T00:00:00Z"), Instant.parse("2025-01-01T00:00:00Z"))
        every { repository.findByCnpjEmpresaAndCodigoIcms(item.cnpjEmpresa, item.codigoIcms) } returns existing
        every { repository.save(any()) } answers { firstArg() }

        val result = service.ingestSingle(item)

        assertEquals(IngestIcmsOutcome.UPDATED, result.outcome)
        verify { repository.save(match { it.id == 42L && it.nomeIcms == "REGRA ICMS ENTRADA 0" }) }
    }

    @Test
    fun `records a failed ICMS item without aborting other batch items`() {
        every { repository.findByCnpjEmpresaAndCodigoIcms(item.cnpjEmpresa, item.codigoIcms) } returns null
        every { repository.findByCnpjEmpresaAndCodigoIcms(item.cnpjEmpresa, "BAD") } throws RuntimeException("db down")
        every { repository.save(any()) } answers { firstArg() }

        val result = service.ingest(IngestIcmsBatchCommand("batch-1", listOf(item, item.copy(codigoIcms = "BAD"))))

        assertEquals(1, result.totalSucceeded)
        assertEquals(1, result.totalFailed)
    }
}
