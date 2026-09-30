package br.com.vertice.emerion_dashboard.domain.exception

class ProductNotFoundException : RuntimeException {
    constructor(id: Long) : super("Product with id $id not found")
    constructor(externalId: String) : super("Product with externalId $externalId not found")
}
