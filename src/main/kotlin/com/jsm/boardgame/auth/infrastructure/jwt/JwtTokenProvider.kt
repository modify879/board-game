package com.jsm.boardgame.auth.infrastructure.jwt

import com.jsm.boardgame.auth.domain.model.AuthUserId
import com.jsm.boardgame.auth.infrastructure.properties.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {
    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))

    fun generateAccessToken(userId: AuthUserId, role: String): String {
        val now = Date()
        val expiration = Date(now.time + jwtProperties.accessTokenExpiration)

        return Jwts.builder()
            .subject(userId.value.toString())
            .claim("role", role)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(secretKey)
            .compact()
    }

    fun generateRefreshToken(): String {
        // refreshToken은 순수 UUID만 반환
        return UUID.randomUUID().toString().replace("-", "")
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            !claims.expiration.before(Date())
        } catch (_: Exception) {
            false
        }
    }

    fun isTokenExpired(token: String): Boolean {
        return try {
            val claims = getClaimsWithoutExpiration(token)
            claims.expiration.before(Date())
        } catch (_: Exception) {
            true // 파싱 실패 시 만료된 것으로 간주
        }
    }

    /**
     * 토큰에서 userId를 추출
     * 만료되지 않은 토큰과 만료된 토큰 모두에서 동작
     * - 만료되지 않은 토큰: 일반 파싱으로 처리
     * - 만료된 토큰: 예외에서 클레임 추출 (refresh token 재발급 시 사용)
     */
    fun getUserId(token: String): AuthUserId {
        val claims = getClaimsWithoutExpiration(token)
        return AuthUserId.of(claims.subject.toLong())
    }

    /**
     * 토큰에서 role을 추출
     * 검증된(만료되지 않은) 토큰에서만 사용
     */
    fun getRole(token: String): String {
        val claims = getClaims(token)
        return claims["role"] as String
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    /**
     * 만료 검증을 건너뛰고 클레임을 추출
     * 만료되지 않은 토큰과 만료된 토큰 모두에서 동작
     * - 만료되지 않은 토큰: 일반 파싱으로 처리 (성능 최적화)
     * - 만료된 토큰: ExpiredJwtException에서 클레임 추출
     */
    private fun getClaimsWithoutExpiration(token: String): Claims {
        return try {
            // 먼저 일반 파싱 시도 (만료되지 않은 경우 - 일반적인 케이스)
            getClaims(token)
        } catch (e: ExpiredJwtException) {
            // 만료된 토큰인 경우, 예외에서 클레임 추출
            e.claims
        } catch (e: Exception) {
            // 다른 예외(서명 오류, 형식 오류 등)는 그대로 던짐
            throw e
        }
    }
}
