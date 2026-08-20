package br.com.vertice.emerion_dashboard.application.customer.ingestion.model

import java.time.Instant
import java.time.LocalDate
import java.math.BigDecimal
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestCustomerAddressDetailCommand

/** Input command for a single customer inside an ingestion batch. */
data class IngestCustomerCommand(
    val externalId: String,
    val cnpjEmpresa: String,
    val nomeFantasia: String,
    val razaoSocial: String,
    val cpfCnpj: String,
    val inscricaoEstadual: String?,
    val regimeTributario: String?,
    val bloqueado: Boolean,
    val dataNascimento: LocalDate? = null,
    val dataCadastro: LocalDate? = null,
    val dataUltimaAtualizacao: LocalDate? = null,
    val email1: String? = null,
    val email2: String? = null,
    val website: String? = null,
    val limiteCredito: BigDecimal? = null,
    val observacoes: String? = null,
    val cnae: String? = null,
    val vendedorExternalId: Long? = null,
    val nomeVendedor: String? = null,
    val codigoTipoCliente: String? = null,
    val codigoGrupoCliente: String? = null,
    val codigoCategoriaCliente: String? = null,
    val uf: String? = null,
    val macroRegiao: String? = null,
    val microRegiao: String? = null,
    val setor: String? = null,
    val enderecos: List<IngestCustomerAddressDetailCommand> = emptyList(),
    val createdAt: Instant?,
)
