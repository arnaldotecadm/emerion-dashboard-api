package br.com.vertice.emerion_dashboard.application.customercredit.query

import br.com.vertice.emerion_dashboard.application.customercredit.query.model.ListCustomerCreditsQuery
import br.com.vertice.emerion_dashboard.domain.customercredit.exception.CustomerCreditNotFoundException
import br.com.vertice.emerion_dashboard.domain.customercredit.model.CustomerCredit
import br.com.vertice.emerion_dashboard.domain.customercredit.repository.CustomerCreditRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerCreditQueryService(
    private val customerCreditRepository: CustomerCreditRepository,
) : CustomerCreditQueryUseCase {

    @Transactional(readOnly = true)
    override fun getById(id: Long): CustomerCredit =
        customerCreditRepository.findById(id) ?: throw CustomerCreditNotFoundException(id)

    @Transactional(readOnly = true)
    override fun list(query: ListCustomerCreditsQuery): Page<CustomerCredit> =
        customerCreditRepository.findAll(
            pageRequest = PageRequest(page = query.page, size = query.size),
            customerExternalId = query.customerExternalId,
            tipo = query.tipo,
            cnpjEmpresa = query.cnpjEmpresa,
        )
}
