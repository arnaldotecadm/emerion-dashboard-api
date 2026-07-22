package br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model

/** Input command for a single customer's full address set inside an ingestion batch. */
data class IngestCustomerAddressCommand(
    val externalId: String,
    val cnpjEmpresa: String?,
    val cpfCnpj: String?,
    val enderecos: List<IngestCustomerAddressDetailCommand>,
)
