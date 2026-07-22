package br.com.vertice.emerion_dashboard.infrastructure.rest.customeraddress.mapper

import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestCustomerAddressCommand
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestCustomerAddressDetailCommand
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerAddressDetailIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerAddressIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerAddressIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult

/** Maps between the generated OpenAPI DTOs and the application layer's use-case commands/results. */
object CustomerAddressIngestionRestMapper {

    fun toCommand(dto: CustomerAddressIngestionBatch): IngestBatchCommand =
        IngestBatchCommand(
            batchId = dto.batchId,
            items = dto.items.map(::toItemCommand),
        )

    fun toItemCommand(dto: CustomerAddressIngestionItem): IngestCustomerAddressCommand =
        IngestCustomerAddressCommand(
            externalId = dto.externalId,
            cnpjEmpresa = dto.cnpjEmpresa,
            cpfCnpj = dto.cpfCnpj,
            enderecos = dto.enderecos.map(::toDetailCommand),
        )

    private fun toDetailCommand(dto: CustomerAddressDetailIngestionItem): IngestCustomerAddressDetailCommand =
        IngestCustomerAddressDetailCommand(
            tipo = dto.tipo,
            cep = dto.cep,
            endereco = dto.endereco,
            numero = dto.numero,
            referencia = dto.referencia,
            bairro = dto.bairro,
            cidade = dto.cidade,
            uf = dto.uf,
            telefone = dto.telefone,
            telefoneContato = dto.telefoneContato,
            complemento = dto.complemento,
            fax = dto.fax,
        )

    fun toResponse(result: IngestBatchResult): IngestionResult =
        IngestionResult(
            batchId = result.batchId,
            totalReceived = result.totalReceived,
            totalSucceeded = result.totalSucceeded,
            totalFailed = result.totalFailed,
            results = result.results.map(::toItemResponse),
        )

    fun toItemResponse(result: IngestItemResult): IngestionItemResult =
        IngestionItemResult(
            externalId = result.externalId,
            outcome = IngestionItemResult.Outcome.valueOf(result.outcome.name),
            errorMessage = result.errorMessage,
        )
}
