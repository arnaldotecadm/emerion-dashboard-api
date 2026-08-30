package br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.mapper

import br.com.vertice.emerion_dashboard.domain.customerorder.model.CustomerOrder
import br.com.vertice.emerion_dashboard.domain.customerorder.model.CustomerOrderItem
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.model.CustomerOrderItemJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.model.CustomerOrderJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.projection.CustomerOrderHeaderProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customerorder.projection.CustomerOrderItemProjection

/** Maps between the domain model and the JPA entities/read projections. Kept out of the entity/domain classes on purpose. */
object CustomerOrderPersistenceMapper {

    /**
     * Read path: header + item native-query projections (see
     * `CustomerOrderQueryRepository`) -> domain model.
     */
    fun toDomain(header: CustomerOrderHeaderProjection, items: List<CustomerOrderItemProjection>): CustomerOrder =
        CustomerOrder(
            id = header.id,
            externalId = header.externalId,
            codigoEmpresa = header.codigoEmpresa,
            codigoCliente = header.codigoCliente,
            cpfCnpj = header.cpfCnpj,
            numeroPedido = header.numeroPedido,
            dataPedido = header.dataPedido,
            statusPedido = header.statusPedido,
            totalPedidoComImpostos = header.totalPedidoComImpostos,
            totalPedidoSemImpostos = header.totalPedidoSemImpostos,
            totalIpi = header.totalIpi,
            totalIcms = header.totalIcms,
            totalPis = header.totalPis,
            totalCofins = header.totalCofins,
            totalSubstituicaoTributaria = header.totalSubstituicaoTributaria,
            totalDescontoIncondicional = header.totalDescontoIncondicional,
            totalFrete = header.totalFrete,
            totalSeguro = header.totalSeguro,
            totalOutrasDespesas = header.totalOutrasDespesas,
            vendedorExternalId = header.vendedorExternalId,
            dataEntregaPrevista = header.dataEntregaPrevista,
            codigoTransportadora = header.codigoTransportadora,
            pedidoAnterior = header.pedidoAnterior,
            regimeTributario = header.regimeTributario,
            nomeRegimeTributario = header.nomeRegimeTributario,
            codigoPadraoFaturamento = header.codigoPadraoFaturamento,
            itens = items.map(::toItemDomain),
            createdAt = header.createdAt,
            updatedAt = header.updatedAt,
        )

    private fun toItemDomain(projection: CustomerOrderItemProjection): CustomerOrderItem =
        CustomerOrderItem(
            codEmp = projection.codEmp,
            dteres = projection.dteres,
            numres = projection.numres,
            produto = projection.produto,
            descricao = projection.descricao,
            quantidade = projection.quantidade,
            valorUnitario = projection.valorUnitario,
            valorTotal = projection.valorTotal,
            seqRe2 = projection.seqRe2,
            codClp = projection.codClp,
            codSt1 = projection.codSt1,
            codUnd = projection.codUnd,
            vluRe2 = projection.vluRe2,
            dscRe2 = projection.dscRe2,
            dsrRe2 = projection.dsrRe2,
            icmsAliquota = projection.icmsAliquota,
            icmsBase = projection.icmsBase,
            icmsValor = projection.icmsValor,
            icmsReducaoBase = projection.icmsReducaoBase,
            icmsSubstituicaoBase = projection.icmsSubstituicaoBase,
            icmsSubstituicaoValor = projection.icmsSubstituicaoValor,
            icmsSubstituicaoAliquota = projection.icmsSubstituicaoAliquota,
            icmsSubstituicaoMargem = projection.icmsSubstituicaoMargem,
            icmsSubstituicaoReducaoBase = projection.icmsSubstituicaoReducaoBase,
            ipiAliquota = projection.ipiAliquota,
            ipiBase = projection.ipiBase,
            ipiValor = projection.ipiValor,
            ipiClassificacao = projection.ipiClassificacao,
            ipiCst = projection.ipiCst,
            pisBase = projection.pisBase,
            pisAliquota = projection.pisAliquota,
            pisValor = projection.pisValor,
            pisCst = projection.pisCst,
            cofinsBase = projection.cofinsBase,
            cofinsAliquota = projection.cofinsAliquota,
            cofinsValor = projection.cofinsValor,
            cofinsCst = projection.cofinsCst,
            descontoValor = projection.descontoValor,
            freteValor = projection.freteValor,
            seguroValor = projection.seguroValor,
            outrasDespesasValor = projection.outrasDespesasValor,
            totalItemTributado = projection.totalItemTributado,
            totRen = projection.totRen,
            totGe2 = projection.totGe2,
            observacao = projection.observacao,
            pedidoCompraCliente = projection.pedidoCompraCliente,
            itemPedidoCompraCliente = projection.itemPedidoCompraCliente,
            nroRe2 = projection.nroRe2,
            flgVal = projection.flgVal,
            flgPac = projection.flgPac,
            flgLib = projection.flgLib,
            codCfo = projection.codCfo,
            codcor = projection.codcor,
            codtam = projection.codtam,
            descricaoNFe = projection.descricaoNFe,
            pesoLiquido = projection.pesoLiquido,
            pesoBruto = projection.pesoBruto,
            referencia = projection.referencia,
            quantidadeFaturada = projection.quantidadeFaturada,
            quantidadeSeparada = projection.quantidadeSeparada,
            custoTotal = projection.custoTotal,
            lucroValor = projection.lucroValor,
            lucroPorcentagem = projection.lucroPorcentagem,
        )

