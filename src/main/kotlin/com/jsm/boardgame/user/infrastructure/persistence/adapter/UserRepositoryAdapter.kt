package com.jsm.boardgame.user.infrastructure.persistence.adapter

import com.jsm.boardgame.user.domain.model.User
import com.jsm.boardgame.user.domain.model.UserId
import com.jsm.boardgame.user.domain.repository.UserRepository
import com.jsm.boardgame.user.infrastructure.persistence.entity.UserJpaEntity
import com.jsm.boardgame.user.infrastructure.persistence.repository.UserJpaRepository
import org.springframework.stereotype.Component

@Component
class UserRepositoryAdapter(
    private val userJpaRepository: UserJpaRepository
) : UserRepository {

    override fun save(user: User): User {
        val entity = UserJpaEntity.from(user)
        val saved = userJpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun existsByUsername(username: String): Boolean =
        userJpaRepository.existsByUsername(username)

    override fun findById(id: UserId): User? =
        userJpaRepository.findById(id.value).map { it.toDomain() }.orElse(null)

    override fun findByUsername(username: String): User? =
        userJpaRepository.findByUsername(username)?.toDomain()
}

