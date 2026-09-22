package br.com.vertice.emerion_dashboard.application.ipi.ingestion

import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiBatchCommand
import br.com.vertice.emerion_dashboard.application.ipi.ingestion.model.IngestIpiCommand
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestBatchResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestItemResult
import br.com.vertice.emerion_dashboard.domain.ingestion.IngestOutcome
import br.com.vertice.emerion_dashboard.domain.ipi.model.Ipi
import br.com.vertice.emerion_dashboard.domain.ipi.repository.IpiRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

@Service
class IngestIpiService(
    private val ipiRepository: IpiRepository,
    private val clock: Clock = Clock.systemUTC(),
) : IngestIpiUseCase {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun ingest(command: IngestIpiBatchCommand): IngestBatchResult {
        val results = command.items.map { ingestItem(it, Instant.now(clock)) }
        logger.info(
            "IPI batch '{}' processed: {} succeeded, {} failed",
            command.batchId,
            results.count { it.outcome != IngestOutcome.FAILED },
            results.count { it.outcome == IngestOutcome.FAILED },
        )
        return IngestBatchResult(command.batchId, results)
    }

    @Transactional
    override fun ingestSingle(command: IngestIpiCommand): IngestItemResult = ingestItem(command, Instant.now(clock))

    private fun ingestItem(item: IngestIpiCommand, now: Instant): IngestItemResult = try {
        val existing = ipiRepository.findByCnpjEmpresaAndCodigoIpi(item.cnpjEmpresa, item.codigoIpi)
        val toSave = existing?.mergeFromIngestion(
            flgAtivo = item.flgAtivo,
            tipoIpi = item.tipoIpi,
            nomeIpi = item.nomeIpi,
            ncmIpi = item.ncmIpi,
            codigoEnquadramentoLegal = item.codigoEnquadramentoLegal,
            cstIpi = item.cstIpi,
            descricaoSituacaoTributariaIpi = item.descricaoSituacaoTributariaIpi,
            aliquotaIpi = item.aliquotaIpi,
            percentualBaseCalculoIpi = item.percentualBaseCalculoIpi,
            flgSineif20 = item.flgSineif20,
            codigoTextoFiscal = item.codigoTextoFiscal,
            cstPis = item.cstPis,
            descricaoSituacaoTributariaPis = item.descricaoSituacaoTributariaPis,
            aliquotaPis = item.aliquotaPis,
            incluiDescontoSuframaPis = item.incluiDescontoSuframaPis,
            cstCofins = item.cstCofins,
            descricaoSituacaoTributariaCofins = item.descricaoSituacaoTributariaCofins,
            aliquotaCofins = item.aliquotaCofins,
            incluiDescontoSuframaCofins = item.incluiDescontoSuframaCofins,
            now = now,
        ) ?: Ipi.newFromIngestion(
            cnpjEmpresa = item.cnpjEmpresa,
            codigoIpi = item.codigoIpi,
            flgAtivo = item.flgAtivo,
            tipoIpi = item.tipoIpi,
            nomeIpi = item.nomeIpi,
            ncmIpi = item.ncmIpi,
            codigoEnquadramentoLegal = item.codigoEnquadramentoLegal,
            cstIpi = item.cstIpi,
            descricaoSituacaoTributariaIpi = item.descricaoSituacaoTributariaIpi,
            aliquotaIpi = item.aliquotaIpi,
            percentualBaseCalculoIpi = item.percentualBaseCalculoIpi,
            flgSineif20 = item.flgSineif20,
            codigoTextoFiscal = item.codigoTextoFiscal,
            cstPis = item.cstPis,
            descricaoSituacaoTributariaPis = item.descricaoSituacaoTributariaPis,
            aliquotaPis = item.aliquotaPis,
            incluiDescontoSuframaPis = item.incluiDescontoSuframaPis,
            cstCofins = item.cstCofins,
            descricaoSituacaoTributariaCofins = item.descricaoSituacaoTributariaCofins,
            aliquotaCofins = item.aliquotaCofins,
            incluiDescontoSuframaCofins = item.incluiDescontoSuframaCofins,
            now = now,
        )
        ipiRepository.save(toSave)
        IngestItemResult(key(item), if (existing == null) IngestOutcome.CREATED else IngestOutcome.UPDATED, null)
    } catch (ex: Exception) {
        logger.error("Failed to ingest IPI cnpjEmpresa='{}', codigoIpi='{}'", item.cnpjEmpresa, item.codigoIpi, ex)
        IngestItemResult(key(item), IngestOutcome.FAILED, ex.message)
    }

    private fun key(item: IngestIpiCommand) = "${item.cnpjEmpresa}:${item.codigoIpi}"
}
