package br.com.vertice.emerion_dashboard.infrastructure.persistence.liberacao.projection

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

interface LiberacaoProjection {
    val id: Long
    val cnpjEmpresa: String
    val codigoEmpresa: Long
    val dataPedido: LocalDate
    val numeroPedido: String
    val numeroLiberacao: Int
    val dataLiberacao: LocalDate
    val horaLiberacao: LocalTime
    val codigoCliente: Long
    val quantidadeSeparada: BigDecimal
    val totalLiberadoSemImpostos: BigDecimal
    val totalLiberadoComImpostos: BigDecimal
    val situacaoLiberacao: String
    val codigoVendedor: Long
    val comissaoLiberacao: BigDecimal
    val totalCusto: BigDecimal
    val createdAt: Instant
    val updatedAt: Instant
}
