package com.jsm.boardgame.auth.infrastructure.jwt

import com.jsm.boardgame.common.properties.AuthTokenProperties
import com.jsm.boardgame.user.infrastructure.jpa.entity.Role
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

@Component
class AuthTokenProvider(
    private val authTokenProperties: AuthTokenProperties
) {

    private val secretKey = Keys.hmacShaKeyFor(authTokenProperties.accessToken.secretKey.toByteArray())

    fun generateAccessToken(userId: Long, roles: List<Role>): String =
        Jwts.builder()
            .signWith(secretKey, Jwts.SIG.HS256)
            .claim(ID, userId)
            .claim(ROLE, roles)
            .claim(EXPIRATION, Date().time + authTokenProperties.accessToken.expirationInSec * 1000)
            .compact()

    fun generateRefreshToken(): String = UUID.randomUUID().toString().replace("-", "")

    fun validate(accessToken: String): Boolean {
        return try {
            val claims = getClaims(accessToken)
            claims["expiration"].toString().toLong() > Date().time
        } catch (_: Exception) {
            false
        }
    }

    fun getUserId(accessToken: String): Long? {
        return try {
            getClaims(accessToken)[ID].toString().toLong()
        } catch (_: Exception) {
            null
        }
    }

    fun getRoles(accessToken: String): List<Role> {
        return try {
            val rawList = getClaims(accessToken)[ROLE] as? List<*> ?: return emptyList()
            rawList.mapNotNull { role ->
                (role as? String)?.let {
                    try {
                        Role.valueOf(it)
                    } catch (_: IllegalArgumentException) {
                        null
                    }
                }
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