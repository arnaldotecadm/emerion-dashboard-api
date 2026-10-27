package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customercredit.ingestion.model.IngestCustomerCreditCommand
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerCreditIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import java.util.*

/** Maps between the generated ingestion DTOs and the application command/result models. */
object CustomerCreditIngestionRestMapper {

    /**
     * Builds a batch command from the bare array emerion-load-service posts
     * (no batchId envelope). A batchId is generated here purely for
     * tracing/logging and the response's `batchId` field.
     */
    fun List<CustomerCreditIngestionItem>.toCommand(): IngestBatchCommand =
        IngestBatchCommand(
            batchId = UUID.randomUUID().toString(),
            items = map { it.toItemCommand() },
        )

    fun CustomerCreditIngestionItem.toItemCommand(): IngestCustomerCreditCommand =
        IngestCustomerCreditCommand(
            customerExternalId = customerExternalId,
            cnpjEmpresa = cnpjEmpresa,
            sequencia = sequencia,
            data = data,
            dataPedido = dataPedido,
            valorUtilizado = valorUtilizado,
            valorTotal = valorTotal,
            saldo = saldo,
            situacao = situacao,
            tipo = tipo,
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
