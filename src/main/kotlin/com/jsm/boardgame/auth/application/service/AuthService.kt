package com.jsm.boardgame.auth.application.service

import com.jsm.boardgame.auth.application.query.AuthTokenQuery
import com.jsm.boardgame.auth.domain.AuthRedisRepository
import com.jsm.boardgame.auth.infrastructure.jwt.AuthTokenProvider
import com.jsm.boardgame.common.infrastructure.utils.PasswordUtils
import com.jsm.boardgame.user.application.service.UserQueryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userQueryService: UserQueryService,
    private val authRedisRepository: AuthRedisRepository,
    private val authTokenProvider: AuthTokenProvider
) {

    @Transactional(readOnly = true)
    fun login(username: String, password: String): AuthTokenQuery {
        val user = userQueryService.getUserByUsername(username) ?: throw IllegalArgumentException("User not found")

        if (!PasswordUtils.matchesPassword(password, user.password)) {
            throw IllegalArgumentException("Invalid password")
        }

        val accessToken = authTokenProvider.generateAccessToken(user.id!!, user.role)
        val refreshToken = authTokenProvider.generateRefreshToken()

        authRedisRepository.saveRefreshToken(user.id, refreshToken)

        return AuthTokenQuery(accessToken, refreshToken)
    }
}