package com.jsm.boardgame.user.application.dto

import com.jsm.boardgame.user.domain.model.UserRole

data class UserDto(
    val id: Long,
    val username: String,
    val nickname: String,
    val role: UserRole,
    val profile: String?,
)



