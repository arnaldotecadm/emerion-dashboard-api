package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.application.customerquotation.ingestion.model.IngestCustomerQuotationBatchCommand
import br.com.vertice.emerion_dashboard.application.customerquotation.ingestion.model.IngestCustomerQuotationCommand
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerQuotationIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerQuotationIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult as IngestionItemResultDto
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult

object CustomerQuotationIngestionRestMapper {
    fun CustomerQuotationIngestionBatch.toCommand(): IngestCustomerQuotationBatchCommand =
        IngestCustomerQuotationBatchCommand(batchId, items.map { it.toItemCommand() })

    fun CustomerQuotationIngestionItem.toItemCommand(): IngestCustomerQuotationCommand =
        IngestCustomerQuotationCommand(
            externalId = externalId,
            cnpjEmpresa = cnpjEmpresa,
            codigoEmpresa = codigoEmpresa,
            codigoCliente = codigoCliente,
            customerExternalId = customerExternalId,
            dteres = dteres,
            numeroPedido = numeroPedido,
            dataPedido = dataPedido,
            totalPedidoComImpostos = totalPedidoComImpostos,
            totalPedidoSemImpostos = totalPedidoSemImpostos,
            cpfCnpj = cpfCnpj,
            statusPedido = statusPedido,
            totalIpi = totalIpi,
            totalIcms = totalIcms,
            totalPis = totalPis,
            totalCofins = totalCofins,
            totalSubstituicaoTributaria = totalSubstituicaoTributaria,
            totalDescontoIncondicional = totalDescontoIncondicional,
            totalFrete = totalFrete,
            totalSeguro = totalSeguro,
            totalOutrasDespesas = totalOutrasDespesas,
            vendedorExternalId = vendedorExternalId,
            atendenteCod = atendenteCod,
            regimeTributario = regimeTributario,
            nomeRegimeTributario = nomeRegimeTributario,
            codigoPadraoFaturamento = codigoPadraoFaturamento,
            numeroCotacao = numeroCotacao,
        )

    fun IngestBatchResult.toResponse(): IngestionResult = IngestionResult(
        batchId = batchId,
        totalReceived = totalReceived,
        totalSucceeded = totalSucceeded,
        totalFailed = totalFailed,
        results = results.map { it.toItemResponse() },
    )

    fun IngestItemResult.toItemResponse(): IngestionItemResultDto = IngestionItemResultDto(
        externalId = externalId,
        outcome = IngestionItemResultDto.Outcome.valueOf(outcome.name),
        errorMessage = errorMessage,
    )
}
