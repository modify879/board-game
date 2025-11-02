package com.jsm.boardgame.user.domain.model

data class User(
    val id: Long?,
    val username: String,
    val password: String,
    val nickname: String,
    val userRole: List<UserRole>,
    val profile: String?,
)

enum class UserRole { USER, ADMIN }