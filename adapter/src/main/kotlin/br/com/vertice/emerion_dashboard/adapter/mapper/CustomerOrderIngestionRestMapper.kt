package br.com.vertice.emerion_dashboard.adapter.mapper

import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestCustomerOrderCommand
import br.com.vertice.emerion_dashboard.application.customerorder.ingestion.model.IngestCustomerOrderItemCommand
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderIngestionBatch
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.CustomerOrderItemIngestionItem
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionItemResult
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.IngestionResult
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

/** Maps between the generated ingestion DTOs and the application command/result models. */
object CustomerOrderIngestionRestMapper {

    fun CustomerOrderIngestionBatch.toCommand(): IngestBatchCommand =
        IngestBatchCommand(
            batchId = batchId,
            items = items.map { it.toItemCommand() },
        )

    fun CustomerOrderIngestionItem.toItemCommand(): IngestCustomerOrderCommand =
        IngestCustomerOrderCommand(
            externalId = externalId,
            cnpjEmpresa = cnpjEmpresa,
            codigoEmpresa = codigoEmpresa,
            codigoCliente = codigoCliente,
            cpfCnpj = cpfCnpj,
            numeroPedido = numeroPedido,
            dataPedido = dataPedido.toLocalOrderDate(),
            statusPedido = statusPedido,
            totalPedidoComImpostos = totalPedidoComImpostos,
            totalPedidoSemImpostos = totalPedidoSemImpostos,
            totalIpi = totalIpi,
            totalIcms = totalIcms,
            totalPis = totalPis,
            totalCofins = totalCofins,
            totalSubstituicaoTributaria = totalSubstituicaoTributaria,
            totalDescontoIncondicional = totalDescontoIncondicional,
            totalFrete = totalFrete,
            totalSeguro = totalSeguro,
            totalOutrasDespesas = totalOutrasDespesas,
            vendedorExternalId = vendedorExternalId,
            dataEntregaPrevista = dataEntregaPrevista?.toLocalOrderDate(),
            codigoTransportadora = codigoTransportadora,
            pedidoAnterior = pedidoAnterior,
            regimeTributario = regimeTributario,
            nomeRegimeTributario = nomeRegimeTributario,
            codigoPadraoFaturamento = codigoPadraoFaturamento,
            ufPedido = ufPedido,
            itens = itens.map { it.toItemLineCommand() },
        )

    private fun CustomerOrderItemIngestionItem.toItemLineCommand(): IngestCustomerOrderItemCommand =
        IngestCustomerOrderItemCommand(
            codEmp = codEmp,
            dteres = dteres,
            numres = numres,
            produto = produto,
            descricao = descricao,
            quantidade = quantidade,
            valorUnitario = valorUnitario,
            valorTotal = valorTotal,
            seqRe2 = seqRe2,
            codClp = codClp,
            codSt1 = codSt1,
            codUnd = codUnd,
            vluRe2 = vluRe2,
            dscRe2 = dscRe2,
            dsrRe2 = dsrRe2,
            icmsAliquota = icmsAliquota,
            icmsBase = icmsBase,
            icmsValor = icmsValor,
            icmsReducaoBase = icmsReducaoBase,
            icmsSubstituicaoBase = icmsSubstituicaoBase,
            icmsSubstituicaoValor = icmsSubstituicaoValor,
            icmsSubstituicaoAliquota = icmsSubstituicaoAliquota,
            icmsSubstituicaoMargem = icmsSubstituicaoMargem,
            icmsSubstituicaoReducaoBase = icmsSubstituicaoReducaoBase,
            ipiAliquota = ipiAliquota,
            ipiBase = ipiBase,
            ipiValor = ipiValor,
            ipiClassificacao = ipiClassificacao,
            ipiCst = ipiCst,
            pisBase = pisBase,
            pisAliquota = pisAliquota,
            pisValor = pisValor,
            pisCst = pisCst,
            cofinsBase = cofinsBase,
            cofinsAliquota = cofinsAliquota,
            cofinsValor = cofinsValor,
            cofinsCst = cofinsCst,
            descontoValor = descontoValor,
            freteValor = freteValor,
            seguroValor = seguroValor,
            outrasDespesasValor = outrasDespesasValor,
            totalItemTributado = totalItemTributado,
            totRen = totRen,
            totGe2 = totGe2,
            observacao = observacao,
            pedidoCompraCliente = pedidoCompraCliente,
            itemPedidoCompraCliente = itemPedidoCompraCliente,
            nroRe2 = nroRe2,
            flgVal = flgVal,
            flgPac = flgPac,
            flgLib = flgLib,
            codCfo = codCfo,
            codcor = codcor,
            codtam = codtam,
            descricaoNFe = descricaoNFe,
            pesoLiquido = pesoLiquido,
            pesoBruto = pesoBruto,
            referencia = referencia,
            quantidadeFaturada = quantidadeFaturada,
            quantidadeSeparada = quantidadeSeparada,
            custoTotal = custoTotal,
            lucroValor = lucroValor,
            lucroPorcentagem = lucroPorcentagem,
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

    private fun String.toLocalOrderDate(): LocalDate =
        try {
            LocalDateTime.parse(this).toLocalDate()
        } catch (_: DateTimeParseException) {
            LocalDate.parse(this)
        }
}
