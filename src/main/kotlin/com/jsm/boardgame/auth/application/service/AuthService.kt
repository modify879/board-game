package com.jsm.boardgame.auth.application.service

import com.jsm.boardgame.auth.application.query.AuthTokenQuery
import com.jsm.boardgame.auth.domain.AuthRedisRepository
import com.jsm.boardgame.auth.infrastructure.jwt.AuthTokenProvider
import com.jsm.boardgame.common.infrastructure.utils.PasswordUtils
import com.jsm.boardgame.common.properties.AuthTokenProperties
import com.jsm.boardgame.user.application.service.UserQueryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userQueryService: UserQueryService,
    private val authRedisRepository: AuthRedisRepository,
    private val authTokenProvider: AuthTokenProvider,
    private val authTokenProperties: AuthTokenProperties
) {

    private val refreshTokenExpirationInSec = authTokenProperties.refreshToken.expirationInSec.toLong()

    @Transactional(readOnly = true)
    fun login(username: String, password: String): AuthTokenQuery {
        val user = userQueryService.getUserByUsername(username) ?: throw IllegalArgumentException("User not found")

        if (!PasswordUtils.matchesPassword(password, user.password)) {
            throw IllegalArgumentException("Invalid password")
        }

        val accessToken = authTokenProvider.generateAccessToken(user.id!!, user.role)
        val refreshToken = authTokenProvider.generateRefreshToken()

        authRedisRepository.saveRefreshToken(
            user.id,
            refreshToken,
            refreshTokenExpirationInSec
        )

        return AuthTokenQuery(accessToken, refreshToken)
    }

    @Transactional(readOnly = true)
    fun reissue(accessToken: String, refreshToken: String): AuthTokenQuery {
        val userId = authTokenProvider.getUserId(accessToken)
            ?: throw IllegalArgumentException("Invalid access token: User ID not found")

        val userRoles = authTokenProvider.getRoles(accessToken)
        if (userRoles.isEmpty()) {
            throw IllegalArgumentException("Invalid access token: Roles not found")
        }

        if (authTokenProvider.validate(accessToken)) {
            authRedisRepository.deleteRefreshToken(userId)
            throw IllegalArgumentException("Access token is not yet expired")
        }

        if (!authRedisRepository.existsRefreshToken(userId, refreshToken)) {
            authRedisRepository.deleteRefreshToken(userId)
            throw IllegalArgumentException("Invalid refresh token")
        }

        val newAccessToken = authTokenProvider.generateAccessToken(userId, userRoles)
        var newRefreshToken = refreshToken

        val ttlSeconds = authRedisRepository.getRefreshTokenTTL(userId, refreshToken)
        if (ttlSeconds <= authTokenProperties.refreshToken.reissueInSec) {
            newRefreshToken = authTokenProvider.generateRefreshToken()
            authRedisRepository.saveRefreshToken(
                userId,
                newRefreshToken,
                refreshTokenExpirationInSec
            )
        }

        return AuthTokenQuery(newAccessToken, newRefreshToken)
    }
}