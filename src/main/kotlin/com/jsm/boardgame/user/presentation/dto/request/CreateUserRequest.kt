package com.jsm.boardgame.user.presentation.dto.request

data class CreateUserRequest(
    val username: String,
    val password: String,
    val confirmPassword: String,
    val nickname: String
)
