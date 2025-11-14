package com.jsm.boardgame.auth.application.dto.response

data class AuthTokenResponse(
    val accessToken: String,
    val refreshToken: String,
)