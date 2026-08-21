package br.com.vertice.emerion_dashboard.infrastructure.persistence.fincre.mapper

import br.com.vertice.emerion_dashboard.domain.fincre.model.Fincre
import br.com.vertice.emerion_dashboard.domain.fincre.model.FincreParcela
import br.com.vertice.emerion_dashboard.infrastructure.persistence.fincre.model.FincreParcelaJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.fincre.model.FincreTituloReceberJpaEntity

object FincrePersistenceMapper {
    fun toDomain(entity: FincreTituloReceberJpaEntity): Fincre =
        Fincre(
            id = entity.id,
            cnpjEmpresa = entity.cnpjEmpresa,
            codigoEmpresa = entity.codigoEmpresa,
            dataEmissao = entity.dataEmissao,
            documento = entity.documento,
            codigoCondicaoRecebimento = entity.codigoCondicaoRecebimento,
            nomeCondicaoRecebimento = entity.nomeCondicaoRecebimento,
            nomeEmpresa = entity.nomeEmpresa,
            codigoComissao = entity.codigoComissao,
            percentualComissao = entity.percentualComissao,
            codigoCliente = entity.codigoCliente,
            nomeCliente = entity.nomeCliente,
            codigoVendedor = entity.codigoVendedor,
            nomeVendedor = entity.nomeVendedor,
            codigoTipoDocumento = entity.codigoTipoDocumento,
            nomeTipoDocumento = entity.nomeTipoDocumento,
            parcelas = entity.parcelas.map(::toParcelaDomain),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    private fun toParcelaDomain(entity: FincreParcelaJpaEntity): FincreParcela =
        FincreParcela(
            numeroParcela = entity.numeroParcela,
            flagIncobravel = entity.flagIncobravel,
            dataIncobravel = entity.dataIncobravel,
            dataVencimento = entity.dataVencimento,
            prazoEmDias = entity.prazoEmDias,
            valorParcela = entity.valorParcela,
            numeroBancario = entity.numeroBancario,
            codigoBanco = entity.codigoBanco,
            nomeBanco = entity.nomeBanco,
            observacoes = entity.observacoes,
            flagCartaAnuencia = entity.flagCartaAnuencia,
            dataCartaAnuencia = entity.dataCartaAnuencia,
            flagPago = entity.flagPago,
        )

    fun toEntity(domain: Fincre, existing: FincreTituloReceberJpaEntity?): FincreTituloReceberJpaEntity {
        val entity = existing ?: FincreTituloReceberJpaEntity(id = domain.id)
        entity.cnpjEmpresa = domain.cnpjEmpresa
        entity.codigoEmpresa = domain.codigoEmpresa
        entity.dataEmissao = domain.dataEmissao
        entity.documento = domain.documento
        entity.codigoCondicaoRecebimento = domain.codigoCondicaoRecebimento
        entity.nomeCondicaoRecebimento = domain.nomeCondicaoRecebimento
        entity.nomeEmpresa = domain.nomeEmpresa
        entity.codigoComissao = domain.codigoComissao
        entity.percentualComissao = domain.percentualComissao
        entity.codigoCliente = domain.codigoCliente
        entity.nomeCliente = domain.nomeCliente
        entity.codigoVendedor = domain.codigoVendedor
        entity.nomeVendedor = domain.nomeVendedor
        entity.codigoTipoDocumento = domain.codigoTipoDocumento
        entity.nomeTipoDocumento = domain.nomeTipoDocumento
        entity.createdAt = domain.createdAt
        entity.updatedAt = domain.updatedAt
        entity.parcelas.clear()
        entity.parcelas.addAll(domain.parcelas.map { toParcelaEntity(it, entity) })
        return entity
    }

    private fun toParcelaEntity(parcela: FincreParcela, parent: FincreTituloReceberJpaEntity) =
        FincreParcelaJpaEntity(
            tituloReceber = parent,
            numeroParcela = parcela.numeroParcela,
            flagIncobravel = parcela.flagIncobravel,
            dataIncobravel = parcela.dataIncobravel,
            dataVencimento = parcela.dataVencimento,
            prazoEmDias = parcela.prazoEmDias,
            valorParcela = parcela.valorParcela,
            numeroBancario = parcela.numeroBancario,
            codigoBanco = parcela.codigoBanco,
            nomeBanco = parcela.nomeBanco,
            observacoes = parcela.observacoes,
            flagCartaAnuencia = parcela.flagCartaAnuencia,
            dataCartaAnuencia = parcela.dataCartaAnuencia,
            flagPago = parcela.flagPago,
        )
}
