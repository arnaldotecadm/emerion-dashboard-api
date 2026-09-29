package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.entity.FincreParcelaJpaEntity
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.entity.FincreTituloReceberJpaEntity
import br.com.vertice.emerion_dashboard.domain.fincre.Fincre
import br.com.vertice.emerion_dashboard.domain.fincre.FincreParcela

/** Maps between the domain model and the JPA entity. Kept out of the entity/domain classes on purpose. */
object FincrePersistenceMapper {

    fun FincreTituloReceberJpaEntity.toDomain(): Fincre =
        Fincre(
            id = id,
            cnpjEmpresa = cnpjEmpresa,
            codigoEmpresa = codigoEmpresa,
            dataEmissao = dataEmissao,
            documento = documento,
            codigoCondicaoRecebimento = codigoCondicaoRecebimento,
            nomeCondicaoRecebimento = nomeCondicaoRecebimento,
            nomeEmpresa = nomeEmpresa,
            codigoComissao = codigoComissao,
            percentualComissao = percentualComissao,
            codigoCliente = codigoCliente,
            nomeCliente = nomeCliente,
            codigoVendedor = codigoVendedor,
            nomeVendedor = nomeVendedor,
            codigoTipoDocumento = codigoTipoDocumento,
            nomeTipoDocumento = nomeTipoDocumento,
            parcelas = parcelas.map { it.toDomain() },
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    private fun FincreParcelaJpaEntity.toDomain(): FincreParcela =
        FincreParcela(
            numeroParcela = numeroParcela,
            flagIncobravel = flagIncobravel,
            dataIncobravel = dataIncobravel,
            dataVencimento = dataVencimento,
            prazoEmDias = prazoEmDias,
            valorParcela = valorParcela,
            numeroBancario = numeroBancario,
            codigoBanco = codigoBanco,
            nomeBanco = nomeBanco,
            observacoes = observacoes,
            flagCartaAnuencia = flagCartaAnuencia,
            dataCartaAnuencia = dataCartaAnuencia,
            flagPago = flagPago,
        )

    /** Applies domain state onto a (possibly new) JPA entity, preserving the generated id and rebuilding child rows. */
    fun Fincre.toEntity(existing: FincreTituloReceberJpaEntity?): FincreTituloReceberJpaEntity {
        val entity = existing ?: FincreTituloReceberJpaEntity(id = id)
        entity.cnpjEmpresa = cnpjEmpresa
        entity.codigoEmpresa = codigoEmpresa
        entity.dataEmissao = dataEmissao
        entity.documento = documento
        entity.codigoCondicaoRecebimento = codigoCondicaoRecebimento
        entity.nomeCondicaoRecebimento = nomeCondicaoRecebimento
        entity.nomeEmpresa = nomeEmpresa
        entity.codigoComissao = codigoComissao
        entity.percentualComissao = percentualComissao
        entity.codigoCliente = codigoCliente
        entity.nomeCliente = nomeCliente
        entity.codigoVendedor = codigoVendedor
        entity.nomeVendedor = nomeVendedor
        entity.codigoTipoDocumento = codigoTipoDocumento
        entity.nomeTipoDocumento = nomeTipoDocumento
        entity.createdAt = createdAt
        entity.updatedAt = updatedAt
        entity.parcelas.clear()
        entity.parcelas.addAll(parcelas.map { it.toEntity(entity) })
        return entity
    }

    private fun FincreParcela.toEntity(parent: FincreTituloReceberJpaEntity) =
        FincreParcelaJpaEntity(
            tituloReceber = parent,
            numeroParcela = numeroParcela,
            flagIncobravel = flagIncobravel,
            dataIncobravel = dataIncobravel,
            dataVencimento = dataVencimento,
            prazoEmDias = prazoEmDias,
            valorParcela = valorParcela,
            numeroBancario = numeroBancario,
            codigoBanco = codigoBanco,
            nomeBanco = nomeBanco,
            observacoes = observacoes,
            flagCartaAnuencia = flagCartaAnuencia,
            dataCartaAnuencia = dataCartaAnuencia,
            flagPago = flagPago,
        )
}
