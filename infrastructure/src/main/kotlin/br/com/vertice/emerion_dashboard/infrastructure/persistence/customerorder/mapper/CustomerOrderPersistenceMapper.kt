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
            codCli = header.codCli,
            cnpjEmpresa = header.cnpjEmpresa,
            nronfe = header.nronfe,
            dteres = header.dteres,
            sitres = header.sitres,
            totger = header.totger,
            totres = header.totres,
            totipi = header.totipi,
            totsub = header.totsub,
            totdescinc = header.totdescinc,
            itens = items.map(::toItemDomain),
            createdAt = header.createdAt,
            updatedAt = header.updatedAt,
        )

    private fun toItemDomain(projection: CustomerOrderItemProjection): CustomerOrderItem =
        CustomerOrderItem(
            produto = projection.produto,
            descricao = projection.descricao,
            quantidade = projection.quantidade,
            valorUnitario = projection.valorUnitario,
            valorTotal = projection.valorTotal,
        )

    /** Write path: JPA entity -> domain model. */
    fun toDomain(entity: CustomerOrderJpaEntity): CustomerOrder =
        CustomerOrder(
            id = entity.id,
            externalId = entity.externalId,
            codCli = entity.codCli,
            cnpjEmpresa = entity.cnpjEmpresa,
            nronfe = entity.nronfe,
            dteres = entity.dteres,
            sitres = entity.sitres,
            totger = entity.totger,
            totres = entity.totres,
            totipi = entity.totipi,
            totsub = entity.totsub,
            totdescinc = entity.totdescinc,
            itens = entity.itens.map(::toItemDomain),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    private fun toItemDomain(entity: CustomerOrderItemJpaEntity): CustomerOrderItem =
        CustomerOrderItem(
            produto = entity.produto,
            descricao = entity.descricao,
            quantidade = entity.quantidade,
            valorUnitario = entity.valorUnitario,
            valorTotal = entity.valorTotal,
        )

    /**
     * Applies domain state onto a (possibly new) JPA entity, preserving the
     * generated id. When `existing` is provided, its managed `itens`
     * collection is cleared and repopulated in place so Hibernate's
     * orphanRemoval deletes the previous rows instead of leaving them
     * orphaned in the database.
     */
    fun toEntity(domain: CustomerOrder, existing: CustomerOrderJpaEntity?): CustomerOrderJpaEntity {
        val entity = existing ?: CustomerOrderJpaEntity(id = domain.id)
        entity.externalId = domain.externalId
        entity.codCli = domain.codCli
        entity.cnpjEmpresa = domain.cnpjEmpresa
        entity.nronfe = domain.nronfe
        entity.dteres = domain.dteres
        entity.sitres = domain.sitres
        entity.totger = domain.totger
        entity.totres = domain.totres
        entity.totipi = domain.totipi
        entity.totsub = domain.totsub
        entity.totdescinc = domain.totdescinc
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.updatedAt
        entity.itens.clear()
        entity.itens.addAll(domain.itens.map { toItemEntity(it, entity) })
        return entity
    }

    private fun toItemEntity(item: CustomerOrderItem, parent: CustomerOrderJpaEntity): CustomerOrderItemJpaEntity =
        CustomerOrderItemJpaEntity(
            customerOrder = parent,
            produto = item.produto,
            descricao = item.descricao,
            quantidade = item.quantidade,
            valorUnitario = item.valorUnitario,
            valorTotal = item.valorTotal,
        )
}
