package com.jsm.boardgame.auth.presentation

import com.jsm.boardgame.auth.application.service.AuthService
import com.jsm.boardgame.auth.presentation.dto.request.LoginRequest
import com.jsm.boardgame.auth.presentation.dto.response.AuthTokenResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): AuthTokenResponse {
        val authToken = authService.login(request.username, request.password)
        return AuthTokenResponse(authToken.accessToken, authToken.refreshToken)
    }
}