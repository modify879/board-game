package com.jsm.boardgame.auth.domain.repository

interface AuthRedisRepository {

    fun saveRefreshToken(userId: Long, refreshToken: String, expiration: Long)

    fun existsRefreshToken(userId: Long, refreshToken: String): Boolean

    fun getRefreshTokenTTL(userId: Long, refreshToken: String): Long?

    fun deleteRefreshToken(userId: Long)
}