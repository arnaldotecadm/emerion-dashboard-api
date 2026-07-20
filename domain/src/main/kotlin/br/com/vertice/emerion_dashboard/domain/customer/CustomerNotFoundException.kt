package br.com.vertice.emerion_dashboard.domain.customer

class CustomerNotFoundException(id: Long) : RuntimeException("Customer with id $id not found")
