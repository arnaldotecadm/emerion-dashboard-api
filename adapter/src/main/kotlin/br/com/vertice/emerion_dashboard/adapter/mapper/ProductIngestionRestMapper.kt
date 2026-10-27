package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.product.ingestion.model.IngestProductCommand
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ProductIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ProductIngestionItem

/** Maps between the generated ingestion DTOs and the application command/result models. */
object ProductIngestionRestMapper {

    fun ProductIngestionBatch.toCommand(): IngestBatchCommand =
        IngestBatchCommand(
            batchId = batchId,
            items = items.map { it.toItemCommand() },
        )

    fun ProductIngestionItem.toItemCommand(): IngestProductCommand =
        IngestProductCommand(
            externalId = externalId,
            cnpjEmpresa = cnpjEmpresa,
            nome = nome,
            descricaoReduzida = descricaoReduzida,
            referenciaInterna = referenciaInterna,
            ncm = ncm,
            cest = cest,
            origemProduto = origemProduto,
            categoria = categoria,
            tipo = tipo,
            marca = marca,
            unidade = unidade,
            unidadeEntrada = unidadeEntrada,
            unidadeSaida = unidadeSaida,
            pesoLiquido = pesoLiquido,
            pesoBruto = pesoBruto,
            descontinuado = descontinuado,
            codigoBarras = codigoBarras,
            codigoBarrasProprio = codigoBarrasProprio,
            preco = preco,
            preco2 = preco2,
            preco3 = preco3,
            preco4 = preco4,
            preco5 = preco5,
            custoUltimaCompra = custoUltimaCompra,
            custoPonderado = custoPonderado,
            markup = markup,
            descontoPadrao = descontoPadrao,
            estoqueDisponivel = estoqueDisponivel,
            estoqueMinimo = estoqueMinimo,
            estoqueMaximo = estoqueMaximo,
            estoqueReservado = estoqueReservado,
            estoqueAdquirido = estoqueAdquirido,
            estoqueAtual = estoqueAtual,
            estoqueRma = estoqueRMA,
            similar = similar,
            quantidadeVolumes = quantidadeVolumes,
            quantidadeEmbalagem = quantidadeEmbalagem,
            localizacao = localizacao,
            cubagem = cubagem,
            codigoBarrasEmbalagem = codigoBarrasEmbalagem,
            ibsCClassTrib = ibsCClassTrib,
            ibsCst = ibsCst,
            fcpEntrada = fcpEntrada,
            fcpSaida = fcpSaida,
            ipiSaida = ipiSaida,
            ipiEntrada = ipiEntrada,
            icmSaida = icmSaida,
            icmEntrada = icmEntrada,
            icmStSaida = icmStSaida,
            icmStEntrada = icmStEntrada,
            observacao = observacao,
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
