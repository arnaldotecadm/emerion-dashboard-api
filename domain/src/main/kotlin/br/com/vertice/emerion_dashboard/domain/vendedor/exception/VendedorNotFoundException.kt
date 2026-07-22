package br.com.vertice.emerion_dashboard.domain.vendedor.exception

class VendedorNotFoundException(id: Long) : RuntimeException("Vendedor with id $id not found")
