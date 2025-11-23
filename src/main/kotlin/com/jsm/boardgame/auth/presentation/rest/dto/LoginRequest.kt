package com.jsm.boardgame.auth.presentation.rest.dto

import com.jsm.boardgame.auth.application.port.`in`.LoginCommand
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "username은 필수입니다")
    val username: String,

    @field:NotBlank(message = "password는 필수입니다")
    val password: String,
) {
    fun toCommand(): LoginCommand = LoginCommand(
        username = username,
        password = password,
    )
}
