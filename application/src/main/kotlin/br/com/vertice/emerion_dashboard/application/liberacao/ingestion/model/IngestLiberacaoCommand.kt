package br.com.vertice.emerion_dashboard.application.liberacao.ingestion.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

data class IngestLiberacaoCommand(
    val cnpjEmpresa: String,
    val codigoEmpresa: Long,
    val dataPedido: LocalDate,
    val numeroPedido: String,
    val numeroLiberacao: Int,
    val dataLiberacao: LocalDate,
    val horaLiberacao: LocalTime,
    val codigoCliente: Long,
    val quantidadeSeparada: BigDecimal,
    val totalLiberadoSemImpostos: BigDecimal,
    val totalLiberadoComImpostos: BigDecimal,
    val situacaoLiberacao: String,
    val codigoVendedor: Long,
    val comissaoLiberacao: BigDecimal,
    val totalCusto: BigDecimal,
    val detalhes: List<IngestLiberacaoDetalheCommand>,
)
