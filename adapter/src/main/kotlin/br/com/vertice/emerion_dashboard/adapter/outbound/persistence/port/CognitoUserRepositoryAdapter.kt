package br.com.vertice.emerion_dashboard.adapter.outbound.persistence.port

import br.com.vertice.emerion_dashboard.adapter.mapper.CognitoUserPersistenceMapper.toDomain
import br.com.vertice.emerion_dashboard.adapter.mapper.CognitoUserPersistenceMapper.toEntity
import br.com.vertice.emerion_dashboard.adapter.outbound.persistence.repository.CognitoUserSpringDataRepository
import br.com.vertice.emerion_dashboard.application.outbound.port.CognitoUserRepository
import br.com.vertice.emerion_dashboard.domain.cognitouser.model.CognitoUser
import org.springframework.stereotype.Component

/** Adapter implementing the domain's outbound port (`CognitoUserRepository`) on top of Spring Data JPA. */
@Component
class CognitoUserRepositoryAdapter(
    private val springDataRepository: CognitoUserSpringDataRepository,
) : CognitoUserRepository {

    override fun findBySub(sub: String): CognitoUser? =
        springDataRepository.findBySub(sub)?.toDomain()

    override fun findAll(): List<CognitoUser> =
        springDataRepository.findAll().map { it.toDomain() }

    override fun save(user: CognitoUser): CognitoUser {
        val existing = user.id?.let { springDataRepository.findById(it).orElse(null) }
            ?: springDataRepository.findBySub(user.sub)
        val entity = user.toEntity(existing)
        val saved = springDataRepository.save(entity)
        return saved.toDomain()
    }
}
