package com.jsm.boardgame.auth.presentation.dto.response

import com.jsm.boardgame.auth.application.port.out.TokenResult

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
) {
    companion object {
        fun from(result: TokenResult): TokenResponse = TokenResponse(
            accessToken = result.accessToken,
            refreshToken = result.refreshToken,
        )
    }
}
