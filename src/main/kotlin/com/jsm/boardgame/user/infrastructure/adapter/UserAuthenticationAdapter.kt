package com.jsm.boardgame.user.infrastructure.adapter

import com.jsm.boardgame.auth.domain.port.out.UserAuthenticationPort
import com.jsm.boardgame.auth.domain.port.out.dto.UserAuthenticationInfo
import com.jsm.boardgame.user.infrastructure.jpa.repository.UserJpaRepository
import org.springframework.stereotype.Component

@Component
class UserAuthenticationAdapter(
    private val userJpaRepository: UserJpaRepository
) : UserAuthenticationPort {

    override fun getUserByUsername(username: String): UserAuthenticationInfo? {
        val userEntity = userJpaRepository.findByUsername(username) ?: return null

        return UserAuthenticationInfo(
            id = userEntity.id!!,
            password = userEntity.password,
            userRoles = userEntity.role.map { it.name }
        )
    }
}