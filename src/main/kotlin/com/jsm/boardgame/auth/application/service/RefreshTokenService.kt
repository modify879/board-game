package com.jsm.boardgame.auth.application.service

import com.jsm.boardgame.auth.application.port.`in`.RefreshTokenCommand
import com.jsm.boardgame.auth.application.port.`in`.RefreshTokenUseCase
import com.jsm.boardgame.auth.application.port.out.TokenResult
import com.jsm.boardgame.auth.application.port.out.UserQueryPort
import com.jsm.boardgame.auth.domain.exception.InvalidTokenException
import com.jsm.boardgame.auth.domain.repository.RefreshTokenRepository
import com.jsm.boardgame.auth.infrastructure.jwt.JwtTokenProvider
import com.jsm.boardgame.auth.infrastructure.properties.JwtProperties
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RefreshTokenService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userQueryPort: UserQueryPort,
    private val jwtProperties: JwtProperties,
) : RefreshTokenUseCase {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @Transactional(readOnly = true)
    override fun refreshToken(command: RefreshTokenCommand): TokenResult {
        // accessToken에서 userId 추출 (만료된 토큰도 파싱 가능)
        val userId = try {
            jwtTokenProvider.getUserId(command.accessToken)
        } catch (_: Exception) {
            throw InvalidTokenException("Invalid access token")
        }

        return try {
            // accessToken이 만료되지 않았으면 재발급 불가하고, 해당 유저의 모든 refreshToken 비동기 삭제
            if (!jwtTokenProvider.isTokenExpired(command.accessToken)) {
                // 코루틴으로 비동기 삭제 (응답 지연 없음)
                coroutineScope.launch {
                    refreshTokenRepository.deleteAllByUserId(userId)
                }
                
                throw InvalidTokenException("Access token is not expired. Token refresh is only allowed for expired tokens.")
            }

            // Redis에서 userId와 refreshToken 조합으로 존재 여부 확인
            if (!refreshTokenRepository.existsByUserIdAndRefreshToken(userId, command.refreshToken)) {
                throw InvalidTokenException("Invalid refresh token")
            }

            val userInfo = userQueryPort.findById(userId.value) ?: throw InvalidTokenException("User not found")

            // 새로운 토큰 생성
            val newAccessToken = jwtTokenProvider.generateAccessToken(userId, userInfo.role)
            val newRefreshToken = jwtTokenProvider.generateRefreshToken()

            // 기존 refreshToken 삭제 후 새로운 refreshToken 저장 (밀리초를 초로 변환)
            val expirationSeconds = jwtProperties.refreshTokenExpiration / 1000
            refreshTokenRepository.deleteByUserIdAndRefreshToken(userId, command.refreshToken)
            refreshTokenRepository.save(userId, newRefreshToken, expirationSeconds)

            TokenResult(
                accessToken = newAccessToken,
                refreshToken = newRefreshToken,
            )
        } catch (e: InvalidTokenException) {
            // userId를 알 수 있는 상황에서 에러 발생 시 모든 refreshToken 삭제
            coroutineScope.launch {
                refreshTokenRepository.deleteAllByUserId(userId)
            }
            throw e
        }
    }
}
