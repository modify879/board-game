package com.jsm.boardgame.user.application.port.`in`

import com.jsm.boardgame.user.domain.model.UserRole

data class RegisterUserCommand(
    val username: String,
    val rawPassword: String,
    val confirmPassword: String,
    val role: UserRole,
    val nickname: String,
)

