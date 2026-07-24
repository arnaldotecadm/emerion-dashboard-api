package br.com.vertice.emerion_dashboard.infrastructure.persistence.cognitouser.adapter

import br.com.vertice.emerion_dashboard.domain.cognitouser.model.CognitoUser
import br.com.vertice.emerion_dashboard.domain.cognitouser.repository.CognitoUserRepository
import br.com.vertice.emerion_dashboard.infrastructure.persistence.cognitouser.mapper.CognitoUserPersistenceMapper
import br.com.vertice.emerion_dashboard.infrastructure.persistence.cognitouser.repository.CognitoUserSpringDataRepository
import org.springframework.stereotype.Component

/** Adapter implementing the domain's outbound port (`CognitoUserRepository`) on top of Spring Data JPA. */
@Component
class CognitoUserRepositoryAdapter(
    private val springDataRepository: CognitoUserSpringDataRepository,
) : CognitoUserRepository {

    override fun findBySub(sub: String): CognitoUser? =
        springDataRepository.findBySub(sub)?.let(CognitoUserPersistenceMapper::toDomain)

    override fun findAll(): List<CognitoUser> =
        springDataRepository.findAll().map(CognitoUserPersistenceMapper::toDomain)

    override fun save(user: CognitoUser): CognitoUser {
        val existing = user.id?.let { springDataRepository.findById(it).orElse(null) }
            ?: springDataRepository.findBySub(user.sub)
        val entity = CognitoUserPersistenceMapper.toEntity(user, existing)
        val saved = springDataRepository.save(entity)
        return CognitoUserPersistenceMapper.toDomain(saved)
    }
}
