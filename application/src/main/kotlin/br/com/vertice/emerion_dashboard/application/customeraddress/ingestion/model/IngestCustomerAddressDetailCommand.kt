package br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model

/** Input command for one address entry inside a customer address ingestion item. */
data class IngestCustomerAddressDetailCommand(
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
