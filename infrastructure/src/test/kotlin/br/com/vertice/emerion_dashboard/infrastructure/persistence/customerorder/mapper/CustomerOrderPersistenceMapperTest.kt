package br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.mapper

import br.com.vertice.emerion_dashboard.domain.customerorder.model.CustomerOrder
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import kotlin.test.assertEquals

class CustomerOrderPersistenceMapperTest {
    @Test
    fun `maps ufPedido between domain and persistence entity`() {
        val order = CustomerOrder.newFromIngestion(
            externalId = "uf-pedido-test",
            cnpjEmpresa = "12345678000199",
            codigoEmpresa = 1,
            codigoCliente = 3206,
            cpfCnpj = null,
            numeroPedido = "NF-UF",
            dataPedido = LocalDate.parse("2026-10-11"),
            statusPedido = "ABERTO",
            totalPedidoComImpostos = BigDecimal.ZERO,
            totalPedidoSemImpostos = BigDecimal.ZERO,
            totalIpi = BigDecimal.ZERO,
            totalIcms = BigDecimal.ZERO,
            totalPis = BigDecimal.ZERO,
            totalCofins = BigDecimal.ZERO,
            totalSubstituicaoTributaria = BigDecimal.ZERO,
            totalDescontoIncondicional = BigDecimal.ZERO,
            ufPedido = "SP",
            itens = emptyList(),
            now = Instant.parse("2026-10-11T00:00:00Z"),
        )

        val entity = CustomerOrderPersistenceMapper.toEntity(order, null)

        assertEquals("SP", entity.ufPedido)
        assertEquals("SP", CustomerOrderPersistenceMapper.toDomain(entity).ufPedido)
    }
}
