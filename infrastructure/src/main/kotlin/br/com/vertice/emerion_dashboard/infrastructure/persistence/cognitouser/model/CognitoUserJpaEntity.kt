package br.com.vertice.emerion_dashboard.infrastructure.persistence.cognitouser.model

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

/**
 * JPA entity for the `cognito_user` table (+ its `cognito_user_group` child
 * table, mapped as a plain `@ElementCollection` of strings since a group
 * membership is just a name, not its own aggregate). Lives entirely in the
 * infrastructure layer: the domain layer never sees this class, only
 * `domain.cognitouser.model.CognitoUser` via `CognitoUserPersistenceMapper`.
 *
 * Unlike the other resources in this API, this is a pure internal cache of
 * an external directory (no ingestion/query REST surface of its own), so it
 * intentionally skips the native-query/projection split used elsewhere -
 * there's no filtering/pagination need to justify it.
 */
@Entity
@Table(
    name = "cognito_user",
    uniqueConstraints = [UniqueConstraint(name = "uk_cognito_user_sub", columnNames = ["sub"])],
)
class CognitoUserJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "sub", nullable = false)
    var sub: String = "",

    @Column(name = "username", nullable = false)
    var username: String = "",

    @Column(name = "email")
    var email: String? = null,

    @Column(name = "enabled", nullable = false)
    var enabled: Boolean = true,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cognito_user_group", joinColumns = [JoinColumn(name = "cognito_user_id")])
    @Column(name = "group_name")
    var groups: MutableList<String> = mutableListOf(),

    @Column(name = "last_synced_at", nullable = false)
    var lastSyncedAt: Instant = Instant.now(),

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
