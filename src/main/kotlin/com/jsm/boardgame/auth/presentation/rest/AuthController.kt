package com.jsm.boardgame.auth.presentation.rest

import com.jsm.boardgame.auth.application.port.`in`.LoginUseCase
import com.jsm.boardgame.auth.application.port.`in`.RefreshTokenUseCase
import com.jsm.boardgame.auth.presentation.rest.dto.LoginRequest
import com.jsm.boardgame.auth.presentation.rest.dto.RefreshTokenRequest
import com.jsm.boardgame.auth.presentation.rest.dto.TokenResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val loginUseCase: LoginUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): TokenResponse {
        val result = loginUseCase.login(request.toCommand())
        return TokenResponse.from(result)
    }

    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): TokenResponse {
        val result = refreshTokenUseCase.refreshToken(request.toCommand())
        return TokenResponse.from(result)
    }
}
