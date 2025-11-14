package com.jsm.boardgame.auth.presentation

import com.jsm.boardgame.auth.application.dto.request.LoginRequest
import com.jsm.boardgame.auth.application.dto.response.AuthTokenResponse
import com.jsm.boardgame.auth.application.service.AuthService
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
    fun login(@RequestBody request: LoginRequest): AuthTokenResponse =
        authService.login(request.username, request.password)

    @PostMapping("/reissue")
    fun reissue(@RequestBody request: AuthTokenResponse): AuthTokenResponse =
        authService.reissue(request.accessToken, request.refreshToken)
}