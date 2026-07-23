package br.com.vertice.emerion_dashboard.infrastructure.rest.vendedor.controller

import br.com.vertice.emerion_dashboard.application.vendedor.query.VendedorQueryUseCase
import br.com.vertice.emerion_dashboard.application.vendedor.query.model.ListVendedoresQuery
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.api.VendedoresApi
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.VendedorPage
import br.com.vertice.emerion_dashboard.infrastructure.rest.generated.model.VendedorResponse
import br.com.vertice.emerion_dashboard.infrastructure.rest.vendedor.mapper.VendedorQueryRestMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

/**
 * Driving/inbound adapter for the vendedor read endpoints consumed by the
 * React frontend. Implements the generated `VendedoresApi` contract,
 * contains no business logic.
 */
@RestController
class VendedorQueryController(
    private val vendedorQueryUseCase: VendedorQueryUseCase,
) : VendedoresApi {

    override fun getVendedorById(id: Long): ResponseEntity<VendedorResponse> {
        val vendedor = vendedorQueryUseCase.getById(id)
        return ResponseEntity.ok(VendedorQueryRestMapper.toResponse(vendedor))
    }

    override fun listVendedores(page: Int, size: Int, nome: String?, situacao: String?, cnpjEmpresa: String?): ResponseEntity<VendedorPage> {
        val query = ListVendedoresQuery(page = page, size = size, nomeContains = nome, situacao = situacao, cnpjEmpresa = cnpjEmpresa)
        val result = vendedorQueryUseCase.list(query)
        return ResponseEntity.ok(VendedorQueryRestMapper.toPageResponse(result))
    }
}
