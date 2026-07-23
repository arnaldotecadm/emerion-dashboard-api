package br.com.vertice.emerion_dashboard.application.customerorder.query

import br.com.vertice.emerion_dashboard.application.customerorder.query.model.ListCustomerOrdersQuery
import br.com.vertice.emerion_dashboard.domain.customerorder.exception.CustomerOrderNotFoundException
import br.com.vertice.emerion_dashboard.domain.customerorder.model.CustomerOrder
import br.com.vertice.emerion_dashboard.domain.customerorder.repository.CustomerOrderRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerOrderQueryService(
    private val customerOrderRepository: CustomerOrderRepository,
) : CustomerOrderQueryUseCase {

    @Transactional(readOnly = true)
    override fun getById(id: Long): CustomerOrder =
        customerOrderRepository.findById(id) ?: throw CustomerOrderNotFoundException(id)

    @Transactional(readOnly = true)
    override fun list(query: ListCustomerOrdersQuery): Page<CustomerOrder> =
        customerOrderRepository.findAll(
            pageRequest = PageRequest(page = query.page, size = query.size),
            codCli = query.codCli,
            sitres = query.sitres,
            cnpjEmpresa = query.cnpjEmpresa,
        )
}
