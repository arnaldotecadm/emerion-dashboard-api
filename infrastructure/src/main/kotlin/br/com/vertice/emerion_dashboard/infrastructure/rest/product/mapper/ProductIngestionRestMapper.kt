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
            unidadeEntrada = dto.unidadeEntrada,
            unidadeSaida = dto.unidadeSaida,
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
            descontoPadrao = dto.descontoPadrao,
            estoqueDisponivel = dto.estoqueDisponivel,
            estoqueMinimo = dto.estoqueMinimo,
            estoqueMaximo = dto.estoqueMaximo,
            estoqueReservado = dto.estoqueReservado,
            estoqueAdquirido = dto.estoqueAdquirido,
            estoqueAtual = dto.estoqueAtual,
            estoqueRma = dto.estoqueRMA,
            similar = dto.similar,
            quantidadeVolumes = dto.quantidadeVolumes,
            quantidadeEmbalagem = dto.quantidadeEmbalagem,
            localizacao = dto.localizacao,
            cubagem = dto.cubagem,
            codigoBarrasEmbalagem = dto.codigoBarrasEmbalagem,
            ibsCClassTrib = dto.ibsCClassTrib,
            ibsCst = dto.ibsCst,
            fcpEntrada = dto.fcpEntrada,
            fcpSaida = dto.fcpSaida,
            ipiSaida = dto.ipiSaida,
            ipiEntrada = dto.ipiEntrada,
            icmSaida = dto.icmSaida,
            icmEntrada = dto.icmEntrada,
            icmStSaida = dto.icmStSaida,
            icmStEntrada = dto.icmStEntrada,
            observacao = dto.observacao,
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
