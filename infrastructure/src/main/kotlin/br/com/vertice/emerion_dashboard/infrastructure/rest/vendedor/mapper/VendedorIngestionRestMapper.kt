package br.com.vertice.emerion_dashboard.infrastructure.rest.vendedor.mapper

import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model.IngestItemResult
import br.com.vertice.emerion_dashboard.application.vendedor.ingestion.model.IngestVendedorCommand
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.VendedorIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.VendedorIngestionItem

/** Maps between the generated OpenAPI DTOs and the application layer's use-case commands/results. */
object VendedorIngestionRestMapper {

    fun toCommand(dto: VendedorIngestionBatch): IngestBatchCommand =
        IngestBatchCommand(
            batchId = dto.batchId,
            items = dto.items.map(::toItemCommand),
        )

    fun toItemCommand(dto: VendedorIngestionItem): IngestVendedorCommand =
        IngestVendedorCommand(
            externalId = dto.externalId,
            cnpjEmpresa = dto.cnpjEmpresa,
            nome = dto.nome,
            apelido = dto.apelido,
            cpfCnpj = dto.cpfCnpj,
            telefone = dto.telefone,
            celular = dto.celular,
            email = dto.email,
            cidade = dto.cidade,
            uf = dto.uf,
            situacao = dto.situacao,
            saldo = dto.saldo,
            dataCadastro = dto.dataCadastro,
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
