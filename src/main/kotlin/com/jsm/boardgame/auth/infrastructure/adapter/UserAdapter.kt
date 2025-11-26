package com.jsm.boardgame.auth.infrastructure.adapter

import com.jsm.boardgame.auth.application.port.out.UserQueryPort
import com.jsm.boardgame.auth.application.port.out.UserQueryResult
import com.jsm.boardgame.user.application.dto.UserDto
import com.jsm.boardgame.user.application.service.UserQueryService
import org.springframework.stereotype.Component

/**
 * Auth 컨텍스트와 User 컨텍스트 사이의 ACL (Anti-Corruption Layer) 어댑터
 * User 컨텍스트의 모델을 Auth 컨텍스트의 모델로 변환
 */
@Component
class UserAdapter(
    private val userQueryService: UserQueryService,
) : UserQueryPort {

    override fun findByUsername(username: String): UserQueryResult? {
        val userDto = userQueryService.findByUsername(username) ?: return null
        return userDto.toQueryResult()
    }

    override fun findById(userId: Long): UserQueryResult? {
        val userDto = userQueryService.findById(userId) ?: return null
        return userDto.toQueryResult()
    }

    override fun verifyPassword(username: String, rawPassword: String): Boolean {
        return userQueryService.verifyPassword(username, rawPassword)
    }

    private fun UserDto.toQueryResult() = UserQueryResult(
        userId = this.id,
        username = this.username,
        role = this.role.name,
    )
}
