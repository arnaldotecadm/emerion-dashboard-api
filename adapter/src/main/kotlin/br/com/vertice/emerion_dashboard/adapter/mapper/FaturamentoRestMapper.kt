package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.domain.liberacao.model.FaturamentoResumo
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.FaturamentoResumoResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.TopLiberacaoCustomer as TopLiberacaoCustomerResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.TopSoldItem as TopSoldItemResponse

object FaturamentoRestMapper {
    fun FaturamentoResumo.toResponse() =
        FaturamentoResumoResponse(
            topSoldItems = topSoldItems.map {
                TopSoldItemResponse(
                    codigoGrupo = it.codigoGrupo,
                    codigoSubGrupo = it.codigoSubGrupo,
                    codigoProduto = it.codigoProduto,
                    descricaoItemLiberacao = it.descricaoItemLiberacao,
                    totalSeparado = it.totalSeparado,
                )
            },
            topCustomers = topCustomers.map {
                TopLiberacaoCustomerResponse(
                    codigoCliente = it.codigoCliente,
                    nomeFantasia = it.razaoSocial,
                    totalLiberadoComImpostos = it.totalLiberadoComImpostos,
                )
            },
            salesByUf = salesByUf.map {
                br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoSalesByUf(
                    uf = it.uf,
                    amount = it.amount,
                    representationPercentage = it.representationPercentage,
                )
            },
            salespeople = salespeople.map {
                br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoSalespersonSummary(
                    codigoVendedor = it.codigoVendedor,
                    nomeVendedor = it.nomeVendedor,
                    clientesResponsaveis = it.clientesResponsaveis,
                    clientesComCompra = it.clientesComCompra,
                    representationPercentage = it.representationPercentage,
                )
            },
            salespersonTotals = salespersonTotals.map {
                br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.LiberacaoSalespersonTotals(
                    codigoVendedor = it.codigoVendedor,
                    nomeVendedor = it.nomeVendedor,
                    totalFaturado = it.totalFaturado,
                    totalComissao = it.totalComissao,
                    totalDespesas = it.totalDespesas,
                    custosTotais = it.custosTotais,
                    totalIcms = it.totalIcms,
                    totalSemNota = it.totalSemNota,
                )
            },
        )
}
