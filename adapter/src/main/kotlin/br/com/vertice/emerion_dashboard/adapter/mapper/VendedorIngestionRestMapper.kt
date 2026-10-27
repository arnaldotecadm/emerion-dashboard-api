package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model.IngestVendedorCommand
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.VendedorIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.VendedorIngestionItem

/** Maps between the generated ingestion DTOs and the application command/result models. */
object VendedorIngestionRestMapper {

    fun VendedorIngestionBatch.toCommand(): IngestBatchCommand =
        IngestBatchCommand(
            batchId = batchId,
            items = items.map { it.toItemCommand() },
        )

    fun VendedorIngestionItem.toItemCommand(): IngestVendedorCommand =
        IngestVendedorCommand(
            externalId = externalId,
            cnpjEmpresa = cnpjEmpresa,
            nome = nome,
            apelido = apelido,
            cpfCnpj = cpfCnpj,
            telefone = telefone,
            celular = celular,
            email = email,
            cidade = cidade,
            uf = uf,
            ativo = situacao,
            saldo = saldo,
            dataCadastro = dataCadastro,
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
