package br.com.vertice.emerion_dashboard.infrastructure.persistence.fincre

import br.com.vertice.emerion_dashboard.domain.fincre.model.Fincre
import br.com.vertice.emerion_dashboard.domain.fincre.model.FincreParcela
import br.com.vertice.emerion_dashboard.domain.fincre.repository.FincreRepository
import br.com.vertice.emerion_dashboard.support.PostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import kotlin.test.assertEquals

@SpringBootTest
class FincreRepositoryAdapterIntegrationTest(
    @Autowired private val fincreRepository: FincreRepository,
) : PostgresIntegrationTest() {

    @Test
    fun `saves a titulo and reads it back by tenant-safe key`() {
        val now = Instant.parse("2024-01-01T00:00:00Z")
        val saved = fincreRepository.save(
            Fincre.newFromIngestion(
                cnpjEmpresa = "15323240000102",
                codigoEmpresa = 1L,
                dataEmissao = LocalDate.parse("2013-02-04"),
                documento = "1",
                nomeCliente = "YUJING INTERNATIONAL LTD",
                parcelas = listOf(
                    FincreParcela(
                        numeroParcela = 1,
                        valorParcela = BigDecimal("480.1700"),
                        dataVencimento = LocalDate.parse("2013-02-04"),
                    ),
                ),
                now = now,
            ),
        )

        val found = fincreRepository.findByCnpjEmpresaAndDocumento("15323240000102", "1")

        assertEquals(saved, found)
    }
}
