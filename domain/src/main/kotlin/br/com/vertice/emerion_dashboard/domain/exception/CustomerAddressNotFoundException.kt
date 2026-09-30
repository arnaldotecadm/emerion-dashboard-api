package br.com.vertice.emerion_dashboard.domain.exception

class CustomerAddressNotFoundException(id: Long) : RuntimeException("CustomerAddress with id $id not found")
