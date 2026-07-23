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
            codCli = customerOrder.codCli,
            cnpjEmpresa = customerOrder.cnpjEmpresa,
            cpfCnpj = customerOrder.cpfCnpj,
            nronfe = customerOrder.nronfe,
            dteres = customerOrder.dteres,
            sitres = customerOrder.sitres,
            totger = customerOrder.totger,
            totres = customerOrder.totres,
            totipi = customerOrder.totipi,
            totsub = customerOrder.totsub,
            totdescinc = customerOrder.totdescinc,
            itens = customerOrder.itens.map(::toItemResponse),
            createdAt = customerOrder.createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = customerOrder.updatedAt.atOffset(ZoneOffset.UTC),
        )

    private fun toItemResponse(item: CustomerOrderItem): CustomerOrderItemResponse =
        CustomerOrderItemResponse(
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
