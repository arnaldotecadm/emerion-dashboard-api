package br.com.vertice.emerion_dashboard.infrastructure.persistence.icms.mapper

import br.com.vertice.emerion_dashboard.domain.icms.model.Icms
import br.com.vertice.emerion_dashboard.infrastructure.persistence.icms.model.IcmsJpaEntity

object IcmsPersistenceMapper {
    fun toDomain(entity: IcmsJpaEntity) = Icms(
        id = entity.id,
        cnpjEmpresa = entity.cnpjEmpresa,
        codigoIcms = entity.codigoIcms,
        tipoIcms = entity.tipoIcms,
        nomeIcms = entity.nomeIcms,
        ufEmitente = entity.ufEmitente,
        codigoRegimeTributario = entity.codigoRegimeTributario,
        aliquotaIcms = entity.aliquotaIcms,
        percentualReducaoValorImposto = entity.percentualReducaoValorImposto,
        percentualBaseCalculoIcms = entity.percentualBaseCalculoIcms,
        situacaoTributariaIcms = entity.situacaoTributariaIcms,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
    )

    fun toEntity(domain: Icms, existing: IcmsJpaEntity?) = IcmsJpaEntity(
        id = existing?.id ?: domain.id,
        cnpjEmpresa = domain.cnpjEmpresa,
        codigoIcms = domain.codigoIcms,
        tipoIcms = domain.tipoIcms,
        nomeIcms = domain.nomeIcms,
        ufEmitente = domain.ufEmitente,
        codigoRegimeTributario = domain.codigoRegimeTributario,
        aliquotaIcms = domain.aliquotaIcms,
        percentualReducaoValorImposto = domain.percentualReducaoValorImposto,
        percentualBaseCalculoIcms = domain.percentualBaseCalculoIcms,
        situacaoTributariaIcms = domain.situacaoTributariaIcms,
        createdAt = domain.createdAt,
        updatedAt = domain.updatedAt,
    )
}
