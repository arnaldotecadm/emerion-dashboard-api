package br.com.vertice.emerion_dashboard.application.customeraddress.query

import br.com.vertice.emerion_dashboard.application.customeraddress.query.model.ListCustomerAddressesQuery
import br.com.vertice.emerion_dashboard.domain.customeraddress.exception.CustomerAddressNotFoundException
import br.com.vertice.emerion_dashboard.domain.customeraddress.model.CustomerAddress
import br.com.vertice.emerion_dashboard.domain.customeraddress.repository.CustomerAddressRepository
import br.com.vertice.emerion_dashboard.domain.shared.Page
import br.com.vertice.emerion_dashboard.domain.shared.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerAddressQueryService(
    private val customerAddressRepository: CustomerAddressRepository,
) : CustomerAddressQueryUseCase {

    @Transactional(readOnly = true)
    override fun getById(id: Long): CustomerAddress =
        customerAddressRepository.findById(id) ?: throw CustomerAddressNotFoundException(id)

    @Transactional(readOnly = true)
    override fun list(query: ListCustomerAddressesQuery): Page<CustomerAddress> =
        customerAddressRepository.findAll(
            pageRequest = PageRequest(page = query.page, size = query.size),
            cpfCnpjContains = query.cpfCnpjContains,
        )
}
