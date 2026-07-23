package br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.repository

import br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.model.CustomerAddressJpaEntity
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.projection.CustomerAddressDetailProjection
import br.com.vertice.emerion_dashboard.infrastructure.persistence.customeraddress.projection.CustomerAddressHeaderProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

/**
 * Read-only side of customer address persistence: native SQL mapped
 * straight to projections (mirrors emerion-load-service's
 * repository/<x>QueryRepository native-query + projection pattern), kept
 * separate from `CustomerAddressSpringDataRepository` (JPA entity,
 * writes/upserts only). Headers are paginated directly (Postgres supports
 * `Pageable`-driven native queries); the matching detail rows for a page of
 * headers are then fetched in one follow-up query and grouped back onto
 * each header in `CustomerAddressRepositoryAdapter`, exactly like
 * emerion-load-service's `CustomerOrderQueryRepository` does for order line
 * items.
 */
interface CustomerAddressQueryRepository : Repository<CustomerAddressJpaEntity, Long> {

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                external_id AS externalId,
                cnpj_empresa AS cnpjEmpresa,
                cpf_cnpj AS cpfCnpj,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM customer_address
            WHERE id = :id
        """,
    )
    fun findHeaderProjectionById(@Param("id") id: Long): CustomerAddressHeaderProjection?

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                id,
                external_id AS externalId,
                cnpj_empresa AS cnpjEmpresa,
                cpf_cnpj AS cpfCnpj,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM customer_address
            WHERE (:cpfCnpjContains IS NULL OR LOWER(cpf_cnpj) LIKE LOWER(CONCAT('%', CAST(:cpfCnpjContains AS text), '%')))
              AND (:cnpjEmpresa IS NULL OR cnpj_empresa = :cnpjEmpresa)
        """,
        countQuery = """
            SELECT count(*)
            FROM customer_address
            WHERE (:cpfCnpjContains IS NULL OR LOWER(cpf_cnpj) LIKE LOWER(CONCAT('%', CAST(:cpfCnpjContains AS text), '%')))
              AND (:cnpjEmpresa IS NULL OR cnpj_empresa = :cnpjEmpresa)
        """,
    )
    fun searchHeaders(
        @Param("cpfCnpjContains") cpfCnpjContains: String?,
        @Param("cnpjEmpresa") cnpjEmpresa: String?,
        pageable: Pageable,
    ): Page<CustomerAddressHeaderProjection>

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                customer_address_id AS customerAddressId,
                tipo,
                cep,
                endereco,
                numero,
                referencia,
                bairro,
                cidade,
                uf,
                telefone,
                telefone_contato AS telefoneContato,
                complemento,
                fax
            FROM customer_address_detail
            WHERE customer_address_id IN (:customerAddressIds)
            ORDER BY customer_address_id, tipo
        """,
    )
    fun findDetailsByCustomerAddressIds(
        @Param("customerAddressIds") customerAddressIds: List<Long>,
    ): List<CustomerAddressDetailProjection>
}
