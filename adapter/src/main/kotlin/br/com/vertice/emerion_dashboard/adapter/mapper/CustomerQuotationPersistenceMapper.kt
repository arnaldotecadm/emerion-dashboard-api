package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.entity.CustomerQuotationJpaEntity
import br.com.vertice.emerion_dashboard.domain.customerquotation.CustomerQuotation
import java.math.BigDecimal

object CustomerQuotationPersistenceMapper {
    fun CustomerQuotationJpaEntity.toDomain(): CustomerQuotation = CustomerQuotation(
        id = id,
        externalId = externalId,
        cnpjEmpresa = cnpjEmpresa,
        codigoEmpresa = codigoEmpresa,
        codigoCliente = codigoCliente,
        customerExternalId = customerExternalId,
        dteres = dteres,
        numeroPedido = numeroPedido,
        dataPedido = dataPedido,
        totalPedidoComImpostos = totalPedidoComImpostos,
        totalPedidoSemImpostos = totalPedidoSemImpostos,
        cpfCnpj = cpfCnpj,
        statusPedido = statusPedido,
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
        atendenteCod = atendenteCod,
        regimeTributario = regimeTributario,
        nomeRegimeTributario = nomeRegimeTributario,
        codigoPadraoFaturamento = codigoPadraoFaturamento,
        numeroCotacao = numeroCotacao,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    fun CustomerQuotation.toEntity(existing: CustomerQuotationJpaEntity?): CustomerQuotationJpaEntity =
        CustomerQuotationJpaEntity(
            id = existing?.id ?: id,
            externalId = externalId,
            cnpjEmpresa = cnpjEmpresa,
            codigoEmpresa = codigoEmpresa,
            codigoCliente = codigoCliente,
            customerExternalId = customerExternalId,
            dteres = dteres,
            numeroPedido = numeroPedido,
            dataPedido = dataPedido,
            totalPedidoComImpostos = totalPedidoComImpostos,
            totalPedidoSemImpostos = totalPedidoSemImpostos,
            cpfCnpj = cpfCnpj,
            statusPedido = statusPedido,
            totalIpi = totalIpi,
            totalIcms = totalIcms,
            totalPis = totalPis ?: BigDecimal.ZERO,
            totalCofins = totalCofins ?: BigDecimal.ZERO,
            totalSubstituicaoTributaria = totalSubstituicaoTributaria,
            totalDescontoIncondicional = totalDescontoIncondicional ?: BigDecimal.ZERO,
            totalFrete = totalFrete ?: BigDecimal.ZERO,
            totalSeguro = totalSeguro ?: BigDecimal.ZERO,
            totalOutrasDespesas = totalOutrasDespesas ?: BigDecimal.ZERO,
            vendedorExternalId = vendedorExternalId,
            atendenteCod = atendenteCod,
            regimeTributario = regimeTributario,
            nomeRegimeTributario = nomeRegimeTributario,
            codigoPadraoFaturamento = codigoPadraoFaturamento,
            numeroCotacao = numeroCotacao,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
