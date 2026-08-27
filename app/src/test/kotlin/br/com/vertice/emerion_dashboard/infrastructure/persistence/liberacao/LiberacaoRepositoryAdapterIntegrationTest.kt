package br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao

import br.com.vertice.emerion_dashboard.domain.liberacao.model.Liberacao
import br.com.vertice.emerion_dashboard.domain.liberacao.model.LiberacaoDetalhe
import br.com.vertice.emerion_dashboard.domain.liberacao.repository.LiberacaoRepository
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import br.com.vertice.emerion_dashboard.support.PostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertEquals

@SpringBootTest
class LiberacaoRepositoryAdapterIntegrationTest(
    @Autowired private val liberacaoRepository: LiberacaoRepository,
) : PostgresIntegrationTest() {

    @Test
    fun `saves a release and details linked by pedido and liberacao numbers`() {
        val saved = liberacaoRepository.save(
            Liberacao.newFromIngestion(
                cnpjEmpresa = "15323240000102",
                codigoEmpresa = 1,
                dataPedido = LocalDate.parse("2026-08-26"),
                numeroPedido = "25687",
                numeroLiberacao = 1,
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
                    LiberacaoDetalhe(
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
                now = Instant.parse("2026-08-26T10:00:00Z"),
            ),
        )

        val found = liberacaoRepository.findByNumeroPedidoAndNumeroLiberacao("25687", 1)

        assertEquals(saved.id, found?.id)
        assertEquals("25687", found?.numeroPedido)
        assertEquals(1, found?.numeroLiberacao)
        assertEquals("15323240000102", found?.cnpjEmpresa)
        assertEquals(1, found?.detalhes?.single()?.numeroSequenciaLiberacao)
        assertEquals("00009", found?.detalhes?.single()?.codigoProduto)

        val readProjection = liberacaoRepository.findById(saved.id!!)
        val secondRelease = liberacaoRepository.save(saved.copy(id = null, numeroLiberacao = 2))
        val matchingOrder = liberacaoRepository.findAll(PageRequest(page = 0, size = 20), "25687")

        assertEquals(saved.id, readProjection?.id)
        assertEquals(BigDecimal("282.2400"), readProjection?.totalCusto)
        assertEquals(BigDecimal("101.3400"), readProjection?.detalhes?.single()?.totalCusto)
        assertEquals(setOf(saved.id, secondRelease.id), matchingOrder.content.map { it.id }.toSet())
    }
}
