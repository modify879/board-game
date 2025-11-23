package com.jsm.boardgame.auth.application.service

import com.jsm.boardgame.auth.application.port.`in`.LoginCommand
import com.jsm.boardgame.auth.application.port.`in`.LoginUseCase
import com.jsm.boardgame.auth.application.port.out.TokenResult
import com.jsm.boardgame.auth.application.port.out.UserQueryPort
import com.jsm.boardgame.auth.domain.exception.InvalidCredentialsException
import com.jsm.boardgame.auth.domain.model.AuthUserId
import com.jsm.boardgame.auth.domain.repository.RefreshTokenRepository
import com.jsm.boardgame.auth.infrastructure.jwt.JwtTokenProvider
import com.jsm.boardgame.auth.infrastructure.properties.JwtProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LoginService(
    private val userQueryPort: UserQueryPort,
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProperties: JwtProperties,
) : LoginUseCase {

    @Transactional(readOnly = true)
    override fun login(command: LoginCommand): TokenResult {
        val userInfo = userQueryPort.findByUsername(command.username)
            ?: throw InvalidCredentialsException()

        if (!userQueryPort.verifyPassword(command.username, command.password)) {
            throw InvalidCredentialsException()
        }

        val authUserId = AuthUserId.of(userInfo.userId)
        val accessToken = jwtTokenProvider.generateAccessToken(authUserId, userInfo.role)
        val refreshToken = jwtTokenProvider.generateRefreshToken()

        // Redis에 refreshToken 저장 (밀리초를 초로 변환)
        val expirationSeconds = jwtProperties.refreshTokenExpiration / 1000
        refreshTokenRepository.save(authUserId, refreshToken, expirationSeconds)

        return TokenResult(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }
}
