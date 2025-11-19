package com.jsm.boardgame.user.application.port.`in`

import com.jsm.boardgame.user.application.port.out.RegisteredUserResult

interface RegisterUserUseCase {
    fun register(command: RegisterUserCommand): RegisteredUserResult
}

