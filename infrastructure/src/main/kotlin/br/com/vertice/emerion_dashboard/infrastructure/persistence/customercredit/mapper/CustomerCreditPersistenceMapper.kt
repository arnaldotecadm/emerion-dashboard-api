package br.com.vertice.emerion_dashboard.infrastructure.persistence.customercredit.mapper

import br.com.vertice.emerion_dashboard.domain.customercredit.model.CustomerCredit
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customercredit.model.CustomerCreditJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customercredit.projection.CustomerCreditProjection

/** Maps between the domain model and the JPA entity/read projection. Kept out of the entity/domain classes on purpose. */
object CustomerCreditPersistenceMapper {

    /** Read path: native-query projection (see `CustomerCreditQueryRepository`) -> domain model. */
    fun toDomain(projection: CustomerCreditProjection): CustomerCredit =
        CustomerCredit(
            id = projection.id,
            customerExternalId = projection.customerExternalId,
            sequencia = projection.sequencia,
            data = projection.data,
            dataPedido = projection.dataPedido,
            valorUtilizado = projection.valorUtilizado,
            valorTotal = projection.valorTotal,
            saldo = projection.saldo,
            situacao = projection.situacao,
            tipo = projection.tipo,
            createdAt = projection.createdAt,
            updatedAt = projection.updatedAt,
        )

    /** Write path: JPA entity -> domain model. */
    fun toDomain(entity: CustomerCreditJpaEntity): CustomerCredit =
        CustomerCredit(
            id = entity.id,
            customerExternalId = entity.customerExternalId,
            sequencia = entity.sequencia,
            data = entity.data,
            dataPedido = entity.dataPedido,
            valorUtilizado = entity.valorUtilizado,
            valorTotal = entity.valorTotal,
            saldo = entity.saldo,
            situacao = entity.situacao,
            tipo = entity.tipo,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    /** Applies domain state onto a (possibly new) JPA entity, preserving the generated id. */
    fun toEntity(domain: CustomerCredit, existing: CustomerCreditJpaEntity?): CustomerCreditJpaEntity =
        CustomerCreditJpaEntity(
            id = existing?.id ?: domain.id,
            customerExternalId = domain.customerExternalId,
            sequencia = domain.sequencia,
            data = domain.data,
            dataPedido = domain.dataPedido,
            valorUtilizado = domain.valorUtilizado,
            valorTotal = domain.valorTotal,
            saldo = domain.saldo,
            situacao = domain.situacao,
            tipo = domain.tipo,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
        )
}
