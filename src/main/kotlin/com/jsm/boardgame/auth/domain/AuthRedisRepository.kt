package com.jsm.boardgame.auth.domain

interface AuthRedisRepository {

    fun saveRefreshToken(userId: Long, refreshToken: String)
}