package br.com.vertice.emerion_dashboard.domain.vendedor.exception

class VendedorNotFoundException : RuntimeException{
    constructor(id: Long) : super("Vendedor with id $id not found")
    constructor(externalId: String) : super("Vendedor with externalId $externalId not found")
}
