package com.jsm.boardgame.auth.application.port.`in`

data class RefreshTokenCommand(
    val accessToken: String,
    val refreshToken: String,
)
