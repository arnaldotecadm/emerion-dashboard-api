package br.com.vertice.emerion_dashboard.application.customerquotation.ingestion

import br.com.vertice.emerion_dashboard.application.customerquotation.ingestion.model.IngestCustomerQuotationBatchCommand
import br.com.vertice.emerion_dashboard.application.customerquotation.ingestion.model.IngestCustomerQuotationCommand
import br.com.vertice.emerion_dashboard.application.outbound.port.CustomerQuotationRepositoryPort
import br.com.vertice.emerion_dashboard.domain.customerquotation.CustomerQuotation
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestOutcome
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

class IngestCustomerQuotationServiceTest {
    private val repository = mockk<CustomerQuotationRepositoryPort>()
    private val service =
        IngestCustomerQuotationService(repository, Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC))
    private val item = IngestCustomerQuotationCommand(
        externalId = "25689",
        cnpjEmpresa = "15323240000102",
        codigoEmpresa = 1,
        codigoCliente = 3206,
        customerExternalId = 3206L,
        dteres = LocalDate.parse("2026-08-27"),
        numeroPedido = "25689",
        dataPedido = LocalDate.parse("2026-08-27"),
        totalPedidoComImpostos = BigDecimal("20.41"),
        totalPedidoSemImpostos = BigDecimal("19.43"),
        cpfCnpj = "06244060000136",
        statusPedido = "Atendida",
        totalIpi = BigDecimal.ZERO,
        totalIcms = BigDecimal("2.33"),
        totalPis = BigDecimal("0.13"),
        totalCofins = BigDecimal("0.58"),
        totalSubstituicaoTributaria = BigDecimal("0.98"),
        totalDescontoIncondicional = BigDecimal.ZERO,
        totalFrete = BigDecimal.ZERO,
        totalSeguro = BigDecimal.ZERO,
        totalOutrasDespesas = BigDecimal.ZERO,
        vendedorExternalId = 3L,
        atendenteCod = null,
        regimeTributario = "1",
        nomeRegimeTributario = "Simples Nacional",
        codigoPadraoFaturamento = "5.102",
    )

    @Test
    fun `creates a customer quotation when tenant-scoped external id is unknown`() {
        every {
            repository.findByCnpjEmpresaAndExternalId(item.cnpjEmpresa, item.externalId)
        } returns null
        val saved = slot<CustomerQuotation>()
        every { repository.save(capture(saved)) } answers { saved.captured.copy(id = 1L) }

        val result = service.ingestSingle(item)

        assertEquals(IngestOutcome.CREATED, result.outcome)
        verify {
            repository.save(
                match { it.cnpjEmpresa == item.cnpjEmpresa && it.externalId == item.externalId },
            )
        }
    }

    @Test
    fun `updates a customer quotation while preserving its identity`() {
        val existing = CustomerQuotation.newFromIngestion(
            externalId = item.externalId,
            cnpjEmpresa = item.cnpjEmpresa,
            codigoEmpresa = item.codigoEmpresa,
            codigoCliente = item.codigoCliente,
            customerExternalId = item.customerExternalId,
            dteres = item.dteres,
            numeroPedido = item.numeroPedido,
            dataPedido = item.dataPedido,
            totalPedidoComImpostos = item.totalPedidoComImpostos,
            totalPedidoSemImpostos = item.totalPedidoSemImpostos,
            cpfCnpj = item.cpfCnpj,
            statusPedido = "Pendente",
            totalIpi = item.totalIpi,
            totalIcms = item.totalIcms,
            totalPis = item.totalPis,
            totalCofins = item.totalCofins,
            totalSubstituicaoTributaria = item.totalSubstituicaoTributaria,
            totalDescontoIncondicional = item.totalDescontoIncondicional,
            totalFrete = item.totalFrete,
            totalSeguro = item.totalSeguro,
            totalOutrasDespesas = item.totalOutrasDespesas,
            vendedorExternalId = item.vendedorExternalId,
            atendenteCod = null,
            regimeTributario = item.regimeTributario,
            nomeRegimeTributario = item.nomeRegimeTributario,
            codigoPadraoFaturamento = item.codigoPadraoFaturamento,
            now = Instant.parse("2025-01-01T00:00:00Z"),
        ).copy(id = 42L)
        every {
            repository.findByCnpjEmpresaAndExternalId(item.cnpjEmpresa, item.externalId)
        } returns existing
        val saved = slot<CustomerQuotation>()
        every { repository.save(capture(saved)) } answers { saved.captured }

        val result = service.ingestSingle(item)

        assertEquals(IngestOutcome.UPDATED, result.outcome)
        verify { repository.save(match { it.id == 42L && it.statusPedido == item.statusPedido }) }
    }

    @Test
    fun `records a failed quotation without aborting the rest of the batch`() {
        every {
            repository.findByCnpjEmpresaAndExternalId(item.cnpjEmpresa, item.externalId)
        } returns null
        every {
            repository.findByCnpjEmpresaAndExternalId(item.cnpjEmpresa, "BAD")
        } throws RuntimeException("db down")
        val saved = slot<CustomerQuotation>()
        every { repository.save(capture(saved)) } answers { saved.captured }

        val result = service.ingest(
            IngestCustomerQuotationBatchCommand(
                "batch-1",
                listOf(item, item.copy(externalId = "BAD")),
            ),
        )

        assertEquals(1, result.totalSucceeded)
        assertEquals(1, result.totalFailed)
        assertEquals(IngestOutcome.FAILED, result.results[1].outcome)
    }
}
