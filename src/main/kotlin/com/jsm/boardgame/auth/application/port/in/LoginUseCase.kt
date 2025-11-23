package com.jsm.boardgame.auth.application.port.`in`

import com.jsm.boardgame.auth.application.port.out.TokenResult

interface LoginUseCase {
    fun login(command: LoginCommand): TokenResult
}
