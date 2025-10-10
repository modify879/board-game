package com.jsm.boardgame.auth.infrastructure.jwt

import com.jsm.boardgame.common.properties.AuthTokenProperties
import com.jsm.boardgame.user.infrastructure.jpa.entity.Role
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

    companion object {
        const val ID = "id"
        const val ROLE = "role"
        const val EXPIRATION = "expiration"
    }
}