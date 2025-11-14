package com.jsm.boardgame.auth.application.dto.request

data class LoginRequest(
    val username: String,
    val password: String,
)