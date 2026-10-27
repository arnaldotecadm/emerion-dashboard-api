package br.com.vertice.emerion_dashboard.adapter.mapper

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

    fun CustomerOrder.toResponse(): CustomerOrderResponse =
        CustomerOrderResponse(
            id = id,
            externalId = externalId,
            codigoEmpresa = codigoEmpresa,
            codigoCliente = codigoCliente,
            nomeFantasia = nomeFantasia,
            cpfCnpj = cpfCnpj,
            numeroPedido = numeroPedido,
            dataPedido = dataPedido,
            statusPedido = statusPedido,
            totalPedidoComImpostos = totalPedidoComImpostos,
            totalPedidoSemImpostos = totalPedidoSemImpostos,
            totalIpi = totalIpi,
            totalIcms = totalIcms,
            totalPis = totalPis,
            totalCofins = totalCofins,
            totalSubstituicaoTributaria = totalSubstituicaoTributaria,
            totalDescontoIncondicional = totalDescontoIncondicional,
            totalFrete = totalFrete,
            totalSeguro = totalSeguro,
            totalOutrasDespesas = totalOutrasDespesas,
            vendedorExternalId = vendedorExternalId,
            dataEntregaPrevista = dataEntregaPrevista,
            codigoTransportadora = codigoTransportadora,
            pedidoAnterior = pedidoAnterior,
            regimeTributario = regimeTributario,
            nomeRegimeTributario = nomeRegimeTributario,
            codigoPadraoFaturamento = codigoPadraoFaturamento,
            ufPedido = ufPedido,
            itens = itens.map { it.toItemResponse() },
            createdAt = createdAt.atOffset(ZoneOffset.UTC),
            updatedAt = updatedAt.atOffset(ZoneOffset.UTC),
        )

    private fun CustomerOrderItem.toItemResponse(): CustomerOrderItemResponse =
        CustomerOrderItemResponse(
            codEmp = codEmp,
            dteres = dteres,
            numres = numres,
            produto = produto,
            descricao = descricao,
            quantidade = quantidade,
            valorUnitario = valorUnitario,
            valorTotal = valorTotal,
            seqRe2 = seqRe2,
            codClp = codClp,
            codSt1 = codSt1,
            codUnd = codUnd,
            vluRe2 = vluRe2,
            dscRe2 = dscRe2,
            dsrRe2 = dsrRe2,
            icmsAliquota = icmsAliquota,
            icmsBase = icmsBase,
            icmsValor = icmsValor,
            icmsReducaoBase = icmsReducaoBase,
            icmsSubstituicaoBase = icmsSubstituicaoBase,
            icmsSubstituicaoValor = icmsSubstituicaoValor,
            icmsSubstituicaoAliquota = icmsSubstituicaoAliquota,
            icmsSubstituicaoMargem = icmsSubstituicaoMargem,
            icmsSubstituicaoReducaoBase = icmsSubstituicaoReducaoBase,
            ipiAliquota = ipiAliquota,
            ipiBase = ipiBase,
            ipiValor = ipiValor,
            ipiClassificacao = ipiClassificacao,
            ipiCst = ipiCst,
            pisBase = pisBase,
            pisAliquota = pisAliquota,
            pisValor = pisValor,
            pisCst = pisCst,
            cofinsBase = cofinsBase,
            cofinsAliquota = cofinsAliquota,
            cofinsValor = cofinsValor,
            cofinsCst = cofinsCst,
            descontoValor = descontoValor,
            freteValor = freteValor,
            seguroValor = seguroValor,
            outrasDespesasValor = outrasDespesasValor,
            totalItemTributado = totalItemTributado,
            totRen = totRen,
            totGe2 = totGe2,
            observacao = observacao,
            pedidoCompraCliente = pedidoCompraCliente,
            itemPedidoCompraCliente = itemPedidoCompraCliente,
            nroRe2 = nroRe2,
            flgVal = flgVal,
            flgPac = flgPac,
            flgLib = flgLib,
            codCfo = codCfo,
            codcor = codcor,
            codtam = codtam,
            descricaoNFe = descricaoNFe,
            pesoLiquido = pesoLiquido,
            pesoBruto = pesoBruto,
            referencia = referencia,
            quantidadeFaturada = quantidadeFaturada,
            quantidadeSeparada = quantidadeSeparada,
            custoTotal = custoTotal,
            lucroValor = lucroValor,
            lucroPorcentagem = lucroPorcentagem,
        )

    fun DomainPage<CustomerOrder>.toPageResponse(): CustomerOrderPage =
        CustomerOrderPage(
            data = content.map { it.toResponse() },
            pagination = PaginationInfo(
                total = totalElements,
                page = page,
                propertySize = size,
                totalPages = totalPages,
            ),
        )
}
