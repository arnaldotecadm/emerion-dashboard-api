package br.com.vertice.emerion_dashboard.infrastructure.rest.customer.mapper

import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestCustomerCommand
import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestItemResult
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestCustomerAddressDetailCommand
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult

/** Maps between the generated OpenAPI DTOs and the application layer's use-case commands/results. */
object CustomerIngestionRestMapper {

    fun toCommand(dto: CustomerIngestionBatch): IngestBatchCommand =
        IngestBatchCommand(
            batchId = dto.batchId,
            items = dto.items.map(::toItemCommand),
        )

    fun toItemCommand(dto: CustomerIngestionItem): IngestCustomerCommand =
        IngestCustomerCommand(
            externalId = dto.externalId,
            cnpjEmpresa = dto.cnpjEmpresa,
            nomeFantasia = dto.nomeFantasia,
            razaoSocial = dto.razaoSocial,
            cpfCnpj = dto.cpfCnpj,
            inscricaoEstadual = dto.inscricaoEstadual,
            regimeTributario = dto.regimeTributario,
            bloqueado = dto.bloqueado,
            dataNascimento = dto.dataNascimento,
            dataCadastro = dto.dataCadastro,
            dataUltimaAtualizacao = dto.dataUltimaAtualizacao,
            email1 = dto.email1,
            email2 = dto.email2,
            website = dto.website,
            limiteCredito = dto.limiteCredito,
            observacoes = dto.observacoes,
            cnae = dto.cnae,
            vendedorExternalId = dto.vendedorExternalId,
            nomeVendedor = dto.nomeVendedor,
            codigoTipoCliente = dto.codigoTipoCliente,
            codigoGrupoCliente = dto.codigoGrupoCliente,
            codigoCategoriaCliente = dto.codigoCategoriaCliente,
            uf = dto.uf,
            macroRegiao = dto.macroRegiao,
            microRegiao = dto.microRegiao,
            setor = dto.setor,
            enderecos = listOf(
                address("FATURAMENTO", dto.faturamentoCep, dto.faturamentoTipoEndereco, dto.faturamentoEndereco, dto.faturamentoNumero, dto.faturamentoComplemento, dto.faturamentoBairro, dto.faturamentoCidade, dto.faturamentoUf, dto.faturamentoDDDTelefone, dto.faturamentoTelefone, dto.faturamentoDDDFax, dto.faturamentoFax, dto.faturamentoContato, dto.faturamentoDDDCelular, dto.faturamentoCelular),
                address("COBRANCA", dto.cobrancaCep, dto.cobrancaTipoEndereco, dto.cobrancaEndereco, dto.cobrancaNumero, dto.cobrancaComplemento, dto.cobrancaBairro, dto.cobrancaCidade, dto.cobrancaUf, dto.cobrancaDDDTelefone, dto.cobrancaTelefone, dto.cobrancaDDDFax, dto.cobrancaFax, dto.cobrancaContato, dto.cobrancaDDDCelular, dto.cobrancaCelular),
                address("ENTREGA", dto.entregaCep, dto.entregaTipoEndereco, dto.entregaEndereco, dto.entregaNumero, dto.entregaComplemento, dto.entregaBairro, dto.entregaCidade, dto.entregaUf, dto.entregaDDDTelefone, dto.entregaTelefone, dto.entregaDDDFax, dto.entregaFax, dto.entregaContato, dto.entregaDDDCelular, dto.entregaCelular),
            ),
            createdAt = dto.createdAt?.toInstant(),
        )

    private fun address(
        tipo: String, cep: String?, tipoEndereco: String?, endereco: String?, numero: String?, complemento: String?,
        bairro: String?, cidade: String?, uf: String?, dddTelefone: String?, telefone: String?, dddFax: String?,
        fax: String?, contato: String?, dddCelular: String?, celular: String?,
    ) = IngestCustomerAddressDetailCommand(
        tipo = tipo, cep = cep, endereco = endereco, numero = numero, referencia = null, bairro = bairro,
        cidade = cidade, uf = uf, telefone = telefone, telefoneContato = contato, complemento = complemento,
        fax = fax, tipoEndereco = tipoEndereco, dddTelefone = dddTelefone, dddFax = dddFax,
        dddCelular = dddCelular, celular = celular,
    )

    fun toResponse(result: IngestBatchResult): IngestionResult =
        IngestionResult(
            batchId = result.batchId,
            totalReceived = result.totalReceived,
            totalSucceeded = result.totalSucceeded,
            totalFailed = result.totalFailed,
            results = result.results.map(::toItemResponse),
        )

    fun toItemResponse(result: IngestItemResult): IngestionItemResult =
        IngestionItemResult(
            externalId = result.externalId,
            outcome = IngestionItemResult.Outcome.valueOf(result.outcome.name),
            errorMessage = result.errorMessage,
        )
}
