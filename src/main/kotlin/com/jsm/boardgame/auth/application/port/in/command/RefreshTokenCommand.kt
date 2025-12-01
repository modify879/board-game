package com.jsm.boardgame.auth.application.port.`in`.command

data class RefreshTokenCommand(
    val accessToken: String,
    val refreshToken: String,
)
