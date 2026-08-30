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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

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
            codigoEmpresa = dto.codigoEmpresa,
            codigoCliente = dto.codigoCliente,
            cpfCnpj = dto.cpfCnpj,
            numeroPedido = dto.numeroPedido,
            dataPedido = dto.dataPedido.toLocalOrderDate(),
            statusPedido = dto.statusPedido,
            totalPedidoComImpostos = dto.totalPedidoComImpostos,
            totalPedidoSemImpostos = dto.totalPedidoSemImpostos,
            totalIpi = dto.totalIpi,
            totalIcms = dto.totalIcms,
            totalPis = dto.totalPis,
            totalCofins = dto.totalCofins,
            totalSubstituicaoTributaria = dto.totalSubstituicaoTributaria,
            totalDescontoIncondicional = dto.totalDescontoIncondicional,
            totalFrete = dto.totalFrete,
            totalSeguro = dto.totalSeguro,
            totalOutrasDespesas = dto.totalOutrasDespesas,
            vendedorExternalId = dto.vendedorExternalId,
            dataEntregaPrevista = dto.dataEntregaPrevista?.toLocalOrderDate(),
            codigoTransportadora = dto.codigoTransportadora,
            pedidoAnterior = dto.pedidoAnterior,
            regimeTributario = dto.regimeTributario,
            nomeRegimeTributario = dto.nomeRegimeTributario,
            codigoPadraoFaturamento = dto.codigoPadraoFaturamento,
            itens = dto.itens.map(::toItemLineCommand),
        )

    private fun toItemLineCommand(dto: CustomerOrderItemIngestionItem): IngestCustomerOrderItemCommand =
        IngestCustomerOrderItemCommand(
            codEmp = dto.codEmp,
            dteres = dto.dteres,
            numres = dto.numres,
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
            codcor = dto.codcor,
            codtam = dto.codtam,
            descricaoNFe = dto.descricaoNFe,
            pesoLiquido = dto.pesoLiquido,
            pesoBruto = dto.pesoBruto,
            referencia = dto.referencia,
            quantidadeFaturada = dto.quantidadeFaturada,
            quantidadeSeparada = dto.quantidadeSeparada,
            custoTotal = dto.custoTotal,
            lucroValor = dto.lucroValor,
            lucroPorcentagem = dto.lucroPorcentagem,
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

    private fun String.toLocalOrderDate(): LocalDate =
        try {
            LocalDateTime.parse(this).toLocalDate()
        } catch (_: DateTimeParseException) {
            LocalDate.parse(this)
        }
}
