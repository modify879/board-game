package com.jsm.boardgame.user.domain.model

class User(
    val id: Long?,
    val username: String,
    val password: String,
    val nickname: String,
    val userRole: List<UserRole>,
    val profile: String?,
)