package br.com.vertice.emerion_dashboard.infrastructure.persistence.ipi.mapper

import br.com.vertice.emerion_dashboard.domain.ipi.model.Ipi
import br.com.vertice.emerion_dashboard.infrastructure.persistence.ipi.model.IpiJpaEntity

object IpiPersistenceMapper {
    fun toDomain(entity: IpiJpaEntity) = Ipi(
        id = entity.id,
        cnpjEmpresa = entity.cnpjEmpresa,
        codigoIpi = entity.codigoIpi,
        flgAtivo = entity.flgAtivo,
        tipoIpi = entity.tipoIpi,
        nomeIpi = entity.nomeIpi,
        ncmIpi = entity.ncmIpi,
        codigoEnquadramentoLegal = entity.codigoEnquadramentoLegal,
        cstIpi = entity.cstIpi,
        descricaoSituacaoTributariaIpi = entity.descricaoSituacaoTributariaIpi,
        aliquotaIpi = entity.aliquotaIpi,
        percentualBaseCalculoIpi = entity.percentualBaseCalculoIpi,
        flgSineif20 = entity.flgSineif20,
        codigoTextoFiscal = entity.codigoTextoFiscal,
        cstPis = entity.cstPis,
        descricaoSituacaoTributariaPis = entity.descricaoSituacaoTributariaPis,
        aliquotaPis = entity.aliquotaPis,
        incluiDescontoSuframaPis = entity.incluiDescontoSuframaPis,
        cstCofins = entity.cstCofins,
        descricaoSituacaoTributariaCofins = entity.descricaoSituacaoTributariaCofins,
        aliquotaCofins = entity.aliquotaCofins,
        incluiDescontoSuframaCofins = entity.incluiDescontoSuframaCofins,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
    )

    fun toEntity(domain: Ipi, existing: IpiJpaEntity?) = IpiJpaEntity(
        id = existing?.id ?: domain.id,
        cnpjEmpresa = domain.cnpjEmpresa,
        codigoIpi = domain.codigoIpi,
        flgAtivo = domain.flgAtivo,
        tipoIpi = domain.tipoIpi,
        nomeIpi = domain.nomeIpi,
        ncmIpi = domain.ncmIpi,
        codigoEnquadramentoLegal = domain.codigoEnquadramentoLegal,
        cstIpi = domain.cstIpi,
        descricaoSituacaoTributariaIpi = domain.descricaoSituacaoTributariaIpi,
        aliquotaIpi = domain.aliquotaIpi,
        percentualBaseCalculoIpi = domain.percentualBaseCalculoIpi,
        flgSineif20 = domain.flgSineif20,
        codigoTextoFiscal = domain.codigoTextoFiscal,
        cstPis = domain.cstPis,
        descricaoSituacaoTributariaPis = domain.descricaoSituacaoTributariaPis,
        aliquotaPis = domain.aliquotaPis,
        incluiDescontoSuframaPis = domain.incluiDescontoSuframaPis,
        cstCofins = domain.cstCofins,
        descricaoSituacaoTributariaCofins = domain.descricaoSituacaoTributariaCofins,
        aliquotaCofins = domain.aliquotaCofins,
        incluiDescontoSuframaCofins = domain.incluiDescontoSuframaCofins,
        createdAt = domain.createdAt,
        updatedAt = domain.updatedAt,
    )
}
