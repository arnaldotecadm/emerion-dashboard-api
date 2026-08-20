package br.com.vertice.emerion_dashboard.infrastructure.rest.product.mapper

import br.com.vertice.emerion_dashboard.domain.product.model.Product
import br.com.vertice.emerion_dashboard.domain.shared.Page as DomainPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.PaginationInfo
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ProductPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.ProductResponse
import java.time.ZoneOffset

/** Maps between the domain model and the generated OpenAPI query DTOs. */
object ProductQueryRestMapper {

    fun toResponse(product: Product): ProductResponse =
        ProductResponse(
            id = product.id,
            externalId = product.externalId,
            cnpjEmpresa = product.cnpjEmpresa,
            nome = product.nome,
            descricaoReduzida = product.descricaoReduzida,
            referenciaInterna = product.referenciaInterna,
            ncm = product.ncm,
            cest = product.cest,
            origemProduto = product.origemProduto,
            categoria = product.categoria,
            tipo = product.tipo,
            marca = product.marca,
            unidade = product.unidade,
            unidadeEntrada = product.unidadeEntrada,
            unidadeSaida = product.unidadeSaida,
            pesoLiquido = product.pesoLiquido,
            pesoBruto = product.pesoBruto,
            descontinuado = product.descontinuado,
            codigoBarras = product.codigoBarras,
            codigoBarrasProprio = product.codigoBarrasProprio,
            preco = product.preco,
            preco2 = product.preco2,
            preco3 = product.preco3,
            preco4 = product.preco4,
            preco5 = product.preco5,
            descontoPadrao = product.descontoPadrao,
            estoqueDisponivel = product.estoqueDisponivel,
            estoqueMinimo = product.estoqueMinimo,
            estoqueMaximo = product.estoqueMaximo,
            estoqueReservado = product.estoqueReservado,
            estoqueAdquirido = product.estoqueAdquirido,
            estoqueAtual = product.estoqueAtual,
            estoqueRMA = product.estoqueRma,
            similar = product.similar,
            quantidadeVolumes = product.quantidadeVolumes,
            quantidadeEmbalagem = product.quantidadeEmbalagem,
            localizacao = product.localizacao,
            cubagem = product.cubagem,
            codigoBarrasEmbalagem = product.codigoBarrasEmbalagem,
            ibsCClassTrib = product.ibsCClassTrib,
            ibsCst = product.ibsCst,
            fcpEntrada = product.fcpEntrada,
            fcpSaida = product.fcpSaida,
            ipiSaida = product.ipiSaida,
            ipiEntrada = product.ipiEntrada,
            icmSaida = product.icmSaida,
            icmEntrada = product.icmEntrada,
            icmStSaida = product.icmStSaida,
            icmStEntrada = product.icmStEntrada,
            observacao = product.observacao,
            createdAt = product.createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = product.updatedAt.atOffset(ZoneOffset.UTC),
        )

    fun toPageResponse(page: DomainPage<Product>): ProductPage =
        ProductPage(
            data = page.content.map(::toResponse),
            pagination = PaginationInfo(
                total = page.totalElements,
                page = page.page,
                propertySize = page.size,
                totalPages = page.totalPages,
            ),
        )
}
