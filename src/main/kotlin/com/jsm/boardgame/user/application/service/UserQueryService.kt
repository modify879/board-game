package com.jsm.boardgame.user.application.service

import com.jsm.boardgame.user.application.dto.response.UserDto
import com.jsm.boardgame.user.domain.model.User
import com.jsm.boardgame.user.domain.model.UserId
import com.jsm.boardgame.user.domain.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * User 컨텍스트의 사용자 조회 서비스
 * 외부 컨텍스트(Auth 등)에 의존하지 않고 순수하게 User 도메인 객체를 반환
 */
@Service
class UserQueryService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    @Transactional(readOnly = true)
    fun findByUsername(username: String): UserDto? {
        val user = userRepository.findByUsername(username) ?: return null
        return user.toDto()
    }

    @Transactional(readOnly = true)
    fun findById(userId: Long): UserDto? {
        val user = userRepository.findById(UserId.of(userId)) ?: return null
        return user.toDto()
    }

    @Transactional(readOnly = true)
    fun verifyPassword(username: String, rawPassword: String): Boolean {
        val user = userRepository.findByUsername(username) ?: return false
        return passwordEncoder.matches(rawPassword, user.password.value())
    }

    private fun User.toDto() = UserDto(
        id = this.id?.value ?: throw IllegalStateException("User ID cannot be null"),
        username = this.username,
        nickname = this.nickname,
        role = this.role,
        profile = this.profile,
    )
}
