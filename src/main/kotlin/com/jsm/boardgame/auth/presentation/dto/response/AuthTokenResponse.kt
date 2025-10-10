package com.jsm.boardgame.auth.presentation.dto.response

data class AuthTokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
