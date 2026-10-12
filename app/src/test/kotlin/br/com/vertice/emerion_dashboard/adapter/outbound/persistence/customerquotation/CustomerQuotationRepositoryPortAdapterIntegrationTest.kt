package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.customerquotation

import br.com.vertice.emerion_dashboard.application.outbound.port.CustomerQuotationRepositoryPort
import br.com.vertice.emerion_dashboard.domain.customerquotation.CustomerQuotation
import br.com.vertice.emerion_dashboard.support.PostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest
class CustomerQuotationRepositoryPortAdapterIntegrationTest(
    @Autowired private val customerQuotationRepository: CustomerQuotationRepositoryPort,
) : PostgresIntegrationTest() {
    @Test
    fun `saves and updates a quotation by tenant-scoped external id`() {
        val quotation = CustomerQuotation.newFromIngestion(
            externalId = "quotation-25689",
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
            numeroCotacao = 1234,
            now = Instant.parse("2026-01-01T00:00:00Z"),
        )

        val saved = customerQuotationRepository.save(quotation)
        val updated = customerQuotationRepository.save(quotation.copy(id = saved.id, statusPedido = "Atualizada"))
        val sameExternalIdForAnotherTenant = customerQuotationRepository.save(
            quotation.copy(cnpjEmpresa = "12345678000199"),
        )

        assertNotNull(saved.id)
        assertEquals(saved.id, updated.id)
        assertEquals("Atualizada", customerQuotationRepository
            .findByCnpjEmpresaAndExternalId(quotation.cnpjEmpresa, quotation.externalId)?.statusPedido)
        assertNotNull(sameExternalIdForAnotherTenant.id)
        assertNotEquals(saved.id, sameExternalIdForAnotherTenant.id)
        assertNull(customerQuotationRepository.findByCnpjEmpresaAndExternalId("00000000000000", quotation.externalId))
    }
}
