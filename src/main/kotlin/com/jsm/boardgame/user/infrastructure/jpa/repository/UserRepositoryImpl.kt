package com.jsm.boardgame.user.infrastructure.jpa.repository

import com.jsm.boardgame.user.domain.model.User
import com.jsm.boardgame.user.domain.repository.UserRepository
import com.jsm.boardgame.user.infrastructure.jpa.entity.UserEntity
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {

    override fun createUser(user: User): User = userJpaRepository.save(UserEntity.from(user)).toDomain()

    override fun findByUsername(username: String): User? = userJpaRepository.findByUsername(username)?.toDomain()

}