package br.com.vertice.emerion_dashboard.infrastructure.rest.customerorder.mapper

import br.com.vertice.emerion_dashboard.domain.customerorder.model.CustomerOrder
import br.com.vertice.emerion_dashboard.domain.customerorder.model.CustomerOrderItem
import br.com.vertice.emerion_dashboard.domain.shared.Page as DomainPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderItemResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.PaginationInfo
import java.time.ZoneOffset

/** Maps between the domain model and the generated OpenAPI query DTOs. */
object CustomerOrderQueryRestMapper {

    fun toResponse(customerOrder: CustomerOrder): CustomerOrderResponse =
        CustomerOrderResponse(
            id = customerOrder.id,
            externalId = customerOrder.externalId,
            codigoEmpresa = customerOrder.codigoEmpresa,
            codigoCliente = customerOrder.codigoCliente,
            cpfCnpj = customerOrder.cpfCnpj,
            numeroPedido = customerOrder.numeroPedido,
            dataPedido = customerOrder.dataPedido,
            statusPedido = customerOrder.statusPedido,
            totalPedidoComImpostos = customerOrder.totalPedidoComImpostos,
            totalPedidoSemImpostos = customerOrder.totalPedidoSemImpostos,
            totalIpi = customerOrder.totalIpi,
            totalIcms = customerOrder.totalIcms,
            totalPis = customerOrder.totalPis,
            totalCofins = customerOrder.totalCofins,
            totalSubstituicaoTributaria = customerOrder.totalSubstituicaoTributaria,
            totalDescontoIncondicional = customerOrder.totalDescontoIncondicional,
            totalFrete = customerOrder.totalFrete,
            totalSeguro = customerOrder.totalSeguro,
            totalOutrasDespesas = customerOrder.totalOutrasDespesas,
            vendedorExternalId = customerOrder.vendedorExternalId,
            dataEntregaPrevista = customerOrder.dataEntregaPrevista,
            codigoTransportadora = customerOrder.codigoTransportadora,
            pedidoAnterior = customerOrder.pedidoAnterior,
            regimeTributario = customerOrder.regimeTributario,
            nomeRegimeTributario = customerOrder.nomeRegimeTributario,
            codigoPadraoFaturamento = customerOrder.codigoPadraoFaturamento,
            itens = customerOrder.itens.map(::toItemResponse),
            createdAt = customerOrder.createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = customerOrder.updatedAt.atOffset(ZoneOffset.UTC),
        )

    private fun toItemResponse(item: CustomerOrderItem): CustomerOrderItemResponse =
        CustomerOrderItemResponse(
            codEmp = item.codEmp,
            dteres = item.dteres,
            numres = item.numres,
            produto = item.produto,
            descricao = item.descricao,
            quantidade = item.quantidade,
            valorUnitario = item.valorUnitario,
            valorTotal = item.valorTotal,
            seqRe2 = item.seqRe2,
            codClp = item.codClp,
            codSt1 = item.codSt1,
            codUnd = item.codUnd,
            vluRe2 = item.vluRe2,
            dscRe2 = item.dscRe2,
            dsrRe2 = item.dsrRe2,
            icmsAliquota = item.icmsAliquota,
            icmsBase = item.icmsBase,
            icmsValor = item.icmsValor,
            icmsReducaoBase = item.icmsReducaoBase,
            icmsSubstituicaoBase = item.icmsSubstituicaoBase,
            icmsSubstituicaoValor = item.icmsSubstituicaoValor,
            icmsSubstituicaoAliquota = item.icmsSubstituicaoAliquota,
            icmsSubstituicaoMargem = item.icmsSubstituicaoMargem,
            icmsSubstituicaoReducaoBase = item.icmsSubstituicaoReducaoBase,
            ipiAliquota = item.ipiAliquota,
            ipiBase = item.ipiBase,
            ipiValor = item.ipiValor,
            ipiClassificacao = item.ipiClassificacao,
            ipiCst = item.ipiCst,
            pisBase = item.pisBase,
            pisAliquota = item.pisAliquota,
            pisValor = item.pisValor,
            pisCst = item.pisCst,
            cofinsBase = item.cofinsBase,
            cofinsAliquota = item.cofinsAliquota,
            cofinsValor = item.cofinsValor,
            cofinsCst = item.cofinsCst,
            descontoValor = item.descontoValor,
            freteValor = item.freteValor,
            seguroValor = item.seguroValor,
            outrasDespesasValor = item.outrasDespesasValor,
            totalItemTributado = item.totalItemTributado,
            totRen = item.totRen,
            totGe2 = item.totGe2,
            observacao = item.observacao,
            pedidoCompraCliente = item.pedidoCompraCliente,
            itemPedidoCompraCliente = item.itemPedidoCompraCliente,
            nroRe2 = item.nroRe2,
            flgVal = item.flgVal,
            flgPac = item.flgPac,
            flgLib = item.flgLib,
            codCfo = item.codCfo,
            codcor = item.codcor,
            codtam = item.codtam,
            descricaoNFe = item.descricaoNFe,
            pesoLiquido = item.pesoLiquido,
            pesoBruto = item.pesoBruto,
            referencia = item.referencia,
            quantidadeFaturada = item.quantidadeFaturada,
            quantidadeSeparada = item.quantidadeSeparada,
            custoTotal = item.custoTotal,
            lucroValor = item.lucroValor,
            lucroPorcentagem = item.lucroPorcentagem,
        )

    fun toPageResponse(page: DomainPage<CustomerOrder>): CustomerOrderPage =
        CustomerOrderPage(
            data = page.content.map(::toResponse),
            pagination = PaginationInfo(
                total = page.totalElements,
                page = page.page,
                propertySize = page.size,
                totalPages = page.totalPages,
            ),
        )
}
