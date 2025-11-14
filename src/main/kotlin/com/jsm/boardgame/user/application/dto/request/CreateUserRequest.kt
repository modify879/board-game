package com.jsm.boardgame.user.application.dto.request

data class CreateUserRequest(
    val username: String,
    val password: String,
    val confirmPassword: String,
    val nickname: String
)
