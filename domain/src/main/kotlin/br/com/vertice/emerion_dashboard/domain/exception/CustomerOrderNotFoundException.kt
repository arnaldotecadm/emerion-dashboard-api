package br.com.vertice.emerion_dashboard.domain.exception

class CustomerOrderNotFoundException(id: Long) : RuntimeException("CustomerOrder with id $id not found")
