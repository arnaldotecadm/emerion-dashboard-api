package br.com.vertice.emerion_dashboard.infrastructure.rest.product.mapper

import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestItemResult
import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestProductCommand
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ProductIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ProductIngestionItem

/** Maps between the generated OpenAPI DTOs and the application layer's use-case commands/results. */
object ProductIngestionRestMapper {

    fun toCommand(dto: ProductIngestionBatch): IngestBatchCommand =
        IngestBatchCommand(
            batchId = dto.batchId,
            items = dto.items.map(::toItemCommand),
        )

    fun toItemCommand(dto: ProductIngestionItem): IngestProductCommand =
        IngestProductCommand(
            externalId = dto.externalId,
            cnpjEmpresa = dto.cnpjEmpresa,
            nome = dto.nome,
            descricaoReduzida = dto.descricaoReduzida,
            referenciaInterna = dto.referenciaInterna,
            ncm = dto.ncm,
            cest = dto.cest,
            origemProduto = dto.origemProduto,
            categoria = dto.categoria,
            tipo = dto.tipo,
            marca = dto.marca,
            unidade = dto.unidade,
            pesoLiquido = dto.pesoLiquido,
            pesoBruto = dto.pesoBruto,
            descontinuado = dto.descontinuado,
            codigoBarras = dto.codigoBarras,
            codigoBarrasProprio = dto.codigoBarrasProprio,
            preco = dto.preco,
            preco2 = dto.preco2,
            preco3 = dto.preco3,
            preco4 = dto.preco4,
            preco5 = dto.preco5,
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
