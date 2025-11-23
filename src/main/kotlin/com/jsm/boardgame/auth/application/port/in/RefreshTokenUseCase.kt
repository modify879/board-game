package com.jsm.boardgame.auth.application.port.`in`

import com.jsm.boardgame.auth.application.port.out.TokenResult

interface RefreshTokenUseCase {
    fun refreshToken(command: RefreshTokenCommand): TokenResult
}
