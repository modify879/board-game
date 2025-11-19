package com.jsm.boardgame.user.presentation.rest.dto

import com.jsm.boardgame.user.application.port.out.RegisteredUserResult
import com.jsm.boardgame.user.domain.model.UserRole

data class UserResponse(
    val id: Long,
    val username: String,
    val role: UserRole,
    val nickname: String,
    val profile: String?
) {
    companion object {
        fun from(result: RegisteredUserResult) = UserResponse(
            id = result.id,
            username = result.username,
            role = result.role,
            nickname = result.nickname,
            profile = result.profile
        )
    }
}

