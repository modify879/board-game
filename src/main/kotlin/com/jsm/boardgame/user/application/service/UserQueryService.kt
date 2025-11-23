package com.jsm.boardgame.user.application.service

import com.jsm.boardgame.auth.application.port.out.UserQueryPort
import com.jsm.boardgame.auth.application.port.out.UserQueryResult
import com.jsm.boardgame.user.domain.model.User
import com.jsm.boardgame.user.domain.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * user 컨텍스트가 auth 컨텍스트의 UserQueryPort를 구현하는 서비스
 * auth가 요구하는 인터페이스를 구현하여 제공
 */
@Service
class UserQueryService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserQueryPort {

    @Transactional(readOnly = true)
    override fun findByUsername(username: String): UserQueryResult? {
        val user = userRepository.findByUsername(username) ?: return null
        return user.toUserQueryResult()
    }

    @Transactional(readOnly = true)
    override fun findById(userId: Long): UserQueryResult? {
        val user = userRepository.findById(
            com.jsm.boardgame.user.domain.model.UserId.of(userId)
        ) ?: return null
        return user.toUserQueryResult()
    }

    @Transactional(readOnly = true)
    override fun verifyPassword(username: String, rawPassword: String): Boolean {
        val user = userRepository.findByUsername(username) ?: return false
        return passwordEncoder.matches(rawPassword, user.password.value())
    }

    private fun User.toUserQueryResult(): UserQueryResult {
        val userId = this.id ?: throw IllegalStateException("User ID cannot be null")
        return UserQueryResult(
            userId = userId.value,
            username = this.username,
            role = this.role.name,
        )
    }
}
