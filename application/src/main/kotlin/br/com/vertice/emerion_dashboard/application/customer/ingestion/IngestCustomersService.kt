package br.com.vertice.emerion_dashboard.application.customer.ingestion

import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestBatchCommand
import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestBatchResult
import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestCustomerCommand
import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestItemResult
import br.com.vertice.emerion_dashboard.application.customer.ingestion.model.IngestOutcome
import br.com.vertice.emerion_dashboard.domain.customer.model.Customer
import br.com.vertice.emerion_dashboard.domain.customer.repository.CustomerRepository
import br.com.vertice.emerion_dashboard.domain.customeraddress.model.CustomerAddress
import br.com.vertice.emerion_dashboard.domain.customeraddress.model.CustomerAddressDetail
import br.com.vertice.emerion_dashboard.domain.customeraddress.repository.CustomerAddressRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

/**
 * Use-case implementation: upserts each item in the batch by externalId so
 * re-sending the same batch from emerion-load-service is a no-op (idempotent).
 * A failure on one item does not abort the rest of the batch.
 */
@Service
class IngestCustomersService(
    private val customerRepository: CustomerRepository,
    private val customerAddressRepository: CustomerAddressRepository,
    private val clock: Clock = Clock.systemUTC(),
) : IngestCustomersUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun ingest(command: IngestBatchCommand): IngestBatchResult {
        logger.info("Ingesting batch '{}' with {} customer(s)", command.batchId, command.items.size)

        val results = command.items.map { item -> ingestItem(item, Instant.now(clock)) }

        logger.info(
            "Batch '{}' processed: {} succeeded, {} failed",
            command.batchId,
            results.count { it.outcome != IngestOutcome.FAILED },
            results.count { it.outcome == IngestOutcome.FAILED },
        )
        return IngestBatchResult(batchId = command.batchId, results = results)
    }

    @Transactional
    override fun ingestSingle(command: IngestCustomerCommand): IngestItemResult {
        logger.info("Ingesting single customer externalId='{}'", command.externalId)
        return ingestItem(command, Instant.now(clock))
    }

    private fun ingestItem(item: IngestCustomerCommand, now: Instant): IngestItemResult {
        return try {
            val existing = customerRepository.findByExternalId(item.externalId)
            val toSave = existing?.mergeFromIngestion(
                cnpjEmpresa = item.cnpjEmpresa,
                nomeFantasia = item.nomeFantasia,
                razaoSocial = item.razaoSocial,
                cpfCnpj = item.cpfCnpj,
                inscricaoEstadual = item.inscricaoEstadual,
                regimeTributario = item.regimeTributario,
                bloqueado = item.bloqueado,
                dataNascimento = item.dataNascimento,
                dataCadastro = item.dataCadastro,
                dataUltimaAtualizacao = item.dataUltimaAtualizacao,
                email1 = item.email1,
                email2 = item.email2,
                website = item.website,
                limiteCredito = item.limiteCredito,
                observacoes = item.observacoes,
                cnae = item.cnae,
                vendedorExternalId = item.vendedorExternalId,
                nomeVendedor = item.nomeVendedor,
                codigoTipoCliente = item.codigoTipoCliente,
                codigoGrupoCliente = item.codigoGrupoCliente,
                codigoCategoriaCliente = item.codigoCategoriaCliente,
                uf = item.uf,
                macroRegiao = item.macroRegiao,
                microRegiao = item.microRegiao,
                setor = item.setor,
                now = now,
            ) ?: Customer.newFromIngestion(
                externalId = item.externalId,
                cnpjEmpresa = item.cnpjEmpresa,
                nomeFantasia = item.nomeFantasia,
                razaoSocial = item.razaoSocial,
                cpfCnpj = item.cpfCnpj,
                inscricaoEstadual = item.inscricaoEstadual,
                regimeTributario = item.regimeTributario,
                bloqueado = item.bloqueado,
                dataNascimento = item.dataNascimento,
                dataCadastro = item.dataCadastro,
                dataUltimaAtualizacao = item.dataUltimaAtualizacao,
                email1 = item.email1,
                email2 = item.email2,
                website = item.website,
                limiteCredito = item.limiteCredito,
                observacoes = item.observacoes,
                cnae = item.cnae,
                vendedorExternalId = item.vendedorExternalId,
                nomeVendedor = item.nomeVendedor,
                codigoTipoCliente = item.codigoTipoCliente,
                codigoGrupoCliente = item.codigoGrupoCliente,
                codigoCategoriaCliente = item.codigoCategoriaCliente,
                uf = item.uf,
                macroRegiao = item.macroRegiao,
                microRegiao = item.microRegiao,
                setor = item.setor,
                createdAt = item.createdAt,
                now = now,
            )
            customerRepository.save(toSave)
            if (item.enderecos.isNotEmpty()) {
                upsertAddresses(item, now)
            }
            IngestItemResult(
                externalId = item.externalId,
                outcome = if (existing == null) IngestOutcome.CREATED else IngestOutcome.UPDATED,
                errorMessage = null,
            )
        } catch (ex: Exception) {
            logger.error("Failed to ingest customer externalId='{}'", item.externalId, ex)
            IngestItemResult(
                externalId = item.externalId,
                outcome = IngestOutcome.FAILED,
                errorMessage = ex.message,
            )
        }
    }

    private fun upsertAddresses(item: IngestCustomerCommand, now: Instant) {
            val existing = customerAddressRepository.findByExternalId(item.externalId)
            val details = item.enderecos.map {
                CustomerAddressDetail(
                    tipo = it.tipo,
                    cep = it.cep,
                    endereco = it.endereco,
                    numero = it.numero,
                    referencia = it.referencia,
                    bairro = it.bairro,
                    cidade = it.cidade,
                    uf = it.uf,
                    telefone = it.telefone,
                    telefoneContato = it.telefoneContato,
                    complemento = it.complemento,
                    fax = it.fax,
                    tipoEndereco = it.tipoEndereco,
                    dddTelefone = it.dddTelefone,
                    dddFax = it.dddFax,
                    dddCelular = it.dddCelular,
                    celular = it.celular,
                )
            }
            val address = existing?.mergeFromIngestion(item.cnpjEmpresa, item.cpfCnpj, details, now)
                ?: CustomerAddress.newFromIngestion(item.externalId, item.cnpjEmpresa, item.cpfCnpj, details, now)
            customerAddressRepository.save(address)
    }
}
