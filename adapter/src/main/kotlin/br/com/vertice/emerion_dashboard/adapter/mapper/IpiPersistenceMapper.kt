package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.entity.IpiJpaEntity
import br.com.vertice.emerion_dashboard.domain.ipi.Ipi

/** Maps between the domain model and the JPA entity. Kept out of the entity/domain classes on purpose. */
object IpiPersistenceMapper {

    fun IpiJpaEntity.toDomain(): Ipi = Ipi(
        id = id,
        cnpjEmpresa = cnpjEmpresa,
        codigoIpi = codigoIpi,
        flgAtivo = flgAtivo,
        tipoIpi = tipoIpi,
        nomeIpi = nomeIpi,
        ncmIpi = ncmIpi,
        codigoEnquadramentoLegal = codigoEnquadramentoLegal,
        cstIpi = cstIpi,
        descricaoSituacaoTributariaIpi = descricaoSituacaoTributariaIpi,
        aliquotaIpi = aliquotaIpi,
        percentualBaseCalculoIpi = percentualBaseCalculoIpi,
        flgSineif20 = flgSineif20,
        codigoTextoFiscal = codigoTextoFiscal,
        cstPis = cstPis,
        descricaoSituacaoTributariaPis = descricaoSituacaoTributariaPis,
        aliquotaPis = aliquotaPis,
        incluiDescontoSuframaPis = incluiDescontoSuframaPis,
        cstCofins = cstCofins,
        descricaoSituacaoTributariaCofins = descricaoSituacaoTributariaCofins,
        aliquotaCofins = aliquotaCofins,
        incluiDescontoSuframaCofins = incluiDescontoSuframaCofins,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    /** Applies domain state onto a (possibly new) JPA entity, preserving the generated id. */
    fun Ipi.toEntity(existing: IpiJpaEntity?): IpiJpaEntity = IpiJpaEntity(
        id = existing?.id ?: id,
        cnpjEmpresa = cnpjEmpresa,
        codigoIpi = codigoIpi,
        flgAtivo = flgAtivo,
        tipoIpi = tipoIpi,
        nomeIpi = nomeIpi,
        ncmIpi = ncmIpi,
        codigoEnquadramentoLegal = codigoEnquadramentoLegal,
        cstIpi = cstIpi,
        descricaoSituacaoTributariaIpi = descricaoSituacaoTributariaIpi,
        aliquotaIpi = aliquotaIpi,
        percentualBaseCalculoIpi = percentualBaseCalculoIpi,
        flgSineif20 = flgSineif20,
        codigoTextoFiscal = codigoTextoFiscal,
        cstPis = cstPis,
        descricaoSituacaoTributariaPis = descricaoSituacaoTributariaPis,
        aliquotaPis = aliquotaPis,
        incluiDescontoSuframaPis = incluiDescontoSuframaPis,
        cstCofins = cstCofins,
        descricaoSituacaoTributariaCofins = descricaoSituacaoTributariaCofins,
        aliquotaCofins = aliquotaCofins,
        incluiDescontoSuframaCofins = incluiDescontoSuframaCofins,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
