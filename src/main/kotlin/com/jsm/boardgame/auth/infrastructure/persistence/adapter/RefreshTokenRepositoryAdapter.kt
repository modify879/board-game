package com.jsm.boardgame.auth.infrastructure.persistence.adapter

import com.jsm.boardgame.auth.domain.model.AuthUserId
import com.jsm.boardgame.auth.domain.repository.RefreshTokenRepository
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RefreshTokenRepositoryAdapter(
    private val redisTemplate: StringRedisTemplate
) : RefreshTokenRepository {

    override fun save(userId: AuthUserId, refreshToken: String, expirationSeconds: Long) {
        val tokenKey = getTokenKey(userId, refreshToken)
        // Redis 키 형식: refresh_token:${userId}_${refreshToken}
        redisTemplate.opsForValue().set(tokenKey, "1", expirationSeconds, TimeUnit.SECONDS)
    }

    override fun existsByUserIdAndRefreshToken(userId: AuthUserId, refreshToken: String): Boolean {
        val key = getTokenKey(userId, refreshToken)
        return redisTemplate.hasKey(key)
    }

    override fun deleteByUserIdAndRefreshToken(userId: AuthUserId, refreshToken: String) {
        val key = getTokenKey(userId, refreshToken)
        redisTemplate.delete(key)
    }

    override fun deleteAllByUserId(userId: AuthUserId) {
        val pattern = "refresh_token:${userId.value}_*"
        val scanOptions = ScanOptions.scanOptions().match(pattern).count(100).build()

        val keysToDelete = mutableSetOf<String>()
        redisTemplate.scan(scanOptions).use { cursor ->
            while (cursor.hasNext()) {
                keysToDelete.add(cursor.next())
            }
        }

        if (keysToDelete.isNotEmpty()) {
            redisTemplate.delete(keysToDelete)
        }
    }

    private fun getTokenKey(userId: AuthUserId, refreshToken: String): String {
        // Redis 키 형식: refresh_token:${userId}_${refreshToken}
        return "refresh_token:${userId.value}_$refreshToken"
    }
}
