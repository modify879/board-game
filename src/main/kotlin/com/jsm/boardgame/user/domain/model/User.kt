package com.jsm.boardgame.user.domain.model

class User(
    val id: UserId?,
    val username: String,
    val nickname: String,
    val password: EncodedPassword,
    val role: UserRole,
    val profile: String?,
) {

    companion object {
        fun create(
            username: String,
            nickname: String,
            encodedPassword: EncodedPassword,
            role: UserRole,
        ): User = User(
            id = null,
            username = username,
            nickname = nickname,
            password = encodedPassword,
            role = role,
            profile = null,
        )
    }
}
