package br.com.vertice.emerion_dashboard.domain.customeraddress.model

/**
 * One address entry (billing/shipping/etc.) belonging to a CustomerAddress.
 * A value object with no id of its own — it is always replaced as a whole
 * list on re-ingestion (see CustomerAddress.mergeFromIngestion), keyed by
 * `tipo` within its parent.
 */
data class CustomerAddressDetail(
    val tipo: String,
    val cep: String?,
    val endereco: String?,
    val numero: String?,
    val referencia: String?,
    val bairro: String?,
    val cidade: String?,
    val uf: String?,
    val telefone: String?,
    val telefoneContato: String?,
    val complemento: String?,
    val fax: String?,
)
