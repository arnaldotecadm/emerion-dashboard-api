package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.domain.product.Product
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ProductResponse
import java.time.ZoneOffset

/** Maps the domain model to the generated OpenAPI response DTO. */
object ProductRestMapper {

    fun Product.toResponse(): ProductResponse =
        ProductResponse(
            id = id,
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
            descontoPadrao = descontoPadrao,
            estoqueDisponivel = estoqueDisponivel,
            estoqueMinimo = estoqueMinimo,
            estoqueMaximo = estoqueMaximo,
            estoqueReservado = estoqueReservado,
            estoqueAdquirido = estoqueAdquirido,
            estoqueAtual = estoqueAtual,
            estoqueRMA = estoqueRma,
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
            createdAt = createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = updatedAt.atOffset(ZoneOffset.UTC),
        )
}
