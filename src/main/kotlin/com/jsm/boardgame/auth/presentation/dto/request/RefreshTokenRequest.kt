package com.jsm.boardgame.auth.presentation.dto.request

import com.jsm.boardgame.auth.application.port.`in`.command.RefreshTokenCommand
import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequest(
    @field:NotBlank(message = "accessToken은 필수입니다")
    val accessToken: String,

    @field:NotBlank(message = "refreshToken은 필수입니다")
    val refreshToken: String,
) {
    fun toCommand(): RefreshTokenCommand = RefreshTokenCommand(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
}
