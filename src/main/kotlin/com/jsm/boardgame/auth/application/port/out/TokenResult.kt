package com.jsm.boardgame.auth.application.port.out

data class TokenResult(
    val accessToken: String,
    val refreshToken: String,
)
