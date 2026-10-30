package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.port

import br.com.vertice.emerion_dashboard.adapter.mapper.SmartStockMapper.toDomain
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository.SmartStockDataRepository
import br.com.vertice.emerion_dashboard.application.cognitouser.UserService
import br.com.vertice.emerion_dashboard.application.outbound.port.SmartStockRepositoryPort
import br.com.vertice.emerion_dashboard.domain.dashboard.StockRecommendation
import org.springframework.stereotype.Component

@Component
class SmartStockRepositoryPortAdapter(
    private val smartStockDataRepository: SmartStockDataRepository,
    private val userService: UserService,
) : SmartStockRepositoryPort {
    override fun getStockRecommendations(): List<StockRecommendation> {
        return smartStockDataRepository.getStockRecommendations(userService.getCurrentCompanyCnpj()).map { it.toDomain() }
    }
}