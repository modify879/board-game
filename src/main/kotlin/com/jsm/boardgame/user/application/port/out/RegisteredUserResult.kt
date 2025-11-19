package com.jsm.boardgame.user.application.port.out

import com.jsm.boardgame.user.domain.model.User
import com.jsm.boardgame.user.domain.model.UserRole

data class RegisteredUserResult(
    val id: Long,
    val username: String,
    val role: UserRole,
    val nickname: String,
    val profile: String?,
) {
    companion object {
        fun from(user: User) = RegisteredUserResult(
            id = user.id?.value ?: throw IllegalStateException("user id is null"),
            username = user.username,
            role = user.role,
            nickname = user.nickname,
            profile = user.profile,
        )
    }
}

