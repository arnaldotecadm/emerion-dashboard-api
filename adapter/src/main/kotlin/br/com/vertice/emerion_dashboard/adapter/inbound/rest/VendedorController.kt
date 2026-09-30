package br.com.vertice.emerion_dashboard.adapter.inbound.rest

import br.com.vertice.emerion_dashboard.adapter.mapper.VendedorRestMapper.toResponse
import br.com.vertice.emerion_dashboard.application.vendedor.VendedorService
import br.com.vertice.emerion_dashboard.domain.vendedor.ListVendedoresQuery
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.VendedoresApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.PaginationInfo
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.VendedorPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.VendedorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class VendedorController(
    private val vendedorService: VendedorService,
) : VendedoresApi {

    override fun getVendedorById(id: Long): ResponseEntity<VendedorResponse> {
        val vendedor = vendedorService.getById(id)
        return ResponseEntity.ok(vendedor.toResponse())
    }

    override fun getVendedorByExternalId(externalId: String): ResponseEntity<VendedorResponse> {
        val vendedor = vendedorService.getByExternalId(externalId)
        return ResponseEntity.ok(vendedor.toResponse())
    }

    override fun listVendedores(page: Int, size: Int, nome: String?, ativo: String?, cnpjEmpresa: String?): ResponseEntity<VendedorPage> {
        val query = ListVendedoresQuery(page = page, size = size, nomeContains = nome, ativo = ativo, cnpjEmpresa = cnpjEmpresa)
        val result = vendedorService.list(query)
        return ResponseEntity.ok(
            VendedorPage(
                data = result.content.map { it.toResponse() },
                pagination = PaginationInfo(
                    total = result.totalElements,
                    page = result.page,
                    propertySize = result.size,
                    totalPages = result.totalPages,
                ),
            ),
        )
    }
}
