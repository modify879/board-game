package com.jsm.boardgame.auth.domain.port.out

interface AuthTokenProvider {

    fun generateAccessToken(userId: Long, roles: List<String>): String

    fun generateRefreshToken(): String

    fun validate(accessToken: String): Boolean

    fun getUserId(accessToken: String): Long?

    fun getUserRoles(accessToken: String): List<String>
}