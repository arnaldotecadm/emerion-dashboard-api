package br.com.vertice.emerion_dashboard.infrastructure.persistence.product.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.product.model.ProductJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.product.projection.ProductProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

/**
 * Read-only side of product persistence: native SQL mapped straight to
 * `ProductProjection` (mirrors emerion-load-service's
 * repository/<x>QueryRepository native-query + projection pattern), kept
 * separate from `ProductSpringDataRepository` (JPA entity, writes/upserts
 * only).
 */
interface ProductQueryRepository : Repository<ProductJpaEntity, Long> {

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                external_id AS externalId,
                cnpj_empresa AS cnpjEmpresa,
                nome,
                descricao_reduzida AS descricaoReduzida,
                referencia_interna AS referenciaInterna,
                ncm,
                cest,
                origem_produto AS origemProduto,
                categoria,
                tipo,
                marca,
                unidade,
                unidade_entrada AS unidadeEntrada,
                unidade_saida AS unidadeSaida,
                peso_liquido AS pesoLiquido,
                peso_bruto AS pesoBruto,
                descontinuado,
                codigo_barras AS codigoBarras,
                codigo_barras_proprio AS codigoBarrasProprio,
                preco,
                preco2,
                preco3,
                preco4,
                preco5,
                desconto_padrao AS descontoPadrao,
                estoque_disponivel AS estoqueDisponivel,
                estoque_minimo AS estoqueMinimo,
                estoque_maximo AS estoqueMaximo,
                estoque_reservado AS estoqueReservado,
                estoque_adquirido AS estoqueAdquirido,
                estoque_atual AS estoqueAtual,
                estoque_rma AS estoqueRma,
                similar_product AS similarProduct,
                quantidade_volumes AS quantidadeVolumes,
                quantidade_embalagem AS quantidadeEmbalagem,
                localizacao,
                cubagem,
                codigo_barras_embalagem AS codigoBarrasEmbalagem,
                ibs_c_class_trib AS ibsCClassTrib,
                ibs_cst AS ibsCst,
                fcp_entrada AS fcpEntrada,
                fcp_saida AS fcpSaida,
                ipi_saida AS ipiSaida,
                ipi_entrada AS ipiEntrada,
                icm_saida AS icmSaida,
                icm_entrada AS icmEntrada,
                icm_st_saida AS icmStSaida,
                icm_st_entrada AS icmStEntrada,
                observacao,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM product
            WHERE id = :id
        """,
    )
    fun findProjectionById(@Param("id") id: Long): ProductProjection?

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                external_id AS externalId,
                cnpj_empresa AS cnpjEmpresa,
                nome,
                descricao_reduzida AS descricaoReduzida,
                referencia_interna AS referenciaInterna,
                ncm,
                cest,
                origem_produto AS origemProduto,
                categoria,
                tipo,
                marca,
                unidade,
                unidade_entrada AS unidadeEntrada,
                unidade_saida AS unidadeSaida,
                peso_liquido AS pesoLiquido,
                peso_bruto AS pesoBruto,
                descontinuado,
                codigo_barras AS codigoBarras,
                codigo_barras_proprio AS codigoBarrasProprio,
                preco,
                preco2,
                preco3,
                preco4,
                preco5,
                desconto_padrao AS descontoPadrao,
                estoque_disponivel AS estoqueDisponivel,
                estoque_minimo AS estoqueMinimo,
                estoque_maximo AS estoqueMaximo,
                estoque_reservado AS estoqueReservado,
                estoque_adquirido AS estoqueAdquirido,
                estoque_atual AS estoqueAtual,
                estoque_rma AS estoqueRma,
                similar_product AS similarProduct,
                quantidade_volumes AS quantidadeVolumes,
                quantidade_embalagem AS quantidadeEmbalagem,
                localizacao,
                cubagem,
                codigo_barras_embalagem AS codigoBarrasEmbalagem,
                ibs_c_class_trib AS ibsCClassTrib,
                ibs_cst AS ibsCst,
                fcp_entrada AS fcpEntrada,
                fcp_saida AS fcpSaida,
                ipi_saida AS ipiSaida,
                ipi_entrada AS ipiEntrada,
                icm_saida AS icmSaida,
                icm_entrada AS icmEntrada,
                icm_st_saida AS icmStSaida,
                icm_st_entrada AS icmStEntrada,
                observacao,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM product
            WHERE (:nomeContains IS NULL OR LOWER(nome) LIKE LOWER(CONCAT('%', CAST(:nomeContains AS text), '%')))
              AND (:cnpjEmpresa IS NULL OR cnpj_empresa = :cnpjEmpresa)
        """,
        countQuery = """
            SELECT count(*)
            FROM product
            WHERE (:nomeContains IS NULL OR LOWER(nome) LIKE LOWER(CONCAT('%', CAST(:nomeContains AS text), '%')))
              AND (:cnpjEmpresa IS NULL OR cnpj_empresa = :cnpjEmpresa)
        """,
    )
    fun search(
        @Param("nomeContains") nomeContains: String?,
        @Param("cnpjEmpresa") cnpjEmpresa: String?,
        pageable: Pageable,
    ): Page<ProductProjection>
}
