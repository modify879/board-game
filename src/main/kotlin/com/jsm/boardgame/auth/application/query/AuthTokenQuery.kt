package com.jsm.boardgame.auth.application.query

data class AuthTokenQuery(
    val accessToken: String,
    val refreshToken: String,
)
