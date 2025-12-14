package com.jsm.boardgame.game.lexio.application.port.out

data class UserQueryResult(
    val userId: Long,
    val nickname: String,
    val profile: String?
)

interface UserInfoPort {
    fun getUserInfo(userId: Long): UserQueryResult
}
