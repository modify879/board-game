package com.jsm.boardgame.auth.domain.repository

import com.jsm.boardgame.auth.domain.model.AuthUserId

interface RefreshTokenRepository {
    fun save(userId: AuthUserId, refreshToken: String, expirationSeconds: Long)
    fun existsByUserIdAndRefreshToken(userId: AuthUserId, refreshToken: String): Boolean
    fun deleteByUserIdAndRefreshToken(userId: AuthUserId, refreshToken: String)
    fun deleteAllByUserId(userId: AuthUserId)
}
