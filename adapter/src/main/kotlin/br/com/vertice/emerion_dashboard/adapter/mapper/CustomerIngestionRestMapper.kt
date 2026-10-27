package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestCustomerCommand
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestCustomerAddressDetailCommand
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult

/** Maps between the generated ingestion DTOs and the application command/result models. */
object CustomerIngestionRestMapper {

    fun CustomerIngestionBatch.toCommand(): IngestBatchCommand =
        IngestBatchCommand(
            batchId = batchId,
            items = items.map { it.toItemCommand() },
        )

    fun CustomerIngestionItem.toItemCommand(): IngestCustomerCommand =
        IngestCustomerCommand(
            externalId = externalId,
            cnpjEmpresa = cnpjEmpresa,
            nomeFantasia = nomeFantasia,
            razaoSocial = razaoSocial,
            cpfCnpj = cpfCnpj,
            inscricaoEstadual = inscricaoEstadual,
            regimeTributario = regimeTributario,
            bloqueado = bloqueado,
            dataNascimento = dataNascimento,
            dataCadastro = dataCadastro,
            dataUltimaAtualizacao = dataUltimaAtualizacao,
            email1 = email1,
            email2 = email2,
            website = website,
            limiteCredito = limiteCredito,
            observacoes = observacoes,
            cnae = cnae,
            vendedorExternalId = vendedorExternalId,
            nomeVendedor = nomeVendedor,
            codigoTipoCliente = codigoTipoCliente,
            codigoGrupoCliente = codigoGrupoCliente,
            codigoCategoriaCliente = codigoCategoriaCliente,
            uf = uf,
            macroRegiao = macroRegiao,
            microRegiao = microRegiao,
            setor = setor,
            enderecos = listOf(
                address(
                    "FATURAMENTO",
                    faturamentoCep,
                    faturamentoTipoEndereco,
                    faturamentoEndereco,
                    faturamentoNumero,
                    faturamentoComplemento,
                    faturamentoBairro,
                    faturamentoCidade,
                    faturamentoUf,
                    faturamentoDDDTelefone,
                    faturamentoTelefone,
                    faturamentoDDDFax,
                    faturamentoFax,
                    faturamentoContato,
                    faturamentoDDDCelular,
                    faturamentoCelular
                ),
                address(
                    "COBRANCA",
                    cobrancaCep,
                    cobrancaTipoEndereco,
                    cobrancaEndereco,
                    cobrancaNumero,
                    cobrancaComplemento,
                    cobrancaBairro,
                    cobrancaCidade,
                    cobrancaUf,
                    cobrancaDDDTelefone,
                    cobrancaTelefone,
                    cobrancaDDDFax,
                    cobrancaFax,
                    cobrancaContato,
                    cobrancaDDDCelular,
                    cobrancaCelular
                ),
                address(
                    "ENTREGA",
                    entregaCep,
                    entregaTipoEndereco,
                    entregaEndereco,
                    entregaNumero,
                    entregaComplemento,
                    entregaBairro,
                    entregaCidade,
                    entregaUf,
                    entregaDDDTelefone,
                    entregaTelefone,
                    entregaDDDFax,
                    entregaFax,
                    entregaContato,
                    entregaDDDCelular,
                    entregaCelular
                ),
            ),
            createdAt = createdAt?.toInstant(),
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

    fun IngestBatchResult.toResponse(): IngestionResult =
        IngestionResult(
            batchId = batchId,
            totalReceived = totalReceived,
            totalSucceeded = totalSucceeded,
            totalFailed = totalFailed,
            results = results.map { it.toItemResponse() },
        )

    fun IngestItemResult.toItemResponse(): IngestionItemResult =
        IngestionItemResult(
            externalId = externalId,
            outcome = IngestionItemResult.Outcome.valueOf(outcome.name),
            errorMessage = errorMessage,
        )
}
