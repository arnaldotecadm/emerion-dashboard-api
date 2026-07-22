package br.com.vertice.emerion_dashboard.domain.customer.exception

class CustomerNotFoundException : RuntimeException {
    constructor(id: Long) : super("Customer with id $id not found")
    constructor(externalId: String) : super("Customer with externalId $externalId not found")
}
