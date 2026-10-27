package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestCustomerAddressCommand
import br.com.vertice.emerion_dashboard.application.customeraddress.ingestion.model.IngestCustomerAddressDetailCommand
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerAddressDetailIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerAddressIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerAddressIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult

/** Maps between the generated ingestion DTOs and the application command/result models. */
object CustomerAddressIngestionRestMapper {

    fun CustomerAddressIngestionBatch.toCommand(): IngestBatchCommand =
        IngestBatchCommand(
            batchId = batchId,
            items = items.map { it.toItemCommand() },
        )

    fun CustomerAddressIngestionItem.toItemCommand(): IngestCustomerAddressCommand =
        IngestCustomerAddressCommand(
            externalId = externalId,
            cnpjEmpresa = cnpjEmpresa,
            cpfCnpj = cpfCnpj,
            enderecos = enderecos.map { it.toDetailCommand() },
        )

    private fun CustomerAddressDetailIngestionItem.toDetailCommand(): IngestCustomerAddressDetailCommand =
        IngestCustomerAddressDetailCommand(
            tipo = tipo,
            cep = cep,
            endereco = endereco,
            numero = numero,
            referencia = referencia,
            bairro = bairro,
            cidade = cidade,
            uf = uf,
            telefone = telefone,
            telefoneContato = telefoneContato,
            complemento = complemento,
            fax = fax,
            tipoEndereco = tipoEndereco,
            dddTelefone = dddTelefone,
            dddFax = dddFax,
            dddCelular = dddCelular,
            celular = celular,
        )

    fun IngestBatchResult.toResponse(): IngestionResult =
        IngestionResult(
            batchId = batchId,
            totalReceived = totalReceived,
            totalSucceeded = totalSucceeded,
            totalFailed = totalFailed,
            results = results.map { it.toItemResponse() },
        )

    fun IngestItemResult.toItemResponse(): IngestionItemResult =
        IngestionItemResult(
            externalId = externalId,
            outcome = IngestionItemResult.Outcome.valueOf(outcome.name),
            errorMessage = errorMessage,
        )
}
