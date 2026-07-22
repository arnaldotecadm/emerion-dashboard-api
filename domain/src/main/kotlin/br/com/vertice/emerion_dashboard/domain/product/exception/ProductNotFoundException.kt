package br.com.vertice.emerion_dashboard.domain.product.exception

class ProductNotFoundException(id: Long) : RuntimeException("Product with id $id not found")
