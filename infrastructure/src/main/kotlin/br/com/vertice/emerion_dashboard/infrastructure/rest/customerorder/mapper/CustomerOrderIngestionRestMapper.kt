package br.com.vertice.emerion_dashboard.infrastructure.rest.customerorder.mapper

import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestCustomerOrderCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestCustomerOrderItemCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderItemIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult

/** Maps between the generated OpenAPI DTOs and the application layer's use-case commands/results. */
object CustomerOrderIngestionRestMapper {

    fun toCommand(dto: CustomerOrderIngestionBatch): IngestBatchCommand =
        IngestBatchCommand(
            batchId = dto.batchId,
            items = dto.items.map(::toItemCommand),
        )

    fun toItemCommand(dto: CustomerOrderIngestionItem): IngestCustomerOrderCommand =
        IngestCustomerOrderCommand(
            externalId = dto.externalId,
            codCli = dto.codCli,
            cnpjEmpresa = dto.cnpjEmpresa,
            nronfe = dto.nronfe,
            dteres = dto.dteres.toInstant(),
            sitres = dto.sitres,
            totger = dto.totger,
            totres = dto.totres,
            totipi = dto.totipi,
            totsub = dto.totsub,
            totdescinc = dto.totdescinc,
            itens = dto.itens.map(::toItemLineCommand),
        )

    private fun toItemLineCommand(dto: CustomerOrderItemIngestionItem): IngestCustomerOrderItemCommand =
        IngestCustomerOrderItemCommand(
            produto = dto.produto,
            descricao = dto.descricao,
            quantidade = dto.quantidade,
            valorUnitario = dto.valorUnitario,
            valorTotal = dto.valorTotal,
            seqRe2 = dto.seqRe2,
            codClp = dto.codClp,
            codSt1 = dto.codSt1,
            codUnd = dto.codUnd,
            vluRe2 = dto.vluRe2,
            dscRe2 = dto.dscRe2,
            dsrRe2 = dto.dsrRe2,
            icmsAliquota = dto.icmsAliquota,
            icmsBase = dto.icmsBase,
            icmsValor = dto.icmsValor,
            icmsReducaoBase = dto.icmsReducaoBase,
            icmsSubstituicaoBase = dto.icmsSubstituicaoBase,
            icmsSubstituicaoValor = dto.icmsSubstituicaoValor,
            icmsSubstituicaoAliquota = dto.icmsSubstituicaoAliquota,
            icmsSubstituicaoMargem = dto.icmsSubstituicaoMargem,
            icmsSubstituicaoReducaoBase = dto.icmsSubstituicaoReducaoBase,
            ipiAliquota = dto.ipiAliquota,
            ipiBase = dto.ipiBase,
            ipiValor = dto.ipiValor,
            ipiClassificacao = dto.ipiClassificacao,
            ipiCst = dto.ipiCst,
            pisBase = dto.pisBase,
            pisAliquota = dto.pisAliquota,
            pisValor = dto.pisValor,
            pisCst = dto.pisCst,
            cofinsBase = dto.cofinsBase,
            cofinsAliquota = dto.cofinsAliquota,
            cofinsValor = dto.cofinsValor,
            cofinsCst = dto.cofinsCst,
            descontoValor = dto.descontoValor,
            freteValor = dto.freteValor,
            seguroValor = dto.seguroValor,
            outrasDespesasValor = dto.outrasDespesasValor,
            totalItemTributado = dto.totalItemTributado,
            totRen = dto.totRen,
            totGe2 = dto.totGe2,
            observacao = dto.observacao,
            pedidoCompraCliente = dto.pedidoCompraCliente,
            itemPedidoCompraCliente = dto.itemPedidoCompraCliente,
            nroRe2 = dto.nroRe2,
            flgVal = dto.flgVal,
            flgPac = dto.flgPac,
            flgLib = dto.flgLib,
            codCfo = dto.codCfo,
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
