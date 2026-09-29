package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.ipi

import br.com.vertice.emerion_dashboard.application.outbound.port.IpiRepositoryPort
import br.com.vertice.emerion_dashboard.domain.ipi.Ipi
import br.com.vertice.emerion_dashboard.support.PostgresIntegrationTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertEquals

/**
 * Exercises `IpiRepositoryPortAdapter` against a real Postgres instance.
 */
@SpringBootTest
@Disabled
class IpiRepositoryPortAdapterIntegrationTest(
    @Autowired private val ipiRepository: IpiRepositoryPort,
) : PostgresIntegrationTest() {
    @Test
    fun `saves an IPI rule and reads it back by tenant-safe key`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val saved = ipiRepository.save(
            Ipi.newFromIngestion(
                cnpjEmpresa = "15323240000102",
                codigoIpi = "22071010E0",
                tipoIpi = "Entrada",
                nomeIpi = "REGRA IPI ENTRADA 0",
                ncmIpi = "22071010",
                cstIpi = "49",
                descricaoSituacaoTributariaIpi = "OUTRAS ENTRADAS",
                aliquotaIpi = BigDecimal("0.0000"),
                percentualBaseCalculoIpi = BigDecimal("100.0000"),
                cstPis = "01",
                aliquotaPis = BigDecimal("0.6500"),
                incluiDescontoSuframaPis = "S",
                cstCofins = "01",
                aliquotaCofins = BigDecimal("3.0000"),
                now = now,
            ),
        )

        val found = ipiRepository.findByCnpjEmpresaAndCodigoIpi("15323240000102", "22071010E0")

        assertEquals(saved, found)
    }
}