    /** Write path: JPA entity -> domain model. */
    fun toDomain(entity: CustomerOrderJpaEntity): CustomerOrder =
        CustomerOrder(
            id = entity.id,
            externalId = entity.externalId,
            codigoEmpresa = entity.codigoEmpresa,
            codigoCliente = entity.codigoCliente,
            cpfCnpj = entity.cpfCnpj,
            numeroPedido = entity.numeroPedido,
            dataPedido = entity.dataPedido,
            statusPedido = entity.statusPedido,
            totalPedidoComImpostos = entity.totalPedidoComImpostos,
            totalPedidoSemImpostos = entity.totalPedidoSemImpostos,
            totalIpi = entity.totalIpi,
            totalIcms = entity.totalIcms,
            totalPis = entity.totalPis,
            totalCofins = entity.totalCofins,
            totalSubstituicaoTributaria = entity.totalSubstituicaoTributaria,
            totalDescontoIncondicional = entity.totalDescontoIncondicional,
            totalFrete = entity.totalFrete,
            totalSeguro = entity.totalSeguro,
            totalOutrasDespesas = entity.totalOutrasDespesas,
            vendedorExternalId = entity.vendedorExternalId,
            dataEntregaPrevista = entity.dataEntregaPrevista,
            codigoTransportadora = entity.codigoTransportadora,
            pedidoAnterior = entity.pedidoAnterior,
            regimeTributario = entity.regimeTributario,
            nomeRegimeTributario = entity.nomeRegimeTributario,
            codigoPadraoFaturamento = entity.codigoPadraoFaturamento,
            itens = entity.itens.map(::toItemDomain),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    private fun toItemDomain(entity: CustomerOrderItemJpaEntity): CustomerOrderItem =
        CustomerOrderItem(
            codEmp = entity.codEmp,
            dteres = entity.dteres,
            numres = entity.numres,
            produto = entity.produto,
            descricao = entity.descricao,
            quantidade = entity.quantidade,
            valorUnitario = entity.valorUnitario,
            valorTotal = entity.valorTotal,
            seqRe2 = entity.seqRe2,
            codClp = entity.codClp,
            codSt1 = entity.codSt1,
            codUnd = entity.codUnd,
            vluRe2 = entity.vluRe2,
            dscRe2 = entity.dscRe2,
            dsrRe2 = entity.dsrRe2,
            icmsAliquota = entity.icmsAliquota,
            icmsBase = entity.icmsBase,
            icmsValor = entity.icmsValor,
            icmsReducaoBase = entity.icmsReducaoBase,
            icmsSubstituicaoBase = entity.icmsSubstituicaoBase,
            icmsSubstituicaoValor = entity.icmsSubstituicaoValor,
            icmsSubstituicaoAliquota = entity.icmsSubstituicaoAliquota,
            icmsSubstituicaoMargem = entity.icmsSubstituicaoMargem,
            icmsSubstituicaoReducaoBase = entity.icmsSubstituicaoReducaoBase,
            ipiAliquota = entity.ipiAliquota,
            ipiBase = entity.ipiBase,
            ipiValor = entity.ipiValor,
            ipiClassificacao = entity.ipiClassificacao,
            ipiCst = entity.ipiCst,
            pisBase = entity.pisBase,
            pisAliquota = entity.pisAliquota,
            pisValor = entity.pisValor,
            pisCst = entity.pisCst,
            cofinsBase = entity.cofinsBase,
            cofinsAliquota = entity.cofinsAliquota,
            cofinsValor = entity.cofinsValor,
            cofinsCst = entity.cofinsCst,
            descontoValor = entity.descontoValor,
            freteValor = entity.freteValor,
            seguroValor = entity.seguroValor,
            outrasDespesasValor = entity.outrasDespesasValor,
            totalItemTributado = entity.totalItemTributado,
            totRen = entity.totRen,
            totGe2 = entity.totGe2,
            observacao = entity.observacao,
            pedidoCompraCliente = entity.pedidoCompraCliente,
            itemPedidoCompraCliente = entity.itemPedidoCompraCliente,
            nroRe2 = entity.nroRe2,
            flgVal = entity.flgVal,
            flgPac = entity.flgPac,
            flgLib = entity.flgLib,
            codCfo = entity.codCfo,
            codcor = entity.codcor,
            codtam = entity.codtam,
            descricaoNFe = entity.descricaoNFe,
            pesoLiquido = entity.pesoLiquido,
            pesoBruto = entity.pesoBruto,
            referencia = entity.referencia,
            quantidadeFaturada = entity.quantidadeFaturada,
            quantidadeSeparada = entity.quantidadeSeparada,
            custoTotal = entity.custoTotal,
            lucroValor = entity.lucroValor,
            lucroPorcentagem = entity.lucroPorcentagem,
        )

    /**
     * Applies domain state onto a (possibly new) JPA entity, preserving the
     * generated id. When `existing` is provided, its line items are upserted
     * in place keyed by `seqRe2` (the item's legacy/external identity)
     * instead of being cleared and fully recreated: items no longer present
     * are removed (letting orphanRemoval delete their rows), items whose
     * seqRe2 is already known are mutated in place, and only genuinely new
     * seqRe2 values are inserted. This avoids Hibernate briefly inserting a
     * duplicate (customer_order_id, seq_re2) row before deleting the old one
     * when re-ingesting an order — a duplicate that a naive clear()+addAll()
     * of the whole collection would attempt and fail the unique constraint
     * on.
     */
    fun toEntity(domain: CustomerOrder, existing: CustomerOrderJpaEntity?): CustomerOrderJpaEntity {
        val entity = existing ?: CustomerOrderJpaEntity(id = domain.id)
        entity.externalId = domain.externalId
        entity.codigoEmpresa = domain.codigoEmpresa
        entity.codigoCliente = domain.codigoCliente
        entity.cpfCnpj = domain.cpfCnpj
        entity.numeroPedido = domain.numeroPedido
        entity.dataPedido = domain.dataPedido
        entity.statusPedido = domain.statusPedido
        entity.totalPedidoComImpostos = domain.totalPedidoComImpostos
        entity.totalPedidoSemImpostos = domain.totalPedidoSemImpostos
        entity.totalIpi = domain.totalIpi
        entity.totalIcms = domain.totalIcms
        entity.totalPis = domain.totalPis
        entity.totalCofins = domain.totalCofins
        entity.totalSubstituicaoTributaria = domain.totalSubstituicaoTributaria
        entity.totalDescontoIncondicional = domain.totalDescontoIncondicional
        entity.totalFrete = domain.totalFrete
        entity.totalSeguro = domain.totalSeguro
        entity.totalOutrasDespesas = domain.totalOutrasDespesas
        entity.vendedorExternalId = domain.vendedorExternalId
        entity.dataEntregaPrevista = domain.dataEntregaPrevista
        entity.codigoTransportadora = domain.codigoTransportadora
        entity.pedidoAnterior = domain.pedidoAnterior
        entity.regimeTributario = domain.regimeTributario
        entity.nomeRegimeTributario = domain.nomeRegimeTributario
        entity.codigoPadraoFaturamento = domain.codigoPadraoFaturamento
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.updatedAt
        upsertItems(entity, domain.itens)
        return entity
    }

    private fun upsertItems(entity: CustomerOrderJpaEntity, itens: List<CustomerOrderItem>) {
        val existingBySeqRe2 = entity.itens.associateBy { it.seqRe2 }
        val incomingSeqRe2s = itens.map { it.seqRe2 }.toSet()
        entity.itens.removeAll { it.seqRe2 !in incomingSeqRe2s }
        itens.forEach { item ->
            val existingItem = existingBySeqRe2[item.seqRe2]
            if (existingItem != null) {
                applyItemFields(existingItem, item)
            } else {
                entity.itens.add(toItemEntity(item, entity))
            }
        }
    }

    private fun applyItemFields(entity: CustomerOrderItemJpaEntity, item: CustomerOrderItem) {
        entity.codEmp = item.codEmp
        entity.dteres = item.dteres
        entity.numres = item.numres
        entity.produto = item.produto
        entity.descricao = item.descricao
        entity.quantidade = item.quantidade
        entity.valorUnitario = item.valorUnitario
        entity.valorTotal = item.valorTotal
        entity.codClp = item.codClp
        entity.codSt1 = item.codSt1
        entity.codUnd = item.codUnd
        entity.vluRe2 = item.vluRe2
        entity.dscRe2 = item.dscRe2
        entity.dsrRe2 = item.dsrRe2
        entity.icmsAliquota = item.icmsAliquota
        entity.icmsBase = item.icmsBase
        entity.icmsValor = item.icmsValor
        entity.icmsReducaoBase = item.icmsReducaoBase
        entity.icmsSubstituicaoBase = item.icmsSubstituicaoBase
        entity.icmsSubstituicaoValor = item.icmsSubstituicaoValor
        entity.icmsSubstituicaoAliquota = item.icmsSubstituicaoAliquota
        entity.icmsSubstituicaoMargem = item.icmsSubstituicaoMargem
        entity.icmsSubstituicaoReducaoBase = item.icmsSubstituicaoReducaoBase
        entity.ipiAliquota = item.ipiAliquota
        entity.ipiBase = item.ipiBase
        entity.ipiValor = item.ipiValor
        entity.ipiClassificacao = item.ipiClassificacao
        entity.ipiCst = item.ipiCst
        entity.pisBase = item.pisBase
        entity.pisAliquota = item.pisAliquota
        entity.pisValor = item.pisValor
        entity.pisCst = item.pisCst
        entity.cofinsBase = item.cofinsBase
        entity.cofinsAliquota = item.cofinsAliquota
        entity.cofinsValor = item.cofinsValor
        entity.cofinsCst = item.cofinsCst
        entity.descontoValor = item.descontoValor
        entity.freteValor = item.freteValor
        entity.seguroValor = item.seguroValor
        entity.outrasDespesasValor = item.outrasDespesasValor
        entity.totalItemTributado = item.totalItemTributado
        entity.totRen = item.totRen
        entity.totGe2 = item.totGe2
        entity.observacao = item.observacao
        entity.pedidoCompraCliente = item.pedidoCompraCliente
        entity.itemPedidoCompraCliente = item.itemPedidoCompraCliente
        entity.nroRe2 = item.nroRe2
        entity.flgVal = item.flgVal
        entity.flgPac = item.flgPac
        entity.flgLib = item.flgLib
        entity.codCfo = item.codCfo
        entity.codcor = item.codcor
        entity.codtam = item.codtam
        entity.descricaoNFe = item.descricaoNFe
        entity.pesoLiquido = item.pesoLiquido
        entity.pesoBruto = item.pesoBruto
        entity.referencia = item.referencia
        entity.quantidadeFaturada = item.quantidadeFaturada
        entity.quantidadeSeparada = item.quantidadeSeparada
        entity.custoTotal = item.custoTotal
        entity.lucroValor = item.lucroValor
        entity.lucroPorcentagem = item.lucroPorcentagem
    }

    private fun toItemEntity(item: CustomerOrderItem, parent: CustomerOrderJpaEntity): CustomerOrderItemJpaEntity =
        CustomerOrderItemJpaEntity(
            customerOrder = parent,
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
}
