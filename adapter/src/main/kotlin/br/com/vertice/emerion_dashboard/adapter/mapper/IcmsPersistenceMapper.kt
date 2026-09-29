package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.entity.IcmsJpaEntity
import br.com.vertice.emerion_dashboard.domain.icms.Icms

/** Maps between the domain model and the JPA entity. Kept out of the entity/domain classes on purpose. */
object IcmsPersistenceMapper {

    fun IcmsJpaEntity.toDomain(): Icms = Icms(
        id = id,
        cnpjEmpresa = cnpjEmpresa,
        codigoIcms = codigoIcms,
        tipoIcms = tipoIcms,
        nomeIcms = nomeIcms,
        ufEmitente = ufEmitente,
        codigoRegimeTributario = codigoRegimeTributario,
        aliquotaIcms = aliquotaIcms,
        percentualReducaoValorImposto = percentualReducaoValorImposto,
        percentualBaseCalculoIcms = percentualBaseCalculoIcms,
        situacaoTributariaIcms = situacaoTributariaIcms,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    /** Applies domain state onto a (possibly new) JPA entity, preserving the generated id. */
    fun Icms.toEntity(existing: IcmsJpaEntity?): IcmsJpaEntity = IcmsJpaEntity(
        id = existing?.id ?: id,
        cnpjEmpresa = cnpjEmpresa,
        codigoIcms = codigoIcms,
        tipoIcms = tipoIcms,
        nomeIcms = nomeIcms,
        ufEmitente = ufEmitente,
        codigoRegimeTributario = codigoRegimeTributario,
        aliquotaIcms = aliquotaIcms,
        percentualReducaoValorImposto = percentualReducaoValorImposto,
        percentualBaseCalculoIcms = percentualBaseCalculoIcms,
        situacaoTributariaIcms = situacaoTributariaIcms,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
