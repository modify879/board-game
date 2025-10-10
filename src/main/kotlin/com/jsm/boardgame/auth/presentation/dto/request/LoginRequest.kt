package com.jsm.boardgame.auth.presentation.dto.request

data class LoginRequest(
    val username: String,
    val password: String,
)
