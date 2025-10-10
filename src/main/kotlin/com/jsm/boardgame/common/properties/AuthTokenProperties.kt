package com.jsm.boardgame.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "auth.token")
data class AuthTokenProperties(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken,
) {
    data class AccessToken(
        val secretKey: String,
        val expirationInSec: Int,
    )

    data class RefreshToken(
        val expirationInSec: Int,
        val redisKey: String,
    )
}
