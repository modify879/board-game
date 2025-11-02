package com.jsm.boardgame.auth.infrastructure.jwt

import com.jsm.boardgame.auth.domain.port.out.AuthTokenProvider
import com.jsm.boardgame.common.properties.AuthTokenProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider(
    private val authTokenProperties: AuthTokenProperties
) : AuthTokenProvider {

    private val secretKey = Keys.hmacShaKeyFor(authTokenProperties.accessToken.secretKey.toByteArray())

    override fun generateAccessToken(userId: Long, roles: List<String>): String =
        Jwts.builder()
            .signWith(secretKey, Jwts.SIG.HS256)
            .claim(ID, userId)
            .claim(ROLE, roles)
            .claim(EXPIRATION, Date().time + authTokenProperties.accessToken.expirationInSec * 1000)
            .compact()

    override fun generateRefreshToken(): String = UUID.randomUUID().toString().replace("-", "")

    override fun validate(accessToken: String): Boolean {
        return try {
            val claims = getClaims(accessToken)
            claims["expiration"].toString().toLong() > Date().time
        } catch (_: Exception) {
            false
        }
    }

    override fun getUserId(accessToken: String): Long? {
        return try {
            getClaims(accessToken)[ID].toString().toLong()
        } catch (_: Exception) {
            null
        }
    }

    override fun getUserRoles(accessToken: String): List<String> {
        return try {
            when (val roles = getClaims(accessToken)[ROLE]) {
                is List<*> -> roles.mapNotNull { it?.toString() }
                else -> emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun getClaims(accessToken: String): Claims =
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(accessToken)
            .payload

    companion object {
        const val ID = "id"
        const val ROLE = "role"
        const val EXPIRATION = "expiration"
    }
}