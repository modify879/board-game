package com.jsm.boardgame.auth.infrastructure.redis

import com.jsm.boardgame.auth.domain.AuthRedisRepository
import com.jsm.boardgame.common.properties.AuthTokenProperties
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class AuthRedisRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val authTokenProperties: AuthTokenProperties
) : AuthRedisRepository {

    override fun saveRefreshToken(userId: Long, refreshToken: String) {
        val redisKey = authTokenProperties.refreshToken.redisKey.replace("{userId}", userId.toString())
        redisTemplate.opsForSet().add(redisKey, refreshToken)

        redisTemplate.expire(
            redisKey,
            Duration.ofSeconds(authTokenProperties.refreshToken.expirationInSec.toLong())
        )
    }
}