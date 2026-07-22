package br.com.vertice.emerion_dashboard.infrastructure.rest.customerorder.mapper

import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestCustomerOrderCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestCustomerOrderItemCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderItemIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult

/** Maps between the generated OpenAPI DTOs and the application layer's use-case commands/results. */
object CustomerOrderIngestionRestMapper {

    fun toCommand(dto: CustomerOrderIngestionBatch): IngestBatchCommand =
        IngestBatchCommand(
            batchId = dto.batchId,
            items = dto.items.map(::toItemCommand),
        )

    fun toItemCommand(dto: CustomerOrderIngestionItem): IngestCustomerOrderCommand =
        IngestCustomerOrderCommand(
            externalId = dto.externalId,
            codCli = dto.codCli,
            cnpjEmpresa = dto.cnpjEmpresa,
            nronfe = dto.nronfe,
            dteres = dto.dteres.toInstant(),
            sitres = dto.sitres,
            totger = dto.totger,
            totres = dto.totres,
            totipi = dto.totipi,
            totsub = dto.totsub,
            totdescinc = dto.totdescinc,
            itens = dto.itens.map(::toItemLineCommand),
        )

    private fun toItemLineCommand(dto: CustomerOrderItemIngestionItem): IngestCustomerOrderItemCommand =
        IngestCustomerOrderItemCommand(
            produto = dto.produto,
            descricao = dto.descricao,
            quantidade = dto.quantidade,
            valorUnitario = dto.valorUnitario,
            valorTotal = dto.valorTotal,
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
