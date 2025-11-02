package com.jsm.boardgame.auth.infrastructure.redis.repository

import com.jsm.boardgame.auth.domain.repository.AuthRedisRepository
import com.jsm.boardgame.common.properties.AuthTokenProperties
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class AuthRedisRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val authTokenProperties: AuthTokenProperties,
) : AuthRedisRepository {

    override fun saveRefreshToken(userId: Long, refreshToken: String, expiration: Long) {
        val redisKey = generateRefreshRedisKey(userId)

        redisTemplate.execute { connection ->
            connection.hashCommands().hSet(
                redisKey.toByteArray(),
                refreshToken.toByteArray(),
                "".toByteArray()
            )
            connection.hashCommands().hExpire(
                redisKey.toByteArray(),
                authTokenProperties.refreshToken.expirationInSec.toLong(),
                refreshToken.toByteArray()
            )
        }
    }

    override fun existsRefreshToken(userId: Long, refreshToken: String): Boolean {
        val redisKey = generateRefreshRedisKey(userId)
        return redisTemplate.opsForHash<String, Any>().hasKey(redisKey, refreshToken)
    }

    override fun getRefreshTokenTTL(userId: Long, refreshToken: String): Long? {
        val redisKey = generateRefreshRedisKey(userId)
        return redisTemplate.execute { connection ->
            connection.hashCommands().hTtl(redisKey.toByteArray(), refreshToken.toByteArray())
        }?.get(0)
    }

    override fun deleteRefreshToken(userId: Long) {
        val redisKey = generateRefreshRedisKey(userId)
        println(redisKey)
        redisTemplate.delete(redisKey)
    }

    private fun generateRefreshRedisKey(userId: Long): String =
        authTokenProperties.refreshToken.redisKey.replace("{userId}", userId.toString())
}